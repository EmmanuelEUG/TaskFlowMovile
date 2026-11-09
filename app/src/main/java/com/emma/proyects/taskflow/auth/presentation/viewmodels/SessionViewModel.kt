package com.emma.proyects.taskflow.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.auth.domain.repository.AuthRepository
import com.emma.proyects.taskflow.auth.presentation.screens.SessionUiState
import com.emma.proyects.taskflow.core.domain.session.SessionMonitor
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode
import com.emma.proyects.taskflow.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionViewModel(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val sessionMonitor: SessionMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SessionUiState(
            isAuthenticated = authRepository.hasActiveSession(),
            currentUser = authRepository.getCachedUser(),
            themeMode = taskRepository.getThemeMode()
        )
    )
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionMonitor.unauthorizedEvents.collect {
                _uiState.update {
                    it.copy(
                        isAuthenticated = false,
                        currentUser = null,
                        sessionExpiredNotice = true
                    )
                }
            }
        }
    }

    fun onAuthenticated(user: User) {
        _uiState.update {
            it.copy(
                isAuthenticated = true,
                currentUser = user,
                sessionExpiredNotice = false
            )
        }
    }

    fun onLogout() {
        authRepository.logout()
        _uiState.update {
            it.copy(
                isAuthenticated = false,
                currentUser = null,
                sessionExpiredNotice = false
            )
        }
    }

    fun consumeSessionExpiredNotice() {
        _uiState.update { it.copy(sessionExpiredNotice = false) }
    }

    fun onCycleTheme() {
        val next = when (_uiState.value.themeMode) {
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.DARK
        }
        onThemeModeSelected(next)
    }

    fun onThemeModeSelected(mode: ThemeMode) {
        val saved = taskRepository.setThemeMode(mode)
        _uiState.update { it.copy(themeMode = saved) }
    }
}
