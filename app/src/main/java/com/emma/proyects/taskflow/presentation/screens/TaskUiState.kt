package com.emma.proyects.taskflow.presentation.screens

import com.emma.proyects.taskflow.data.model.AppRoute
import com.emma.proyects.taskflow.data.model.Importance
import com.emma.proyects.taskflow.data.model.RecurrenceUnit
import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.ThemeMode

data class TaskFormUiState(
    val editingTaskId: String? = null,
    val title: String = "",
    val showTitleError: Boolean = false,
    val description: String = "",
    val importance: Importance = Importance.MEDIUM,
    val priorityLevel: Int = 3,
    val area: String = "Escuela",
    val selectedDate: String = "",
    val selectedTime: String = "",
    val isRecurring: Boolean = false,
    val recurrenceIntervalText: String = "1",
    val recurrenceUnit: RecurrenceUnit = RecurrenceUnit.DAYS
) {
    val isEditing: Boolean get() = editingTaskId != null
}

data class TaskUiState(
    val allTasks: List<Task> = emptyList(),
    val overdueTasks: List<Task> = emptyList(),
    val pendingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val selectedArea: String = "Todas",
    val selectedImportance: Importance? = null,
    val sortDescendingByPriority: Boolean = true,
    val currentRoute: AppRoute = AppRoute.HOME,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val selectedTaskForDetail: Task? = null,
    val isFormOpen: Boolean = false,
    val formState: TaskFormUiState = TaskFormUiState(),
    val taskPendingDeletion: Task? = null
) {
    val totalTasksCount: Int get() = allTasks.size
    val completedTasksCount: Int get() = allTasks.count { it.isCompleted }
    val overdueTasksCount: Int get() = allTasks.count { it.isOverdue && !it.isCompleted }

    val punctualityRate: Int
        get() {
            if (completedTasksCount == 0) return 100
            val completedOnTime = allTasks.count { it.isCompleted && !it.isOverdue }
            return ((completedOnTime.toFloat() / completedTasksCount.toFloat()) * 100).toInt()
        }

    val areaDistribution: Map<String, Int>
        get() = allTasks.groupBy { it.area }.mapValues { it.value.size }
}