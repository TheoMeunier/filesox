package tmeunier.fr.dtos.requests

import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@RegisterForReflection
data class UpdatePasswordRequest(
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    val password: String,

    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, message = "New password must be at least 8 characters long")
    val confirmPassword: String,
)

@RegisterForReflection
data class UpdateProfileRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be a valid email address")
    val email: String,

    @field:NotBlank(message = "Name is required")
    val name: String,
)