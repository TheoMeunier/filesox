package fr.tmeunier.domaine.services.filesSystem.local

import java.io.File
import java.nio.file.Paths
import java.util.*

object LocalStorageUploadService {

    private val uploads = mutableMapOf<String, MutableList<File>>()
    private val uploadDir = Paths.get("storages/uploads/").toFile()

    fun initMultipart(path: String): String {
        val id = UUID.randomUUID().toString()
        uploads[id] = mutableListOf()

        return id
    }

    fun uploadMultipart(key: String, uploadId: String?, chunkNumber: Int, fileBytes: ByteArray?, totalChunks: Int): String? {
        val tempDir = File(System.getProperty("java.io.tmpdir"), "uploads")
        if (!tempDir.exists()) tempDir.mkdirs()

        val chunkFile = File(tempDir, "$uploadId-$chunkNumber")
        chunkFile.writeBytes(fileBytes!!)

        return uploadId
    }

    fun closeMultiPart(remotePath: String, uplId: String?) {
        val tempDir = File(System.getProperty("java.io.tmpdir"), "uploads")
        if (!uploadDir.exists()) uploadDir.mkdirs()

        val chunks = tempDir.listFiles { file -> file.name.startsWith(uplId!!) }
            ?.sortedBy { it.name }

        val finalFile = File(uploadDir, remotePath)
        finalFile.outputStream().use { output ->
            chunks?.forEach { chunk ->
                chunk.inputStream().use { it.copyTo(output) }
                chunk.delete()
            }
        }
    }
}