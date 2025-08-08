package tmeunier.fr.exceptions.storage

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class UploadIdNotFoundException(message: String) : ApiException(
    "UPLOAD_ID_NOT_FOUND",
    message,
    Response.Status.BAD_REQUEST,
)