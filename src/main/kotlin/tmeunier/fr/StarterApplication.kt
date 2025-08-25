package tmeunier.fr

import io.quarkus.runtime.StartupEvent
import jakarta.enterprise.event.Observes
import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.transaction.Transactional
import tmeunier.fr.databases.seeder.CreateDefaultUserSeed

@Singleton
class StarterApplication {

    @Inject
    lateinit var createDefaultUserSeed: CreateDefaultUserSeed

    @Transactional
    fun onStart(@Observes event: StartupEvent) {
        println("🚀 Démarrage de l'application...")
        execute()
    }

    private fun execute() {
        createDefaultUserSeed.execute()
    }
}