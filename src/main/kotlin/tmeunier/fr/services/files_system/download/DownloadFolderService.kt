package tmeunier.fr.services.files_system.download

import tmeunier.fr.services.logger
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.services.files_system.StorageService
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.math.min

@ApplicationScoped
class DownloadFolderService(
    private val s3Client: S3Client,
    private val storageService: StorageService,
)
{
    private val bucketName = "cdn"
    private val chunkSize: Int = 5 * 1024 * 1024 // 5MB
    private val zipBufferSize: Int = 65536 // 64KB

    /**
     * Récupère récursivement la structure du dossier depuis la base de données
     */
    fun getFolderStructure(folderPath: String): List<FolderItem> {
        val folderItems = mutableListOf<FolderItem>()

        if (folderPath.isEmpty() || folderPath == "/" || folderPath == "root") {
            // Dossier racine - récupérer tous les éléments sans parent
            collectRootItems(folderItems)
        } else {
            // Dossier spécifique - vérifier qu'il existe et récupérer son contenu
            val folder = FolderEntity.findByPath(folderPath)
            if (folder != null) {
                collectFolderContents(folder, "", folderItems)
            } else {
                logger.warn { "Dossier non trouvé: $folderPath" }
            }
        }

        return folderItems
    }

    /**
     * Collecte les éléments du dossier racine (sans parent)
     */
    private fun collectRootItems(result: MutableList<FolderItem>) {
        try {
            // Fichiers sans parent (racine)
            val rootFiles = FileEntity.list("parent IS NULL")
            rootFiles.forEach { file ->
                val s3Key = buildS3Key(file)
                logger.info { "Fichier racine: ${file.name} -> S3 key: $s3Key" }
                result.add(FolderItem(file, null, s3Key, file.name, false))
            }

            // Dossiers sans parent (racine)
            val rootFolders = FolderEntity.list("parent IS NULL")
            rootFolders.forEach { folder ->
                // Ajout du dossier lui-même
                result.add(FolderItem(null, folder, "", "${folder.path}/", true))

                // Récursion dans le dossier
                collectFolderContents(folder, folder.path, result)
            }

        } catch (e: Exception) {
            logger.error { "Erreur lors de la collecte des éléments racine: ${e.message}" }
            throw RuntimeException("Erreur lors de l'exploration du dossier racine", e)
        }
    }

    /**
     * Collecte le contenu d'un dossier spécifique de manière récursive
     */
    private fun collectFolderContents(folder: FolderEntity, relativePath: String, result: MutableList<FolderItem>) {
        try {
            // Fichiers directs dans ce dossier en utilisant la relation parent
            val directFiles = FileEntity.findAllByParentId(folder.id)
            directFiles.forEach { file ->
                val s3Key = buildS3Key(file)
                logger.info { "Fichier dans dossier ${folder.path}: ${file.name} -> S3 key: $s3Key" }

                val zipEntryPath = if (relativePath.isEmpty()) file.name else "$relativePath/${file.name}"
                result.add(FolderItem(file, null, s3Key, zipEntryPath, false))
            }

            // Sous-dossiers en utilisant la relation parent
            val subFolders = FolderEntity.findAllByParentId(folder.id)
            subFolders.forEach { subFolder ->
                val zipFolderPath = if (relativePath.isEmpty()) subFolder.path else "$relativePath/${subFolder.path}"

                // Ajout du dossier lui-même
                result.add(FolderItem(null, subFolder, "", "$zipFolderPath/", true))

                // Récursion dans le sous-dossier
                collectFolderContents(subFolder, zipFolderPath, result)
            }

        } catch (e: Exception) {
            logger.error { "Erreur lors de la collecte du contenu du dossier ${folder.path}: ${e.message}" }
            throw RuntimeException("Erreur lors de l'exploration du dossier: ${folder.path}", e)
        }
    }

    /**
     * Crée le stream ZIP avec tous les fichiers (structure BDD + fichiers S3)
     */
    fun createZipStream(folderStructure: List<FolderItem>, output: OutputStream, startTime: Instant) {
        var filesProcessed = 0
        var foldersProcessed = 0
        var totalBytesWritten = 0L

        BufferedOutputStream(output, zipBufferSize).use { bufferedOutput ->
            ZipOutputStream(bufferedOutput).use { zipOut ->

                folderStructure.forEach { item ->
                    try {
                        if (item.isFolder) {
                            // Création d'une entrée dossier (structure depuis BDD)
                            createFolderEntry(zipOut, item.zipPath)
                            foldersProcessed++
                            logger.info { "Dossier ajouté au ZIP: ${item.zipPath}" }
                        } else {
                            // Ajout du fichier au ZIP (fichier depuis S3)
                            logger.info { "Tentative d'ajout fichier: ${item.zipPath} avec S3 key: ${item.s3Key}" }
                            val bytesWritten = addFileToZip(zipOut, item)
                            totalBytesWritten += bytesWritten
                            filesProcessed++

                            // Log périodique
                            if (filesProcessed % 5 == 0) {
                                val totalFiles = folderStructure.count { !it.isFolder }
                                logger.info { "ZIP: $filesProcessed/$totalFiles fichiers traités (${totalBytesWritten / 1024 / 1024} MB)" }
                            }
                        }

                        // Vérification périodique du timeout
                        checkZipTimeout(startTime)

                    } catch (e: Exception) {
                        logger.error { "Erreur lors de l'ajout de ${item.zipPath} au ZIP: ${e.message}" }
                        // Continue avec les autres éléments
                    }
                }

                zipOut.finish()
                logger.info { "ZIP créé avec succès: $filesProcessed fichiers, $foldersProcessed dossiers, ${totalBytesWritten / 1024 / 1024} MB" }
            }
        }
    }

    /**
     * Crée une entrée dossier dans le ZIP (basée sur la structure BDD)
     */
    private fun createFolderEntry(zipOut: ZipOutputStream, folderPath: String) {
        val folderEntry = ZipEntry(folderPath)
        folderEntry.time = System.currentTimeMillis()
        zipOut.putNextEntry(folderEntry)
        zipOut.closeEntry()
    }

    private fun addFileToZip(zipOut: ZipOutputStream, item: FolderItem): Long {
        var totalBytesRead = 0L

        try {
            // Vérification que c'est bien un fichier
            if (item.fileEntity == null) {
                logger.warn { "Tentative d'ajout d'un non-fichier au ZIP: ${item.zipPath}" }
                return 0L
            }

            logger.info { "Traitement fichier: ${item.fileEntity.name}, S3 key: ${item.s3Key}, isExist: ${item.fileEntity.isExist}" }

            // Test d'existence sur S3 (ignorer le flag isExist pour l'instant)
            logger.info { "Vérification existence S3 pour: ${item.s3Key} (isExist en BDD: ${item.fileEntity.isExist})" }
            val metadata = try {
                getFileMetadata(item.s3Key)
            } catch (e: S3Exception) {
                if (e.statusCode() == 404) {
                    logger.warn { "Fichier S3 non trouvé: ${item.s3Key} pour fichier BDD: ${item.fileEntity.name}" }
                    // Marquer le fichier comme non-existant
                    item.fileEntity.isExist = false
                    item.fileEntity.persist()
                    return 0L
                } else {
                    logger.error { "Erreur S3 lors de la vérification: ${e.message}" }
                    throw e
                }
            }

            // Si on arrive ici, le fichier existe sur S3, on peut mettre à jour le flag
            if (!item.fileEntity.isExist) {
                logger.info { "Fichier trouvé sur S3, mise à jour isExist=true pour: ${item.fileEntity.name}" }
                item.fileEntity.isExist = true
                item.fileEntity.persist()
            }

            val fileSize = metadata.contentLength()
            logger.info { "Métadonnées S3 récupérées pour ${item.s3Key}: ${fileSize} bytes" }

            // Création de l'entrée ZIP avec métadonnées enrichies
            val zipEntry = ZipEntry(item.zipPath).apply {
                time = metadata.lastModified()?.toEpochMilli() ?: System.currentTimeMillis()
                size = fileSize
                comment = "Type: ${item.fileEntity.type}, Taille originale: ${item.fileEntity.size} bytes"
            }
            zipOut.putNextEntry(zipEntry)

            // Streaming du fichier par chunks depuis S3
            var bytesProcessed = 0L
            while (bytesProcessed < fileSize) {
                val start = bytesProcessed
                val end = min(bytesProcessed + chunkSize - 1, fileSize - 1)

                logger.debug { "Téléchargement chunk: bytes $start-$end pour ${item.s3Key}" }
                val bytesRead = downloadChunkToZip(item.s3Key, start, end, zipOut)
                bytesProcessed += bytesRead
                totalBytesRead += bytesRead
            }

            zipOut.closeEntry()
            logger.info { "Fichier ajouté au ZIP avec succès: ${item.zipPath} (${totalBytesRead} bytes)" }

        } catch (e: Exception) {
            logger.error { "Erreur lors de l'ajout du fichier ${item.zipPath}: ${e.message}" }
            // Ne pas throw pour continuer avec les autres fichiers
        }

        return totalBytesRead
    }

    /**
     * Télécharge un chunk directement dans le ZIP depuis S3
     */
    private fun downloadChunkToZip(s3Key: String, start: Long, end: Long, zipOut: ZipOutputStream): Long {
        val getRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .range("bytes=$start-$end")
            .build()

        var bytesRead = 0L

        try {
            s3Client.getObject(getRequest).use { s3Object ->
                val buffer = ByteArray(8192)
                var len: Int

                while (s3Object.read(buffer).also { len = it } != -1) {
                    zipOut.write(buffer, 0, len)
                    bytesRead += len
                }
            }
            logger.debug { "Chunk téléchargé: ${bytesRead} bytes pour $s3Key" }
        } catch (e: Exception) {
            logger.error { "Erreur téléchargement chunk $s3Key ($start-$end): ${e.message}" }
            throw e
        }

        return bytesRead
    }

    /**
     * Récupération des métadonnées S3
     */
    private fun getFileMetadata(s3Key: String): HeadObjectResponse {
        val headRequest = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build()

        return s3Client.headObject(headRequest)
    }

    /**
     * Construction de la clé S3 pour un fichier (seuls les fichiers sont sur S3)
     * Format S3: UUID.extension (ex: 550e8400-e29b-41d4-a716-446655440000.pdf)
     */
    private fun buildS3Key(fileEntity: FileEntity): String {
        val extension = storageService.pathInfo(fileEntity.name)["extension"]
        val s3Key = "${fileEntity.id}.$extension"
        logger.debug { "S3 key construit: $s3Key pour fichier: ${fileEntity.name}" }
        return s3Key
    }

    /**
     * Obtient les statistiques d'un dossier
     */
    fun getFolderStats(folderPath: String): Map<String, Any> {
        val structure = getFolderStructure(folderPath)
        val files = structure.filter { !it.isFolder }
        val folders = structure.filter { it.isFolder }

        val totalSize = files.sumOf { item ->
            try {
                item.fileEntity?.size ?: 0L
            } catch (e: Exception) {
                0L
            }
        }

        return mapOf(
            "folderPath" to folderPath,
            "totalItems" to structure.size,
            "filesCount" to files.size,
            "foldersCount" to folders.size,
            "totalSizeBytes" to totalSize,
            "totalSizeMB" to (totalSize / 1024.0 / 1024.0),
            "estimatedZipSizeMB" to (totalSize * 0.9 / 1024.0 / 1024.0) // Estimation
        )
    }

    /**
     * Calcul estimé de la taille du ZIP
     */
    fun calculateEstimatedZipSize(folderStructure: List<FolderItem>): Long {
        return folderStructure
            .filter { !it.isFolder }
            .sumOf { item ->
                item.fileEntity?.size ?: 0L
            } * 90 / 100 + (folderStructure.size * 100) // 90% + overhead
    }

    /**
     * Nettoyage du nom de dossier pour le ZIP
     */
    fun sanitizeFolderName(folderPath: String): String {
        return folderPath
            .replace("/", "_")
            .replace("\\", "_")
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .take(50)
    }

    /**
     * Vérification du timeout pour la création de ZIP
     */
    private fun checkZipTimeout(startTime: Instant) {
        val maxZipTime = 1800L // 30 minutes max pour un ZIP
        if (java.time.Duration.between(startTime, Instant.now()).seconds > maxZipTime) {
            throw WebApplicationException("Timeout lors de la création du ZIP", Response.Status.REQUEST_TIMEOUT)
        }
    }
}

/**
 * Classe représentant un élément dans la structure de dossier
 * Correction: séparation entre FileEntity et FolderEntity
 */
data class FolderItem(
    val fileEntity: FileEntity?, // Non-null pour les fichiers (contenu sur S3)
    val folderEntity: FolderEntity?, // Non-null pour les dossiers (structure en BDD)
    val s3Key: String, // Vide pour les dossiers, clé S3 pour les fichiers
    val zipPath: String, // Chemin dans le ZIP
    val isFolder: Boolean // true = dossier (BDD), false = fichier (S3)
)