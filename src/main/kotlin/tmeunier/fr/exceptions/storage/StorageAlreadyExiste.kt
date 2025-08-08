package tmeunier.fr.exceptions.storage

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class StorageAlreadyExistException(message: String) : ApiException(
    "STORAGE_ALREADY_EXIST",
    message,
    Response.Status.CONFLICT,
)