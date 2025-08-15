package tmeunier.fr.exceptions.s3

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class S3DeleteObjectException(message: String) : ApiException(
    "S3_DELETE_OBJECT_EXCEPTION",
    message,
    Response.Status.BAD_REQUEST,
)