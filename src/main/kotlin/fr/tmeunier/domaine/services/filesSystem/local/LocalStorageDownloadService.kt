package fr.tmeunier.domaine.services.filesSystem.local

import fr.tmeunier.domaine.response.S3File
import fr.tmeunier.domaine.services.filesSystem.AbstractDownloadFileSystem
import fr.tmeunier.domaine.services.filesSystem.service.StorageService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object LocalStorageDownloadService : AbstractDownloadFileSystem() {
    suspend fun copyToCache(path: String, cachePath: String) {
        withContext(Dispatchers.IO) {
            val sourceFile = Paths.get("storages/uploads/$path")
            val cacheFile = Paths.get(cachePath)

            Files.createDirectories(cacheFile.parent)
            Files.copy(sourceFile, cacheFile, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    override suspend fun getObjectFileFlow(call: ApplicationCall, key: String) {
        val localFile = File("storages/uploads/$key")

        call.respondOutputStream(ContentType.Application.OctetStream) {
            localFile.inputStream().buffered(8192).use { input ->
                val bytes = ByteArray(8192)
                var bytesRead: Int

                while (input.read(bytes).also { bytesRead = it } != -1) {
                    write(bytes, 0, bytesRead)
                    flush()
                }
            }
        }
    }

    override suspend fun addFileToZip(file: S3File, zipEntry: ZipEntry, zipOutputStream: ZipOutputStream) {
        val localFile = File("storages/uploads/${file.id}.${StorageService.pathinfo(file.name)["extension"]}")

        if (localFile.exists()) {
            zipEntry.size = localFile.length()
            zipOutputStream.putNextEntry(zipEntry)

            withContext(Dispatchers.IO) {
                localFile.inputStream().use { input ->
                    input.copyTo(zipOutputStream, 8192)
                }
            }

            zipOutputStream.closeEntry()
        }
    }
}