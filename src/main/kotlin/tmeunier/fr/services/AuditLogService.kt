package tmeunier.fr.services

import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.core.SecurityContext
import tmeunier.fr.databases.entities.AuditLogEntity
import tmeunier.fr.databases.entities.UserEntity
import tmeunier.fr.databases.interfaces.AuditableInterface
import tmeunier.fr.enums.AuditAction
import java.util.UUID

@ApplicationScoped
class AuditService(
    private val securityContext: SecurityContext,
    private val routingContext: RoutingContext,
) {

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    fun logAction(action: AuditAction, entity: AuditableInterface, details: String? = null) {
        val log = AuditLogEntity().apply {
            this.id = UUID.randomUUID()
            this.action = action
            this.entityType = entity.getEntityType()
            this.entityId = entity.getAuditId()
            this.entityName = entity.getAuditName()
            this.details = details ?: generateDefaultDetails(action, entity)
            this.user = getCurrentUser()
            this.ipAddress = getClientIpAddress()
        }

        log.persist()
    }

    private fun generateDefaultDetails(action: AuditAction, entity: AuditableInterface): String {
        return when (action) {
            AuditAction.CREATE -> "${entity.getEntityType()} '${entity.getAuditName()}' created"
            AuditAction.UPDATE -> "${entity.getEntityType()} '${entity.getAuditName()}' edit"
            AuditAction.DELETE -> "${entity.getEntityType()} '${entity.getAuditName()}' deleted"
            AuditAction.MOVE -> "${entity.getEntityType()} '${entity.getAuditName()}' moved"
            AuditAction.SHARE -> "${entity.getEntityType()} '${entity.getAuditName()}' shared"
            AuditAction.UNSHARE -> "Partage de ${entity.getEntityType()} '${entity.getAuditName()}' cancel"
            AuditAction.DOWNLOAD -> "${entity.getEntityType()} '${entity.getAuditName()}' downloaded"
            AuditAction.ACCESS -> "${entity.getEntityType()} '${entity.getAuditName()}' accessed"
        }
    }

    private fun getCurrentUser(): UserEntity? {
        return try {
            val userId = UUID.fromString(securityContext.userPrincipal.name)
            UserEntity.findById(userId)
        } catch (e: Exception) {
            null
        }
    }

    private fun getClientIpAddress(): String? {
      return try {
          routingContext.request().remoteAddress()?.host()
      } catch (e: Exception) {
          null
      }
    }
}