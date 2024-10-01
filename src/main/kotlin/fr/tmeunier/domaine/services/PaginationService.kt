package fr.tmeunier.domaine.services

import fr.tmeunier.config.Database.dbQuery
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.ceil

object PaginationService {

    @Serializable
    data class PaginationResponse<T>(
        val data: List<T>,
        val total: Int,
        @SerialName("total_pages") val totalPages: Int,
        @SerialName("current_page") val currentPage: Int,
        @SerialName("per_page") val perPage: Int,
        val from: Int,
        val to: Int
    )

    suspend fun <R> paginate(
        page: Int,
        perPage: Int,
        query: () -> Query,
        mapper: (ResultRow) -> R
    ): PaginationResponse<R> = dbQuery {
        val total = transaction { query().count() }
        val totalPages = ceil(total.toDouble() / perPage).toInt()

        val to =
            if (totalPages <= perPage) totalPages else if (page + perPage / 2 <= totalPages) page + perPage / 2 else totalPages
        val from = if (totalPages <= perPage) 1 else if (to - perPage + 1 >= 1) to - perPage + 1 else 1

        val data = transaction {
            query()
                .limit(perPage, offset = ((page - 1) * perPage).toLong())
                .map { mapper(it) }
        }


        PaginationResponse(
            data,
            total.toInt(),
            totalPages,
            page,
            perPage,
            from = from,
            to = to
        )
    }
}