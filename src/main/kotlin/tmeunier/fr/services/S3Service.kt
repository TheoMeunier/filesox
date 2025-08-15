package tmeunier.fr.services

import jakarta.enterprise.context.ApplicationScoped
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import tmeunier.fr.exceptions.s3.S3GetObjectException
import tmeunier.fr.exceptions.s3.S3DeleteObjectException

@ApplicationScoped
class S3Service(
    private val s3Client: S3Client,
) {
    private val bucketName = "cdn"

    fun downloadObject(key: String): ByteArray? {
        try {
            val getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()

            return s3Client.getObjectAsBytes(getObjectRequest).asByteArray()
        } catch (e: Exception) {
            throw S3GetObjectException("Error while downloading object $key: ${e.message}")
        }
    }

    fun deleteObject(key: String) {
        try {
            s3Client.deleteObject {
                DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()
            }
        } catch (e: Exception) {
            throw S3DeleteObjectException("Error while deleting object $key: ${e.message}")
        }
    }
}

