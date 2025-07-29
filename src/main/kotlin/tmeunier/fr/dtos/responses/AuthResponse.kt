package tmeunier.fr.dtos.responses

data class LoginResponse(
    val token: String,
    val refreshToken: String
)

data class RegisterResponse(
    val name: String,
    val email: String,
    val filePath: String? = null,
)