package fr.tmeunier.web.controller.profile

import fr.tmeunier.domaine.models.getPathFromShare
import fr.tmeunier.domaine.repositories.ShareRepository
import fr.tmeunier.domaine.requests.DeleteShareRequest
import fr.tmeunier.domaine.requests.UserPrincipal
import fr.tmeunier.domaine.response.ProfileSharesResponse
import fr.tmeunier.domaine.services.PaginationService
import fr.tmeunier.domaine.services.utils.formatDate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object ProfileShareController {

    suspend fun getShares(call: ApplicationCall) {
        val user = call.principal<UserPrincipal>()
        val page = call.parameters["page"]?.toInt() ?: 1

        val respond = PaginationService.paginate(page, 10, { ShareRepository.findAllByUser(user?.id!!) }) { row ->
            val path = getPathFromShare(row[ShareRepository.Shares.type], row[ShareRepository.Shares.storageId])

            ProfileSharesResponse(
                id = row[ShareRepository.Shares.id],
                path = path,
                expiredAt = formatDate(row[ShareRepository.Shares.expiredAt],"dd/MM/yyyy HH:mm"),
                createdAt = formatDate(row[ShareRepository.Shares.createdAt]),
            )
        }

        return call.respond(HttpStatusCode.OK, respond)
    }

    suspend fun delete(call: ApplicationCall) {
        val request = call.receive<DeleteShareRequest>()
        ShareRepository.delete(request.id)

        return call.respond(HttpStatusCode.OK)
    }
}