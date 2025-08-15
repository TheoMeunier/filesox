package tmeunier.fr.exceptions.s3

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class S3GetObjectException(message: String) : ApiException(
    "ERROR_GET_OBJECT_S3_EXCEPTION",
    message,
    Response.Status.NOT_FOUND,
)