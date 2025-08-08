package tmeunier.fr.actions.storages.uploaded

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.dtos.requests.CompleteMultipartUploadRequest
import tmeunier.fr.dtos.responses.S3File
import tmeunier.fr.services.files_system.UploadMultipartService
import tmeunier.fr.services.logger

@ApplicationScoped
class CompletedUploadStorageAction(
    private val uploader: UploadMultipartService,
) {

    fun execute(request: CompleteMultipartUploadRequest): Boolean {
        uploader.completeUpload(request.uploadId)

        val file = FileEntity.findById(request.fileId)

        if (file != null && request.isExist != file.isExist) {
            file.isExist = request.isExist
            file.persist()

            return true
        }

        return true
    }
}