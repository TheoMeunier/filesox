package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.config.Security
import fr.tmeunier.domaine.requests.InitialUploadRequest
import fr.tmeunier.domaine.response.S3File
import fr.tmeunier.domaine.services.LogService
import fr.tmeunier.domaine.services.filesSystem.StorageService
import fr.tmeunier.domaine.services.filesSystem.StorageService.toHumanReadableValue
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

object FileRepository {

    private val database = Database.getConnexion()

    object Files : Table() {

        val id = uuid("id")
        val name = varchar("name", length = 255)
        val size = long("size")
        val icon = varchar("icon", length = 255)
        val type = varchar("type", length = 255)
        val parentId = (uuid("parent_id") references FolderRepository.Folders.id).nullable()
        val updatedAt = datetime("updated_at")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Files)
        }
    }

    fun findById(id: UUID): S3File? {
        return transaction {
            Files.select { Files.id eq id }.map {
                S3File(
                    it[Files.id],
                    it[Files.name],
                    it[Files.type],
                    it[Files.size].toHumanReadableValue(),
                    it[Files.parentId],
                    it[Files.icon]
                )
            }.firstOrNull()
        }
    }

    suspend fun findAllByParentId(uuid: String): List<S3File> = dbQuery {
        (if (uuid != "null") {
            Files.select { Files.parentId eq UUID.fromString(uuid) }
        } else {
            Files.select { Files.parentId.isNull() }
        }).map {
            S3File(
                it[Files.id],
                it[Files.name],
                it[Files.type],
                it[Files.size].toHumanReadableValue(),
                it[Files.parentId],
                it[Files.icon]
            )
        }
    }

    suspend fun create(file: InitialUploadRequest, parentId: UUID?): UUID = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_UPLOAD, "${file.name} file uploaded")

        Files.insert {
            it[id] = UUID.randomUUID()
            it[name] = file.name
            it[size] = file.size
            it[type] = file.type
            it[icon] = StorageService.getIconForFile(file.name)
            it[Files.parentId] = parentId
            it[updatedAt] =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified), java.time.ZoneId.systemDefault())
        } get Files.id
    }

    suspend fun update(id: UUID, name: String, parentId: UUID?) = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_UPDATE, "$name file updated")

        Files.update({ Files.id eq id }) {
            it[Files.name] = name
            it[Files.parentId] = parentId
            it[updatedAt] = LocalDateTime.now()
        }
    }

    suspend fun move(id: UUID, parentId: UUID?) = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_MOVE, "$id file moved")

        Files.update({ Files.id eq id }) {
            it[Files.parentId] = parentId
            it[updatedAt] = LocalDateTime.now()
        }
    }

    suspend fun delete(name: String, id: UUID) = dbQuery {
        LogService.add(Security.getUserId(), LogService.ACTION_DELETE, "$name file deleted")

        Files.deleteWhere { Files.id eq id }
    }

    suspend fun deleteByParentId(parentId: UUID) = dbQuery {
        Files.deleteWhere { Files.parentId eq parentId }
    }
}