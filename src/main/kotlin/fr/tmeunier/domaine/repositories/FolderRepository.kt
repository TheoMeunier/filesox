package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.domaine.response.S3Folder
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object FolderRepository {

    private val database = Database.getConnexion()

    object Folders : Table() {
        val id = uuid("id")
        val path = varchar("path", length = 255)
        val parentId = (uuid("parent_id") references id).nullable()
        val updatedAt = datetime("updated_at")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Folders)
        }
    }

    fun findByPath(path: String): S3Folder? {
        return transaction(database) {
            Folders.select { Folders.path eq path }.map {
                S3Folder(
                    it[Folders.id],
                    it[Folders.path],
                    it[Folders.parentId]
                )
            }.firstOrNull()
        }
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

    fun findByIdOrParentId(uuid: String): MutableList<S3Folder> {
        val folders = mutableListOf<S3Folder>()

        transaction(database) {
            val query = if (uuid != "null") {
                Folders.select { Folders.parentId eq UUID.fromString(uuid) }
            } else {
                Folders.select { Folders.parentId.isNull() }
            }

            query.forEach {
                folders.add(
                    S3Folder(
                        it[Folders.id],
                        it[Folders.path],
                        it[Folders.parentId]
                    )
                )
            }
        }

        return folders
    }

    fun findByIdOrPath(path: String): MutableList<S3Folder> {
        val folders = mutableListOf<S3Folder>()

        transaction(database) {
            val query = if (path != "null") {
                Folders.select { Folders.path like "$path%"}
            } else {
                Folders.select { Folders.path.isNull() }
            }

            query.forEach {
                folders.add(
                    S3Folder(
                        it[Folders.id],
                        it[Folders.path],
                        it[Folders.parentId]
                    )
                )
            }
        }

        return folders
    }

    fun create(path: String, parentId: UUID?): UUID {
        return transaction(database) {
            Folders.insert {
                it[id] = UUID.randomUUID()
                it[Folders.path] = path
                it[Folders.parentId] = parentId
                it[Folders.updatedAt] = java.time.LocalDateTime.now()
            }
        } get Folders.id
    }

    fun update (id: UUID, path: String, parentId: UUID?) {
        transaction(database) {
            Folders.update({ Folders.id eq id }) {
                it[Folders.path] = path
                it[Folders.parentId] = parentId
                it[Folders.updatedAt] = java.time.LocalDateTime.now()
            }
        }
    }

    fun delete (id: UUID) {
        transaction(database) {
            Folders.deleteWhere { Folders.id eq id }
        }
    }

    fun deleteByParentId (parentId: UUID) {
        transaction(database) {
            Folders.deleteWhere { Folders.parentId eq parentId }
        }
    }
}