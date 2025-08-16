package tmeunier.fr.resources.admin.logs

import jakarta.annotation.security.RolesAllowed
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.databases.entities.AuditLogEntity
import tmeunier.fr.dtos.responses.AuditLogResponse

@Path("/api/admin/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GetAllAuditLogResource
{
    @RolesAllowed("Administration")
    @Transactional
    @GET
    fun getAllAuditLogs(): Response {
        val logs = AuditLogEntity.listAll().map { audit ->
            AuditLogResponse(
                id = audit.id,
                action = audit.action.toString(),
                details = audit.details,
                username = audit.user?.name ?: "Server",
                createdAt = audit.timestamp.toString()
            )
        }

        return Response.ok(logs).build()
    }
}