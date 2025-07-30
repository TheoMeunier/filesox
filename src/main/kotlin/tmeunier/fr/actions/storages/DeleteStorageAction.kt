package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.DeleteStorageRequest

@ApplicationScoped
class DeleteStorageAction
{
    fun execute(request: DeleteStorageRequest): Long {
        return if (request.isFolder) {
           FolderEntity.delete("id = ?1", request.id)
        } else {
            FileEntity.delete("id = ?1", request.id)
        }
    }
}