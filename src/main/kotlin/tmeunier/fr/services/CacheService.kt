package tmeunier.fr.services

import io.quarkus.redis.datasource.RedisDataSource
import io.quarkus.redis.datasource.value.ValueCommands
import jakarta.enterprise.context.ApplicationScoped
import java.util.Base64

@ApplicationScoped
class CacheService(
    private val redisDataSource: RedisDataSource
) {
    private val valueCommands: ValueCommands<String, String> by lazy {
        redisDataSource.value(String::class.java)
    }

    fun getFromRedisCache(cacheKey: String): ByteArray? {
        return try {
            valueCommands.get(cacheKey)?.let { encodedData ->
                Base64.getDecoder().decode(encodedData)
            }
        } catch (e: Exception) {
            logger.error{"Erreur lors de la récupération du cache Redis pour $cacheKey: ${e.message}"}
            null
        }
    }

    fun setRedisCache(cacheKey: String, imageData: ByteArray) {
        try {
            logger.info{"💾 Tentative d'enregistrement dans Redis pour la clé: $cacheKey"}
            logger.info{"📊 Taille des données: ${imageData.size} bytes"}

            val encoded = Base64.getEncoder().encodeToString(imageData)
            logger.info{"🔄 Données encodées en Base64, taille: ${encoded.length} caractères"}

            valueCommands.set("test-key-${System.currentTimeMillis()}", "test-value")
            logger.info{"✅ Test d'écriture simple réussi"}

            valueCommands.setex(cacheKey, 86400, encoded)
            logger.info{"✅ Données enregistrées dans Redis pour la clé: $cacheKey"}

            val verification = valueCommands.get(cacheKey)
            if (verification != null) {
                logger.info{"✅ Vérification réussie - données bien enregistrées"}
            } else {
                logger.error{"❌ Vérification échouée - données non trouvées après enregistrement"}
            }

        } catch (e: Exception) {
            logger.error{"💥 Erreur cache Redis pour $cacheKey: ${e.message}"}
            e.printStackTrace()
        }
    }

    fun deleteFromRedisCache(cacheKey: String) {
        try {
            redisDataSource.key().del(cacheKey)
        } catch (e: Exception) {
            logger.error{"Erreur lors de la suppression du cache Redis pour $cacheKey: ${e.message}"}
        }
    }


    fun existsInCache(cacheKey: String): Boolean {
        return try {
            redisDataSource.key().exists(cacheKey) > false
        } catch (e: Exception) {
            logger.warn{"Erreur lors de la vérification de l'existence dans le cache Redis pour $cacheKey: ${e.message}"}
            false
        }
    }

    fun testConnection(): Boolean {
        return try {
            valueCommands.set("test-connection", "ok")
            val result = valueCommands.get("test-connection")
            redisDataSource.key().del("test-connection")
            result == "ok"
        } catch (e: Exception) {
            logger.error("Test de connexion Redis échoué: ${e.message}")
            false
        }
    }
}