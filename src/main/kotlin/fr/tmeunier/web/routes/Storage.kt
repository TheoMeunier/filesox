package fr.tmeunier.web.routes

import fr.tmeunier.config.Security
import fr.tmeunier.core.permissions.withAnyRole
import fr.tmeunier.web.controller.storage.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.storageRoute() {
    route("/storages") {
        post { StorageController.listFoldersAndFiles(call) }

        get { StorageController.search(call) }

        withAnyRole(Security.ADMIN, Security.DOWNLOAD) {
            post("/download") { StorageController.download(call) }
        }

        withAnyRole(Security.ADMIN, Security.EDIT_FILE) {
            post("/update") { StorageController.update(call) }
            post("/move") { StorageController.move(call) }
        }

        withAnyRole(Security.ADMIN, Security.SHARE_FILE) {
            post("/share") { ShareController.share(call) }
            get("/share/dl/{uuid}") {
                ShareController.shareDownload(call)
            }
            get("/share/{uuid}") {
                ShareController.getShared(call)
            }
        }

        withAnyRole(Security.ADMIN, Security.DELETE_FILE_FOLDER) {
            post("/delete") { StorageController.delete(call) }
        }
    }

    route("/folders") {
        withAnyRole(Security.ADMIN, Security.CREATE_FILE_FOLDER) {
            post("/create") { FolderController.create(call) }
        }
    }

    route("/images") {
        post { FileController.image(call) }
    }

    route("/files") {
        route("/upload") {
            post("/verify") { UploadController.verify(call) }

            post("/init") { UploadController.initUploader(call) }

            post { UploadController.upload(call) }

            post("/complete") { UploadController.completedUpload(call) }
        }
    }
}

