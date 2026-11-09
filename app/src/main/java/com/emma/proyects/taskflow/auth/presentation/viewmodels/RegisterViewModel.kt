package com.emma.proyects.taskflow.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.auth.domain.model.AuthConstants
import com.emma.proyects.taskflow.auth.domain.repository.AuthRepository
import com.emma.proyects.taskflow.auth.presentation.screens.RegisterUiState
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update {
            it.copy(name = value, nameError = null, generalError = null)
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(email = value, emailError = null, generalError = null)
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                passwordError = null,
                confirmPasswordError = null,
                generalError = null
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                confirmPassword = value,
                confirmPasswordError = null,
                generalError = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onSubmitRegister() {
        val current = _uiState.value
        if (current.isSubmitting) return

        val trimmedName = current.name.trim()
        val trimmedEmail = current.email.trim()

        val nameError = validateName(trimmedName)
        val emailError = validateEmail(trimmedEmail)
        val passwordError = validatePassword(current.password)
        val confirmError = validateConfirmPassword(current.password, current.confirmPassword)

        if (nameError != null || emailError != null || passwordError != null || confirmError != null) {
            _uiState.update {
                it.copy(
                    nameError = nameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmError,
                    generalError = null
                )
            }
            return
        }

        _uiState.update {
            it.copy(isSubmitting = true, generalError = null)
        }

        viewModelScope.launch {
            val result = authRepository.register(
                name = trimmedName,
                email = trimmedEmail,
                password = current.password
            )
            result.fold(
                onSuccess = { session ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            registeredUser = session.user
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            generalError = error.toUiText(R.string.error_register_failed)
                        )
                    }
                }
            )
        }
    }

    fun consumeRegisterSuccess() {
        _uiState.update { it.copy(registeredUser = null) }
    }

    private fun validateName(name: String): UiText? = when {
        name.isBlank() -> UiText.StringResource(R.string.validation_name_required)
        name.length < AuthConstants.MIN_NAME_LENGTH -> UiText.PluralResource(
            R.plurals.validation_name_min_length,
            AuthConstants.MIN_NAME_LENGTH
        )
        else -> null
    }

    private fun validateEmail(email: String): UiText? = when {
        email.isBlank() -> UiText.StringResource(R.string.validation_email_required)
        !EMAIL_REGEX.matches(email) -> UiText.StringResource(R.string.validation_email_invalid)
        else -> null
    }

    private fun validatePassword(password: String): UiText? = when {
        password.isBlank() -> UiText.StringResource(R.string.validation_password_required)
        password.length < AuthConstants.MIN_PASSWORD_LENGTH -> UiText.PluralResource(
            R.plurals.validation_password_min_length,
            AuthConstants.MIN_PASSWORD_LENGTH
        )
        else -> null
    }

    private fun validateConfirmPassword(password: String, confirm: String): UiText? = when {
        confirm.isBlank() -> UiText.StringResource(R.string.validation_confirm_password_required)
        password != confirm -> UiText.StringResource(R.string.validation_passwords_mismatch)
        else -> null
    }

    private companion object {
        val EMAIL_REGEX = Regex(AuthConstants.EMAIL_PATTERN)
    }
}
