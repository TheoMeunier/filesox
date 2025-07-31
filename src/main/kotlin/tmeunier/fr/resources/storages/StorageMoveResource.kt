package tmeunier.fr.resources.storages

import io.quarkus.security.Authenticated
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.storages.MoveStorageAction
import tmeunier.fr.dtos.requests.MoveStorageRequest

@Path("/api/storages/move")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StorageMoveResource(
    private val moveStorageAction: MoveStorageAction
) {
    @Authenticated
    @Transactional
    @POST
    fun moveStorage(@Valid request: MoveStorageRequest): Response {
        val result = moveStorageAction.execute(request)

        return Response.ok(result)
            .build()
    }
}