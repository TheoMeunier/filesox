package tmeunier.fr.resources.profile

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.shares.GetAllShareByUserAction
import tmeunier.fr.databases.entities.AuditLogEntity
import tmeunier.fr.dtos.responses.AuditLogResponse
import java.util.UUID

@Path("/api/profile/logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GetAllAuditLogsProfileResource(
    private val identity: SecurityIdentity,
)
{
    @Authenticated
    @Transactional
    @GET
    fun getAllAuditLogs(): Response {
        val userId = UUID.fromString(identity.principal.name)
        val audits = AuditLogEntity.findByAuthUserId(userId).map { audit ->
            AuditLogResponse(
                id = audit.id,
                action = audit.action.toString(),
                details = audit.details,
                createdAt = audit.timestamp.toString()
            )
        }

        return Response.ok(audits).build()
    }
}