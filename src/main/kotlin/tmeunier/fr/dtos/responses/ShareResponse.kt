package tmeunier.fr.dtos.responses

import io.quarkus.runtime.annotations.RegisterForReflection
import java.util.*

@RegisterForReflection
data class ShareProfileResponse(
    val id: UUID,
    val path: String,
    val expiredAt: String,
    val createdAt: String
)

@RegisterForReflection
data class ShareAdminResponse(
    val id: UUID,
    val path: String,
    val username: String,
    val expiredAt: String,
    val createdAt: String
)