package tmeunier.fr.services.files_system

import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.core.sync.RequestBody
import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.exceptions.storage.UploadIdNotFoundException
import tmeunier.fr.services.logger
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

data class MultipartUploadContext(
    val uploadId: String,
    val bucketName: String,
    val key: String,
    val completedParts: MutableList<CompletedPart> = mutableListOf(),
    var nextPartNumber: Int = 1
) {
    fun incrementPartNumber() {
        nextPartNumber++
    }
}

@ApplicationScoped
class UploadMultipartService(
    private val s3Client: S3Client
) {
    val activeUploads  = ConcurrentHashMap<String, MultipartUploadContext>()
    val bucketName = "cdn"

    // init upload multipart
    fun initUpload(key: String, contentType: String): String {
        return try {
            val request = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build()


            val response = s3Client.createMultipartUpload(request)
            val uploadId = response.uploadId()


            val context = MultipartUploadContext(uploadId, bucketName, key,)
            activeUploads[uploadId] = context

            uploadId

        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize multipart upload for key: $key", e)
        }
    }

    fun uploadPart(uploadId: String, partData: InputStream, partSize: Long): String {
        val context = activeUploads[uploadId]
            ?: throw UploadIdNotFoundException("Upload id not found: $uploadId")

        return try {
            val partNumber = context.nextPartNumber

            val uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(context.key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .contentLength(partSize)
                .build()

            val response = s3Client.uploadPart(
                uploadPartRequest,
                RequestBody.fromInputStream(partData, partSize)
            )

            val completedPart = CompletedPart.builder()
                .partNumber(partNumber)
                .eTag(response.eTag())
                .build()

            context.completedParts.add(completedPart)
            context.incrementPartNumber()

            response.eTag()
        } catch (e: Exception) {
            throw RuntimeException("Error upload part ${context.nextPartNumber}", e)
        }
    }

    fun completeUpload(uploadId: String): String {
        val context = activeUploads[uploadId]
            ?: throw UploadIdNotFoundException("Upload id not found: $uploadId")

        return try {
            val completedUpload = CompletedMultipartUpload.builder()
                .parts(context.completedParts)
                .build()

            val request = CompleteMultipartUploadRequest.builder()
                .bucket(context.bucketName)
                .key(context.key)
                .uploadId(uploadId)
                .multipartUpload(completedUpload)
                .build()

            val response = s3Client.completeMultipartUpload(request)

            // Clear cache
            activeUploads.remove(uploadId)

            response.location()
        } catch (e: Exception) {
            throw RuntimeException("Error while finalizing the upload", e)
        }
    }
}