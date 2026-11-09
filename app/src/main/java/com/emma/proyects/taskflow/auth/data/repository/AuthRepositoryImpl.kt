package com.emma.proyects.taskflow.auth.data.repository

import com.emma.proyects.taskflow.auth.data.datasource.AuthApiService
import com.emma.proyects.taskflow.auth.data.dto.LoginRequestDto
import com.emma.proyects.taskflow.auth.data.dto.RegisterRequestDto
import com.emma.proyects.taskflow.auth.data.mapper.toDomain
import com.emma.proyects.taskflow.auth.domain.model.AuthSession
import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.auth.domain.repository.AuthRepository
import com.emma.proyects.taskflow.core.data.network.ApiErrorMapper
import com.emma.proyects.taskflow.core.domain.session.SessionStorage

class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val sessionStorage: SessionStorage,
    private val errorMapper: ApiErrorMapper
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthSession> {
        return runCatching {
            val response = apiService.login(
                LoginRequestDto(
                    email = email.trim().lowercase(),
                    password = password
                )
            )
            val session = response.toDomain()
            sessionStorage.saveSession(session)
            session
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override suspend fun register(name: String, email: String, password: String): Result<AuthSession> {
        return runCatching {
            val response = apiService.register(
                RegisterRequestDto(
                    name = name.trim(),
                    email = email.trim().lowercase(),
                    password = password
                )
            )
            val session = response.toDomain()
            sessionStorage.saveSession(session)
            session
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override suspend fun fetchCurrentUser(): Result<User> {
        return runCatching {
            val user = apiService.getMe().toDomain()
            sessionStorage.getToken()?.let { token ->
                sessionStorage.saveSession(AuthSession(accessToken = token, user = user))
            }
            user
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override fun getCachedUser(): User? = sessionStorage.getCurrentUser()

    override fun hasActiveSession(): Boolean = sessionStorage.hasActiveSession()

    override fun logout() {
        sessionStorage.clearSession()
    }
}
