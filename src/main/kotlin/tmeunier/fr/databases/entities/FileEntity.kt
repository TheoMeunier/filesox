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
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "files")
class FileEntity : PanacheEntityBase {
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

    companion object : PanacheCompanion<FileEntity> {
        fun findById(id: UUID): FileEntity? {
            return find("id = ?1", id).firstResult()
        }

        fun findByName(name: String): FileEntity? {
            return find("name", name).firstResult()
        }

        fun findAllByParentId(parentId: UUID): List<FileEntity> {
            return list("parent.id", parentId)
        }
    }
}