package tmeunier.fr.exceptions.storage

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class ErrorS3Exception(message: String) : ApiException(
    "ERROR_S3",
    message,
    Response.Status.INTERNAL_SERVER_ERROR,
)