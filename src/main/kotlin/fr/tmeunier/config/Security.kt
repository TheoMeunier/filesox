package fr.tmeunier.config

import io.ktor.server.auth.jwt.*

object Security {
    private var userId = 0;

    const val jwtAudience = "jwt-audience"
    const val jwtRealm = "cdn-tmeunier-jwt-realm"
    const val jwtSecret = "cdn-tmeunier-jwt-issuer-secret"
    const val jwtIssuer = "cdn-tmeunier-jwt-issuer"

    fun customValidator(credential: JWTCredential): JWTPrincipal? {
        userId = credential.payload.getClaim("id").asInt()
        return if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
    }

    fun getUserId(): Int {
        return userId
    }

    // define const permissions
    const val ADMIN = "Administration"
    const val CREATE_FILE_FOLDER = "Create file or folder"
    const val DELETE_FILE_FOLDER = "Delete file or folder"
    const val DOWNLOAD = "Download"
    const val EDIT_FILE = "Edit file"
    const val SHARE_FILE = "Share files"
}
