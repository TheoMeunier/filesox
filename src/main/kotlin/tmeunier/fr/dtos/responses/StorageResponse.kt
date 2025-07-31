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

data class UploadCompleteResponse(
    val uploadId: String,
    val filename: String,
)