package fr.tmeunier.domaine.repositories

import fr.tmeunier.config.Database
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.domaine.models.UserWidthPermissionResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object UsersPermissionsRepository {
    private val database = Database.getConnexion()

    object UsersPermissions : Table("users_permissions") {
        val id: Column<Int> = integer("id").autoIncrement()
        val userId: Column<Int> = integer("user_id").references(UserRepository.Users.id)
        val permissionId: Column<Int> = integer("permission_id").references(PermissionRepository.Permissions.id)

        override val primaryKey = PrimaryKey(id)
    }

    fun findUserPermissions(userId: Int): List<String> {
        return transaction(database) {
            UsersPermissions.join(PermissionRepository.Permissions, JoinType.INNER,
                additionalConstraint = { UsersPermissions.permissionId eq PermissionRepository.Permissions.id }
            )
                .select { UsersPermissions.userId eq userId }
                .map {
                    it[PermissionRepository.Permissions.name]
                }
        }
    }

    suspend fun findUserWithPermissions(userId: Int): UserWidthPermissionResponse = dbQuery {
        val user = UserRepository.Users.select { UserRepository.Users.id eq userId }.single()
        val permissions = UsersPermissions.join(
            PermissionRepository.Permissions,
            JoinType.INNER,
            additionalConstraint = { UsersPermissions.permissionId eq PermissionRepository.Permissions.id }
        )
            .select { UsersPermissions.userId eq userId }
            .map {
                it[PermissionRepository.Permissions.name]
            }

        UserWidthPermissionResponse(
            user[UserRepository.Users.id],
            user[UserRepository.Users.name],
            user[UserRepository.Users.email],
            user[UserRepository.Users.filePath],
            permissions
        )
    }

    suspend fun create(userId: Int, permissionsId: List<Int>) = dbQuery {
        UsersPermissions.batchInsert(permissionsId) { permissionId ->
            this[UsersPermissions.userId] = userId
            this[UsersPermissions.permissionId] = permissionId
        }
    }

    suspend fun sync(userId: Int, permissionsId: List<Int>) = dbQuery {
        transaction {
            UsersPermissions.deleteWhere { UsersPermissions.userId eq userId }

            UsersPermissions.batchInsert(permissionsId) { permissionId ->
                this[UsersPermissions.userId] = userId
                this[UsersPermissions.permissionId] = permissionId
            }
        }
    }

    suspend fun delete(userId: Int) = dbQuery {
        UsersPermissions.deleteWhere { UsersPermissions.userId eq userId }
    }
}