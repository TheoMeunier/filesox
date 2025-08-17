package tmeunier.fr.dtos.responses

import java.util.*

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val permissions: List<String>,
    val filePath: String? = null,
    val createdAt: String? = null
)

data class PermissionResponse(
    val id: UUID,
    val name: String,
)
