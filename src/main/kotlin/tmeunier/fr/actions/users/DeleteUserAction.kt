package tmeunier.fr.actions.users

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.databases.entities.PermissionEntity
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.CreateUserRequest
import tmeunier.fr.dtos.responses.UserResponse
import tmeunier.fr.exceptions.auth.UserAlreadyExistFountException
import tmeunier.fr.exceptions.storage.StorageNotFoundException
import tmeunier.fr.services.PasswordService
import java.util.UUID

@ApplicationScoped
class DeleteUserAction
{
    fun execute(userId: UUID): Long
    {
        return UserEntity.delete("id = ?1", userId)
    }
}