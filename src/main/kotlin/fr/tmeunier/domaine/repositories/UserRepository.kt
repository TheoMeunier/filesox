package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.config.Security
import fr.tmeunier.domaine.models.User
import fr.tmeunier.domaine.services.LogService
import fr.tmeunier.domaine.services.utils.HashService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object UserRepository {

    private val database = Database.getConnexion()

    object Users : Table("users") {
        val id: Column<Int> = integer("id").autoIncrement()
        val name: Column<String> = varchar("name", length = 255)
        val email: Column<String> = varchar("email", length = 255)
        val filePath: Column<UUID?> = uuid("file_path").nullable()
        val password: Column<String> = varchar("password", length = 255)
        val layout: Column<Boolean> = bool("layout").default(false)
        val createdAt: Column<LocalDateTime> = datetime("created_at")
        val updatedAt: Column<LocalDateTime> = datetime("updated_at")

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun create(name: String, email: String, password: String, filePath: UUID?): Int = dbQuery {
        if (Security.getUserId() != 0) {
            LogService.add(Security.getUserId(), LogService.ACTION_CREATE, "$name created")
        }

        Users.insert {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.filePath] = filePath
            it[Users.password] = HashService.hashPassword(password)
            it[createdAt] = LocalDateTime.now()
            it[updatedAt] = LocalDateTime.now()
        } get Users.id
    }

    suspend fun update(id: Int, name: String, email: String): Int = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_UPDATE, "$name updated")

        Users.update({ Users.id eq id }) {
            it[Users.name] = name
            it[Users.email] = email
            it[updatedAt] = LocalDateTime.now()
        }
    }

    suspend fun adminUpdate(id: Int, name: String, email: String, filePath: UUID?) = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_UPDATE, "$name updated")

        Users.update({ Users.id eq id }) {
            it[Users.name] = name
            it[Users.email] = email
            it[Users.filePath] = filePath
            it[updatedAt] = LocalDateTime.now()
        }
    }

    suspend fun updatePassword(id: Int, password: String) = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_UPDATE, "updated account")

        Users.update({ Users.id eq id }) {
            it[Users.password] = HashService.hashPassword(password)
            it[updatedAt] = LocalDateTime.now()
        }
    }

    suspend fun delete(id: Int) = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_DELETE, "updated account")
        Users.deleteWhere { Users.id eq id }
    }

    suspend fun findByEmail(email: String): User? = findBy { Users.email eq email }

    suspend fun findById(id: Int): User? = findBy { Users.id eq id }

    private suspend fun findBy(where: SqlExpressionBuilder.() -> Op<Boolean>): User? = dbQuery {
        Users.select(where)
            .map {
                User(
                    it[Users.id],
                    it[Users.name],
                    it[Users.email],
                    it[Users.password],
                    it[Users.filePath],
                    it[Users.layout],
                    it[Users.createdAt],
                    it[Users.updatedAt]
                )
            }
            .singleOrNull()
    }
}