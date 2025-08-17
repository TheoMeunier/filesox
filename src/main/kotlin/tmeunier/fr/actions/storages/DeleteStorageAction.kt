package tmeunier.fr.actions.storages

import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.DeleteStorageRequest
import tmeunier.fr.exceptions.common.NotFoundException
import tmeunier.fr.exceptions.common.UnauthorizedException
import tmeunier.fr.exceptions.storage.StorageNotFoundException
import java.time.LocalDateTime
import java.util.*

@ApplicationScoped
class DeleteStorageAction(
    private val identity: SecurityIdentity,
) {
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

        verifyFolderIsNotRootFolderUser(folderId)

        FileEntity.update("deletedAt = ?1 where parent.id = ?2", LocalDateTime.now(), folder.id)

        val subFolders = FolderEntity.list("path LIKE ?1", folder.path + "%")
        subFolders.forEach {
            FileEntity.update("deletedAt = ?1 where parent.id = ?2", LocalDateTime.now(), it.id)
        }

        folder.delete()
    }

    private fun verifyFolderIsNotRootFolderUser(folderId: UUID) {
        val user = UserEntity.findById(UUID.fromString(identity.principal.name)) ?: throw NotFoundException()

        if (user.filePath.id === folderId) {
            throw UnauthorizedException()
        }
    }
}