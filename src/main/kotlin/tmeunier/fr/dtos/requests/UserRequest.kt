package tmeunier.fr.dtos.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateUserRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email format is invalid")
    val email: String,

    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val password: String,

    @field:NotNull(message = "File path is required")
    val filePath: String,

    @field:NotNull(message = "Permissions is required")
    val permissions: List<UUID> = emptyList()
)

data class UpdateUserRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email format is invalid")
    val email: String,

    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotNull(message = "File path is required")
    val filePath: String,

    @field:NotNull(message = "Permissions is required")
    val permissions: List<UUID> = emptyList()
)
