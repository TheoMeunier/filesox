package tmeunier.fr.actions.users

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.databases.entities.PermissionEntity
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.UpdateUserRequest
import tmeunier.fr.dtos.responses.UserResponse
import tmeunier.fr.exceptions.common.NotFoundException
import tmeunier.fr.exceptions.storage.StorageNotFoundException
import tmeunier.fr.services.logger
import java.util.UUID

@ApplicationScoped
class UpdateUserAction {

    fun execute(payload: UpdateUserRequest, userId: UUID): UserResponse {
        logger.info { "Updating user $userId" }

        val user = UserEntity.findById(userId) ?: throw NotFoundException()

        logger.info { "Found user ${user.name}" }
        logger.info { "Update file path to ${payload.filePath} for user ${user.filePath}" }

        val rootFolderUser = getRootFolderUser(payload.filePath, user.filePath)

        logger.info { "updated user ${user.name} with new path ${rootFolderUser.path}" }

        user.name = payload.name
        user.email = payload.email
        user.filePath = rootFolderUser.id

        updateUerPermissions(user, payload.permissions)

        user.persist()

        return UserResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            permissions = user.permissions.map { it.name },
        )
    }

    private fun getRootFolderUser(rootFolderUser: String, userFilePath: UUID): FolderEntity {
        val userRootFolder = FolderEntity.findById(userFilePath) ?: throw NotFoundException()

        if (userRootFolder.path !== "/$rootFolderUser") {
            return FolderEntity.findByPath("/$rootFolderUser") ?: throw StorageNotFoundException("Root folder not found.")
        }

        return userRootFolder
    }

    private fun updateUerPermissions(user: UserEntity, request: List<UUID>) {
        user.permissions.clear()

        request.forEach { permission ->
            logger.info { "Adding permission $permission to user ${user.name}" }

            user.permissions.add(
                PermissionEntity.findById(permission)
                    ?: throw IllegalArgumentException("Permission '$permission' does not exist.")
            )
        }
    }
}