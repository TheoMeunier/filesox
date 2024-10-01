package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.domaine.models.Permission
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object PermissionRepository {
    private val database = Database.getConnexion()

    object Permissions : Table() {
        val id: Column<Int> = integer("id").autoIncrement()
        val name: Column<String> = varchar("name", length = 255)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Permissions)
        }
    }

    suspend fun findAll(): List<Permission> = dbQuery {
        Permissions.selectAll().map {
            Permission(
                id = it[Permissions.id],
                name = it[Permissions.name]
            )
        }
    }
}