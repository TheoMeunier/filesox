package tmeunier.fr.actions.profile

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.jwt.JsonWebToken
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.UpdatePasswordRequest
import tmeunier.fr.exceptions.auth.UserAlreadyExistFountException
import tmeunier.fr.exceptions.common.UnauthorizedException
import tmeunier.fr.services.PasswordService
import java.util.UUID

@ApplicationScoped
class ProfileUpdatePasswordAction(
    private val passwordService: PasswordService,
    private val jwt: JsonWebToken
) {
    fun execute(request: UpdatePasswordRequest): UserEntity {
        val userId = UUID.fromString(jwt.name)
        val user = UserEntity.findById(userId) ?: throw UnauthorizedException()

        if (request.password != request.confirmPassword) throw UserAlreadyExistFountException("Passwords do not match.")

        user.password = passwordService.hashPassword(request.password)
        user.persist()

        return user
    }
}