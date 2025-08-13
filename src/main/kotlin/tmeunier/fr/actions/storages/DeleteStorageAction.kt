package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.DeleteStorageRequest
import tmeunier.fr.exceptions.storage.StorageNotFoundException
import java.time.LocalDateTime

@ApplicationScoped
class DeleteStorageAction {
    fun execute(request: DeleteStorageRequest): Long {
        return (if (request.isFolder) {
            val folder = FolderEntity.findById(request.id) ?: throw StorageNotFoundException("Folder ${request.id} not found")
            FileEntity.update("deletedAt = ?1 where parent = null where path = ?2", LocalDateTime.now(), folder.path + "%", )

            folder.delete()
        } else {
            val file = FileEntity.findById(request.id) ?: throw StorageNotFoundException("File ${request.id} not found")

            file.deletedAt = LocalDateTime.now()
            file.persist()
        }) as Long
    }
}