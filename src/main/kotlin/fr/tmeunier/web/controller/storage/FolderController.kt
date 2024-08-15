package fr.tmeunier.web.controller.storage

import aws.sdk.kotlin.services.s3.model.S3Exception
import fr.tmeunier.config.S3Config
import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.requests.*
import fr.tmeunier.domaine.services.filesSystem.FolderSystemService
import fr.tmeunier.domaine.services.filesSystem.StorageService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import java.io.File

object FolderController {

    suspend fun create(call: ApplicationCall) {
        val request = call.receive<FolderCreateRequest>()
        FolderRepository.create(request.path, request.parentId)
        call.respond(HttpStatusCode.Created)
    }

    suspend fun download(call: ApplicationCall) {
        val request = call.receive<DownloadRequest>()

        if (request.isFolder) {
            val zipFile = File(".cache/media/${request.path}.zip")

            if (!zipFile.exists()) {
                S3Config.makeClient()?.let {
                    FolderSystemService.downloadFolder(it, request.path, ".cache/media/${request.path}")
                }

                StorageService.zipFolder(".cache/media/${request.path}", ".cache/media/${request.path}.zip")
                call.respondFile(File(".cache/media/${request.path}.zip"))
            } else {
                call.respondFile(zipFile)
            }
        } else {
            val fileInCache = File(".cache/media/${request.path}")

            if (!fileInCache.exists()) {
                S3Config.makeClient()
                    ?.let { it1 ->
                        FolderSystemService.downloadFileMultipart(
                            it1,
                            "${request.path}",
                            ".cache/media/${request.path}"
                        )
                    }
                call.respondFile(File(".cache/media/${request.path}"))
            } else {
                call.respondFile(fileInCache)
            }
        }
    }

    suspend fun upload(call: ApplicationCall) {
        val multipart = call.receiveMultipart()
        var uploadId: String? = null
        var chunkNumber: Int? = null
        var totalChunks: Int? = null
        var originalFileName: String? = null
        var fileBytes: ByteArray? = null

        println("multipart " + multipart)

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

        if (chunkNumber != null && totalChunks != null && originalFileName != null && fileBytes != null) {
            val key = "$originalFileName"

            runBlocking {
                try {
                    uploadId = S3Config.makeClient()?.let {
                        FolderSystemService.uploadMultipart(it, key, uploadId, chunkNumber!!, fileBytes, totalChunks!!)
                    }
                } catch (e: S3Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error uploading chunk ${e.message}")
                }
            }

            call.respond(HttpStatusCode.OK, "Chunk $chunkNumber uploaded successfully")
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid upload data")
        }
    }
}