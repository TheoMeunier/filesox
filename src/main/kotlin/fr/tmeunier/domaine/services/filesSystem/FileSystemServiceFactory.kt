package fr.tmeunier.domaine.services.filesSystem

import fr.tmeunier.domaine.services.filesSystem.provider.LocalStorageProvider
import fr.tmeunier.domaine.services.filesSystem.provider.S3StorageProvider

enum class StorageEnumType {
    S3, LOCAL
}

object FileSystemServiceFactory
{
    private lateinit var storageType: StorageEnumType

    fun initialize(config: String) {
        storageType = StorageEnumType.valueOf(config.toUpperCase())
    }

    fun createStorageService(): FileSystemInterface {
        return when (storageType) {
            StorageEnumType.S3 -> S3StorageProvider()
            StorageEnumType.LOCAL -> LocalStorageProvider()
        }
    }
}