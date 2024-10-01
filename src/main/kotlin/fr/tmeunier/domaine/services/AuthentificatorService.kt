package fr.tmeunier.domaine.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import fr.tmeunier.config.Security
import fr.tmeunier.domaine.models.User
import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.repositories.RefreshTokenRepository
import fr.tmeunier.domaine.repositories.UsersPermissionsRepository
import java.time.LocalDateTime
import java.time.ZoneId

object AuthentificatorService {
    private const val JWT_ACCESS_TOKEN_EXPIRATION_TIME: Long = 2 // access token refreshed every 2 mins
    private const val JWT_REFRESH_TOKEN_EXPIRATION_TIME: Long = 60 * 16 // maximum session lifetime of 16h

    suspend fun createJwtToken(user: User): String {

        val role = UsersPermissionsRepository.findUserPermissions(user.id)
        val folder = if (user.filePath != null) FolderRepository.findById(user.filePath) else null

        return JWT.create()
            .withSubject(user.id.toString())
            .withClaim("id", user.id)
            .withClaim("name", user.name)
            .withClaim("email", user.email)
            .withClaim("file_path", folder?.path)
            .withClaim("path_id", folder?.id.toString())
            .withClaim("layout", user.layout)
            .withClaim("roles", role)
            .withAudience(Security.jwtAudience)
            .withIssuer(Security.jwtIssuer)
            .withExpiresAt(
                LocalDateTime.now().plusMinutes(JWT_ACCESS_TOKEN_EXPIRATION_TIME).atZone(ZoneId.systemDefault())
                    .toInstant()
            )
            .sign(Algorithm.HMAC256(Security.jwtSecret))
    }

    suspend fun createRefreshToken(userId: Int): String {
        return RefreshTokenRepository.create(userId, JWT_REFRESH_TOKEN_EXPIRATION_TIME)
    }

    suspend fun updateRefreshToken(refreshToken: String): String {
        RefreshTokenRepository.update(refreshToken, JWT_REFRESH_TOKEN_EXPIRATION_TIME)
        return refreshToken
    }

    fun refreshTokenIsValid(expiresAt: LocalDateTime): Boolean {
        return LocalDateTime.now().isBefore(expiresAt)
    }
}