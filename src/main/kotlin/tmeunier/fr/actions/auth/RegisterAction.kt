package tmeunier.fr.actions.auth

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.LoginRequest
import tmeunier.fr.dtos.requests.RegisterRequest
import tmeunier.fr.dtos.responses.LoginResponse
import tmeunier.fr.services.AuthService
import tmeunier.fr.services.PasswordService
import java.util.UUID

@ApplicationScoped
class RegisterAction(
    private val passwordService: PasswordService
) {
    fun execute(request: RegisterRequest): UserEntity {
        val existingUser = UserEntity.findByEmail(request.email)

        if (existingUser != null)  throw IllegalArgumentException("User with email ${request.email} already exists.")

        val user = UserEntity().apply {
            id = UUID.randomUUID()
            name = request.name
            email = request.email
            filePath = request.filePath ?: "default/path/${request.name}"
            password = passwordService.hashPassword(request.password)
        }

        user.persist()

        return user
    }
}