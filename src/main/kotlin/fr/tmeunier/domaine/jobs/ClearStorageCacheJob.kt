package fr.tmeunier.domaine.jobs

import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.Executors

object ClearStorageCacheJob
{
    // while in 24 hours
    private const val PROVISION_TASK_RATE = 86400000L

    private const val GIGABYTE: Long = 1024L * 1024L * 1024L
    private const val MAX_CACHE_SIZE_GB: Long = 5L
    private const val MAX_CACHE_SIZE_BYTES: Long = MAX_CACHE_SIZE_GB * GIGABYTE

    private val clearStorageCacheJob = CoroutineScope(Executors.newFixedThreadPool(2).asCoroutineDispatcher() + CoroutineName("ClearStorageCacheJob"))

    fun initJob()
    {
        clearStorageCache()
    }

    private fun clearStorageCache() = clearStorageCacheJob.launch {
        while (true)
        {
            delay(PROVISION_TASK_RATE)

            val folder = File("storages/cache")

            if (getFolderSize(folder) > MAX_CACHE_SIZE_BYTES) {
                folder.walkTopDown()
                    .filter { it.isFile }
                    .forEach { it.delete() }
            }
        }
    }

    private fun getFolderSize(folder: File): Long {
        return folder.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }
}