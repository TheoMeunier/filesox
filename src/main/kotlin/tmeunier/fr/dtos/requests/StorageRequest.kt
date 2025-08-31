package tmeunier.fr.dtos.requests

import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*


@RegisterForReflection
data class FolderCreateRequest(
    @field:NotBlank(message = "The name of the folder is required")
    val path: String,

    val parentId: UUID
)

// Storage actions
@RegisterForReflection
data class GetStorageByPathRequest(
    @field:NotBlank(message = "The name of the folder is required")
    val path: String,
)

@RegisterForReflection
data class UpdateStorageRequest(
    @field:NotNull(message = "Storage ID is required")
    val id: UUID,

    @field:NotBlank(message = "Storage update name is required")
    val newName: String,

    val parentId: UUID?
)

@RegisterForReflection
data class MoveStorageRequest(
    @field:NotNull(message = "Storage ID is required")
    val id: UUID,

    @field:NotBlank(message = "The move path is required")
    val newPath: String,

    @field:NotBlank(message = "The storage name is required")
    val storageName: String,

    @field:NotNull(message = "Parent ID is required")
    val parentId: UUID,
)

@RegisterForReflection
data class DeleteStorageRequest(
    @field:NotNull(message = "Storage ID is required")
    val id: UUID,
    val isFolder: Boolean = false
)

