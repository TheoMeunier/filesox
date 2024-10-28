package fr.tmeunier.web.controller.storage

import fr.tmeunier.domaine.requests.GetPathImageRequest
import fr.tmeunier.domaine.services.filesSystem.FileSystemServiceFactory
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.io.File

object FileController {
    suspend fun image(call: ApplicationCall) {
        val request = call.receive<GetPathImageRequest>()
        val path = request.path + "." + request.type.split("/").reversed()[0].split(".").reversed()[0]
        val localPathCache = "storages/.cache/${path}"

        val fileInCache = File(localPathCache)

        if (!fileInCache.exists()) {
            FileSystemServiceFactory.createStorageService().download(path, localPathCache)
            call.respondFile(File(localPathCache))
        } else {
            call.respondFile(fileInCache)
        }
    }
}