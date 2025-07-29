package tmeunier.fr.actions.auth

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.RefreshTokenEntity
import tmeunier.fr.dtos.requests.AuthRefreshTokenRequest
import tmeunier.fr.dtos.responses.LoginResponse
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
        ) ?: throw IllegalArgumentException("Invalid refresh token")

        if (rt.expiredAt.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Refresh token expired")
        }

        return LoginResponse(
            token = authService.generateToken(rt.user),
            refreshToken = authService.generateRefreshToken(rt.user)
        )
    }
}