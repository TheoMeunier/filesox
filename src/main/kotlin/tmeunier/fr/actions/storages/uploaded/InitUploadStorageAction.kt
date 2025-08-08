package tmeunier.fr.actions.storages.uploaded

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.requests.InitUploadMultipartRequest
import tmeunier.fr.dtos.responses.UploadResponse
import tmeunier.fr.exceptions.storage.StorageAlreadyExistException
import tmeunier.fr.services.files_system.StorageService
import tmeunier.fr.services.files_system.UploadMultipartService
import java.util.UUID

@ApplicationScoped
class InitUploadStorageAction(
    private val uploader: UploadMultipartService,
    private val storageService: StorageService,
) {

    fun execute(request: InitUploadMultipartRequest): UploadResponse {
        val fileUUID = UUID.randomUUID()

        val parentId = if (request.webRelativePath === "") {
            request.parentId
        } else {
            request.webRelativePath?.let { it1 -> createFolderUploadFile(it1, request.parentId) }
        }

        val filename = fileUUID.toString() + '.' + storageService.pathInfo(request.name)["extension"]
        val uploadId = uploader.initUpload(filename, request.type)

        if (request.isExist) {
            throw StorageAlreadyExistException("File $filename already exist in storage")
        } else {
            createFile(fileUUID, request, parentId)
        }

        return UploadResponse(uploadId, fileUUID)
    }

    private fun createFile(fileUUID: UUID, request: InitUploadMultipartRequest, parentId: UUID?) {
        val file = FileEntity().apply {
            id = fileUUID
            name = request.name
            size = request.size
            icon = storageService.getIconForFile(request.name)
            type = request.type
            parent = parentId?.let { FolderEntity.findById(parentId) }
        }

        file.persist()
    }

    private fun createFolderUploadFile(path: String, parentId: UUID?): UUID {
        val folderPathRequest = path.substringBeforeLast("/") + '/'
        val folderParent = parentId?.let { FolderEntity.findById(it) }
        val folderParentPath = if (folderParent !== null) folderParent.path else ""
        val folder = FolderEntity.findByPath(folderParentPath + folderPathRequest)

        if (folder !== null) {
            return folder.id
        }

        val newFolder = FolderEntity().apply {
            id = UUID.randomUUID()
            this.path = folderParentPath + folderPathRequest
            parent = folderParent
        }

        newFolder.persist()
        return newFolder.id
    }
}