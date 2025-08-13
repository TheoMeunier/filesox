package tmeunier.fr.resources.shares

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.shares.CreateShareAction
import tmeunier.fr.databases.entities.ShareEntity
import tmeunier.fr.dtos.requests.CreateShareRequest
import java.util.UUID


@Path("/api/shares/delete")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class DeleteShareResource(
    private val identity: SecurityIdentity
) {

    @Authenticated
    @Transactional
    @DELETE
    @Path("/{uuid}")
    fun execute(
        @PathParam("uuid") shareId: UUID,
    ): Response {

        if (identity.hasRole("Administration")) {
            ShareEntity.delete("id = ?1", shareId)
        } else {
            val authId = UUID.fromString(identity.principal.name)
            ShareEntity.delete("id = ?1 AND user.id = ?2", shareId, authId)
        }

        return Response.ok().build()
    }
}