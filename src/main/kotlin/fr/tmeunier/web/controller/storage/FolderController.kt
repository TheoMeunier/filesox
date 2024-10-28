package fr.tmeunier.web.controller.storage

import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.requests.FolderCreateRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object FolderController {

    suspend fun create(call: ApplicationCall) {
        val request = call.receive<FolderCreateRequest>()
        FolderRepository.create(request.path, request.parentId)
        call.respond(HttpStatusCode.Created)
    }
}