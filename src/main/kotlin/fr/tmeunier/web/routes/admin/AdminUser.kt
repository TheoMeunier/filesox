package fr.tmeunier.web.routes.admin

import fr.tmeunier.web.controller.admin.AdminUserController
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.adminUserRouting()
{
    route("/admin")
    {
        get("/permissions") {
            return@get AdminUserController.getAllPermissions(call)
        }

        get("/users")
        {
            return@get AdminUserController.getAll(call)
        }

        post("/users/create")
        {
            return@post AdminUserController.create(call)
        }

        post("/users/update/{id}")
        {
            return@post AdminUserController.update(call)
        }

        delete("/users/delete/{id}")
        {
            return@delete AdminUserController.delete(call)
        }
    }
}