package tmeunier.fr.databases.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity : PanacheEntityBase {
    @Id
    @Column(nullable = false, length = 100)
    lateinit var id: UUID

    @Column(nullable = false, length = 255)
    lateinit var name: String

    @Column(nullable = false, unique = true, length = 255)
    lateinit var email: String

    @Column(nullable = false)
    lateinit var password: String

    @Column(name = "file_path", nullable = false)
    lateinit var filePath: UUID

    @Column(nullable = false)
    var layout: Boolean = false

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    open var refreshToken: MutableList<RefreshTokenEntity> = mutableListOf()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_permissions",
        joinColumns = [JoinColumn("user_id")],
        inverseJoinColumns = [JoinColumn("permission_id")]
    )
    open var permissions: MutableSet<PermissionEntity> = mutableSetOf()

    companion object : PanacheCompanion<UserEntity> {
        fun findByEmail(email: String): UserEntity? {
            return find("email", email).firstResult()
        }

        fun findById(id: UUID): UserEntity? {
            return find("id = ?1", id).firstResult()
        }
    }
}