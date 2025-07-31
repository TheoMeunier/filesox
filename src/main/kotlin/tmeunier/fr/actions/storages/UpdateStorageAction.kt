package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.MoveStorageRequest
import tmeunier.fr.dtos.requests.UpdateStorageRequest
import tmeunier.fr.dtos.responses.S3File
import tmeunier.fr.dtos.responses.S3Folder
import tmeunier.fr.exceptions.common.UnauthorizedException
import java.time.LocalDateTime

@ApplicationScoped
class UpdateStorageAction
{
    fun execute(request: UpdateStorageRequest): Any {
        val isFolder = request.name.endsWith("/") || request.name.startsWith("/")

        return if (isFolder) {
            updateFolder(request)
        } else {
            updateFile(request)
        }
    }

    private fun updateFolder(request: UpdateStorageRequest): S3Folder {
        val folder = FolderEntity.findById(request.id) ?: throw UnauthorizedException()
        val folders = FolderEntity.list("path like ?1", "${request.name}%")

        folders.forEach {
            it.path = it.path.replace(request.name, request.newName)
            it.persist()
        }

        folder.path = request.newName
        folder.updatedAt = LocalDateTime.now()
        folder.persist()

        return S3Folder(folder.id, folder.path, folder.parent?.id)
    }

    private fun updateFile(request: UpdateStorageRequest): S3File {
        val file = FileEntity.findById(request.id)
            ?: throw UnauthorizedException()

        file.name = request.newName
        file.updatedAt = LocalDateTime.now()
        file.persist()

        return S3File(
            file.id,
            file.name,
            file.type,
            file.size.toString(),
            file.parent?.id,
            file.icon
        )
    }
}