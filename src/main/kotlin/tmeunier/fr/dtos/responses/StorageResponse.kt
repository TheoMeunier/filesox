package tmeunier.fr.dtos.responses

import io.quarkus.runtime.annotations.RegisterForReflection
import java.util.*

@RegisterForReflection
data class S3File(
    val id: UUID,
    val name: String,
    val type: String,
    val size: String,
    val parentId: UUID?,
    val icon: String?,
)

@RegisterForReflection
data class S3Folder(
    val id: UUID,
    val path: String,
    val parentId: UUID?
)

@RegisterForReflection
data class S3Response(
    val folder: S3Folder?,
    val folders: List<S3Folder>?,
    val files: List<S3File>,
)

@RegisterForReflection
data class UploadResponse(
    val uploadId: String,
    val fileId: UUID,
)

@RegisterForReflection
sealed class UploadPartResult {
    data class PartUploaded(val etag: String) : UploadPartResult()
    data class Completed(val etag: String, val s3Location: String) : UploadPartResult()
}