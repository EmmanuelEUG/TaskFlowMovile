package com.emma.proyects.taskflow.auth.presentation.screens

import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val generalError: UiText? = null,
    val isSubmitting: Boolean = false,
    val authenticatedUser: User? = null
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val nameError: UiText? = null,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val confirmPasswordError: UiText? = null,
    val generalError: UiText? = null,
    val isSubmitting: Boolean = false,
    val registeredUser: User? = null
)

data class SessionUiState(
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val sessionExpiredNotice: Boolean = false
)
