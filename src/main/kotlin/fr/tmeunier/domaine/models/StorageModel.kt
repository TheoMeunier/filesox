package fr.tmeunier.domaine.models

import fr.tmeunier.domaine.repositories.FileRepository
import fr.tmeunier.domaine.repositories.FolderRepository
import fr.tmeunier.domaine.repositories.ShareRepository
import java.util.UUID

data class FolderModel(
    val id: UUID,
    val path: String,
)

data class ShareModel(
    val id: UUID,
    val storageId: UUID,
    val type: String,
    val password: String?,
    val expiredAt: java.time.LocalDateTime,
)

fun getPathFromShare(type: String, id: UUID): String {
    return if (type === "folder") {
        val folder = FolderRepository.findById(id)
        folder?.path ?: "./"
    } else {
        val file = FileRepository.findById(id)
        file?.name ?: "./"
    }
}