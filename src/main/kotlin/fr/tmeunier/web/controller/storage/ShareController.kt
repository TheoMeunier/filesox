package fr.tmeunier.web.controller.storage

import fr.tmeunier.config.Security
import fr.tmeunier.domaine.repositories.FileRepository
import fr.tmeunier.domaine.repositories.ShareRepository
import fr.tmeunier.domaine.requests.CheckPasswordShareRequest
import fr.tmeunier.domaine.requests.CreateShareRequest
import fr.tmeunier.domaine.services.filesSystem.FileSystemServiceFactory
import fr.tmeunier.domaine.services.utils.HashService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.util.*

object ShareController {
    suspend fun share(call: ApplicationCall) {
        val request = call.receive<CreateShareRequest>()

        val expiredAt = when (request.typeDuration) {
            "hours" -> java.time.LocalDateTime.now().plusHours(request.duration.toLong())
            "days" -> java.time.LocalDateTime.now().plusDays(request.duration.toLong())
            "weeks" -> java.time.LocalDateTime.now().plusWeeks(request.duration.toLong())
            "months" -> java.time.LocalDateTime.now().plusMonths(request.duration.toLong())
            else -> throw IllegalArgumentException("Invalid type duration")
        }

        ShareRepository.create(request.storageId, request.type, Security.getUserId(), request.password, expiredAt)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun getShared(call: ApplicationCall) {
        val id = UUID.fromString(call.parameters["uuid"])
        val share = ShareRepository.findAllById(id)

        return call.respond(HttpStatusCode.OK, share)
    }

    suspend fun shareDownload(call: ApplicationCall) {
        val id = UUID.fromString(call.parameters["uuid"])
        val share = ShareRepository.findById(id)

        if (share.expiredAt.isBefore(java.time.LocalDateTime.now())) {
            call.respond(HttpStatusCode.BadRequest, "Share expired")
        }

        if (share.password != null) {
            val request = call.receive<CheckPasswordShareRequest>()
            if (!HashService.hashVerify(request.password, share.password)) {
                call.respond(HttpStatusCode.BadRequest, "Invalid password")
            }
        }

        if (share.type === "file") {
            val file = FileRepository.findById(share.storageId)
            FileSystemServiceFactory.createStorageService().downloadMultipart(call, file?.id.toString(), false, file?.name)
        } else {
            FileSystemServiceFactory.createStorageService().downloadMultipart(call, share.storageId.toString(), true, null)
        }
    }
}