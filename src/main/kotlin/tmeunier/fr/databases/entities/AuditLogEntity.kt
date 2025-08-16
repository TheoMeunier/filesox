package tmeunier.fr.databases.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import tmeunier.fr.enums.AuditAction
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
    lateinit var details: String

    @Column(name = "old_values", columnDefinition = "TEXT")
    var oldValues: String? = null

    @Column(name = "new_values", columnDefinition = "TEXT")
    var newValues: String? = null

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "user_id" )
    var user: UserEntity? = null

    @Column(name = "ip_address")
    var ipAddress: String? = null

    @CreationTimestamp
    var timestamp: LocalDateTime = LocalDateTime.now()

    companion object: PanacheCompanion<AuditLogEntity> {
        fun findByAuthUserId(id: UUID):  List<AuditLogEntity> {
            return list("user.id = ?1", id)
        }
    }
}