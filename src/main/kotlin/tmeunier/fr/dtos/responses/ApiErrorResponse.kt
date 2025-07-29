package tmeunier.fr.dtos.responses

import java.time.Instant

data class ApiErrorResponse(
    val error: String,
    val message: String,
    val details: String? = null,
    val timestamp: String = Instant.now().toString(),
    val path: String? = null,
    val traceId: String? = null
)