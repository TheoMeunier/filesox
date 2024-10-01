package fr.tmeunier.domaine.response

import fr.tmeunier.domaine.services.serializer.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class S3File(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val type: String,
    val size: String,
    @Serializable(with = UUIDSerializer::class)
    @SerialName("parent_id") val parentId: UUID?,
    val icon: String?,
)

@Serializable
data class S3Folder(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val path: String,
    @Serializable(with = UUIDSerializer::class)
    @SerialName("parent_id") val parentId: UUID?
)

@Serializable
data class S3Response(
    val folder: S3Folder?,
    val folders: List<S3Folder>,
    val files: List<S3File>,
)

@Serializable
data class UploadCompleteResponse(
    @SerialName("upload_id") val uploadId: String,
    val filename: String,
)

