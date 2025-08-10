package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.StreamingOutput
import software.amazon.awssdk.services.s3.model.S3Exception
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.exceptions.storage.ErrorCreatingZipException
import tmeunier.fr.exceptions.storage.ErrorS3Exception
import tmeunier.fr.exceptions.storage.StorageNotFoundException
import tmeunier.fr.services.files_system.download.DownloadFileService
import tmeunier.fr.services.files_system.StorageService
import tmeunier.fr.services.files_system.download.DownloadFolderService
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
        return if (objectKey.contains(".")) {
            downloadFile(objectKey)
        } else {
            downloadFolder(objectKey)
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
                throw StorageNotFoundException("File $objectKey not found")
            }

            throw ErrorS3Exception("Error while downloading file $objectKey: ${e.message}")
        }
    }

    private fun downloadFolder(objectKey: String): Response {
        val startTime = Instant.now()
        val objectKey = "/$objectKey/"

        return try {
            val folderStructure = downloadFolderService.getFolderStructure(objectKey)

            if (folderStructure.isEmpty()) {
               throw StorageNotFoundException("No folder found: $objectKey")
            }

            val zipFilename = downloadFolderService.sanitizeFolderName(objectKey) + "_" +
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now()) + ".zip"

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
           throw ErrorCreatingZipException("Error creating zip file: ${e.message}")
        }
    }
}