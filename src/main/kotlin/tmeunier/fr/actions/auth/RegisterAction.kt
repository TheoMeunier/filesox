package tmeunier.fr.actions.auth

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.BadRequestException
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.LoginRequest
import tmeunier.fr.dtos.requests.RegisterRequest
import tmeunier.fr.dtos.responses.LoginResponse
import tmeunier.fr.exceptions.auth.UserAlreadyExistFountException
import tmeunier.fr.exceptions.auth.UserNotFountException
import tmeunier.fr.services.AuthService
import tmeunier.fr.services.PasswordService
import java.util.UUID

@ApplicationScoped
class RegisterAction(
    private val passwordService: PasswordService
) {
    fun execute(request: RegisterRequest): UserEntity {
        val existingUser = UserEntity.findByEmail(request.email)

        if (existingUser != null) throw UserNotFountException(request.email)

        val user = UserEntity().apply {
            id = UUID.randomUUID()
            name = request.name
            email = request.email
            filePath = UUID.randomUUID()
            password = passwordService.hashPassword(request.password)
        }

        user.persist()

        return user
    }
}