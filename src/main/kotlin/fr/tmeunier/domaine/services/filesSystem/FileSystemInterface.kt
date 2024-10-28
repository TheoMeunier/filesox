package fr.tmeunier.domaine.services.filesSystem

import io.ktor.server.application.*

interface FileSystemInterface
{
    suspend fun delete(path: String)

    suspend fun download(path: String, localPathCache: String)

    suspend fun downloadMultipart(call: ApplicationCall, id: String, isFolder: Boolean,  path: String?): Unit?

    suspend fun initMultipart(path: String): String?

    suspend fun uploadMultipart(key: String, uploadId: String?, chunkNumber: Int, fileBytes: ByteArray?, totalChunks: Int): String?

    suspend fun closeMultiPart(remotePath: String, uplId: String?)
}