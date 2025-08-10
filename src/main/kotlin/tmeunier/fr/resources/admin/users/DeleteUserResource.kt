package tmeunier.fr.resources.admin.users

import jakarta.annotation.security.RolesAllowed
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
import tmeunier.fr.actions.users.CreateUserAction
import tmeunier.fr.actions.users.DeleteUserAction
import tmeunier.fr.dtos.requests.CreateUserRequest
import java.util.UUID

@Path("/api/admin/users/delete")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class DeleteUserResource(
    private val deleteUserAction: DeleteUserAction
)
{
    @RolesAllowed("Administration")
    @Transactional
    @DELETE
    @Path("/{uuid}")
    fun execute(
        @PathParam("uuid") userId: UUID,
    ): Response {
        val user = deleteUserAction.execute(userId)
        return Response.ok(user).build()
    }
}