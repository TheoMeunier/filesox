package tmeunier.fr.actions.shares

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.FileEntity
import tmeunier.fr.databases.entities.FolderEntity
import tmeunier.fr.databases.entities.ShareEntity
import tmeunier.fr.dtos.responses.ShareAdminResponse
import tmeunier.fr.dtos.responses.ShareProfileResponse
import java.util.UUID

@ApplicationScoped
class GetAllSharesAction
{
    fun execute(): List<ShareAdminResponse>
    {
        val shares = ShareEntity.listAll()

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
            ShareAdminResponse(
                id = share.id,
                path = getStorageName(share, fileNames, folderPaths),
                username = share.user.name,
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