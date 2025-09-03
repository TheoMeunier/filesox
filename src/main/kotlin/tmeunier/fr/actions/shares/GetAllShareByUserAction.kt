package tmeunier.fr.actions.shares

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.databases.entities.ShareEntity
import tmeunier.fr.dtos.responses.ShareProfileResponse
import java.util.*

@ApplicationScoped
class GetAllShareByUserAction {
    fun execute(userId: UUID): List<ShareProfileResponse> {
        val shares = ShareEntity.find("user.id = ?1", userId).list()

        val fileIds = shares.filter { it.type == "file" }.map { it.storageId }
        val folderIds = shares.filter { it.type == "folder" }.map { it.storageId }

        val fileNames = if (fileIds.isNotEmpty()) {
            FileEntity.find("id in ?1", fileIds).list()
                .associateBy({ it.id }, { it.name })
        } else emptyMap()

        val folderPaths = if (folderIds.isNotEmpty()) {
            FolderEntity.find("id in ?1", folderIds).list()
                .associateBy({ it.id }, { it.path })
        } else emptyMap()

        return shares.map { share ->
            ShareProfileResponse(
                id = share.id,
                path = getStorageName(share, fileNames, folderPaths),
                password = share.password,
                expiredAt = share.expiredAt.toString(),
                createdAt = share.createdAt.toString()
            )
        }
    }

    private fun getStorageName(
        share: ShareEntity,
        fileNames: Map<UUID, String>,
        folderPaths: Map<UUID, String>
    ): String {
        return when (share.type) {
            "file" -> fileNames[share.storageId] ?: ""
            "folder" -> folderPaths[share.storageId] ?: ""
            else -> ""
        }
    }
}