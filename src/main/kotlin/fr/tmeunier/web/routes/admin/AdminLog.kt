package fr.tmeunier.web.routes.admin

import fr.tmeunier.config.Security
import fr.tmeunier.web.controller.admin.AdminLogController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import fr.tmeunier.core.permissions.withRole

fun Route.adminLogRouting()
{
    route("admin")
    {
       withRole(Security.ADMIN) {
           get("/logs")
           {
               return@get AdminLogController.getAll(call)
           }
       }
    }
}