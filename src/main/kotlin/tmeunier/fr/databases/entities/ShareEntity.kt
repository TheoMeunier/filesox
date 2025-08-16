package tmeunier.fr.databases.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import tmeunier.fr.databases.interfaces.AuditableInterface
import tmeunier.fr.databases.listeners.AuditListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "shares")
@EntityListeners(AuditListener::class)
class ShareEntity : PanacheEntityBase, AuditableInterface {
    @Id
    @Column(nullable = false)
    lateinit var id: UUID

    @Column(name = "storage_id", nullable = false)
    lateinit var storageId: UUID

    @Column(nullable = false)
    lateinit var type: String

    @Column(nullable = true)
    var password: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    lateinit var user: UserEntity

    @Column(name = "expired_at", nullable = false)
    lateinit var expiredAt: LocalDateTime

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    lateinit var createdAt: LocalDateTime

    override fun getAuditId() = id.toString()

    override fun getAuditName() = storageId.toString()

    override fun getEntityType() = "SHARE"

    companion object : PanacheCompanion<ShareEntity> {
        fun findById(id: UUID): ShareEntity? {
            return find("id = ?1", id).firstResult()
        }
    }
}