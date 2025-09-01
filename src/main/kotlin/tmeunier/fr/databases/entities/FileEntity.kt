package tmeunier.fr.databases.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
import tmeunier.fr.databases.interfaces.AuditableInterface
import tmeunier.fr.databases.listeners.AuditListener
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "files")
@EntityListeners(AuditListener::class)
class FileEntity : PanacheEntityBase, AuditableInterface {
    @Id
    @Column(nullable = false)
    lateinit var id: UUID

    @Column(nullable = false, length = 255)
    lateinit var name: String

    @Column(nullable = false)
    var size: Long = 0

    @Column(nullable = false, length = 50)
    lateinit var icon: String

    @Column(nullable = false, length = 100)
    lateinit var type: String

    @Column(name = "is_exist", nullable = false)
    var isExist: Boolean = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    var parent: FolderEntity? = null

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: LocalDateTime? = null


    override fun getAuditId() = id.toString()

    override fun getAuditName() = name

    override fun getEntityType() = "FILE"

    companion object : PanacheCompanion<FileEntity> {
        fun findById(id: UUID): FileEntity? {
            return find("id = ?1", id).firstResult()
        }

        fun findByName(name: String): FileEntity? {
            return find("name", name).firstResult()
        }

        fun findAllByParentId(parentId: UUID): List<FileEntity> {
            return list("parent.id = ?1 AND deletedAt IS NULL", parentId)
        }

        fun search(query: String): List<FileEntity> {
            val likeQuery = "%${query.replace("%", "\\%").replace("_", "\\_")}%"
            return list("name LIKE ?1 AND deletedAt IS NULL", likeQuery)
        }
    }
}