package com.emma.proyects.taskflow.tasks.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.auth.domain.repository.AuthRepository
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.core.presentation.toUiText
import com.emma.proyects.taskflow.tasks.domain.model.DueDateFilter
import com.emma.proyects.taskflow.tasks.domain.model.Importance
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.tasks.domain.repository.TaskRepository
import com.emma.proyects.taskflow.tasks.presentation.screens.RecurrenceFilter
import com.emma.proyects.taskflow.tasks.presentation.screens.TasksHomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TasksHomeUiState())
    val uiState: StateFlow<TasksHomeUiState> = _uiState.asStateFlow()

    init {
        syncUserFromStorage()
        loadTasks()
    }

    fun syncUserFromStorage() {
        val user = authRepository.getCachedUser()
        _uiState.update {
            it.copy(
                userName = user?.name.orEmpty(),
                userEmail = user?.email.orEmpty()
            )
        }
    }

    fun loadTasks(isPullRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { state ->
                if (isPullRefresh) {
                    state.copy(isRefreshing = true, errorMessage = null)
                } else {
                    state.copy(
                        isLoading = state.allTasks.isEmpty(),
                        isRefreshing = state.allTasks.isNotEmpty(),
                        errorMessage = null
                    )
                }
            }

            taskRepository.getTasks().fold(
                onSuccess = { tasks ->
                    applyFiltersAndUpdate(
                        tasks = tasks,
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.toUiText(R.string.error_load_tasks)
                        )
                    }
                }
            )
        }
    }

    fun onToggleTask(task: Task) {
        if (_uiState.value.isMutating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true) }
            val nextCompleted = !task.isCompleted
            taskRepository.toggleTaskCompletion(task.id, nextCompleted).fold(
                onSuccess = {
                    val feedback = if (nextCompleted) {
                        UiText.StringResource(R.string.msg_task_completed_named, task.title)
                    } else {
                        UiText.StringResource(R.string.msg_task_reopened)
                    }
                    refreshAfterMutation(feedback)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            userMessage = error.toUiText(R.string.error_update_task)
                        )
                    }
                }
            )
        }
    }

    fun onRestoreSeedTasks() {
        if (_uiState.value.isMutating) return
        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true) }
            taskRepository.restoreSeedTasks().fold(
                onSuccess = { tasks ->
                    applyFiltersAndUpdate(
                        tasks = tasks,
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = null,
                        userMessage = UiText.StringResource(R.string.msg_seed_loaded)
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            userMessage = error.toUiText(R.string.error_load_seed)
                        )
                    }
                }
            )
        }
    }

    private suspend fun refreshAfterMutation(message: UiText) {
        taskRepository.getTasks().fold(
            onSuccess = { tasks ->
                applyFiltersAndUpdate(
                    tasks = tasks,
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = null,
                    userMessage = message
                )
            },
            onFailure = { error ->
                _uiState.update {
                    it.copy(
                        isMutating = false,
                        userMessage = error.toUiText(R.string.error_reload_after_mutation)
                    )
                }
            }
        )
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFiltersAndUpdate(tasks = _uiState.value.allTasks)
    }

    fun onAreaSelected(area: String?) {
        val nextArea = if (area != null && _uiState.value.selectedArea.equals(area, ignoreCase = true)) {
            null
        } else {
            area
        }
        _uiState.update { it.copy(selectedArea = nextArea) }
        applyFiltersAndUpdate(tasks = _uiState.value.allTasks)
    }

    fun onImportanceSelected(importance: Importance?) {
        val next = if (_uiState.value.selectedImportance == importance) null else importance
        _uiState.update { it.copy(selectedImportance = next) }
        applyFiltersAndUpdate(tasks = _uiState.value.allTasks)
    }

    fun onDueDateFilterSelected(filter: DueDateFilter) {
        val nextFilter = if (_uiState.value.selectedDueDateFilter == filter && filter != DueDateFilter.ALL) {
            DueDateFilter.ALL
        } else {
            filter
        }
        _uiState.update { it.copy(selectedDueDateFilter = nextFilter) }
        applyFiltersAndUpdate(tasks = _uiState.value.allTasks)
    }

    fun onRecurrenceFilterSelected(filter: RecurrenceFilter) {
        val next = if (_uiState.value.selectedRecurrenceFilter == filter && filter != RecurrenceFilter.ALL) {
            RecurrenceFilter.ALL
        } else {
            filter
        }
        _uiState.update { it.copy(selectedRecurrenceFilter = next) }
        applyFiltersAndUpdate(tasks = _uiState.value.allTasks)
    }

    fun onToggleSortOrder() {
        _uiState.update { it.copy(sortDescendingByPriority = !it.sortDescendingByPriority) }
        applyFiltersAndUpdate(tasks = _uiState.value.allTasks)
    }

    fun onClearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedArea = null,
                selectedImportance = null,
                selectedDueDateFilter = DueDateFilter.ALL,
                selectedRecurrenceFilter = RecurrenceFilter.ALL
            )
        }
        applyFiltersAndUpdate(tasks = _uiState.value.allTasks)
    }

    fun showUserMessage(message: UiText) {
        _uiState.update { it.copy(userMessage = message) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    private fun applyFiltersAndUpdate(
        tasks: List<Task>,
        isLoading: Boolean = _uiState.value.isLoading,
        isRefreshing: Boolean = _uiState.value.isRefreshing,
        errorMessage: UiText? = _uiState.value.errorMessage,
        userMessage: UiText? = _uiState.value.userMessage
    ) {
        val current = _uiState.value
        val query = current.searchQuery.trim().lowercase()
        val area = current.selectedArea
        val importance = current.selectedImportance
        val dueDateFilter = current.selectedDueDateFilter
        val recurrenceFilter = current.selectedRecurrenceFilter
        val sortDesc = current.sortDescendingByPriority

        val filtered = tasks.filter { task ->
            val matchesQuery = query.isEmpty() ||
                task.title.lowercase().contains(query) ||
                task.description.lowercase().contains(query)
            val matchesArea = area == null || task.area.equals(area, ignoreCase = true)
            val matchesImportance = importance == null || task.importance == importance
            val matchesDueDate = matchesDueDateFilter(task, dueDateFilter)
            val matchesRecurrence = when (recurrenceFilter) {
                RecurrenceFilter.ALL -> true
                RecurrenceFilter.RECURRING_ONLY -> task.isRecurring
                RecurrenceFilter.NON_RECURRING_ONLY -> !task.isRecurring
            }
            matchesQuery && matchesArea && matchesImportance && matchesDueDate && matchesRecurrence
        }.let { list ->
            if (sortDesc) {
                list.sortedWith(compareByDescending<Task> { it.priorityLevel }.thenBy { it.title })
            } else {
                list.sortedWith(compareBy<Task> { it.priorityLevel }.thenByDescending { it.title })
            }
        }

        val overdue = filtered.filter { it.isOverdue && !it.isCompleted }
        val pending = filtered.filter { !it.isOverdue && !it.isCompleted }
        val completed = filtered.filter { it.isCompleted }.take(TaskConstants.RECENT_COMPLETED_LIMIT)
        val weeklyCounts = computeWeeklyCompletedCounts(tasks)

        _uiState.update {
            it.copy(
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                isMutating = false,
                errorMessage = errorMessage,
                userMessage = userMessage,
                allTasks = tasks,
                overdueTasks = overdue,
                pendingTasks = pending,
                completedTasks = completed,
                weeklyCompletedCounts = weeklyCounts
            )
        }
    }

    private fun computeWeeklyCompletedCounts(tasks: List<Task>): List<Int> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = monday.plusDays(TaskConstants.WEEK_LAST_DAY_OFFSET)
        val counts = MutableList(TaskConstants.DAYS_IN_WEEK) { 0 }

        tasks.filter { it.isCompleted }.forEach { task ->
            val completedDate = task.completedAtMillis?.let { millis ->
                Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
            } ?: today

            if (!completedDate.isBefore(monday) && !completedDate.isAfter(sunday)) {
                val index = (completedDate.dayOfWeek.value - 1)
                    .coerceIn(TaskConstants.MIN_LIST_INDEX, TaskConstants.WEEK_MAX_DAY_INDEX)
                counts[index] = counts[index] + 1
            }
        }
        return counts
    }

    private enum class BoundedDueWindow {
        TODAY,
        THIS_WEEK,
        NEXT_WEEK,
        LATER
    }

    private fun matchesDueDateFilter(task: Task, filter: DueDateFilter): Boolean {
        val zone = ZoneId.systemDefault()
        val effectiveMillis = task.dueDateMillis
        return when (filter) {
            DueDateFilter.ALL -> true
            DueDateFilter.NO_DATE -> effectiveMillis == null && task.dueDateFormatted.isNullOrBlank()
            DueDateFilter.TODAY -> matchesBoundedWindow(effectiveMillis, BoundedDueWindow.TODAY, zone)
            DueDateFilter.THIS_WEEK -> matchesBoundedWindow(effectiveMillis, BoundedDueWindow.THIS_WEEK, zone)
            DueDateFilter.NEXT_WEEK -> matchesBoundedWindow(effectiveMillis, BoundedDueWindow.NEXT_WEEK, zone)
            DueDateFilter.LATER -> matchesBoundedWindow(effectiveMillis, BoundedDueWindow.LATER, zone)
        }
    }

    private fun matchesBoundedWindow(
        effectiveMillis: Long?,
        window: BoundedDueWindow,
        zone: ZoneId
    ): Boolean {
        if (effectiveMillis == null) return false

        val taskDateTime = Instant.ofEpochMilli(effectiveMillis).atZone(zone).toLocalDateTime()
        val today = LocalDate.now(zone)

        val startOfToday = today.atStartOfDay()
        val endOfToday = today.atTime(LocalTime.MAX)

        val sundayThisWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val saturdayThisWeek = sundayThisWeek.plusDays(TaskConstants.WEEK_LAST_DAY_OFFSET)
        val startOfThisWeek = sundayThisWeek.atStartOfDay()
        val endOfThisWeek = saturdayThisWeek.atTime(LocalTime.MAX)

        val sundayNextWeek = sundayThisWeek.plusWeeks(TaskConstants.PRIORITY_FACTOR_ONE_WEEK)
        val saturdayNextWeek = sundayNextWeek.plusDays(TaskConstants.WEEK_LAST_DAY_OFFSET)
        val startOfNextWeek = sundayNextWeek.atStartOfDay()
        val endOfNextWeek = saturdayNextWeek.atTime(LocalTime.MAX)

        return when (window) {
            BoundedDueWindow.TODAY -> !taskDateTime.isBefore(startOfToday) && !taskDateTime.isAfter(endOfToday)
            BoundedDueWindow.THIS_WEEK -> !taskDateTime.isBefore(startOfThisWeek) && !taskDateTime.isAfter(endOfThisWeek)
            BoundedDueWindow.NEXT_WEEK -> !taskDateTime.isBefore(startOfNextWeek) && !taskDateTime.isAfter(endOfNextWeek)
            BoundedDueWindow.LATER -> taskDateTime.isAfter(endOfNextWeek)
        }
    }
}
