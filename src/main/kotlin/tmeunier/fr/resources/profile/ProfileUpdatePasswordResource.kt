package tmeunier.fr.resources.profile

import io.quarkus.security.Authenticated
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.profile.ProfileUpdatePasswordAction
import tmeunier.fr.dtos.requests.UpdatePasswordRequest

@Path("/api/profile/update-password")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProfileUpdatePasswordResource(
    private val profileUpdatePasswordAction: ProfileUpdatePasswordAction,
) {

    @Authenticated
    @Transactional
    @POST
    fun profileUpdatePassword(
        @Valid request: UpdatePasswordRequest
    ): Response {
        profileUpdatePasswordAction.execute(request)
        return Response.ok().build()
    }
}