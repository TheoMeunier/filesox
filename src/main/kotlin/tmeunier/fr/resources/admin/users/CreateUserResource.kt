package tmeunier.fr.resources.admin.users

import jakarta.annotation.security.RolesAllowed
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.users.CreateUserAction
import tmeunier.fr.dtos.requests.CreateUserRequest

@Path("/api/admin/users/create")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CreateUserResource(
    private val createUserAction: CreateUserAction
)
{
    @RolesAllowed("Administration")
    @Transactional
    @POST
    fun execute(
        @Valid request: CreateUserRequest
    ): Response {
        val user = createUserAction.execute(request)

        return Response.ok(user).build()
    }
}