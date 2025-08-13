package tmeunier.fr.resources.shares

import io.quarkus.security.Authenticated
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.shares.CreateShareAction
import tmeunier.fr.actions.storages.DownloadStorageAction
import tmeunier.fr.databases.entities.ShareEntity
import tmeunier.fr.dtos.requests.CreateShareRequest
import java.util.UUID


@Path("/api/shares/dl")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class DownloadShareResource(
    private val downloadShareAction: DownloadStorageAction,
) {
    @Transactional
    @GET
    @Path("/{storageObject}")
    fun execute(
        @PathParam("storageObject") objectStorage: String,
    ): Response {
        return downloadShareAction.execute(objectStorage)
    }
}