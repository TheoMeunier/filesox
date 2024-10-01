package fr.tmeunier.web.controller.admin

import fr.tmeunier.domaine.models.getPathFromShare
import fr.tmeunier.domaine.repositories.FileRepository
import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.repositories.ShareRepository
import fr.tmeunier.domaine.repositories.UserRepository
import fr.tmeunier.domaine.requests.DeleteShareRequest
import fr.tmeunier.domaine.response.AdminSharesResponse
import fr.tmeunier.domaine.services.PaginationService
import fr.tmeunier.domaine.services.utils.formatDate
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.selectAll

object AdminShareController
{
    suspend fun findAll(call: ApplicationCall) {
        val page = call.parameters["page"]?.toInt() ?: 1

        val response = PaginationService.paginate(page, 10, { ShareRepository.findAll() }) { row ->
            val path = getPathFromShare(row[ShareRepository.Shares.type], row[ShareRepository.Shares.storageId])

            AdminSharesResponse(
                id = row[ShareRepository.Shares.id],
                username = row[UserRepository.Users.name],
                path = path,
                expiredAt = formatDate(row[ShareRepository.Shares.expiredAt], "dd/MM/yyyy mm:HH"),
                createdAt = formatDate(row[ShareRepository.Shares.createdAt])
            )
        }

        return call.respond(HttpStatusCode.OK, response)
    }

    suspend fun delete(call: ApplicationCall) {
        val request = call.receive<DeleteShareRequest>()
        ShareRepository.delete(request.id)

        return call.respond(HttpStatusCode.OK)
    }
}