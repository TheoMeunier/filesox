package tmeunier.fr.actions.profile

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.jwt.JsonWebToken
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.UpdatePasswordRequest
import tmeunier.fr.dtos.requests.UpdateProfileRequest
import tmeunier.fr.exceptions.auth.UserAlreadyExistFountException
import tmeunier.fr.exceptions.common.UnauthorizedException
import tmeunier.fr.services.PasswordService
import java.util.UUID

@ApplicationScoped
class ProfileUpdateAction(
    private val jwt: JsonWebToken
) {
    fun execute(request: UpdateProfileRequest): UserEntity {
        val userId = UUID.fromString(jwt.name)
        val user = UserEntity.findById(userId) ?: throw UnauthorizedException()

        user.name = request.name
        user.email = request.email
        user.persist()

        return user
    }
}