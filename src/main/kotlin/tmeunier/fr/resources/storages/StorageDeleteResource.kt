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
import tmeunier.fr.actions.storages.DeleteStorageAction
import tmeunier.fr.dtos.requests.DeleteStorageRequest

@Path("/api/storages/delete")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StorageDeleteResource(
    private val storageDeleteAction: DeleteStorageAction
) {
    @Authenticated
    @Transactional
    @RolesAllowed("Administrator", "Delete file or folder")
    @POST
    fun deleteStorage(@Valid request: DeleteStorageRequest): Response {
        storageDeleteAction.execute(request)

        return Response.ok()
            .status(201)
            .build()
    }
}