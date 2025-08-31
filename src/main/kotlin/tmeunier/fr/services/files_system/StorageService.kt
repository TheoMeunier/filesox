package tmeunier.fr.services.files_system

import jakarta.enterprise.context.ApplicationScoped
import java.text.StringCharacterIterator

@ApplicationScoped
class StorageService {
    fun pathInfo(path: String): Map<String, String> {
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

    fun getIconForFile(fileName: String): String {
        return when {
            fileName.endsWith(".pdf") -> "pdf"
            fileName.endsWith(".doc") || fileName.endsWith(".docx") || fileName.endsWith(".odt") -> "doc"
            fileName.endsWith(".csv") -> "csv"
            fileName.endsWith(".xls") || fileName.endsWith(".xlsx") -> "xls"
            fileName.endsWith(".ppt") || fileName.endsWith(".pptx") -> "ppt"
            fileName.endsWith(".txt") -> "txt"
            fileName.endsWith(".zip") || fileName.endsWith(".tar") || fileName.endsWith(".gz") -> "zip"
            fileName.endsWith(".rar") -> "rar"
            fileName.endsWith(".php") -> "php"
            fileName.endsWith(".html") -> "html"
            fileName.endsWith(".css") || fileName.endsWith(".scss") || fileName.endsWith(".sass") || fileName.endsWith(".less") -> "css"
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
            fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".svg") -> "image"
            else -> "file"
        }
    }
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