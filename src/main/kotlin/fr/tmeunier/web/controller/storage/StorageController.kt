package fr.tmeunier.web.controller.storage

import fr.tmeunier.domaine.repositories.FileRepository
import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.requests.*
import fr.tmeunier.domaine.response.S3Response
import fr.tmeunier.domaine.services.filesSystem.FileSystemServiceFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object StorageController {

    suspend fun listFoldersAndFiles(call: ApplicationCall) {
        val request = call.receive<GetStorageByPathRequest>()

        val folder = FolderRepository.findByPath(request.path)

        val folders = FolderRepository.findByIdOrParentId(folder?.id.toString())
        val files = FileRepository.findAllByParentId(folder?.id.toString())

        call.respond(S3Response(folder, folders, files))
    }

    suspend fun search(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val files = FileRepository.search(search)

        call.respond(S3Response(null, null, files))
    }

    suspend fun download(call: ApplicationCall) {
        val request = call.receive<DownloadRequest>()
        try {
            FileSystemServiceFactory.createStorageService().downloadMultipart(call,request.id.toString(), request.isFolder,  request.path)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Error downloading: ${e.message}")
        }
    }

    suspend fun update(call: ApplicationCall) {
        val request = call.receive<UpdateStorageRequest>()

        if (request.name.endsWith('/')) {
            val folders = FolderRepository.findByIdOrPath(request.name)

            folders.forEach { folder ->
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

    suspend fun delete(call: ApplicationCall) {
        val request = call.receive<DeleteStorageRequest>()

        if (request.isFolder) {
            val folder = FolderRepository.findById(request.id)
            val folders = folder?.path?.let { FolderRepository.findByIdOrPath(it) }

            folders?.forEach { folder ->
                val files = FileRepository.findAllByParentId(folder.id.toString())

                files.forEach { file ->
                    FileSystemServiceFactory.createStorageService().delete(file.name)
                }

                FileRepository.deleteByParentId(folder.id)
            }

            folders?.forEach { folder -> FolderRepository.delete(folder.id) }
        } else {
            val file = FileRepository.findById(request.id)

            FileSystemServiceFactory.createStorageService().delete(file?.id.toString())
            FileRepository.delete(file!!.name, request.id)
        }

        call.respond(HttpStatusCode.OK)
    }
}