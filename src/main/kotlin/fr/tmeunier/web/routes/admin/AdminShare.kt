package fr.tmeunier.web.routes.admin

import fr.tmeunier.config.Security
import fr.tmeunier.web.controller.admin.AdminShareController
import io.ktor.server.application.*
import io.ktor.server.routing.*
import fr.tmeunier.core.permissions.withRole

fun Route.adminShareRouting()
{
    route("admin")
    {
       withRole(Security.ADMIN) {
           get("/shares")
           {
               return@get AdminShareController.findAll(call)
           }

           post("/shares/delete}")
           {
               return@post AdminShareController.delete(call)
           }
       }
    }
}