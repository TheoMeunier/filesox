package tmeunier.fr.exceptions.redis

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class ErrorDeleteKeyRedisException(message: String) : ApiException(
    "ERROR_DELETE_KEY_REDIS_EXCEPTION",
    message,
    Response.Status.BAD_REQUEST,
)