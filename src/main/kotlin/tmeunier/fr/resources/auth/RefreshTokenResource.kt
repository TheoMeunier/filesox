package tmeunier.fr.resources.auth

import io.quarkus.security.Authenticated
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.auth.RefreshTokenAction
import tmeunier.fr.dtos.requests.AuthRefreshTokenRequest

@Path("/api/auth/refresh-token")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class RefreshTokenResource(
    private val refreshTokenAction: RefreshTokenAction
)
{

    @Transactional
    @POST
    fun refreshToken(
        @Valid request: AuthRefreshTokenRequest
    ): Response? {
        val result = refreshTokenAction.execute(request)

        return Response.ok(result).build()
    }
}