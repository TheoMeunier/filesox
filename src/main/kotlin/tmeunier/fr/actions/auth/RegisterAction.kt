package tmeunier.fr.actions.auth

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.BadRequestException
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.RegisterRequest
import tmeunier.fr.exceptions.auth.UserNotFountException
import tmeunier.fr.services.PasswordService
import java.util.*

@ApplicationScoped
class RegisterAction(
    private val passwordService: PasswordService
) {
    fun execute(request: RegisterRequest): UserEntity {
        val existingUser = UserEntity.findByEmail(request.email)
        val rootFolder = FolderEntity.findByPath("/") ?: throw BadRequestException("Root folder not found")

        if (existingUser != null) throw UserNotFountException(request.email)

        val user = UserEntity().apply {
            id = UUID.randomUUID()
            name = request.name
            email = request.email
            filePath = rootFolder
            password = passwordService.hashPassword(request.password)
        }

        user.persist()

        return user
    }
}