package tmeunier.fr.crons

import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.services.files_system.StorageService
import tmeunier.fr.services.logger

@ApplicationScoped
class DeleteS3FileCron(
    private val storageService: StorageService,
    private val s3Client: S3Client
) {

    @Scheduled(every = "10m")
    fun execute() {
        val files = FileEntity.find("deletedAt != IS NULL", System.currentTimeMillis()).list()

        files.forEach {
            val s3Filename = it.id.toString() + "." + storageService.pathInfo(it.name)["extension"]

            s3Client.deleteObject {
                DeleteObjectRequest.builder()
                    .bucket("cdn")
                    .key(s3Filename)
                    .build()
            }

            it.delete()

            logger.info {
                "Share with id ${it.id} has been deleted because it expired."
            }
        }
    }
}