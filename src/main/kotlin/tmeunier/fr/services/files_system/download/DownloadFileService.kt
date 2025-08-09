package tmeunier.fr.services.files_system.download

import jakarta.enterprise.context.ApplicationScoped
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectResponse
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.min

@ApplicationScoped
class DownloadFileService(
    private val s3Client: S3Client,
) {
    private val bucketName = "cdn"
    private val CHUNK_SIZE: Int = 8 * 1024 * 1024 // 8MB fixe

    fun streamFileInChunks(objectKey: String, fileSize: Long, output: OutputStream) {
        var bytesDownloaded = 0L
        var chunkNumber = 0

        while (bytesDownloaded < fileSize) {
            val start = bytesDownloaded
            val end = min(bytesDownloaded + CHUNK_SIZE - 1, fileSize - 1)

            println("Téléchargement chunk $chunkNumber: bytes $start-$end (${end - start + 1} bytes)")

            try {
                downloadAndWriteChunk(objectKey, start, end, output)
                bytesDownloaded = end + 1
                chunkNumber++

                // Log du progrès
                val progress = (bytesDownloaded.toDouble() / fileSize * 100).toInt()
                println("Progrès: $progress% ($bytesDownloaded/$fileSize bytes)")

            } catch (e: Exception) {
                throw RuntimeException("Erreur lors du téléchargement du chunk $chunkNumber", e)
            }
        }

        println("Téléchargement terminé: $chunkNumber chunks, $bytesDownloaded bytes")
    }

    fun downloadAndWriteChunk(objectKey: String, start: Long, end: Long, output: OutputStream) {
        val getRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .range("bytes=$start-$end")
            .build()

        try {
            s3Client.getObject(getRequest).use { s3Object ->
                transferData(s3Object, output)
            }
        } catch (e: IOException) {
            throw RuntimeException("Erreur lors du streaming du chunk bytes=$start-$end", e)
        }
    }

    fun getFileMetadata(objectKey: String): HeadObjectResponse {
        val headRequest = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .build()

        return s3Client.headObject(headRequest)
    }

    @Throws(IOException::class)
    fun transferData(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(8192) // Buffer plus petit pour le transfert de chaque chunk
        var bytesRead: Int

        while ((input.read(buffer).also { bytesRead = it }) != -1) {
            output.write(buffer, 0, bytesRead)
            output.flush() // Important pour le streaming temps réel
        }
    }

    fun extractFileName(objectKey: String): String {
        val lastSlash = objectKey.lastIndexOf('/')
        return if (lastSlash >= 0) objectKey.substring(lastSlash + 1) else objectKey
    }
}