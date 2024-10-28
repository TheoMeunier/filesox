package fr.tmeunier.domaine.seeder

import fr.tmeunier.config.Database
import fr.tmeunier.domaine.repositories.PermissionRepository.Permissions
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object PermissionSeeder
{
    private val database = Database.getConnexion()

    fun seed() {
        val permissionsToInsert = listOf(
            "Administration",
            "Create file or folder",
            "Delete file or folder",
            "Download",
            "Edit file",
            "Share files",
            "Rename file or folder"
        )

        transaction(database) {
            val permissions = Permissions.selectAll().count()

            if (permissions == 0L) {
                permissionsToInsert.forEach { permission ->
                    Permissions.insert {
                        it[name] = permission
                    }
                }
            }
        }
    }
}