package tmeunier.fr.exceptions.storage

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class DownloadChunkFileException(message: String) : ApiException(
    "ERROR_DOWNLOAD_CHUNK_FILE",
    message,
    Response.Status.INTERNAL_SERVER_ERROR,
)