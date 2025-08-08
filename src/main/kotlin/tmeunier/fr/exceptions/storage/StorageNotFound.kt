package tmeunier.fr.exceptions.storage

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class StorageNotFoundException(message: String) : ApiException(
    "STORAGE_NOT_FOUND",
    message,
    Response.Status.NOT_FOUND,
)