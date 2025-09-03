package tmeunier.fr.actions.shares

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.databases.entities.ShareEntity
import tmeunier.fr.exceptions.storage.StorageNotFoundException

@ApplicationScoped
class DownloadShareAction {
    fun execute(objectStorage: String, password: String? = null): Boolean {

        val storage = if (objectStorage.contains(".")) {
            FileEntity.findByName(objectStorage)
        } else {
            FolderEntity.findByPath(objectStorage)
        }

        val storageId = when (storage) {
            is FileEntity -> storage.id
            is FolderEntity -> storage.id
            else -> throw StorageNotFoundException("Storage not found: $objectStorage")
        }
        val share = ShareEntity.findByStorageId(storageId)

        if (share === null) {
            return false
        }

        if (share.password != null && share.password!!.isNotEmpty()) {
            if (password == null || password.isEmpty()) return false
            val isValid = share.password == password
            if (!isValid) return false
            return true
        }

        return true
    }
}