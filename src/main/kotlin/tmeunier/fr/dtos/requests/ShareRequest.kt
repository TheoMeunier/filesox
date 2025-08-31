package tmeunier.fr.dtos.requests

import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

@RegisterForReflection
data class CreateShareRequest(
    @field:NotNull(message = "Storage is required")
    val storageId: UUID,

    @field:NotBlank(message = "Type is required")
    val type: String,

    @field:Nullable
    val password: String? = null,

    @field:NotNull(message = "File path is required")
    val duration: Number,

    @field:NotNull(message = "Type duration is required")
    @field:NotBlank(message = "Type duration is required")
    val typeDuration: String
)