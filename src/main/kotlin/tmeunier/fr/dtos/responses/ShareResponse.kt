package tmeunier.fr.dtos.responses

import java.util.UUID

data class ShareProfileResponse(
    val id: UUID,
    val path: String,
    val expiredAt: String,
    val createdAt: String
)
data class ShareAdminResponse(
    val id: UUID,
    val path: String,
    val username: String,
    val expiredAt: String,
    val createdAt: String
)