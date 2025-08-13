package tmeunier.fr.resources.shares

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.shares.CreateShareAction
import tmeunier.fr.dtos.requests.CreateShareRequest
import java.util.UUID


@Path("/api/shares/create")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CreateShareResource(
    private val createShareAction: CreateShareAction,
    private val identity: SecurityIdentity
) {

    @Authenticated
    @Transactional
    @POST
    fun execute(
        @Valid request: CreateShareRequest
    ): Response {
        val authId = UUID.fromString(identity.principal.name)
        val result = createShareAction.execute(request, authId)

        return Response.ok(result).build()
    }
}