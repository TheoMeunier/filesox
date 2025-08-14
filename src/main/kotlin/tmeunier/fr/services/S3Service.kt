package tmeunier.fr.services

import jakarta.enterprise.context.ApplicationScoped
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest

@ApplicationScoped
class S3Service(
    private val s3Client: S3Client,
) {
    private val bucketName = "cdn"

    fun downloadObject(key: String): ByteArray {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()

        return s3Client.getObjectAsBytes(getObjectRequest).asByteArray()
    }

    fun deleteObject(key: String) {
        s3Client.deleteObject { it.bucket(bucketName).key(key) }
    }
}

