package com.emma.proyects.taskflow.auth.domain.repository

import com.emma.proyects.taskflow.auth.domain.model.AuthSession
import com.emma.proyects.taskflow.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<AuthSession>
    suspend fun register(name: String, email: String, password: String): Result<AuthSession>
    suspend fun fetchCurrentUser(): Result<User>
    fun getCachedUser(): User?
    fun hasActiveSession(): Boolean
    fun logout()
}
