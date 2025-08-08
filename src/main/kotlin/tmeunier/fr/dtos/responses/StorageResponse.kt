package tmeunier.fr.dtos.responses

import java.util.UUID

data class S3File(
    val id: UUID,
    val name: String,
    val type: String,
    val size: String,
    val parentId: UUID?,
    val icon: String?,
)

data class S3Folder(
    val id: UUID,
    val path: String,
    val parentId: UUID?
)

data class S3Response(
    val folder: S3Folder?,
    val folders: List<S3Folder>?,
    val files: List<S3File>,
)

data class UploadResponse(
    val uploadId: String,
    val fileId: UUID,
)

sealed class UploadPartResult {
    data class PartUploaded(val etag: String) : UploadPartResult()
    data class Completed(val etag: String, val s3Location: String) : UploadPartResult()
}