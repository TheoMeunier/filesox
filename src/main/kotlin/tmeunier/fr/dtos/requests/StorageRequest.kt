package tmeunier.fr.dtos.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID


// folder
data class FolderCreateRequest(
    @field:NotBlank(message = "The name of the folder is required")
    val path: String,

    val parentId: UUID
)

// Storage actions
data class GetStorageByPathRequest(
    @field:NotBlank(message = "The name of the folder is required")
    val path: String,
)

data class UpdateStorageRequest(
    @field:NotNull(message = "Storage ID is required")
    val id: UUID,

    @field:NotBlank(message = "Storage name is required")
    val name: String,

    @field:NotBlank(message = "Storage update name is required")
    val newName: String,

    val parentId: UUID?
)

data class MoveStorageRequest(
    @field:NotNull(message = "Storage ID is required")
    val id: UUID,

    @field:NotBlank(message = "The move path is required")
    val newPath: String,

    val parentId: UUID?,
)

data class DeleteStorageRequest(
    @field:NotNull(message = "Storage ID is required")
    val id: UUID,
    val isFolder: Boolean = false
)

