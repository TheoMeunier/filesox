package tmeunier.fr.databases.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import tmeunier.fr.databases.interfaces.AuditableInterface
import tmeunier.fr.databases.listeners.AuditListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "folders")
@EntityListeners(AuditListener::class)
class FolderEntity : PanacheEntityBase, AuditableInterface {
    @Id
    @Column(nullable = false)
    lateinit var id: UUID

    @Column(nullable = false, length = 500)
    lateinit var path: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    var parent: FolderEntity? = null

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var folders: MutableSet<FolderEntity> = mutableSetOf()

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var files: MutableSet<FileEntity> = mutableSetOf()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime

    override fun getAuditId() = id.toString()

    override fun getAuditName() = path

    override fun getEntityType() = "FOLDER"

    companion object : PanacheCompanion<FolderEntity> {
        fun findById(id: UUID): FolderEntity? {
            return find("id = ?1", id).firstResult()
        }

        fun findByPath(path: String): FolderEntity? {
            return find("path", path).firstResult()
        }

        fun findAllByParentId(parentId: UUID): List<FolderEntity> {
            return find("parent.id", parentId).list()
        }
    }
}