package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.exceptions.storage.StorageNotFoundException
import tmeunier.fr.services.CacheService
import tmeunier.fr.services.S3Service
import tmeunier.fr.services.files_system.StorageService
import tmeunier.fr.services.logger
import java.util.UUID

@ApplicationScoped
class GetImageStorageAction(
    private val storageService: StorageService,
    private val s3Service: S3Service,
    private val cacheService: CacheService
) {
    fun execute(imageId: UUID): Pair<ByteArray, String>? {
        val file = FileEntity.findById(imageId) ?: throw StorageNotFoundException("" +
                "Image $imageId not found")

        val extention = ".${storageService.pathInfo(file.name)["extension"]!!}"
        val nameFileS3 = "${file.id}$extention"
        val minuteType = getMinuteType(extention)

        logger.info { "Downloading image $nameFileS3 from S3" }

        if (cacheService.existsInCache(nameFileS3)) {
            val cacheFile = cacheService.getFromRedisCache(nameFileS3) ?: throw StorageNotFoundException("Image $imageId not found in cache")

            return Pair(cacheFile, minuteType)
        }

        val imageData = s3Service.downloadObject(nameFileS3) ?: throw StorageNotFoundException("Image $imageId not found in S3")

        if (imageData.isEmpty()) {
            throw StorageNotFoundException("Image $imageId not found in S3")
        }

        cacheService.setRedisCache(nameFileS3, imageData)
        return Pair(imageData, minuteType)
    }

    private fun getMinuteType(extension: String): String = when (extension) {
        ".jpg", "jpeg" -> "image/jpeg"
        ".png" -> "image/png"
        ".gif" -> "image/gif"
        ".webp" -> "image/webp"
        ".svg" -> "image/svg+xml"
        else -> "application/octet-stream"
    }
}