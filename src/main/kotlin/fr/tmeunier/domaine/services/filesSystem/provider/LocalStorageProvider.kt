package fr.tmeunier.domaine.services.filesSystem.provider

import fr.tmeunier.domaine.services.filesSystem.FileSystemInterface
import fr.tmeunier.domaine.services.filesSystem.local.LocalStorageDownloadService
import fr.tmeunier.domaine.services.filesSystem.local.LocalStorageUploadService
import io.ktor.server.application.*
import java.nio.file.Paths
import java.util.*

class LocalStorageProvider: FileSystemInterface {

    override suspend fun delete(path: String) {
        val file = Paths.get(path).toFile()

        if (file.exists()) {
            file.delete()
        }
    }

    override suspend fun download(path: String, localPathCache: String) {
        LocalStorageDownloadService.copyToCache(path, localPathCache)
    }

    override suspend fun downloadMultipart(call: ApplicationCall, id: String, isFolder: Boolean,  path: String?) {
        return if (isFolder) {
            LocalStorageDownloadService.downloadFolderMultipart(call, UUID.fromString(id))
        } else {
            LocalStorageDownloadService.downloadFileMultipart(call, id, path!!)
        }
    }

    override suspend fun initMultipart(path: String): String {
        return LocalStorageUploadService.initMultipart(path)
    }

    override suspend fun uploadMultipart(
        key: String,
        uploadId: String?,
        chunkNumber: Int,
        fileBytes: ByteArray?,
        totalChunks: Int
    ): String? {
        return LocalStorageUploadService.uploadMultipart(
            key,
            uploadId,
            chunkNumber,
            fileBytes,
            totalChunks
        )
    }

    override suspend fun closeMultiPart(remotePath: String, uplId: String?) {
        LocalStorageUploadService.closeMultiPart(remotePath, uplId)
    }
}