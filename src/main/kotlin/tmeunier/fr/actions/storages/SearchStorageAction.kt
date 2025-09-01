package tmeunier.fr.actions.storages

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.dtos.responses.S3File
import tmeunier.fr.dtos.responses.S3Folder
import tmeunier.fr.dtos.responses.S3Response
import tmeunier.fr.services.files_system.toHumanReadableValue

@ApplicationScoped
class SearchStorageAction {

    fun execute(query: String): S3Response {
        val files = FileEntity.search(query)
            .map { S3File(it.id, it.name, it.type, it.size.toHumanReadableValue(), it.parent?.id, it.icon) }

        val folders = FolderEntity.search(query).map { S3Folder(it.id, it.path, it.parent?.id) }

        return S3Response(
            folder = null,
            folders = folders,
            files = files,
        )
    }

}