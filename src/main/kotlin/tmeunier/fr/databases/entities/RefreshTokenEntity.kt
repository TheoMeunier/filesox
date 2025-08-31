package tmeunier.fr.databases.entities

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntityBase
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "refresh_token")
class RefreshTokenEntity : PanacheEntityBase {
    @Id
    @Column(nullable = false, length = 100)
    lateinit var id: UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    open lateinit var user: UserEntity

    @Column(name = "refresh_token", nullable = false)
    lateinit var refreshToken: UUID

    @Column(name = "expired_at", nullable = false, updatable = false)
    lateinit var expiredAt: LocalDateTime

    companion object : PanacheCompanion<RefreshTokenEntity> {
        fun findByRefreshToken(refreshToken: UUID): RefreshTokenEntity? {
            return find("refreshToken = ?1", refreshToken).firstResult()
        }
    }
}