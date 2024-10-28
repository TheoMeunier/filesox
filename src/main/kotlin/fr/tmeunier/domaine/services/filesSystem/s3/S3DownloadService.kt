package fr.tmeunier.domaine.services.filesSystem.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.smithy.kotlin.runtime.content.toFlow
import fr.tmeunier.config.S3Config
import fr.tmeunier.domaine.response.S3File
import fr.tmeunier.domaine.services.filesSystem.AbstractDownloadFileSystem
import fr.tmeunier.domaine.services.filesSystem.service.StorageService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object S3DownloadService : AbstractDownloadFileSystem() {
    suspend fun downloadToCache(client: S3Client, remotePath: String, localPath: String) {
        client.getObject(GetObjectRequest {
            key = remotePath
            bucket = S3Config.bucketName
        }) {
            Files.createDirectories(Paths.get(localPath).parent)

            val writer = withContext(Dispatchers.IO) {
                Paths.get(localPath).toFile().outputStream()
            }

            withContext(Dispatchers.IO) {
                it.body?.toFlow(65_536)?.cancellable()?.collect { dataPart ->
                    withContext(Dispatchers.IO) { writer.write(dataPart) }
                }
            }

            writer.close()
        }
    }

    override suspend fun getObjectFileFlow(call: ApplicationCall, key: String) {
        val client = S3Config.makeClient() ?: throw IllegalStateException("Unable to create S3 client")

        call.respondOutputStream(ContentType.Application.OctetStream) {
            client.getObject(GetObjectRequest {
                this.key = key
                bucket = S3Config.bucketName
            }) { response ->
                response.body?.toFlow(8192)?.buffer(100)?.collect { dataPart ->
                    withContext(Dispatchers.IO) {
                        write(dataPart)
                        flush()
                    }
                }
            }
        }
    }

    override suspend fun addFileToZip(file: S3File, zipEntry: ZipEntry, zipOutputStream: ZipOutputStream) {
        val client = S3Config.makeClient() ?: throw IllegalStateException("Unable to create S3 client")
        zipOutputStream.putNextEntry(zipEntry)

        client.getObject(GetObjectRequest {
            key = file.id.toString() + "." + StorageService.pathinfo(file.name)["extension"]
            bucket = S3Config.bucketName
        }) { response ->
            response.body?.toFlow(8192)?.buffer(100)?.collect { dataPart ->
                withContext(Dispatchers.IO) {
                    zipOutputStream.write(dataPart)
                }
            }
        }

        zipOutputStream.closeEntry()
    }
}