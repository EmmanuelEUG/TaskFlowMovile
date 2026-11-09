package com.emma.proyects.taskflow

import com.emma.proyects.taskflow.auth.domain.model.AuthConstants
import com.emma.proyects.taskflow.auth.domain.model.AuthSession
import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.auth.domain.repository.AuthRepository
import com.emma.proyects.taskflow.auth.presentation.viewmodels.LoginViewModel
import com.emma.proyects.taskflow.auth.presentation.viewmodels.RegisterViewModel
import com.emma.proyects.taskflow.core.domain.network.AppException
import com.emma.proyects.taskflow.core.presentation.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginRegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeAuthRepository : AuthRepository {
        var failureToReturn: Throwable? = null
        override suspend fun login(email: String, password: String): Result<AuthSession> {
            return failureToReturn?.let { Result.failure(it) }
                ?: Result.success(AuthSession("tok-1", User("u-1", "Emma", email)))
        }

        override suspend fun register(name: String, email: String, password: String): Result<AuthSession> {
            return failureToReturn?.let { Result.failure(it) }
                ?: Result.success(AuthSession("tok-2", User("u-2", name, email)))
        }

        override suspend fun fetchCurrentUser(): Result<User> = Result.failure(UnsupportedOperationException())
        override fun getCachedUser(): User? = null
        override fun hasActiveSession(): Boolean = false
        override fun logout() = Unit
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loginViewModel_validatesEmailAndPasswordBeforeCallingRepository() = runTest {
        val repo = FakeAuthRepository()
        val vm = LoginViewModel(repo)

        vm.onEmailChange("correo-invalido")
        vm.onPasswordChange("123")
        vm.onSubmitLogin()

        assertEquals(
            UiText.StringResource(R.string.validation_email_invalid),
            vm.uiState.value.emailError
        )
        assertEquals(
            UiText.PluralResource(
                R.plurals.validation_password_short,
                AuthConstants.MIN_PASSWORD_LENGTH
            ),
            vm.uiState.value.passwordError
        )
        assertNull(vm.uiState.value.authenticatedUser)

        vm.onEmailChange("emma@upchiapas.edu.mx")
        vm.onPasswordChange("Password123")
        vm.onSubmitLogin()
        advanceUntilIdle()

        assertNull(vm.uiState.value.emailError)
        assertNull(vm.uiState.value.passwordError)
        assertEquals("emma@upchiapas.edu.mx", vm.uiState.value.authenticatedUser?.email)
    }

    @Test
    fun loginViewModel_mapsAppExceptionResourceError() = runTest {
        val repo = FakeAuthRepository().apply {
            failureToReturn = AppException.ResourceMessage(R.string.error_http_unauthorized)
        }
        val vm = LoginViewModel(repo)

        vm.onEmailChange("emma@upchiapas.edu.mx")
        vm.onPasswordChange("Password123")
        vm.onSubmitLogin()
        advanceUntilIdle()

        assertEquals(
            UiText.StringResource(R.string.error_http_unauthorized),
            vm.uiState.value.generalError
        )
    }

    @Test
    fun registerViewModel_validatesConfirmationMismatchAndMinLength() = runTest {
        val repo = FakeAuthRepository()
        val vm = RegisterViewModel(repo)

        vm.onNameChange("Emmanuel")
        vm.onEmailChange("emma@upchiapas.edu.mx")
        vm.onPasswordChange("Password123")
        vm.onConfirmPasswordChange("OtraClave")
        vm.onSubmitRegister()

        assertEquals(
            UiText.StringResource(R.string.validation_passwords_mismatch),
            vm.uiState.value.confirmPasswordError
        )
        assertNull(vm.uiState.value.registeredUser)

        vm.onConfirmPasswordChange("Password123")
        vm.onSubmitRegister()
        advanceUntilIdle()

        assertNull(vm.uiState.value.confirmPasswordError)
        assertEquals("Emmanuel", vm.uiState.value.registeredUser?.name)
    }
}
