package com.emma.proyects.taskflow

import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.tasks.domain.model.Importance
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode
import com.emma.proyects.taskflow.tasks.domain.repository.TaskRepository
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TaskFormViewModel
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
class TaskFormViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeTaskRepository : TaskRepository {
        var createdCount = 0
        override suspend fun getTasks(): Result<List<Task>> = Result.success(emptyList())
        override suspend fun getTaskById(taskId: String): Result<Task> = Result.success(
            Task(
                id = taskId,
                title = "Tarea existente",
                description = "Detalle",
                area = "Proyecto",
                importance = Importance.HIGH,
                priorityLevel = 5,
                dueDateFormatted = null
            )
        )

        override suspend fun createTask(task: Task): Result<Task> {
            createdCount++
            return Result.success(task.copy(id = "new-1"))
        }

        override suspend fun updateTask(task: Task): Result<Task> = Result.success(task)
        override suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean): Result<Task> =
            Result.failure(UnsupportedOperationException())
        override suspend fun deleteTask(taskId: String): Result<Unit> = Result.success(Unit)
        override suspend fun restoreSeedTasks(): Result<List<Task>> = Result.success(emptyList())
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
    fun taskFormViewModel_validatesTitleAreaAndRecurrenceInterval() = runTest {
        val repo = FakeTaskRepository()
        val vm = TaskFormViewModel(taskId = null, taskRepository = repo)

        vm.onTitleChange("")
        vm.onAreaChange("")
        vm.onRecurringToggle(true)
        vm.onRecurrenceIntervalChange("0")
        vm.onSaveTask()

        assertEquals(
            UiText.StringResource(R.string.validation_title_required),
            vm.uiState.value.titleError
        )
        assertEquals(
            UiText.StringResource(R.string.validation_area_required),
            vm.uiState.value.areaError
        )
        assertEquals(
            UiText.StringResource(
                R.string.validation_recurrence_interval_min,
                TaskConstants.MIN_RECURRENCE_INTERVAL
            ),
            vm.uiState.value.recurrenceIntervalError
        )
        assertEquals(0, repo.createdCount)

        vm.onTitleChange("Estudiar Arquitectura")
        vm.onAreaChange("Escuela")
        vm.onRecurrenceIntervalChange("2")
        vm.onSaveTask()
        advanceUntilIdle()

        assertNull(vm.uiState.value.titleError)
        assertNull(vm.uiState.value.areaError)
        assertNull(vm.uiState.value.recurrenceIntervalError)
        assertEquals(1, repo.createdCount)
        assertEquals(
            UiText.StringResource(R.string.msg_task_created),
            vm.uiState.value.savedSuccessMessage
        )
    }

    @Test
    fun taskFormViewModel_initializesDefaultAreaOnceInCreateMode() = runTest {
        val repo = FakeTaskRepository()
        val vm = TaskFormViewModel(taskId = null, taskRepository = repo)

        vm.initializeDefaultArea("Escuela")
        assertEquals("Escuela", vm.uiState.value.area)

        vm.onAreaChange("Casa")
        vm.initializeDefaultArea("Escuela")
        assertEquals("Casa", vm.uiState.value.area)
    }

    @Test
    fun taskFormViewModel_preloadsTaskInEditMode() = runTest {
        val repo = FakeTaskRepository()
        val vm = TaskFormViewModel(taskId = "t-99", taskRepository = repo)
        advanceUntilIdle()

        assertEquals("Tarea existente", vm.uiState.value.title)
        assertEquals("Proyecto", vm.uiState.value.area)
        assertEquals(5, vm.uiState.value.priorityLevel)
    }
}
