package fr.tmeunier

import fr.tmeunier.config.Database
import fr.tmeunier.config.configureHTTP
import fr.tmeunier.domaine.jobs.ClearStorageCacheJob
import fr.tmeunier.domaine.jobs.ShareJob
import fr.tmeunier.domaine.repositories.*
import fr.tmeunier.domaine.seeder.PermissionSeeder
import fr.tmeunier.domaine.seeder.UserSeeder
import fr.tmeunier.domaine.services.filesSystem.FileSystemServiceFactory
import fr.tmeunier.web.routes.configurationRoute
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

val dotenv = dotenv {}

suspend fun main() {
    // init config
    Database.init()
    FileSystemServiceFactory.initialize(dotenv["STORAGE"])

    // init database schema
    initDatabaseSchema()

    //jobs
    ShareJob.initJob()
    ClearStorageCacheJob.initJob()

    // start server
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configurationRoute()
}

suspend fun initDatabaseSchema() {
    transaction {
        SchemaUtils.create(UserRepository.Users)
        SchemaUtils.create(RefreshTokenRepository.RefreshToken)
        SchemaUtils.create(PermissionRepository.Permissions)
        SchemaUtils.create(UsersPermissionsRepository.UsersPermissions)
        SchemaUtils.create(ShareRepository.Shares)
        SchemaUtils.create(LogRepository.Logs)
        SchemaUtils.create(FolderRepository.Folders)
        SchemaUtils.create(FileRepository.Files)
        SchemaUtils.create(UploadedFileRepository.UploadedFiles)
    }

    PermissionSeeder.seed()
    UserSeeder.seed()
}
