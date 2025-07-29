package tmeunier.fr.resources.auth

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.auth.RegisterAction
import tmeunier.fr.dtos.requests.RegisterRequest
import tmeunier.fr.dtos.responses.RegisterResponse

@Path("/api/auth/register")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class RegisterResource(
    private val registerAction: RegisterAction
) {
    @Transactional
    @POST
    fun register(
        @Valid request: RegisterRequest
    ): Response {
        val result = registerAction.execute(request)

        return Response.ok(RegisterResponse(
            name = result.name,
            email = result.email,
            filePath = result.filePath,
        )).build()
    }
}