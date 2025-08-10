package tmeunier.fr.databases.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.security.Permission
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "permissions")
class PermissionEntity : PanacheEntityBase {
    @Id
    @Column(nullable = false, length = 100)
    lateinit var id: UUID

    @Column(nullable = false, length = 255)
    lateinit var name: String

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    var users: MutableSet<UserEntity> = mutableSetOf()

    companion object : PanacheCompanion<PermissionEntity> {
        fun findByName(name: String): PermissionEntity? {
            return find("name", name).firstResult()
        }

        fun findById(uuid: UUID): PermissionEntity? {
            return find("id = ?1", uuid).firstResult()
        }
    }
}