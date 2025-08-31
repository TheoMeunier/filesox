package tmeunier.fr.dtos.responses

import io.quarkus.runtime.annotations.RegisterForReflection
import java.util.*

@RegisterForReflection
data class AuditLogResponse(
    val id: UUID,
    val action: String,
    val details: String,
    val username: String? = null,
    val createdAt: String,
)