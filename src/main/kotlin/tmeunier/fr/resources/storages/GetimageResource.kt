package tmeunier.fr.resources.storages

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.storages.GetImageStorageAction
import java.util.UUID

@ApplicationScoped
@Path("/api/images")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GetImageResource(
    private val getImageStorageAction: GetImageStorageAction
)
{
    @GET
    @Path("/{image}")
    fun getImage(
        @PathParam("image") image: UUID,
    ): Response {
        val (imageData, mineType) = getImageStorageAction.execute(image) ?: return Response.status(Response.Status.NOT_FOUND).build()

        return Response.ok(imageData)
            .type(mineType)
            .header("Cache-Control", "max-age=31536000, public")
            .header("Content-Disposition", "inline;")
            .build()
    }
}