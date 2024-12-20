package fr.tmeunier.domaine.seeder

import fr.tmeunier.domaine.repositories.PermissionRepository
import fr.tmeunier.domaine.repositories.UserRepository
import fr.tmeunier.domaine.repositories.UsersPermissionsRepository

object UserSeeder {
    suspend fun seed() {
        if (UserRepository.findByEmail("admin@filesox.fr") == null) {
            val userId = UserRepository.create(null, "Administrator", "admin@filesox.fr", "adminadmin", null)
            val adminRole = PermissionRepository.findName("Administration")

            UsersPermissionsRepository.sync(userId, listOf(adminRole.id))
        }
    }
}