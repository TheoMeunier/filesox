package tmeunier.fr.actions.shares

import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.databases.entities.ShareEntity
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.dtos.requests.CreateShareRequest
import tmeunier.fr.exceptions.common.NotFoundException
import java.time.LocalDateTime
import java.util.UUID

@ApplicationScoped
class CreateShareAction
{
    fun execute(request: CreateShareRequest, authUserId: UUID)
    {
        val auth = UserEntity.findById(authUserId) ?: throw NotFoundException()

        val share = ShareEntity().apply {
            id = UUID.randomUUID()
            storageId = request.storageId
            type = request.type
            password = request.password
            user = auth
            expiredAt = calculateExpireAt(request.duration, request.typeDuration)
        }

        share.persist()
    }

    private fun calculateExpireAt(duration: Number, typeDuration: String): LocalDateTime {
        val now = LocalDateTime.now()
        return when (typeDuration) {
            "days" -> now.plusDays(duration.toLong())
            "hours" -> now.plusHours(duration.toLong())
            "minutes" -> now.plusMinutes(duration.toLong())
            "months" -> java.time.LocalDateTime.now().plusMonths(duration.toLong())
            else -> throw IllegalArgumentException("Invalid type duration: $typeDuration")
        }
    }
}