package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.domaine.models.Permission
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

object PermissionRepository {
    private val database = Database.getConnexion()

    object Permissions : Table("permissions") {
        val id: Column<Int> = integer("id").autoIncrement()
        val name: Column<String> = varchar("name", length = 255)

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun findName(name: String ): Permission = dbQuery {
        Permissions.select({ Permissions.name eq name }).map {
            Permission(
                id = it[Permissions.id],
                name = it[Permissions.name]
            )
        }.first()
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