package tmeunier.fr.resources.auth

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.databases.entities.RefreshTokenEntity
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.AuthRefreshTokenRequest
import tmeunier.fr.services.logger
import java.util.*

@Path("/api/auth/logout")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class LogoutResource {

    @Transactional
    @POST
    fun logout(
        @Valid request: AuthRefreshTokenRequest
    ): Response? {
        logger.info { "Logout requested for user ${request.refreshToken}" }

        val refreshToken =
            RefreshTokenEntity.find("refreshToken = ?1", UUID.fromString(request.refreshToken)).firstResult()
                ?: return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid refresh token")
                    .build()

        logger.info { "User ${refreshToken.user.id} logged out" }

        val user = UserEntity.findById(refreshToken.user.id)
            ?: return Response.status(Response.Status.BAD_REQUEST)
                .entity("User not found")
                .build()

        RefreshTokenEntity.delete("user.id = ?1", user.id)

        return Response.ok().build()
    }
}