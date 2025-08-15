package tmeunier.fr.exceptions.redis

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class ErrorSetKeyRedisException(message: String) : ApiException(
    "ERROR_SET_KEY_REDIS_EXCEPTION",
    message,
    Response.Status.BAD_REQUEST,
)