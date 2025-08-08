package tmeunier.fr.exceptions.storage

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class FileChunkIsEmptyException() : ApiException(
    "FILE_CHUNK_IS_EMPTY",
    "The file chunk is empty. Please provide a valid file chunk.",
    Response.Status.BAD_REQUEST,
)