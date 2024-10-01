package fr.tmeunier.domaine.services

import ch.qos.logback.core.joran.action.Action
import fr.tmeunier.config.Database.dbQuery
import fr.tmeunier.domaine.repositories.LogRepository

object LogService {
    const val ACTION_CREATE = "created"
    const val ACTION_UPDATE = "updated"
    const val ACTION_DELETE = "deleted"
    const val ACTION_UPLOAD = "uploaded"
    const val ACTION_MOVE = "moved"
    const val ACTION_RESTORE = "restore"
    const val ACTION_LOGIN = "login"

    suspend fun add(user: Int, action: String, subject: String) = dbQuery {
        LogRepository.create(user, action, subject)
    }
}