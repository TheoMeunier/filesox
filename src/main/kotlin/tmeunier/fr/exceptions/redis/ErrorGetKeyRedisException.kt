package tmeunier.fr.exceptions.redis

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class ErrorGetKeyRedisException(message: String) : ApiException(
    "ERROR_GET_KEY_REDIS_EXCEPTION",
    message,
    Response.Status.NOT_FOUND,
)