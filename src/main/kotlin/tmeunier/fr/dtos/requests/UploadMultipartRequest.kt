package tmeunier.fr.dtos.requests

import io.quarkus.runtime.annotations.RegisterForReflection
import java.util.*

@RegisterForReflection
data class VerifyIsFileExistUploadMultipartRequest(
    val name: String,
    val parentId: UUID,
)

@RegisterForReflection
data class VerifyUploadMultipartRequest(
    val files: List<VerifyIsFileExistUploadMultipartRequest>,
)

@RegisterForReflection
data class InitUploadMultipartRequest(
    val name: String,
    val size: Long,
    val type: String,
    val lastModified: Long,
    val webRelativePath: String?,
    val parentId: UUID?,
    val totalChunks: Int,
    val isExist: Boolean,
)

@RegisterForReflection
data class CompleteMultipartUploadRequest(
    val uploadId: String,
    val fileId: UUID,
    val isExist: Boolean,
)