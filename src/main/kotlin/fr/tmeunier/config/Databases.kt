package fr.tmeunier.config

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object Database {
     private lateinit var connexion: Database;

    private val dotenv: Dotenv = dotenv {
        directory = "."
        filename = ".env"
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }

    private val db_host = dotenv["DB_HOST"] ?: "0.0.0.0"
    private val db_port = dotenv["DB_PORT"] ?: "3306"
    private val db_name = dotenv["DB_NAME"] ?: "cdn"
    private val db_username = dotenv["DB_USER"] ?: "cdn"
    private val db_password = dotenv["DB_PASSWORD"] ?: "cdn"

    fun init() {
        connexion =  Database.connect(
            url = "jdbc:mariadb://${db_host}:${db_port}/${db_name}",
            driver = "org.mariadb.jdbc.Driver",
            user = db_username,
            password = db_password
        )
    }

    fun getConnexion(): Database {
        if (!::connexion.isInitialized) {
            throw IllegalStateException("Database connexion has not been initialized")
        }

        return connexion
    }


    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

}

