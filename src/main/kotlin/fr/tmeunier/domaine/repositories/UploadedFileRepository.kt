package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.domaine.models.UploadedFile
import fr.tmeunier.domaine.requests.InitialUploadRequest
import fr.tmeunier.domaine.services.filesSystem.service.StorageService
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.select
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object UploadedFileRepository {

    private val database = Database.getConnexion()

    object UploadedFiles : Table("uploaded_files") {

        val id = uuid("id")
        val name = varchar("name", length = 255)
        val size = long("size")
        val icon = varchar("icon", length = 255)
        val type = varchar("type", length = 255)
        val parentId = (uuid("parent_id") references FolderRepository.Folders.id).nullable()
        val uploadId = uuid("upload_id")
        val updatedAt = datetime("updated_at")

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun findByUploadedId(uploadId: UUID): UploadedFile = dbQuery {
        UploadedFiles.select { UploadedFiles.uploadId eq uploadId }.map {
            UploadedFile(
                it[UploadedFiles.id],
                it[UploadedFiles.name],
                it[UploadedFiles.size],
                it[UploadedFiles.type],
                it[UploadedFiles.updatedAt].atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ,
                it[UploadedFiles.parentId]
            )
        }.first()
    }

    suspend fun create(id: UUID, file: InitialUploadRequest, parentId: UUID?, uploadId: UUID): UUID = dbQuery {
        UploadedFiles.insert {
            it[UploadedFiles.id] = id
            it[name] = file.name
            it[size] = file.size
            it[type] = file.type
            it[icon] = StorageService.getIconForFile(file.name)
            it[UploadedFiles.parentId] = parentId
            it[UploadedFiles.uploadId] = uploadId
            it[updatedAt] =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified), ZoneId.systemDefault())
        } get UploadedFiles.id
    }

    suspend fun delete(uploadId: UUID) = dbQuery {
        UploadedFiles.deleteWhere { UploadedFiles.uploadId eq uploadId }
    }
}