package fr.tmeunier.web.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import fr.tmeunier.config.Security
import fr.tmeunier.core.permissions.roleBased
import fr.tmeunier.core.permissions.withRole
import fr.tmeunier.domaine.requests.UserPrincipal
import fr.tmeunier.web.routes.admin.adminLogRouting
import fr.tmeunier.web.routes.admin.adminShareRouting
import fr.tmeunier.web.routes.admin.adminUserRouting
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

fun Application.configurationRoute() {
    install(ContentNegotiation) {
        json()
    }

    authentication {
        jwt("jwt") {
            realm = Security.jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(Security.jwtSecret))
                    .withAudience(Security.jwtAudience)
                    .withIssuer(Security.jwtIssuer)
                    .build()
            )
            validate { credential ->
                Security.customValidator(credential)
            }

            roleBased {
                extractRoles { principal ->
                    when (principal) {
                        is UserPrincipal -> principal.roles.toSet()
                        else -> emptySet()
                    }
                }
            }
        }
    }

    routing {
        authRouting()

        authenticate("jwt") {
            storageRoute()
            profileRouting()

            route("/"){

                withRole(Security.ADMIN){
                    adminUserRouting()
                    adminLogRouting()
                    adminShareRouting()
                }
            }
        }
    }
}
