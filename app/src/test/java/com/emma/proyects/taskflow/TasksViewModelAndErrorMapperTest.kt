package com.emma.proyects.taskflow

import com.emma.proyects.taskflow.auth.domain.model.AuthSession
import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.core.data.network.ApiErrorMapper
import com.emma.proyects.taskflow.core.domain.network.AppException
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.core.presentation.toUiText
import com.emma.proyects.taskflow.auth.domain.repository.AuthRepository
import com.emma.proyects.taskflow.tasks.domain.model.DueDateFilter
import com.emma.proyects.taskflow.tasks.domain.model.Importance
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode
import com.emma.proyects.taskflow.tasks.domain.repository.TaskRepository
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TasksViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModelAndErrorMapperTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String): Result<AuthSession> =
            Result.failure(UnsupportedOperationException())
        override suspend fun register(name: String, email: String, password: String): Result<AuthSession> =
            Result.failure(UnsupportedOperationException())
        override suspend fun fetchCurrentUser(): Result<User> =
            Result.failure(UnsupportedOperationException())
        override fun getCachedUser(): User = User("u-1", "Emma", "emma@example.com")
        override fun hasActiveSession(): Boolean = true
        override fun logout() = Unit
    }

    private class FakeTaskRepository(
        private val seededTasks: List<Task>
    ) : TaskRepository {
        override suspend fun getTasks(): Result<List<Task>> = Result.success(seededTasks)
        override suspend fun getTaskById(taskId: String): Result<Task> =
            Result.failure(UnsupportedOperationException())
        override suspend fun createTask(task: Task): Result<Task> =
            Result.failure(UnsupportedOperationException())
        override suspend fun updateTask(task: Task): Result<Task> =
            Result.failure(UnsupportedOperationException())
        override suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean): Result<Task> =
            Result.failure(UnsupportedOperationException())
        override suspend fun deleteTask(taskId: String): Result<Unit> = Result.success(Unit)
        override suspend fun restoreSeedTasks(): Result<List<Task>> = Result.success(seededTasks)
        override fun getThemeMode(): ThemeMode = ThemeMode.SYSTEM
        override fun setThemeMode(mode: ThemeMode): ThemeMode = mode
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
    fun tasksViewModel_tracksHasActiveFilters_areaToggle_andDueDateFilters() = runTest {
        val zone = ZoneId.systemDefault()
        val todayNoonMillis = LocalDate.now(zone).atTime(LocalTime.NOON).atZone(zone).toInstant().toEpochMilli()
        val futureMillis = LocalDate.now(zone).plusDays(21).atTime(LocalTime.NOON).atZone(zone).toInstant().toEpochMilli()

        val tasks = listOf(
            Task(
                id = "1",
                title = "Entrega hoy",
                description = "Revisar apuntes",
                area = "Escuela",
                importance = Importance.HIGH,
                priorityLevel = 5,
                dueDateFormatted = "Hoy, 12:00",
                dueDateMillis = todayNoonMillis
            ),
            Task(
                id = "2",
                title = "Sin fecha",
                description = "Limpiar cuarto",
                area = "Casa",
                importance = Importance.LOW,
                priorityLevel = 2,
                dueDateFormatted = null,
                dueDateMillis = null
            ),
            Task(
                id = "3",
                title = "Futuro lejano",
                description = "Viaje",
                area = "Proyecto",
                importance = Importance.MEDIUM,
                priorityLevel = 3,
                dueDateFormatted = "Pronto",
                dueDateMillis = futureMillis
            )
        )

        val vm = TasksViewModel(FakeTaskRepository(tasks), FakeAuthRepository())
        advanceUntilIdle()

        assertFalse(vm.uiState.value.hasActiveFilters)
        assertNull(vm.uiState.value.selectedArea)
        assertEquals(3, vm.uiState.value.pendingTasks.size)

        vm.onAreaSelected("Escuela")
        assertTrue(vm.uiState.value.hasActiveFilters)
        assertEquals("Escuela", vm.uiState.value.selectedArea)
        assertEquals(1, vm.uiState.value.pendingTasks.size)

        vm.onAreaSelected("Escuela")
        assertFalse(vm.uiState.value.hasActiveFilters)
        assertNull(vm.uiState.value.selectedArea)
        assertEquals(3, vm.uiState.value.pendingTasks.size)

        vm.onDueDateFilterSelected(DueDateFilter.NO_DATE)
        assertEquals(listOf("2"), vm.uiState.value.pendingTasks.map { it.id })

        vm.onDueDateFilterSelected(DueDateFilter.TODAY)
        assertEquals(listOf("1"), vm.uiState.value.pendingTasks.map { it.id })

        vm.onDueDateFilterSelected(DueDateFilter.LATER)
        assertEquals(listOf("3"), vm.uiState.value.pendingTasks.map { it.id })

        vm.onClearFilters()
        assertFalse(vm.uiState.value.hasActiveFilters)
        assertEquals(DueDateFilter.ALL, vm.uiState.value.selectedDueDateFilter)
        assertEquals(3, vm.uiState.value.pendingTasks.size)
    }

    @Test
    fun apiErrorMapper_mapsServerDetailAndLocalizedResources() {
        val mapper = ApiErrorMapper(Json { ignoreUnknownKeys = true })

        val jsonErrorBody = """{"detail":"Correo ya registrado"}""".toResponseBody(
            "application/json".toMediaType()
        )
        val detailException = mapper.map(
            HttpException(Response.error<String>(409, jsonErrorBody))
        )
        assertEquals(AppException.ServerMessage("Correo ya registrado"), detailException)
        assertEquals(
            UiText.DynamicString("Correo ya registrado"),
            detailException.toUiText(R.string.error_load_tasks)
        )

        val emptyUnauthorizedBody = "".toResponseBody("application/json".toMediaType())
        val unauthorizedException = mapper.map(
            HttpException(Response.error<String>(401, emptyUnauthorizedBody))
        )
        assertEquals(
            AppException.ResourceMessage(R.string.error_http_unauthorized),
            unauthorizedException
        )
        assertEquals(
            UiText.StringResource(R.string.error_http_unauthorized),
            unauthorizedException.toUiText(R.string.error_load_tasks)
        )

        val timeoutException = mapper.map(SocketTimeoutException("timeout"))
        assertEquals(
            AppException.ResourceMessage(R.string.error_timeout),
            timeoutException
        )

        val ioException = mapper.map(IOException("offline"))
        assertEquals(
            AppException.ResourceMessage(R.string.error_connection),
            ioException
        )
    }
}
