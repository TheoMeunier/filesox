package tmeunier.fr.resources.storages

import io.smallrye.jwt.auth.principal.JWTParser
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import tmeunier.fr.actions.storages.DownloadStorageAction

@Path("/api/download")
class DownloadStorageResource(
    private val downloadStorageAction: DownloadStorageAction,
    private val jwtParser: JWTParser
) {
    @GET
    @Path("/{objectKey}")
    fun downloadFile(
        @PathParam("objectKey") objectKey: String,
        @QueryParam("token") token: String,
    ): Response? {
        jwtParser.parse(token) ?: return Response.status(Response.Status.UNAUTHORIZED).build()
        return downloadStorageAction.execute(objectKey)
    }
}