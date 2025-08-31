package tmeunier.fr.dtos.responses

import io.quarkus.runtime.annotations.RegisterForReflection
import java.util.*

@RegisterForReflection
data class LoginResponse(
    val token: String,
    val refreshToken: String
)

@RegisterForReflection
data class RegisterResponse(
    val name: String,
    val email: String,
    val filePath: UUID? = null,
)