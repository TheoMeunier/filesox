package tmeunier.fr

import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/hello")
class ExampleResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun hello(request: Request) = mapOf("message" to "Hello ${request.full_name}")
}


data class Request(
    val full_name: String,
)