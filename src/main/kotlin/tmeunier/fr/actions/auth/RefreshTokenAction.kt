package tmeunier.fr.actions.auth

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.RefreshTokenEntity
import tmeunier.fr.dtos.requests.AuthRefreshTokenRequest
import tmeunier.fr.dtos.responses.LoginResponse
import tmeunier.fr.exceptions.auth.InvalidCredentialsException
import tmeunier.fr.exceptions.auth.RefreshTokenExpiredException
import tmeunier.fr.services.AuthService
import java.time.LocalDateTime
import java.util.UUID

@ApplicationScoped
class RefreshTokenAction(
    private val authService: AuthService,
) {
    fun execute(request: AuthRefreshTokenRequest): LoginResponse {
        val rt = RefreshTokenEntity.findByRefreshToken(
            UUID.fromString(request.refreshToken)
        ) ?: throw InvalidCredentialsException()

        if (rt.expiredAt.isBefore(LocalDateTime.now())) {
            throw RefreshTokenExpiredException()
        }

        return LoginResponse(
            token = authService.generateToken(rt.user),
            refreshToken = authService.generateRefreshToken(rt.user)
        )
    }
}