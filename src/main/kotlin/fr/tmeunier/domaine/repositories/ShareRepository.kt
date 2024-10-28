package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.domaine.models.ShareModel
import fr.tmeunier.domaine.response.ShareShowResponse
import fr.tmeunier.domaine.services.utils.HashService
import fr.tmeunier.domaine.services.utils.formatDate

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

object ShareRepository {

    private val database = Database.getConnexion()

    object Shares : Table("shares") {
        val id = uuid("id")
        val storageId = uuid("storage_id")
        val type = varchar("type", length = 255)
        val userId = integer("user_id").references(UserRepository.Users.id)
        val password = varchar("password", length = 255).nullable()
        val expiredAt = datetime("expired_at")
        val createdAt = datetime("created_at")

        override val primaryKey = PrimaryKey(id)
    }

    fun findAll(): Query {
        return transaction {
            Shares.innerJoin(UserRepository.Users)
                .selectAll()
                .orderBy(Shares.createdAt to SortOrder.DESC)
        }
    }

    fun findAllByUser(user: Int) = transaction {
        Shares.innerJoin(UserRepository.Users)
            .select { Shares.userId eq user }
            .orderBy(Shares.createdAt to SortOrder.DESC)
    }

    suspend fun findAllById(id: UUID): List<ShareShowResponse> = dbQuery {
        Shares.select { Shares.storageId eq id }
            .map {
                ShareShowResponse(
                    id = it[Shares.id],
                    expiredAt = formatDate(it[Shares.expiredAt], "dd/MM/yyyy HH:mm"),
                    createdAt = it[Shares.createdAt].toString()
                )
            }
    }

    suspend fun findById(id: UUID): ShareModel = dbQuery {
        Shares.select { Shares.id eq id }
            .map {
                ShareModel(
                    id = it[Shares.id],
                    storageId = it[Shares.storageId],
                    type = it[Shares.type],
                    password = it[Shares.password],
                    expiredAt = it[Shares.expiredAt],
                )
            }
            .first()
    }

    //Get all shares with export date before now
    suspend fun findAllExpired(): List<ShareModel> = dbQuery {
        Shares.select { Shares.expiredAt less LocalDateTime.now() }
            .map {
                ShareModel(
                    id = it[Shares.id],
                    storageId = it[Shares.storageId],
                    type = it[Shares.type],
                    password = it[Shares.password],
                    expiredAt = it[Shares.expiredAt],
                )
            }
    }

    suspend fun create(storageId: UUID, type: String, userId: Int, password: String?, expiredAt: LocalDateTime) =
        dbQuery {
            Shares.insert {
                it[id] = UUID.randomUUID()
                it[Shares.storageId] = storageId
                it[Shares.type] = type
                it[Shares.userId] = userId
                it[Shares.password] = password?.let { HashService.hashPassword(password) }
                it[Shares.expiredAt] = expiredAt
                it[createdAt] = LocalDateTime.now()
            }
        }

    suspend fun delete(id: UUID) = dbQuery {
        Shares.deleteWhere { Shares.id eq id }
    }
}