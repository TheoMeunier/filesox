package fr.tmeunier.domaine.services.filesSystem

import fr.tmeunier.domaine.repositories.FileRepository
import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.response.S3File
import fr.tmeunier.domaine.response.S3Folder
import fr.tmeunier.domaine.services.filesSystem.service.StorageService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

abstract class AbstractDownloadFileSystem {
    protected abstract suspend fun getObjectFileFlow(
        call: ApplicationCall,
        key: String
    )

    protected abstract suspend fun addFileToZip(file: S3File, zipEntry: ZipEntry, zipOutputStream: ZipOutputStream)

    suspend fun downloadFileMultipart(call: ApplicationCall, id: String, file: String) {
        val filename = id + "." + StorageService.pathinfo(file)["extension"]

        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName, filename
            ).toString()
        )
        call.response.header(HttpHeaders.CacheControl, "no-cache, no-store, must-revalidate")
        call.response.header(HttpHeaders.Pragma, "no-cache")
        call.response.header(HttpHeaders.Expires, "0")

        getObjectFileFlow(call, filename)
    }

    suspend fun downloadFolderMultipart(call: ApplicationCall, id: UUID) {
        val rootFolder = FolderRepository.findById(id) ?: run {
            call.respond(HttpStatusCode.NotFound, "Unable to find folder")
            return
        }

        val zipFilename = "toto.zip"

        val byteArrayOutputStream = ByteArrayOutputStream()
        ZipOutputStream(byteArrayOutputStream).use { zipOutputStream ->

            suspend fun addFolderToZip(folder: S3Folder, parentPath: String = "") {
                val folderPath = if (parentPath.isEmpty()) "${folder.path}/" else "$parentPath${folder.path}/"
                val files = FileRepository.findAllByParentId(folder.id.toString())

                files.forEach { file ->
                    val zipEntry = ZipEntry("$folderPath${file.name}")
                    addFileToZip(file, zipEntry, zipOutputStream)
                }

                val subFolders = FolderRepository.findByIdOrParentId(folder.id.toString())
                subFolders.forEach { subFolder ->
                    addFolderToZip(subFolder, folderPath)
                }
            }

            addFolderToZip(rootFolder)
        }

        val zipBytes = byteArrayOutputStream.toByteArray()

        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, zipFilename).toString()
        )
        call.respondBytes(zipBytes, ContentType.Application.Zip)
    }
}