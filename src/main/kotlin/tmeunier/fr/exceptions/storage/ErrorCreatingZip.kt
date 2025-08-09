package tmeunier.fr.exceptions.storage

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class ErrorCreatingZipException(message: String) : ApiException(
    "ERROR_CREATING_ZIP",
    message,
    Response.Status.INTERNAL_SERVER_ERROR,
)