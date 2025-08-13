package tmeunier.fr.resources.storages

import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import tmeunier.fr.actions.storages.DownloadStorageAction

@Path("/api/download")
class DownloadStorageResource(
    private val downloadStorageAction: DownloadStorageAction
)
{
    @GET
    @RolesAllowed("Administration", "Download")
    @Path("/{objectKey}")
    fun downloadFile(
        @PathParam("objectKey") objectKey: String,
    ): Response? {
        return downloadStorageAction.execute(objectKey)
    }
}