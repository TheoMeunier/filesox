package tmeunier.fr.resources.storages

import io.quarkus.security.Authenticated
import jakarta.annotation.security.RolesAllowed
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.auth.LoginAction
import tmeunier.fr.actions.profile.ProfileUpdatePasswordAction
import tmeunier.fr.actions.storages.UpdateStorageAction
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.FolderCreateRequest
import tmeunier.fr.dtos.requests.LoginRequest
import tmeunier.fr.dtos.requests.UpdatePasswordRequest
import tmeunier.fr.dtos.requests.UpdateStorageRequest
import java.util.UUID

@Path("/api/storages/update")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StorageUpdateResource(
    private val updateStorageAction: UpdateStorageAction
) {
    @Authenticated
    @Transactional
    @RolesAllowed("Administrator", "Edit file")
    @POST
    fun updateStorage(@Valid request: UpdateStorageRequest): Response {
      val result = updateStorageAction.execute(request)

        return Response.ok(result)
            .build()
    }
}