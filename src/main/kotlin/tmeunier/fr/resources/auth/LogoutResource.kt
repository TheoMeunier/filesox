package tmeunier.fr.resources.auth

import io.quarkus.security.Authenticated
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.jwt.JsonWebToken
import tmeunier.fr.databases.entities.RefreshTokenEntity
import tmeunier.fr.databases.entities.UserEntity
import java.util.UUID

@Path("/api/auth/logout")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class LogoutResource(
    private val jwt: JsonWebToken
) {

    @Transactional
    @Authenticated
    @POST
    fun logout(): Response? {
        val user = UserEntity.findById(UUID.fromString(jwt.name))

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity("User not found")
                .build()
        }

        RefreshTokenEntity.delete("user.id = ?1", user.id)

        return Response.ok().build()
    }
}