package fr.tmeunier.domaine.services.filesSystem.service

import java.io.*
import java.text.StringCharacterIterator
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object StorageService {

    fun getIconForFile(fileName: String): String {
        return when {
            fileName.endsWith(".pdf") -> "pdf"
            fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".odt") -> "doc"
            fileName.endsWith(".csv") -> "csv"
            fileName.endsWith(".xls") || fileName.endsWith(".xlsx") -> "xls"
            fileName.endsWith(".ppt") || fileName.endsWith(".pptx") -> "ppt"
            fileName.endsWith(".txt") -> "txt"
            fileName.endsWith(".zip") || fileName.endsWith(".tar") || fileName.endsWith(".gz")  -> "zip"
            fileName.endsWith(".rar") -> "rar"
            fileName.endsWith(".php") -> "php"
            fileName.endsWith(".html") -> "html"
            fileName.endsWith(".css") || fileName.endsWith(".scss") || fileName.endsWith(".sass") || fileName.endsWith(".less")-> "css"
            fileName.endsWith(".sql") -> "sql"
            fileName.endsWith(".js") -> "js"
            fileName.endsWith(".jar") -> "jar"
            fileName.endsWith(".json") -> "json"
            fileName.endsWith(".ps") -> "ps"
            fileName.endsWith(".bin") -> "bin"
            fileName.endsWith(".exe") -> "exe"
            fileName.endsWith(".iso") -> "iso"
            fileName.endsWith(".mp3") || fileName.endsWith(".wav") -> "mp3"
            fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mkv") -> "mp4"
            fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".svg")-> "image"
            else -> "file"
        }
    }

    fun pathinfo(path: String): Map<String, String> {
        val file = java.io.File(path)
        val filename = file.name
        val basename = filename.substringBeforeLast(".")
        val extension = filename.substringAfterLast(".", "")
        val dirname = file.parent ?: ""

        return mapOf(
            "dirname" to dirname,
            "basename" to basename,
            "extension" to extension,
            "filename" to filename
        )
    }

    fun Long.toHumanReadableValue(): String {
        var bytes = this
        if (-1000 < bytes && bytes < 1000) {
            return "${bytes}B";
        }
        val ci = StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000
            ci.next()
        }
        return String.format("%.2f %cB", bytes / 1000.0, ci.current())
    }

    fun parseFileSize(sizeString: String): Long {
        val numericPart = sizeString.replace(",", ".").replace(Regex("[^\\d.]"), "")
        val size = numericPart.toDoubleOrNull() ?: return -1
        return when {
            sizeString.endsWith("MB", ignoreCase = true) -> (size * 1024 * 1024).toLong()
            sizeString.endsWith("KB", ignoreCase = true) -> (size * 1024).toLong()
            sizeString.endsWith("B", ignoreCase = true) -> size.toLong()
            else -> size.toLong() // Assume bytes if no unit is specified
        }
    }

    fun zipFolder(folderPath: String, zipFilePath: String) {
        val folder = File(folderPath)
        val zipFile = File(zipFilePath)

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            addFolderToZip(folderPath, folder, zos)
        }
    }

    fun addFolderToZip(parentPath: String, folder: File, zos: ZipOutputStream) {
        folder.listFiles()?.forEach { file ->
            val entryPath = if (parentPath.isNotEmpty()) "${File(parentPath).toPath().relativize(file.toPath())}" else file.name

            if (file.isDirectory) {
                addFolderToZip(parentPath, file, zos)
            } else {
                zos.putNextEntry(ZipEntry(entryPath))
                FileInputStream(file).use { input ->
                    BufferedInputStream(input).use { bufferedInput ->
                        bufferedInput.copyTo(zos)
                    }
                }
                zos.closeEntry()
            }
        }
    }
}