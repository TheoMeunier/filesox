package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.StreamingOutput
import software.amazon.awssdk.services.s3.model.S3Exception
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.services.files_system.download.DownloadFileService
import tmeunier.fr.services.files_system.StorageService
import tmeunier.fr.services.files_system.download.DownloadFolderService
import tmeunier.fr.services.logger
import java.io.OutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ApplicationScoped
class DownloadStorageAction(
    private val downloadService: DownloadFileService,
    private val storageService: StorageService,
    private val downloadFolderService: DownloadFolderService
) {
    fun execute(objectKey: String): Response {
        logger.info { "Processing download request for $objectKey" }

        if (objectKey.contains(".")) {
            return downloadFile(objectKey)
        } else {
            return downloadFolder(objectKey)
        }
    }

    private fun downloadFile(objectKey: String): Response {
        try {
            val file = FileEntity.find("name = ?1", objectKey).firstResult()
            val s3Filename = file?.id.toString() + "." + storageService.pathInfo(file!!.name)["extension"]

            val metadata = downloadService.getFileMetadata(s3Filename)
            val fileSize = metadata.contentLength()

            val streamingOutput = StreamingOutput { output: OutputStream ->
                downloadService.streamFileInChunks(s3Filename, fileSize, output)
            }

            return Response.ok(streamingOutput)
                .header(
                    "Content-Type",
                    metadata.contentType() ?: "application/octet-stream"
                )
                .header("Content-Length", fileSize)
                .header("Content-Disposition", "attachment; filename=\"${downloadService.extractFileName(file.name)}\"")
                .header("Cache-Control", "no-cache")
                .header("Accept-Ranges", "bytes")
                .build()

        } catch (e: S3Exception) {
            if (e.statusCode() == 404) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("Fichier non trouvé: $objectKey")
                    .build()
            }
            throw RuntimeException("Erreur lors de l'accès à S3", e)
        }
    }

    private fun downloadFolder(objectKey: String): Response {
        val startTime = Instant.now()

        val objectKey = "/$objectKey/"

        return try {
            logger.info { "Début création ZIP pour dossier: $objectKey" }

            // Récupération récursive de tous les fichiers du dossier
            val folderStructure = downloadFolderService.getFolderStructure(objectKey)

            if (folderStructure.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(mapOf("error" to "Dossier vide ou non trouvé", "folder" to objectKey))
                    .build()
            }

            logger.info { "Structure dossier récupérée: ${folderStructure.size} fichiers trouvés" }

            // Calcul de la taille estimée (optionnel, pour les headers)
            val estimatedSize = downloadFolderService.calculateEstimatedZipSize(folderStructure)

            // Création du nom de fichier ZIP
            val zipFilename = downloadFolderService.sanitizeFolderName(objectKey) + "_" +
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now()) + ".zip"

            // Stream de création du ZIP
            val streamingOutput = StreamingOutput { output ->
                downloadFolderService.createZipStream(folderStructure, output, startTime)
            }

            Response.ok(streamingOutput)
                .header("Content-Type", "application/zip")
                .header("Content-Disposition", "attachment; filename=\"$zipFilename\"")
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .build()

        } catch (e: Exception) {
            logger.error { "Erreur lors de la création du ZIP pour $objectKey : ${e.message}" }

            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Erreur lors de la création du ZIP", "message" to e.message))
                .build()
        }
    }
}