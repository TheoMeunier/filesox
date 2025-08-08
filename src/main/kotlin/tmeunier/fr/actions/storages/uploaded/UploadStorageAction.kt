package tmeunier.fr.actions.storages.uploaded

import jakarta.enterprise.context.ApplicationScoped
import org.jboss.resteasy.reactive.multipart.FileUpload
import tmeunier.fr.dtos.responses.UploadPartResult
import tmeunier.fr.services.files_system.UploadMultipartService
import tmeunier.fr.services.logger
import kotlin.io.path.inputStream

@ApplicationScoped
class UploadStorageAction(
    private val uploadService: UploadMultipartService,
) {

    fun execute(uploadId: String, chunkNumber: Int, totalChunks: Int, file: FileUpload): UploadPartResult {
        logger.info { "Processing chunk $chunkNumber/$totalChunks for upload $uploadId" }

        if (file.size() <= 0) {
            throw IllegalArgumentException("File chunk is empty")
        }

        val inputStream = file.uploadedFile().inputStream()

        val etag = uploadService.uploadPart(
            uploadId = uploadId,
            partData = inputStream,
            partSize = file.size()
        )

        logger.info { "Successfully uploaded chunk $chunkNumber/$totalChunks for upload $uploadId, ETag: $etag" }


        return UploadPartResult.PartUploaded(etag)
    }
}