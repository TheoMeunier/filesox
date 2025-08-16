package tmeunier.fr.databases.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import tmeunier.fr.enums.AuditAction
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "audit_logs")
class AuditLogEntity: PanacheEntityBase {

    @Id
    @Column(nullable = false)
    lateinit var id: UUID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var action: AuditAction = AuditAction.CREATE

    @Column(name = "entity_type", nullable = false)
    var entityType: String = ""

    @Column(name = "entity_id")
    var entityId: String? = null

    @Column(name = "entity_name")
    var entityName: String? = null

    @Column(columnDefinition = "TEXT")
    var details: String? = null

    @Column(name = "old_values", columnDefinition = "TEXT")
    var oldValues: String? = null

    @Column(name = "new_values", columnDefinition = "TEXT")
    var newValues: String? = null

    @Column(name = "user_id")
    var userId: String? = null

    @Column(name = "ip_address")
    var ipAddress: String? = null

    @CreationTimestamp
    var timestamp: LocalDateTime = LocalDateTime.now()
}