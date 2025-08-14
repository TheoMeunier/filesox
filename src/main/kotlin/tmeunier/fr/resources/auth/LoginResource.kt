package tmeunier.fr.resources.auth

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.auth.LoginAction
import tmeunier.fr.dtos.requests.LoginRequest
import tmeunier.fr.services.CacheService

@Path("/api/auth/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class LoginResource(
    private val loginAction: LoginAction
) {

    @Transactional
    @POST
    fun login(@Valid request: LoginRequest): Response {
        val result = loginAction.execute(request)

        return Response.ok(result).build()
    }
}