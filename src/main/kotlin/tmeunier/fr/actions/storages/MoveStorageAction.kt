package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.MoveStorageRequest
import tmeunier.fr.exceptions.common.UnauthorizedException
import java.time.LocalDateTime

@ApplicationScoped
class MoveStorageAction
{
    fun execute(request: MoveStorageRequest): Any {
        val isFolder = request.path.endsWith("/")

        return if (isFolder) {
            moveFolder(request)
        } else {
            moveFile(request)
        }
    }

    private fun moveFolder(request: MoveStorageRequest): FolderEntity {
        val newPath = request.newPath.replace(request.path, "")
        val parentFolder = FolderEntity.findByPath(newPath) ?: throw UnauthorizedException()
        val folders = FolderEntity.list("path like ?1", "${request.path}%")

        folders.forEach {
            it.path = it.path.replace(request.path, request.newPath)
            it.parent = parentFolder
            it.persist()
        }

        return FolderEntity.findById(request.id)!!
    }

    private fun moveFile(request: MoveStorageRequest): FileEntity {
        val file = FileEntity.findById(request.id)
            ?: throw UnauthorizedException()

        val parentFolder = request.newPath.let {
            FolderEntity.findByPath(request.newPath)
                ?: throw UnauthorizedException()
        }

        file.parent = parentFolder
        file.updatedAt = LocalDateTime.now()
        file.persist()

        return file
    }
}