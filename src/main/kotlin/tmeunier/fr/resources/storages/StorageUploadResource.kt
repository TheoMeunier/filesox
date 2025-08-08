package tmeunier.fr.resources.storages

import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload
import tmeunier.fr.actions.storages.uploaded.CompletedUploadStorageAction
import tmeunier.fr.actions.storages.uploaded.InitUploadStorageAction
import tmeunier.fr.actions.storages.uploaded.UploadStorageAction
import tmeunier.fr.actions.storages.uploaded.VerifyUploadStorageAction
import tmeunier.fr.dtos.requests.CompleteMultipartUploadRequest
import tmeunier.fr.dtos.requests.InitUploadMultipartRequest
import tmeunier.fr.dtos.requests.VerifyUploadMultipartRequest
import tmeunier.fr.dtos.responses.UploadPartResult

@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/files/upload")
class StorageUploadResource(
    private val verifyFileStorageAction: VerifyUploadStorageAction,
    private val initUploadService: InitUploadStorageAction,
    private val completedUploadService: CompletedUploadStorageAction,
    private val uploadStorageAction: UploadStorageAction
) {

    @POST
    @Path("/verify")
    fun verifyIsFileExists(
        @Valid request: VerifyUploadMultipartRequest
    ): Response {
        val result = verifyFileStorageAction.execute(request)

        return if (result) {
            Response.status(Response.Status.CONFLICT)
                .entity("Files already exist in storage:")
                .build()
        } else {
            Response.ok("All files are ready for upload.").build()
        }
    }

    @POST
    @Path("/init")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    fun initUpload(@Valid request: InitUploadMultipartRequest): Response {
        val response = initUploadService.execute(request)

        return Response.ok(response).build()
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    fun uploadPart(
        @RestForm("uploadId") uploadId: String,
        @RestForm("chunkNumber") chunkNumber: Int,
        @RestForm("totalChunks") totalChunks: Int,
        @RestForm("file") file: FileUpload
    ): Response {
        return try {
            val result = uploadStorageAction.execute(uploadId, chunkNumber, totalChunks, file)

            when (result) {
                is UploadPartResult.PartUploaded -> {
                    Response.ok(
                        mapOf(
                            "status" to "chunk_uploaded",
                            "uploadId" to uploadId,
                            "chunkNumber" to chunkNumber,
                            "totalChunks" to totalChunks,
                            "etag" to result.etag
                        )
                    ).build()
                }

                is UploadPartResult.Completed -> {
                    Response.ok(
                        mapOf(
                            "status" to "upload_completed",
                            "uploadId" to uploadId,
                            "s3Location" to result.s3Location,
                            "finalEtag" to result.etag
                        )
                    ).build()
                }
            }
        } catch (e: Exception) {
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to (e.message ?: "Unknown error")))
                .build()
        }
    }

    @POST
    @Path("/complete")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    fun completeUpload(
        @Valid request: CompleteMultipartUploadRequest
    ): Response? {
        completedUploadService.execute(request)

        return Response.ok("Upload completed with success").build()
    }
}