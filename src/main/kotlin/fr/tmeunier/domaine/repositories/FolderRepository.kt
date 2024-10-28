package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.config.Security
import fr.tmeunier.domaine.response.S3Folder
import fr.tmeunier.domaine.services.LogService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

object FolderRepository {

    private val database = Database.getConnexion()

    object Folders : Table("folders") {
        val id = uuid("id")
        val path = varchar("path", length = 255)
        val parentId = uuid("parent_id").references(id, onDelete =  ReferenceOption.CASCADE).nullable()
        val createdAt = datetime("created_at")
        val updatedAt = datetime("updated_at")

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun findByPath(path: String): S3Folder? = dbQuery {
        Folders.select { Folders.path eq path }.map {
            S3Folder(
                it[Folders.id],
                it[Folders.path],
                it[Folders.parentId]
            )
        }.firstOrNull()
    }

    fun findById(id: UUID): S3Folder? {
        return transaction(database) {
            Folders.select { Folders.id eq id }.map {
                S3Folder(
                    it[Folders.id],
                    it[Folders.path],
                    it[Folders.parentId]
                )
            }.firstOrNull()
        }
    }

    suspend fun findByIdOrParentId(uuid: String): List<S3Folder> = dbQuery {
        (if (uuid != "null") {
            Folders.select { Folders.parentId eq UUID.fromString(uuid) }
        } else {
            Folders.select { Folders.parentId.isNull() }
        }).map {
            S3Folder(
                it[Folders.id],
                it[Folders.path],
                it[Folders.parentId]
            )
        }
    }

    suspend fun findByIdOrPath(path: String): List<S3Folder> = dbQuery {
        (if (path != "null") {
            Folders.select { Folders.path like "$path%" }
        } else {
            Folders.select { Folders.path.isNull() }
        }).orderBy(Folders.createdAt to SortOrder.DESC).map {
            S3Folder(
                it[Folders.id],
                it[Folders.path],
                it[Folders.parentId]
            )
        }
    }

    suspend fun create(path: String, parentId: UUID?): UUID = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_CREATE, "$path folder created")

        Folders.insert {
            it[id] = UUID.randomUUID()
            it[Folders.path] = path
            it[Folders.parentId] = parentId
            it[updatedAt] = LocalDateTime.now()
            it[createdAt] = LocalDateTime.now()
        } get Folders.id
    }

    suspend fun update(id: UUID, path: String, parentId: UUID?) = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_UPDATE, "$path folder updated")

        Folders.update({ Folders.id eq id }) {
            it[Folders.path] = path
            it[Folders.parentId] = parentId
            it[updatedAt] = LocalDateTime.now()
        }
    }

    suspend fun delete(id: UUID) = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_UPDATE, "$id folder deleted")

        Folders.deleteWhere { Folders.id eq id }
    }

    suspend fun deleteByParentId(parentId: UUID) = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_UPDATE, "$parentId folder deleted")

        transaction(database) {
            Folders.deleteWhere { Folders.parentId eq parentId }
        }
    }
}