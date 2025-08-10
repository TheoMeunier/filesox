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
import tmeunier.fr.actions.auth.LoginAction
import tmeunier.fr.actions.profile.ProfileUpdatePasswordAction
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.FolderCreateRequest
import tmeunier.fr.dtos.requests.LoginRequest
import tmeunier.fr.dtos.requests.UpdatePasswordRequest
import tmeunier.fr.dtos.responses.S3Folder
import java.util.UUID

@Path("/api/storages/folders/create")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StorageCreateFolderResource {

    @Transactional
    @RolesAllowed("Administration", "Create file or folder")
    @POST
    fun storageCreateFolder(@Valid request: FolderCreateRequest): Response {
        val parentFolder = request.parentId.let { FolderEntity.findById(it) }

        val folderPath = if (!request.path.endsWith("/")) {
            "${parentFolder?.path}${request.path}/"
        } else {
            parentFolder?.path + request.path
        }

        val folder = FolderEntity().apply {
            id = UUID.randomUUID()
            path = folderPath
            parent = parentFolder
        }

        folder.persist()

        return Response.ok(S3Folder(
            id = folder.id,
            path = folder.path,
            parentId = parentFolder?.id
        ))
            .status(Response.Status.CREATED)
            .build()
    }
}