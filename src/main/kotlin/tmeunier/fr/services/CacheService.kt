package tmeunier.fr.services

import io.quarkus.redis.datasource.RedisDataSource
import io.quarkus.redis.datasource.value.ValueCommands
import jakarta.enterprise.context.ApplicationScoped
import tmeunier.fr.exceptions.redis.RedisDeleteKeyException
import tmeunier.fr.exceptions.redis.RedisGetKeyException
import tmeunier.fr.exceptions.redis.RedisSetKeyException
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
            throw RedisGetKeyException("Error getting key $cacheKey from Redis: ${e.message}")
        }
    }

    fun setRedisCache(cacheKey: String, imageData: ByteArray) {
        try {
            val encoded = Base64.getEncoder().encodeToString(imageData)
            valueCommands.setex(cacheKey, 86400, encoded)

        } catch (e: Exception) {
            throw RedisSetKeyException("Error setting key $cacheKey in Redis: ${e.message}")
        }
    }

    fun deleteFromRedisCache(cacheKey: String) {
        try {
            redisDataSource.key().del(cacheKey)
        } catch (e: Exception) {
            throw RedisDeleteKeyException("Error deleting key $cacheKey from Redis: ${e.message}")
        }
    }


    fun existsInCache(cacheKey: String): Boolean {
        return try {
            redisDataSource.key().exists(cacheKey) > false
        } catch (e: Exception) {
            throw RedisGetKeyException("Error getting key $cacheKey from Redis: ${e.message}")
        }
    }
}