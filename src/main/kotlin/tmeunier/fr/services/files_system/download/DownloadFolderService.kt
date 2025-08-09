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
    private val chunkSize: Int = 5 * 1024 * 1024
    private val zipBufferSize: Int = 65536

    fun getFolderStructure(folderPath: String): List<FolderItem> {
        val folderItems = mutableListOf<FolderItem>()

        if (folderPath.isEmpty() || folderPath == "/" || folderPath == "root") {
            collectRootItems(folderItems)
        } else {
            val folder = FolderEntity.findByPath(folderPath)

            if (folder != null) {
                collectFolderContents(folder, "", folderItems)
            } else {
                logger.warn { "Folder not found: $folderPath" }
            }
        }

        return folderItems
    }

    private fun collectRootItems(result: MutableList<FolderItem>) {
        try {
            val rootFiles = FileEntity.list("parent IS NULL")

            rootFiles.forEach { file ->
                val s3Key = buildS3Key(file)
                result.add(FolderItem(file, null, s3Key, file.name, false))
            }

            val rootFolders = FolderEntity.list("parent IS NULL")
            rootFolders.forEach { folder ->
                result.add(FolderItem(null, folder, "", "${folder.path}/", true))
                collectFolderContents(folder, folder.path, result)
            }

        } catch (e: Exception) {
            logger.error { "Error collecting root elements : ${e.message}" }
            throw RuntimeException("Error collecting root elements", e)
        }
    }

    private fun collectFolderContents(folder: FolderEntity, relativePath: String, result: MutableList<FolderItem>) {
        try {
            val directFiles = FileEntity.findAllByParentId(folder.id)

            directFiles.forEach { file ->
                val s3Key = buildS3Key(file)
                val zipEntryPath = if (relativePath.isEmpty()) file.name else "$relativePath/${file.name}"

                result.add(FolderItem(file, null, s3Key, zipEntryPath, false))
            }

            val subFolders = FolderEntity.findAllByParentId(folder.id)

            subFolders.forEach { subFolder ->
                val zipFolderPath = if (relativePath.isEmpty()) subFolder.path else "$relativePath/${subFolder.path}"
                result.add(FolderItem(null, subFolder, "", "$zipFolderPath/", true))
                collectFolderContents(subFolder, zipFolderPath, result)
            }

        } catch (e: Exception) {
            logger.error { "Error collecting folder contents ${folder.path}: ${e.message}" }
            throw RuntimeException("Error collecting folder contents: ${folder.path}", e)
        }
    }

    fun createZipStream(folderStructure: List<FolderItem>, output: OutputStream, startTime: Instant) {
        var filesProcessed = 0
        var foldersProcessed = 0
        var totalBytesWritten = 0L

        BufferedOutputStream(output, zipBufferSize).use { bufferedOutput ->
            ZipOutputStream(bufferedOutput).use { zipOut ->

                folderStructure.forEach { item ->
                    try {
                        if (item.isFolder) {
                            createFolderEntry(zipOut, item.zipPath)
                            foldersProcessed++
                            logger.info { "Folder add in ZIP: ${item.zipPath}" }
                        } else {
                            val bytesWritten = addFileToZip(zipOut, item)
                            totalBytesWritten += bytesWritten
                            filesProcessed++

                            if (filesProcessed % 5 == 0) {
                                val totalFiles = folderStructure.count { !it.isFolder }
                                logger.info { "ZIP: $filesProcessed/$totalFiles processed files (${totalBytesWritten / 1024 / 1024} MB)" }
                            }
                        }

                        checkZipTimeout(startTime)

                    } catch (e: Exception) {
                        logger.error { "Error for add  ${item.zipPath} au ZIP: ${e.message}" }
                    }
                }

                zipOut.finish()
                logger.info { "ZIP create successfully: $filesProcessed files, $foldersProcessed folders, ${totalBytesWritten / 1024 / 1024} MB" }
            }
        }
    }

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

    private fun getFileMetadata(s3Key: String): HeadObjectResponse {
        val headRequest = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build()

        return s3Client.headObject(headRequest)
    }

    private fun buildS3Key(fileEntity: FileEntity): String {
        return "${fileEntity.id}.${storageService.pathInfo(fileEntity.name)["extension"]}"
    }

    fun sanitizeFolderName(folderPath: String): String {
        return folderPath
            .replace("/", "_")
            .replace("\\", "_")
            .replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .take(50)
    }

    private fun checkZipTimeout(startTime: Instant) {
        val maxZipTime = 1800L

        if (java.time.Duration.between(startTime, Instant.now()).seconds > maxZipTime) {
            throw WebApplicationException("Timeout lors de la création du ZIP", Response.Status.REQUEST_TIMEOUT)
        }
    }
}


data class FolderItem(
    val fileEntity: FileEntity?,
    val folderEntity: FolderEntity?,
    val s3Key: String,
    val zipPath: String,
    val isFolder: Boolean
)