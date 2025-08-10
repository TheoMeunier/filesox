package tmeunier.fr.resources.storages

import jakarta.annotation.security.RolesAllowed
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.storages.UpdateStorageAction
import tmeunier.fr.dtos.requests.UpdateStorageRequest

@Path("/api/storages/update")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StorageUpdateResource(
    private val updateStorageAction: UpdateStorageAction
) {
    @Transactional
    @RolesAllowed("Administration", "Edit file")
    @POST
    fun updateStorage(@Valid request: UpdateStorageRequest): Response {
      val result = updateStorageAction.execute(request)

        return Response.ok(result)
            .build()
    }
}