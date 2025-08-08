package tmeunier.fr.actions.storages.uploaded

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.dtos.requests.VerifyUploadMultipartRequest

@ApplicationScoped
class VerifyUploadStorageAction {

    fun execute(request: VerifyUploadMultipartRequest): Boolean {
        return request.files.any { file ->
            FileEntity.find("name = ?1 and parent.id = ?2", file.name, file.parentId).firstResult() != null
        }
    }
}