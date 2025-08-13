package tmeunier.fr.resources.admin.shares

import jakarta.annotation.security.RolesAllowed
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.shares.GetAllSharesAction
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.responses.UserResponse

@Path("/api/admin/shares")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GetAllShareResource(
    private val getAllSharesAction: GetAllSharesAction
) {

    @RolesAllowed("Administration")
    @Transactional
    @GET
    fun listingShares(): Response {
        val shares = getAllSharesAction.execute()

        return Response.ok(shares).build()
    }
}