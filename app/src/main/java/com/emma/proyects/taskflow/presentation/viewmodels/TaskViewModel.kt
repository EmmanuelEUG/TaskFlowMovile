package com.emma.proyects.taskflow.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.TaskFilter
import com.emma.proyects.taskflow.data.model.TaskPriority
import com.emma.proyects.taskflow.data.model.ThemeMode
import com.emma.proyects.taskflow.data.repository.TaskRepository
import com.emma.proyects.taskflow.presentation.screens.TaskUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val tasks = repository.getTasks()
        val theme = repository.getThemeMode()
        updateDashboardState(tasks = tasks, filter = TaskFilter.ALL, themeMode = theme)
    }

    fun onToggleTaskCompletion(taskId: String) {
        val updatedTasks = repository.toggleTaskCompletion(taskId)
        updateDashboardState(
            tasks = updatedTasks,
            filter = _uiState.value.selectedFilter
        )
    }

    fun onFilterSelected(filter: TaskFilter) {
        updateDashboardState(
            tasks = _uiState.value.allTasks,
            filter = filter
        )
    }

    fun onDeleteTask(taskId: String) {
        val updatedTasks = repository.deleteTask(taskId)
        updateDashboardState(
            tasks = updatedTasks,
            filter = _uiState.value.selectedFilter
        )
    }

    fun onRestoreSeedTasks() {
        val restoredTasks = repository.restoreSeedTasks()
        updateDashboardState(
            tasks = restoredTasks,
            filter = TaskFilter.ALL
        )
    }

    fun onCycleThemeMode() {
        val nextMode = when (_uiState.value.themeMode) {
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
        }
        val savedMode = repository.setThemeMode(nextMode)
        _uiState.update { currentState ->
            currentState.copy(themeMode = savedMode)
        }
    }

    private fun updateDashboardState(
        tasks: List<Task>,
        filter: TaskFilter,
        themeMode: ThemeMode = _uiState.value.themeMode
    ) {
        val filtered = when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
            TaskFilter.HIGH -> tasks.filter { it.priority == TaskPriority.HIGH }
            TaskFilter.MEDIUM -> tasks.filter { it.priority == TaskPriority.MEDIUM }
            TaskFilter.LOW -> tasks.filter { it.priority == TaskPriority.LOW }
        }

        val total = tasks.size
        val completed = tasks.count { it.isCompleted }
        val pending = total - completed
        val progressFloat = if (total > 0) completed.toFloat() / total.toFloat() else 0f
        val progressPercent = (progressFloat * 100).roundToInt()

        _uiState.update { currentState ->
            currentState.copy(
                allTasks = tasks,
                visibleTasks = filtered,
                selectedFilter = filter,
                totalCount = total,
                pendingCount = pending,
                completedCount = completed,
                completionProgress = progressFloat,
                completionPercentInt = progressPercent,
                themeMode = themeMode
            )
        }
    }
}