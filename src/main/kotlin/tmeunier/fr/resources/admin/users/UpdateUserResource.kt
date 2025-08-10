package tmeunier.fr.resources.admin.users

import jakarta.annotation.security.RolesAllowed
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.users.CreateUserAction
import tmeunier.fr.actions.users.UpdateUserAction
import tmeunier.fr.dtos.requests.CreateUserRequest
import tmeunier.fr.dtos.requests.UpdateUserRequest
import java.util.UUID

@Path("/api/admin/users/update")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class UpdateUserResource(
    private val updateUserAction: UpdateUserAction
)
{
    @RolesAllowed("Administration")
    @Transactional
    @POST
    @Path("/{uuid}")
    fun execute(
        @Valid request: UpdateUserRequest,
        @PathParam("uuid") userId: UUID
    ): Response {
        val user = updateUserAction.execute(request, userId)

        return Response.ok(user).build()
    }
}