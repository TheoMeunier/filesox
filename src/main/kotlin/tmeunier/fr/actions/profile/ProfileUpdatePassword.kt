package tmeunier.fr.actions.profile

import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.UpdatePasswordRequest
import tmeunier.fr.exceptions.auth.UserAlreadyExistFountException
import tmeunier.fr.exceptions.common.UnauthorizedException
import tmeunier.fr.services.PasswordService
import tmeunier.fr.services.logger
import java.util.*

@ApplicationScoped
class ProfileUpdatePasswordAction(
    private val passwordService: PasswordService,
    private val identity: SecurityIdentity
) {
    fun execute(request: UpdatePasswordRequest): Boolean {
        val userId = UUID.fromString(identity.principal.name)
        val user = UserEntity.findById(userId) ?: throw UnauthorizedException()

        if (request.password != request.confirmPassword) throw UserAlreadyExistFountException("Passwords do not match.")

        user.password = passwordService.hashPassword(request.password)
        user.persist()

        logger.info { "Password successfully updated for user ${request.password}" }

        return true
    }
}