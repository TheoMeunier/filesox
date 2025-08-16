package tmeunier.fr.dtos.responses

import java.util.UUID

data class AuditLogResponse(
    val id: UUID,
    val action: String,
    val details: String,
    val username: String? = null,
    val createdAt: String,
)