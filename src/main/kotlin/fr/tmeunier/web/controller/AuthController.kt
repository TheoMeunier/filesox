package fr.tmeunier.web.controller

import fr.tmeunier.domaine.models.RefreshTokenResponse
import fr.tmeunier.domaine.repositories.RefreshTokenRepository
import fr.tmeunier.domaine.repositories.UserRepository
import fr.tmeunier.domaine.requests.RefreshTokenRequests
import fr.tmeunier.domaine.requests.UserLoginRequest
import fr.tmeunier.domaine.requests.UserRegisterRequest
import fr.tmeunier.domaine.services.AuthentificatorService
import fr.tmeunier.domaine.services.utils.HashService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*

object AuthController
{
    private val userRepository = UserRepository
    private val refreshTokenRepository = RefreshTokenRepository

    suspend fun login(call: ApplicationCall)
    {
        val request = call.receive<UserLoginRequest>()

        val user = userRepository.findByEmail(request.email)

        if (user !== null && HashService.hashVerify(request.password, user.password))
        {
           val isRefreshToken = refreshTokenRepository.findByUserId(user.id)

            if (isRefreshToken !== null)
            {
                if (AuthentificatorService.refreshTokenIsValid(isRefreshToken.expiredAt))
                {
                    return call.respond(
                        HttpStatusCode.OK,
                        RefreshTokenResponse(AuthentificatorService.createJwtToken(user), AuthentificatorService.updateRefreshToken(isRefreshToken.token))
                    )
                }
            }

            return call.respond(
                HttpStatusCode.OK,
                RefreshTokenResponse(AuthentificatorService.createJwtToken(user), AuthentificatorService.createRefreshToken(user.id))
            )

        } else {
           return call.respond(HttpStatusCode.Unauthorized)
        }
    }

    suspend fun refresh(call: ApplicationCall)
    {
        val request = call.receive<RefreshTokenRequests>()
        val refreshToken = refreshTokenRepository.findByToken(request.refreshToken)

        if (refreshToken !== null && AuthentificatorService.refreshTokenIsValid(refreshToken.expiredAt))
        {
            val user = userRepository.findById(refreshToken.userId)

            return call.respond(
                HttpStatusCode.OK,
                RefreshTokenResponse(AuthentificatorService.createJwtToken(user!!), AuthentificatorService.updateRefreshToken(refreshToken.token))
            )
        }

        return call.respond(HttpStatusCode.Unauthorized)
    }

    suspend fun register(call: ApplicationCall)
    {
        val request = call.receive<UserRegisterRequest>()
        val user = userRepository.create(null, request.name, request.email, request.password, null)

        return call.respond(user)
    }

    suspend fun logout(call: ApplicationCall)
    {
        val request = call.receive<RefreshTokenRequests>()
        refreshTokenRepository.delete(request.refreshToken)

        return call.respond(HttpStatusCode.OK)
    }
}