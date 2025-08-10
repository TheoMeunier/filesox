package tmeunier.fr.dtos.responses

import org.antlr.v4.runtime.atn.ATN
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val permissions: List<String>,
    val createdAt: String? = null
)

data class PermissionResponse(
    val id: UUID,
    val name: String,
)
