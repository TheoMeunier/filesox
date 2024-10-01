package fr.tmeunier.domaine.jobs

import fr.tmeunier.domaine.repositories.ShareRepository
import kotlinx.coroutines.*
import java.util.concurrent.Executors

object ShareJob
{
    private const val PROVISION_TASK_RATE = 15_000_000L

    private val shareJobContext = CoroutineScope(Executors.newFixedThreadPool(2).asCoroutineDispatcher() + CoroutineName("ShareJob"))

    fun initJob()
    {
        clearShare()
    }

    private fun clearShare() = shareJobContext.launch {
        while (true)
        {
            delay(PROVISION_TASK_RATE)

            val share = ShareRepository.findAllExpired()
            share.forEach {
                ShareRepository.delete(it.id)
            }
        }
    }
}