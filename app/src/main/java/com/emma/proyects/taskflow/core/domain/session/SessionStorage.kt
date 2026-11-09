package com.emma.proyects.taskflow.core.domain.session

import com.emma.proyects.taskflow.auth.domain.model.AuthSession
import com.emma.proyects.taskflow.auth.domain.model.User

interface SessionStorage {
    fun saveSession(session: AuthSession)
    fun getToken(): String?
    fun getCurrentUser(): User?
    fun hasActiveSession(): Boolean
    fun clearSession()
}
