package tmeunier.fr.exceptions.common

import jakarta.ws.rs.core.Response
import tmeunier.fr.exceptions.core.ApiException

class NotFoundException : ApiException(
    "NOT_FOUND",
    "The requested resource was not found.",
    Response.Status.NOT_FOUND,
)