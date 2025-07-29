package tmeunier.fr.services

import io.smallrye.jwt.build.Jwt
import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.RefreshTokenEntity
import tmeunier.fr.databases.entities.UserEntity
import java.time.Duration
import java.time.Instant
import java.time.ZoneId.*
import java.util.UUID

@ApplicationScoped
class AuthService() {

    fun generateToken(user: UserEntity): String {
        return Jwt.claims()
            .subject(user.id.toString())
            .issuer("https://filesox.io/issuer")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(Duration.ofHours(10)))
            .claim("id", user.id.toString())
            .claim("username", user.name)
            .claim("email", user.email)
            .sign()
    }

    fun generateRefreshToken(user: UserEntity): String {
        val uuid = UUID.randomUUID().toString()

        val rt = RefreshTokenEntity().apply {
            id = UUID.randomUUID()
            this.user = user
            refreshToken = UUID.fromString(uuid)
            expiredAt = Instant.now().plus(Duration.ofDays(3)).atZone(systemDefault()).toLocalDateTime()
        }

        rt.persist()

        return uuid
    }

}