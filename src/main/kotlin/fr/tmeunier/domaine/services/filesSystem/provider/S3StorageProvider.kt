package fr.tmeunier.domaine.services.filesSystem.provider

import aws.sdk.kotlin.services.s3.model.DeleteObjectRequest
import fr.tmeunier.config.S3Config
import fr.tmeunier.domaine.services.filesSystem.FileSystemInterface
import fr.tmeunier.domaine.services.filesSystem.s3.S3DownloadService
import fr.tmeunier.domaine.services.filesSystem.s3.S3UploadService
import io.ktor.server.application.*
import java.util.*

class S3StorageProvider: FileSystemInterface {

    override suspend fun delete(path: String) {
        val client = S3Config.makeClient()

        client?.deleteObject(DeleteObjectRequest {
            bucket = S3Config.bucketName
            key = path
        })
    }

    override suspend fun download(path: String, localPathCache: String) {
        S3Config.makeClient()?.let {
            S3DownloadService.downloadToCache(it, path, localPathCache)
        }
    }

    override suspend fun downloadMultipart(call: ApplicationCall, id: String, isFolder: Boolean, path: String?): Unit? {
        return S3Config.makeClient()?.let {
            if (isFolder) {
                S3DownloadService.downloadFolderMultipart(call, UUID.fromString(id))
            } else {
                S3DownloadService.downloadFileMultipart(call, id, path!!)
            }
        }
    }

    override suspend fun initMultipart(path: String): String? {
       return S3Config.makeClient()?.let {
           S3UploadService.initiateMultipartUpload(it, path)
       }
    }

    override suspend fun uploadMultipart(key: String, uploadId: String?, chunkNumber: Int, fileBytes: ByteArray?, totalChunks: Int): String? {
        return S3Config.makeClient()?.let {
            S3UploadService.uploadMultipart(it, key, uploadId, chunkNumber, fileBytes, totalChunks)
        }
    }

    override suspend fun closeMultiPart(remotePath: String, uplId: String?) {
      S3Config.makeClient()?.let {
          S3UploadService.completeMultipartUpload(it, remotePath, uplId)
      }
    }
}