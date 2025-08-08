package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.MoveStorageRequest
import tmeunier.fr.dtos.responses.S3File
import tmeunier.fr.dtos.responses.S3Folder
import tmeunier.fr.exceptions.common.UnauthorizedException
import tmeunier.fr.exceptions.storage.StorageNotFoundException
import tmeunier.fr.services.logger
import java.time.LocalDateTime

@ApplicationScoped
class MoveStorageAction
{
    val ROOT_PATH = "./"
    val ROOT_FOLDER = "/"

    fun execute(request: MoveStorageRequest): Any {
        val isFolder = request.storageName.endsWith("/")

        return if (isFolder) {
            moveFolder(request)
        } else {
            moveFile(request)
        }
    }

    private fun moveFolder(request: MoveStorageRequest): S3Folder {
        val folder = FolderEntity.findById(request.id) ?: throw UnauthorizedException()

        // Verify if new folder parent exists
        val movePath = if (request.newPath == ROOT_PATH) ROOT_FOLDER else "/${request.newPath}"
        val newParent = request.parentId.let { FolderEntity.findByPath(movePath) } ?: throw StorageNotFoundException("Folder $movePath not found")

        val actualNewPath =  if (movePath === ROOT_FOLDER) {
            "/${folder.path.split("/").reversed()[1]}/"
        } else {
            "${newParent.path}${folder.path.trim('/')}/"
        }

        val folders = FolderEntity
            .list("path like ?1", "${folder.path}%")
            .sortedBy { it.path.count { chart -> chart == '/'} }

        folders.forEach { folderToMove ->
            val oldPath = folderToMove.path

            val newFolderPath = if (folderToMove.id == folder.id) {
                actualNewPath
            } else {
                oldPath.replace(folder.path, actualNewPath)
            }

            folderToMove.path = newFolderPath

            // update the parent folder
            if (folderToMove.id == folder.id) {
                folderToMove.parent = newParent
            } else {
                val parentPath = newFolderPath.substringBeforeLast("/")
                folderToMove.parent = FolderEntity.findByPath(parentPath)
            }

            folderToMove.persist()
        }


        return S3Folder(folder.id, folder.path, newParent.id)
    }

    private fun moveFile(request: MoveStorageRequest): S3File {
        val file = FileEntity.findById(request.id) ?: throw StorageNotFoundException("File ${request.id} not found")

        val parentFolder = request.newPath.let {
            val folderPath = if (it == ROOT_PATH) ROOT_FOLDER else "/$it"

            FolderEntity.findByPath(folderPath)
                ?: throw StorageNotFoundException("Folder $folderPath not found")
        }

        file.parent = parentFolder
        file.updatedAt = LocalDateTime.now()
        file.persist()

        return S3File(
            id = file.id,
            name = file.name,
            type = file.type,
            size = file.size.toString(),
            parentId = file.parent?.id,
            icon = file.icon
        )
    }
}