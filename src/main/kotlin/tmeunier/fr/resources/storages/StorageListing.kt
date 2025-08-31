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
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.GetStorageByPathRequest
import tmeunier.fr.dtos.responses.S3File
import tmeunier.fr.dtos.responses.S3Folder
import tmeunier.fr.dtos.responses.S3Response
import tmeunier.fr.services.files_system.toHumanReadableValue

@Path("/api/storages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StorageListing {

    @Authenticated
    @Transactional
    @POST
    fun storageCreateFolder(@Valid request: GetStorageByPathRequest): Response {
        val folder = FolderEntity.findByPath(request.path)
            ?: throw IllegalArgumentException("Folder not found for path: ${request.path}")

        val folders = FolderEntity.findAllByParentId(folder.id).map { S3Folder(it.id, it.path, it.parent?.id) }
        val files = FileEntity.findAllByParentId(folder.id)
            .map { S3File(it.id, it.name, it.type, it.size.toHumanReadableValue(), it.parent?.id, it.icon) }

        return Response.ok(
            S3Response(
                folder = folder.let { S3Folder(it.id, it.path, it.parent?.id) },
                folders = folders,
                files = files,
            )
        )
            .status(200)
            .build()
    }
}