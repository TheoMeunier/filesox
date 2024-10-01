package fr.tmeunier

import fr.tmeunier.config.Database
import fr.tmeunier.config.configureHTTP
import fr.tmeunier.domaine.jobs.ShareJob
import fr.tmeunier.web.routes.configurationRoute
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*


fun main() {
    Database.init()

    ShareJob.initJob()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configurationRoute()
}
