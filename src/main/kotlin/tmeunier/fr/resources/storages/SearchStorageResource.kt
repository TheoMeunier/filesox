package tmeunier.fr.resources.storages

import io.quarkus.security.Authenticated
import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.storages.SearchStorageAction

@Path("/api/storages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SearchStorageResource(
    private val searchStorageAction: SearchStorageAction
) {

    @Authenticated
    @Transactional
    @GET
    fun searchObject(
        @QueryParam("search") search: String,
    ): Response {
        val result = searchStorageAction.execute(search)
        return Response.ok(result).build()
    }
}