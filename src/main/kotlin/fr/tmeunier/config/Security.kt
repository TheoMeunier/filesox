package fr.tmeunier.config

import fr.tmeunier.domaine.requests.UserPrincipal
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

object Security {
    const val jwtAudience = "jwt-audience"
    const val jwtRealm = "cdn-tmeunier-jwt-realm"
    const val jwtSecret = "cdn-tmeunier-jwt-issuer-secret"
    const val jwtIssuer = "cdn-tmeunier-jwt-issuer"

    fun customValidator(credential: JWTCredential): Principal {
        val userId = credential.payload.getClaim("id")?.asInt()
            ?: throw IllegalArgumentException("Missing or invalid 'id' claim")
        val name = credential.payload.getClaim("name")?.asString()
            ?: throw IllegalArgumentException("Missing or invalid 'name' claim")
        val email = credential.payload.getClaim("email")?.asString()
            ?: throw IllegalArgumentException("Missing or invalid 'email' claim")
        val roles = credential.payload.getClaim("roles")?.asList(String::class.java)
            ?: throw IllegalArgumentException("Missing or invalid 'roles' claim")

        return UserPrincipal(userId, name, email, roles)
    }

    // define const permissions
    const val ADMIN = "Administration"
    const val CREATE_FILE_FOLDER = "Create file or folder"
    const val DELETE_FILE_FOLDER = "Delete file or folder"
    const val DOWNLOAD = "Download"
    const val EDIT_FILE = "Edit file"
    const val SHARE_FILE = "Share files"
}
