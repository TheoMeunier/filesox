package tmeunier.fr.databases.listeners

import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import tmeunier.fr.databases.interfaces.AuditableInterface
import tmeunier.fr.enums.AuditAction
import tmeunier.fr.services.AuditService


@ApplicationScoped
class AuditListener(
    private val auditService: AuditService
) {
    @PostPersist
    fun afterCreate(entity: Any) {
        if (entity is AuditableInterface) {
            auditService.logAction(AuditAction.CREATE, entity)
        }
    }

    @PostUpdate
    fun afterUpdate(entity: Any) {
        if (entity is AuditableInterface) {
            auditService.logAction(AuditAction.UPDATE, entity)
        }
    }

    @PostRemove
    fun afterDelete(entity: Any) {
        if (entity is AuditableInterface) {
            auditService.logAction(AuditAction.DELETE, entity)
        }
    }
}