package tmeunier.fr.resources.admin.users

import jakarta.annotation.security.RolesAllowed
import jakarta.transaction.Transactional
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import tmeunier.fr.databases.entities.PermissionEntity
import tmeunier.fr.dtos.responses.PermissionResponse

@Path("/api/admin/permissions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GetAllPermissionsResource {

    @RolesAllowed("Administration")
    @Transactional
    @GET
    fun getAllPermissions(): Response {
        val permissions = PermissionEntity.listAll().map {
            PermissionResponse(
                it.id,
                it.name,
            )
        }

        return Response.ok(permissions).build()
    }
}