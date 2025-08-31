package tmeunier.fr.dtos.responses

import io.quarkus.runtime.annotations.RegisterForReflection
import java.util.*

@RegisterForReflection
data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val permissions: List<String>,
    val filePath: String? = null,
    val createdAt: String? = null
)

@RegisterForReflection
data class PermissionResponse(
    val id: UUID,
    val name: String,
)
