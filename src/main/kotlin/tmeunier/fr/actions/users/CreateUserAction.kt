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
class CreateUserAction(
    private val passwordService: PasswordService
)
{
    fun execute(payload: CreateUserRequest): UserResponse
    {
        val existingUser = UserEntity.findByEmail(payload.email)

        if (existingUser != null) throw UserAlreadyExistFountException(payload.email)

        val rootFolderUser = getRootFolderUser(payload.filePath)

        val user = UserEntity().apply {
            id = UUID.randomUUID()
            name = payload.name
            email = payload.email
            filePath = rootFolderUser.id
            password = passwordService.hashPassword(payload.password)
            payload.permissions.forEach { permission ->
                this.permissions.add(
                    PermissionEntity.findById(permission)
                        ?: throw IllegalArgumentException("Permission '$permission' does not exist.")
                )
            }
        }

        user.persist()

        return UserResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            permissions = user.permissions.map { it.name },
        )
    }

    private fun getRootFolderUser(rootFolderUser: String): FolderEntity
    {
        val folder = FolderEntity.findByPath("/$rootFolderUser")

        if (folder === null) {
            val rootFolder = FolderEntity.findByPath("/")

            if (rootFolder === null) throw StorageNotFoundException("Root folder not found. Please create a root folder first.")

            val newFolder = FolderEntity().apply {
                id = UUID.randomUUID()
                this.path = rootFolderUser
                this.parent = rootFolder
            }

            newFolder.persist()

            return newFolder
        }

        return folder
    }
}