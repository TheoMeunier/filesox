package tmeunier.fr.resources.shares

import jakarta.transaction.Transactional
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.shares.DownloadShareAction
import tmeunier.fr.actions.storages.DownloadStorageAction


@Path("/api/shares/dl")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class DownloadShareResource(
    private val shareDownloadAction: DownloadShareAction,
    private val downloadStorageAction: DownloadStorageAction,
) {
    @Transactional
    @GET
    @Path("/{storageObject}")
    fun execute(
        @PathParam("storageObject") objectStorage: String,
        @QueryParam("password") password: String? = null,
    ): Response {
        val result = shareDownloadAction.execute(objectStorage, password)

        if (!result) {
            return Response.status(Response.Status.UNAUTHORIZED).build()
        }

        return downloadStorageAction.execute(objectStorage)
    }
}