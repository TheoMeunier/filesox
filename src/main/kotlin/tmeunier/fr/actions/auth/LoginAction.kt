package tmeunier.fr.actions.auth

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.LoginRequest
import tmeunier.fr.dtos.responses.LoginResponse
import tmeunier.fr.exceptions.auth.InvalidCredentialsException
import tmeunier.fr.exceptions.auth.UserNotFountException
import tmeunier.fr.services.AuthService
import tmeunier.fr.services.PasswordService

@ApplicationScoped
class LoginAction(
    private val authService: AuthService,
    private val passwordService: PasswordService
) {
    fun execute(request: LoginRequest): LoginResponse {
        val user = UserEntity.findByEmail(request.email)

        if (user === null) throw UserNotFountException(request.email)

        if (passwordService.verifyPassword(request.password, user.password)) {
            return LoginResponse(
                token = authService.generateToken(user),
                refreshToken = authService.generateRefreshToken(user)
            )
        } else {
            passwordService.verifyPassword(request.password, "filesox")
            false
        }

        throw InvalidCredentialsException()
    }
}