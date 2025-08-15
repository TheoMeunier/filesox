package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.DeleteStorageRequest
import tmeunier.fr.exceptions.storage.StorageNotFoundException
import tmeunier.fr.services.logger
import java.time.LocalDateTime
import java.util.UUID

@ApplicationScoped
class DeleteStorageAction {
    fun execute(request: DeleteStorageRequest) {
        if (request.isFolder) {
            deleteFolder(request.id)
        } else {
            val file = FileEntity.findById(request.id) ?: throw StorageNotFoundException("File ${request.id} not found")

            file.deletedAt = LocalDateTime.now()
            file.persist()
        }
    }

    private fun deleteFolder(folderId: UUID) {
        val folder = FolderEntity.findById(folderId) ?: throw StorageNotFoundException("Folder ${folderId} not found")

        FileEntity.update("deletedAt = ?1 where parent.id = ?2", LocalDateTime.now(), folder.id)


        val subFolders = FolderEntity.list("path LIKE ?1", folder.path + "%")
        subFolders.forEach {
            FileEntity.update("deletedAt = ?1 where parent.id = ?2", LocalDateTime.now(), it.id)
        }

        folder.delete()
    }
}