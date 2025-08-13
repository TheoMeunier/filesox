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
import java.util.UUID

@Path("/api/profile/shares")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GetAllShareProfileResource(
    private val identity: SecurityIdentity,
    private val getAllShareProfileResource: GetAllShareByUserAction
)
{
    @Authenticated
    @Transactional
    @GET
    fun getAllShareProfile(): Response {
        val shares = getAllShareProfileResource.execute(UUID.fromString(identity.principal.name))
        return Response.ok(shares).build()
    }
}