package tmeunier.fr.services

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import jakarta.enterprise.context.ApplicationScoped
import jakarta.resource.spi.ConfigProperty


@ApplicationScoped
class PasswordService
{
    private val argon2: Argon2? = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

    var iterations: Int = 2
    var memory: Int = 65536
    var parallelism: Int = 1

    fun hashPassword(password: String): String {
        try {
            return argon2!!.hash(iterations, memory, parallelism, password.toCharArray())
        } finally {
            argon2!!.wipeArray(password.toCharArray())
        }
    }

    fun verifyPassword(password: String, hash: String?): Boolean {
        try {
            return argon2!!.verify(hash, password.toCharArray())
        } finally {
            argon2!!.wipeArray(password.toCharArray())
        }
    }
}