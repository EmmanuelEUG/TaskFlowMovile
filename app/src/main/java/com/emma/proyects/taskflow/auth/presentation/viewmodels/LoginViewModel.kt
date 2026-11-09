package com.emma.proyects.taskflow.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.auth.domain.model.AuthConstants
import com.emma.proyects.taskflow.auth.domain.repository.AuthRepository
import com.emma.proyects.taskflow.auth.presentation.screens.LoginUiState
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(email = value, emailError = null, generalError = null)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(password = value, passwordError = null, generalError = null)
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSubmitLogin() {
        val current = _uiState.value
        if (current.isSubmitting) return

        val trimmedEmail = current.email.trim()
        val emailError = validateEmail(trimmedEmail)
        val passwordError = validatePassword(current.password)

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    generalError = null
                )
            }
            return
        }

        _uiState.update {
            it.copy(isSubmitting = true, generalError = null)
        }

        viewModelScope.launch {
            val result = authRepository.login(email = trimmedEmail, password = current.password)
            result.fold(
                onSuccess = { session ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            authenticatedUser = session.user
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            generalError = error.toUiText(R.string.error_login_failed)
                        )
                    }
                }
            )
        }
    }

    fun consumeLoginSuccess() {
        _uiState.update { it.copy(authenticatedUser = null) }
    }

    private fun validateEmail(email: String): UiText? = when {
        email.isBlank() -> UiText.StringResource(R.string.validation_email_required)
        !EMAIL_REGEX.matches(email) -> UiText.StringResource(R.string.validation_email_invalid)
        else -> null
    }

    private fun validatePassword(password: String): UiText? = when {
        password.isBlank() -> UiText.StringResource(R.string.validation_password_required)
        password.length < AuthConstants.MIN_PASSWORD_LENGTH -> UiText.PluralResource(
            R.plurals.validation_password_short,
            AuthConstants.MIN_PASSWORD_LENGTH
        )
        else -> null
    }

    private companion object {
        val EMAIL_REGEX = Regex(AuthConstants.EMAIL_PATTERN)
    }
}
