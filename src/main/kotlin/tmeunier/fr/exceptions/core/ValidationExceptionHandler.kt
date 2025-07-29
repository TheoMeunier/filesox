package tmeunier.fr.exceptions.core

import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class ValidationExceptionHandler : ExceptionMapper<ConstraintViolationException> {

    override fun toResponse(exception: ConstraintViolationException): Response {
        val errors = exception.constraintViolations.map { violation ->
            mapOf(
                "field" to violation.propertyPath.toString(),
                "message" to violation.message
            )
        }

        return Response.status(400)
            .entity(mapOf("errors" to errors))
            .build()
    }
}