package fr.tmeunier.web.controller.storage

import aws.sdk.kotlin.services.s3.model.S3Exception
import fr.tmeunier.domaine.repositories.FileRepository
import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.repositories.UploadedFileRepository
import fr.tmeunier.domaine.requests.CompletedUpload
import fr.tmeunier.domaine.requests.InitialUploadRequest
import fr.tmeunier.domaine.requests.VerifyUploadListRequest
import fr.tmeunier.domaine.response.UploadCompleteResponse
import fr.tmeunier.domaine.services.filesSystem.FileSystemServiceFactory
import fr.tmeunier.domaine.services.filesSystem.service.StorageService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import java.util.*

object UploadController {
    suspend fun verify(call: ApplicationCall) {
        var exists = false
        val request = call.receive<VerifyUploadListRequest>()

        request.files.forEach {
            exists = FileRepository.exists(it.name, it.parentId)
        }

        if (exists) {
            call.respond(HttpStatusCode.OK, mapOf("exist" to true))
        } else {
            call.respond(HttpStatusCode.OK, mapOf("exist" to false))
        }
    }

    suspend fun initUploader(call: ApplicationCall) {
        val request = call.receive<InitialUploadRequest>()
        val fileUuid: UUID = UUID.randomUUID()

        // Create folder if it doesn't exist
        val parentId = if (request.webRelativePath === "") {
            request.parentId
        } else {
            request.webRelativePath?.let { it1 -> createFolderUploadFile(it1, request.parentId) }
        }

        val filename = fileUuid.toString() + '.' + StorageService.pathinfo(request.name)["extension"]
        val uploadId = FileSystemServiceFactory.createStorageService().initMultipart(filename)

        if (request.isExist) {
            UploadedFileRepository.create(fileUuid, request, parentId, UUID.fromString(uploadId))
        } else {
            FileRepository.create(fileUuid, request, parentId)
        }

        if (uploadId != null) {
            call.respond(HttpStatusCode.OK, UploadCompleteResponse(uploadId, filename))
        } else {
            call.respond(HttpStatusCode.InternalServerError, "Failed to initiate upload")
        }
    }

    suspend fun upload(call: ApplicationCall) {
        val multipart = call.receiveMultipart()
        var uploadId: String? = null
        var chunkNumber: Int? = null
        var totalChunks: Int? = null
        var originalFileName: String? = null
        var fileBytes: ByteArray? = null

        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    when (part.name) {
                        "uploadId" -> uploadId = part.value
                        "chunkNumber" -> chunkNumber = part.value.toIntOrNull()
                        "totalChunks" -> totalChunks = part.value.toIntOrNull()
                    }
                }

                is PartData.FileItem -> {
                    originalFileName = part.originalFileName
                    fileBytes = part.streamProvider().readBytes()
                }

                else -> {}
            }
            part.dispose()
        }

        if (uploadId != null && chunkNumber != null && totalChunks != null && originalFileName != null && fileBytes != null) {
            val key = "$originalFileName"

            runBlocking {
                try {
                    FileSystemServiceFactory.createStorageService().uploadMultipart(
                        key,
                        uploadId,
                        chunkNumber!!,
                        fileBytes,
                        totalChunks!!
                    )
                    call.respond(HttpStatusCode.OK, "Chunk $chunkNumber uploaded successfully")
                } catch (e: S3Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error uploading chunk ${e.message}")
                }
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid upload data")
        }
    }

    suspend fun completedUpload(call: ApplicationCall) {
        val request = call.receive<CompletedUpload>()

        runBlocking {
            try {
                FileSystemServiceFactory.createStorageService().closeMultiPart(request.filename, request.uploadId)

                if (request.isExist) {
                    // add switch file in uploaded_files to files
                    val file = UploadedFileRepository.findByUploadedId(UUID.fromString(request.uploadId))
                    FileRepository.deleteByParentIdAndName(file.name, file.parentId)

                    FileRepository.create(
                        file.id, InitialUploadRequest(file.name, file.size, file.type, file.lastModified, null, file.parentId, 0, false),
                        file.parentId
                    )
                }

                call.respond(HttpStatusCode.OK, "Upload completed successfully")
            } catch (e: S3Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error completing upload ${e.message}")
            }
        }
    }

    private suspend fun createFolderUploadFile(path: String, parentId: UUID?): UUID {
        val folderPathRequest = path.substringBeforeLast("/") + '/'
        val folderParent = parentId?.let { FolderRepository.findById(it) }
        val folderParentPath = if (folderParent !== null) folderParent.path else ""
        val folder = FolderRepository.findByPath(folderParentPath + folderPathRequest)

         if (folder !== null) {
             return folder.id
        }

        val folderPath = (folderParent?.path ?: "") + folderPathRequest
        return  FolderRepository.create(folderPath, parentId ?: folderParent?.id)
    }
}