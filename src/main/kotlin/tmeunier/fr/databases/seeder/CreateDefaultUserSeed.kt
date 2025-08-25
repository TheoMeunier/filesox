package tmeunier.fr.databases.seeder

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.databases.entities.PermissionEntity
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.exceptions.common.NotFoundException
import tmeunier.fr.services.PasswordService
import java.util.*

@ApplicationScoped
class CreateDefaultUserSeed {

    @Inject
    lateinit var passwordService: PasswordService

    @Transactional
    fun execute() {
        val defaultUser = UserEntity.findByEmail("admin@filesox.fr")

        if (defaultUser === null) {
            val rootPath = FolderEntity.findByPath("/") ?: throw NotFoundException()
            val role = PermissionEntity.findByName("Administration") ?: throw NotFoundException()

            val user = UserEntity().apply {
                id = UUID.randomUUID()
                name = "Admin"
                email = "admin@filesox.fr"
                password = passwordService.hashPassword("adminadmin")
                filePath = rootPath
                permissions = mutableSetOf(role)
            }

            user.persist()
        }

        return println("Default user already exists")
    }
}