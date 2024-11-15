package fr.tmeunier.web.controller.storage

import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.requests.FolderCreateRequest
import fr.tmeunier.domaine.requests.UserPrincipal
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object FolderController {

    suspend fun create(call: ApplicationCall) {
        val request = call.receive<FolderCreateRequest>()
        val user = call.principal<UserPrincipal>()

        FolderRepository.create(user?.id!!, request.path, request.parentId)
        call.respond(HttpStatusCode.Created)
    }
}