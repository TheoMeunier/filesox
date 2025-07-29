package tmeunier.fr.dtos.requests

import jakarta.validation.constraints.*

data class LoginRequest(
    @field:NotBlank(message = "L'email ne peut pas être vide")
    @field:Email(message = "Format d'email invalide")
    val email: String,

    @field:NotBlank(message = "Le mot de passe ne peut pas être vide")
    @field:Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    val password: String,
)

data class RegisterRequest(
    @field:NotBlank(message = "L'email ne peut pas être vide")
    @field:Email(message = "Format d'email invalide")
    val email: String,

    @field:NotBlank(message = "L'utilisateur ne peut pas être vide")
    val name: String,

    @field:NotBlank(message = "Le mot de passe ne peut pas être vide")
    @field:Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    val password: String,

    val filePath: String? = null,
)

data class AuthRefreshTokenRequest(
    @field:NotBlank(message = "Le token d'authentification ne peut pas être vide")
    val refreshToken: String
)