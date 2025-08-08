package tmeunier.fr.dtos.requests

import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload
import java.util.UUID

data class VerifyIsFileExistUploadMultipartRequest(
    val name: String,
    val parentId: UUID,
)

data class VerifyUploadMultipartRequest(
    val files: List<VerifyIsFileExistUploadMultipartRequest>,
)

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

data class CompleteMultipartUploadRequest(
    val uploadId: String,
    val fileId: UUID,
    val isExist: Boolean,
)