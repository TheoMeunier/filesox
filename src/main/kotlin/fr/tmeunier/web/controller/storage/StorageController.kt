package fr.tmeunier.web.controller.storage

import aws.smithy.kotlin.runtime.util.type
import fr.tmeunier.config.S3Config
import fr.tmeunier.config.Security
import fr.tmeunier.domaine.response.S3Response
import fr.tmeunier.domaine.repositories.FileRepository
import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.repositories.ShareRepository
import fr.tmeunier.domaine.requests.*
import fr.tmeunier.domaine.services.filesSystem.s3.S3ActionService
import fr.tmeunier.domaine.services.filesSystem.s3.S3DownloadService
import fr.tmeunier.domaine.services.utils.HashService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

object StorageController {

    suspend fun listFoldersAndFiles(call: ApplicationCall) {
        val request = call.receive<GetStorageByPathRequest>()

        val folder = FolderRepository.findByPath(request.path)

        val folders = FolderRepository.findByIdOrParentId(folder?.id.toString())
        val files = FileRepository.findAllByParentId(folder?.id.toString())

        call.respond(S3Response(folder, folders, files))
    }

    suspend fun download(call: ApplicationCall) {
        val request = call.receive<DownloadRequest>()
        try {
            if (request.isFolder) {
               S3Config.makeClient()?.let { S3DownloadService.downloadFolder(call, it, request.id) }
            } else {
                S3Config.makeClient()?.let { S3DownloadService.downloadFile(call, it, request.id.toString(), request.path) }
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error downloading: ${e.message}")
        }
    }

    suspend fun update(call: ApplicationCall) {
        val request = call.receive<UpdateStorageRequest>()

        if (request.name.endsWith('/')) {
            val folders = FolderRepository.findByIdOrPath(request.name)

            folders.forEach() { folder ->
                val name = folder.path.replace(request.name, request.newName)
                FolderRepository.update(folder.id, name, folder.parentId)
            }

            FolderRepository.update(request.id, request.newName, request.parentId)
        } else {
            FileRepository.update(request.id, request.newName, request.parentId)
        }

        call.respond(HttpStatusCode.OK)
    }

    suspend fun move(call: ApplicationCall) {
        val request = call.receive<MoveStorageRequest>()
        val isFolder = request.path.endsWith('/')

        if (isFolder) {
            val np = request.newPath.replace(request.path, "")
            val newFolderParent = FolderRepository.findByPath(np)
            val folders = FolderRepository.findByIdOrPath(request.path)

            folders.forEach { folder ->
                val name = folder.path.replace(request.path, request.newPath)
                FolderRepository.update(folder.id, name, newFolderParent?.id)
            }
        } else {
            val newParentFolder = FolderRepository.findByPath(request.newPath)
            FileRepository.move(request.id, newParentFolder?.id )
        }

        call.respond(HttpStatusCode.OK)
    }

    suspend fun share(call: ApplicationCall) {
        val request = call.receive<CreateShareRequest>()

        val expiredAt = when (request.typeDuration) {
            "hours" -> java.time.LocalDateTime.now().plusHours(request.duration.toLong())
            "days" -> java.time.LocalDateTime.now().plusDays(request.duration.toLong())
            "weeks" -> java.time.LocalDateTime.now().plusWeeks(request.duration.toLong())
            "months" -> java.time.LocalDateTime.now().plusMonths(request.duration.toLong())
            else -> throw IllegalArgumentException("Invalid type duration")
        }

        ShareRepository.create(request.storageId, request.type, Security.getUserId(), request.password, expiredAt)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun getShared(call: ApplicationCall) {
        val id = UUID.fromString(call.parameters["uuid"])
        val share = ShareRepository.findAllById(id)

        return call.respond(HttpStatusCode.OK, share)
    }

    suspend fun shareDownlaod(call: ApplicationCall) {
        val id = UUID.fromString(call.parameters["uuid"])
        val share = ShareRepository.findById(id)

        if (share.expiredAt.isBefore(java.time.LocalDateTime.now())) {
            call.respond(HttpStatusCode.BadRequest, "Share expired")
        }

        if (share.password != null) {
            val request = call.receive<CheckPasswordShareRequest>()
            if (!HashService.hashVerify(request.password, share.password)) {
                call.respond(HttpStatusCode.BadRequest, "Invalid password")
            }
        }

        if (share.type === "file") {
            val file = FileRepository.findById(share.storageId)
            S3Config.makeClient()?.let { file?.name?.let { it1 ->
                S3DownloadService.downloadFile(call, it, file.id.toString(),
                    it1
                )
            } }
        } else {
            S3Config.makeClient()?.let { S3DownloadService.downloadFolder(call, it, share.storageId)}
        }
    }

    suspend fun delete(call: ApplicationCall) {
        val request = call.receive<DeleteStorageRequest>()

        if (request.isFolder) {
            val folder = FolderRepository.findById(request.id)
            val folders = folder?.path?.let { FolderRepository.findByIdOrPath(it) }

            folders?.forEach { folder ->
                val files = FileRepository.findAllByParentId(folder.id.toString())

                files.forEach { file ->
                    S3Config.makeClient()?.let { S3ActionService.delete(it, file.name) }
                }

                FileRepository.deleteByParentId(folder.id)
            }

            folders?.forEach { folder -> FolderRepository.delete(folder.id) }
        } else {
            val file = FileRepository.findById(request.id)

            S3Config.makeClient()?.let { S3ActionService.delete(it, file?.id.toString()) }
            FileRepository.delete(file!!.name, request.id)
        }

        call.respond(HttpStatusCode.OK)
    }
}