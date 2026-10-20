package com.emma.proyects.taskflow.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.emma.proyects.taskflow.data.model.AppRoute
import com.emma.proyects.taskflow.data.model.Importance
import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.ThemeMode
import com.emma.proyects.taskflow.data.repository.TaskRepository
import com.emma.proyects.taskflow.presentation.screens.TaskUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val tasks = repository.getTasks()
        val theme = repository.getThemeMode()
        updateState(tasks = tasks, themeMode = theme)
    }

    fun onToggleTask(taskId: String) {
        val updatedTasks = repository.toggleTask(taskId)
        updateState(tasks = updatedTasks)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        updateState(tasks = _uiState.value.allTasks)
    }

    fun onAreaSelected(area: String) {
        _uiState.update { it.copy(selectedArea = area) }
        updateState(tasks = _uiState.value.allTasks)
    }

    fun onImportanceSelected(importance: Importance?) {
        val newImportance = if (_uiState.value.selectedImportance == importance) null else importance
        _uiState.update { it.copy(selectedImportance = newImportance) }
        updateState(tasks = _uiState.value.allTasks)
    }

    fun onNavigate(route: AppRoute) {
        _uiState.update { it.copy(currentRoute = route) }
    }

    fun onRestoreSeed() {
        val restored = repository.restoreSeedTasks()
        updateState(tasks = restored)
    }

    fun onCycleTheme() {
        val next = when (_uiState.value.themeMode) {
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.DARK
        }
        val saved = repository.setThemeMode(next)
        _uiState.update { it.copy(themeMode = saved) }
    }

    private fun updateState(
        tasks: List<Task>,
        themeMode: ThemeMode = _uiState.value.themeMode
    ) {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val area = _uiState.value.selectedArea
        val importance = _uiState.value.selectedImportance

        val filtered = tasks.filter { task ->
            val matchesQuery = query.isEmpty() ||
                    task.title.lowercase().contains(query) ||
                    task.description.lowercase().contains(query)
            val matchesArea = area == "Todas" || task.area.equals(area, ignoreCase = true)
            val matchesImportance = importance == null || task.importance == importance

            matchesQuery && matchesArea && matchesImportance
        }

        val overdue = filtered.filter { it.isOverdue && !it.isCompleted }
        val pending = filtered.filter { !it.isOverdue && !it.isCompleted }
        val completed = filtered.filter { it.isCompleted }.take(6)

        _uiState.update {
            it.copy(
                allTasks = tasks,
                overdueTasks = overdue,
                pendingTasks = pending,
                completedTasks = completed,
                themeMode = themeMode
            )
        }
    }
}