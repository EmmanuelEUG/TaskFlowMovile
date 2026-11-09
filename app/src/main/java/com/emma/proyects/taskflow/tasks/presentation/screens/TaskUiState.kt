package com.emma.proyects.taskflow.tasks.presentation.screens

import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.tasks.domain.model.DueDateFilter
import com.emma.proyects.taskflow.tasks.domain.model.Importance
import com.emma.proyects.taskflow.tasks.domain.model.RecurrenceUnit
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import java.time.DayOfWeek

enum class RecurrenceFilter {
    ALL, RECURRING_ONLY, NON_RECURRING_ONLY
}

data class TasksHomeUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isMutating: Boolean = false,
    val errorMessage: UiText? = null,
    val userMessage: UiText? = null,
    val allTasks: List<Task> = emptyList(),
    val overdueTasks: List<Task> = emptyList(),
    val pendingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val selectedArea: String? = null,
    val selectedImportance: Importance? = null,
    val selectedDueDateFilter: DueDateFilter = DueDateFilter.ALL,
    val selectedRecurrenceFilter: RecurrenceFilter = RecurrenceFilter.ALL,
    val sortDescendingByPriority: Boolean = true,
    val weeklyCompletedCounts: List<Int> = List(TaskConstants.DAYS_IN_WEEK) { 0 }
) {
    val activeTodoCount: Int get() = pendingTasks.size + overdueTasks.size
    val totalTasksCount: Int get() = allTasks.size
    val completedTasksCount: Int get() = allTasks.count { it.isCompleted }
    val overdueTasksCount: Int get() = allTasks.count { it.isOverdue && !it.isCompleted }

    val hasActiveFilters: Boolean
        get() = searchQuery.isNotBlank() ||
            selectedArea != null ||
            selectedImportance != null ||
            selectedDueDateFilter != DueDateFilter.ALL ||
            selectedRecurrenceFilter != RecurrenceFilter.ALL

    val isFilteredEmpty: Boolean
        get() = allTasks.isNotEmpty() &&
            overdueTasks.isEmpty() &&
            pendingTasks.isEmpty() &&
            completedTasks.isEmpty()

    val punctualityRate: Int
        get() {
            if (completedTasksCount == 0) return TaskConstants.PERCENT_MIN
            val completedOnTime = allTasks.count { it.isCompleted && !it.isOverdue }
            return ((completedOnTime.toFloat() / completedTasksCount.toFloat()) * TaskConstants.PERCENT_MAX).toInt()
        }

    val areaDistribution: Map<String, Int>
        get() = allTasks.groupBy { it.area }.mapValues { it.value.size }
}

data class TaskDetailUiState(
    val isLoading: Boolean = true,
    val isMutating: Boolean = false,
    val task: Task? = null,
    val errorMessage: UiText? = null,
    val userMessage: UiText? = null,
    val showDeleteDialog: Boolean = false,
    val deletedFeedback: UiText? = null
)

data class TaskFormUiState(
    val editingTaskId: String? = null,
    val isLoadingInitial: Boolean = false,
    val loadErrorMessage: UiText? = null,
    val isSaving: Boolean = false,
    val saveErrorMessage: UiText? = null,
    val title: String = "",
    val titleError: UiText? = null,
    val description: String = "",
    val importance: Importance = Importance.MEDIUM,
    val priorityLevel: Int = TaskConstants.DEFAULT_PRIORITY,
    val area: String = "",
    val hasInitializedDefaultArea: Boolean = false,
    val areaError: UiText? = null,
    val selectedDate: String = "",
    val selectedDateMillis: Long? = null,
    val selectedTime: String = "",
    val selectedHour: Int? = null,
    val selectedMinute: Int? = null,
    val isCompleted: Boolean = false,
    val createdDateFormatted: String? = null,
    val completedAtMillis: Long? = null,
    val isRecurring: Boolean = false,
    val recurrenceIntervalText: String = TaskConstants.DEFAULT_RECURRENCE_INTERVAL_TEXT,
    val recurrenceIntervalError: UiText? = null,
    val recurrenceUnit: RecurrenceUnit = RecurrenceUnit.DAYS,
    val recurrenceDaysOfWeek: List<DayOfWeek> = emptyList(),
    val recurrenceDaysOfMonth: List<Int> = emptyList(),
    val savedSuccessMessage: UiText? = null
) {
    val isEditing: Boolean get() = editingTaskId != null
}
