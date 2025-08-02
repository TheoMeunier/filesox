package tmeunier.fr.resources.admin.users

import jakarta.annotation.security.RolesAllowed
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.responses.UserResponse

@Path("/api/admin/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ListingUserResource {

    @RolesAllowed("Administration")
    @Transactional
    @GET
    fun listingUser(): Response {
        val users = UserEntity.listAll().map { user ->
            UserResponse(
                id = user.id,
                name = user.name,
                email = user.email,
                permissions = user.permissions.map { it.name },
                createdAt = user.createdAt.toString()
            )
        }

        return Response.ok(users).build()
    }
}