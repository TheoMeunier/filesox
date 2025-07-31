package tmeunier.fr.dtos.responses

import java.util.UUID

data class LoginResponse(
    val token: String,
    val refreshToken: String
)

data class RegisterResponse(
    val name: String,
    val email: String,
    val filePath: UUID? = null,
)