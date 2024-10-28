package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.domaine.models.RefreshToken
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object RefreshTokenRepository {

    private val database = Database.getConnexion()

    object RefreshToken : Table("refresh_token") {
        val id: Column<Int> = integer("id").autoIncrement()
        val refreshToken: Column<String> = varchar("refresh_token", length = 255)
        val userId: Column<Int> = (integer("user_id") references UserRepository.Users.id)
        val expiredAt = datetime("expired_at")

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun findByUserId(userId: Int): fr.tmeunier.domaine.models.RefreshToken? = dbQuery {
        RefreshToken.select { RefreshToken.userId eq userId }
            .map {
                RefreshToken(
                    it[RefreshToken.id],
                    it[RefreshToken.refreshToken],
                    it[RefreshToken.userId],
                    it[RefreshToken.expiredAt]
                )
            }
            .singleOrNull()
    }

    suspend fun findByToken(token: String): fr.tmeunier.domaine.models.RefreshToken? = dbQuery {
        RefreshToken.select { RefreshToken.refreshToken eq token }
            .map {
                RefreshToken(
                    it[RefreshToken.id],
                    it[RefreshToken.refreshToken],
                    it[RefreshToken.userId],
                    it[RefreshToken.expiredAt]
                )
            }
            .singleOrNull()
    }

    suspend fun create(userId: Int, duration: Long): String = dbQuery {
        RefreshToken.insert {
            it[refreshToken] = UUID.randomUUID().toString()
            it[RefreshToken.userId] = userId
            it[expiredAt] = LocalDateTime.now().plusMinutes(duration)
        } get RefreshToken.refreshToken
    }

    suspend fun update(token: String, duration: Long): Unit = dbQuery {
        RefreshToken.update({ RefreshToken.refreshToken eq token }) {
            it[expiredAt] = LocalDateTime.now().plusMinutes(duration)
        }
    }

    suspend fun delete(token: String) = dbQuery {
        RefreshToken.deleteWhere { refreshToken eq token }
    }
}