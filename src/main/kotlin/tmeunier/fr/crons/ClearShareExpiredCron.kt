package tmeunier.fr.crons

import io.quarkus.scheduler.Scheduled
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import tmeunier.fr.databases.entities.ShareEntity
import tmeunier.fr.services.logger
import java.time.LocalDateTime

@ApplicationScoped
class ClearShareExpiredCron {

    @Scheduled(every="10m")
    @Transactional
    fun execute() {
        val shares = ShareEntity.find("expiredAt < ?1", LocalDateTime.now()).list()

        shares.forEach {
           it.delete()

            logger.info {
                "Share with id ${it.id} has been deleted because it expired."
            }
        }
    }

}