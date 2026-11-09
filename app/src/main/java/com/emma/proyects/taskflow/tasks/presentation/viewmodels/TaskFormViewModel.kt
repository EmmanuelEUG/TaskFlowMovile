package com.emma.proyects.taskflow.tasks.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.core.presentation.toUiText
import com.emma.proyects.taskflow.tasks.domain.model.Importance
import com.emma.proyects.taskflow.tasks.domain.model.RecurrenceUnit
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.tasks.domain.repository.TaskRepository
import com.emma.proyects.taskflow.tasks.domain.util.TaskDateFormatter
import com.emma.proyects.taskflow.tasks.presentation.screens.TaskFormUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

class TaskFormViewModel(
    private val taskId: String?,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TaskFormUiState(
            editingTaskId = taskId,
            isLoadingInitial = taskId != null
        )
    )
    val uiState: StateFlow<TaskFormUiState> = _uiState.asStateFlow()

    init {
        if (taskId != null) {
            loadTaskForEdit()
        }
    }

    fun initializeDefaultArea(defaultArea: String) {
        _uiState.update { state ->
            if (!state.isEditing && !state.hasInitializedDefaultArea && state.area.isBlank()) {
                state.copy(area = defaultArea, hasInitializedDefaultArea = true)
            } else {
                state.copy(hasInitializedDefaultArea = true)
            }
        }
    }

    fun loadTaskForEdit() {
        val targetId = taskId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInitial = true, loadErrorMessage = null) }
            taskRepository.getTaskById(targetId).fold(
                onSuccess = { task ->
                    val (datePart, timePart) = TaskDateFormatter.splitDueDateParts(task.dueDateFormatted)
                    val zone = ZoneId.systemDefault()
                    val existingZdt = task.dueDateMillis?.let { Instant.ofEpochMilli(it).atZone(zone) }
                    val utcMidnightMillis = existingZdt?.toLocalDate()
                        ?.atStartOfDay(ZoneOffset.UTC)
                        ?.toInstant()
                        ?.toEpochMilli()

                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            loadErrorMessage = null,
                            title = task.title,
                            description = task.description,
                            importance = task.importance,
                            priorityLevel = task.priorityLevel,
                            area = task.area,
                            hasInitializedDefaultArea = true,
                            selectedDate = datePart,
                            selectedDateMillis = utcMidnightMillis,
                            selectedTime = timePart,
                            selectedHour = existingZdt?.hour,
                            selectedMinute = existingZdt?.minute,
                            isCompleted = task.isCompleted,
                            createdDateFormatted = task.createdDateFormatted.ifBlank { null },
                            completedAtMillis = task.completedAtMillis,
                            isRecurring = task.isRecurring,
                            recurrenceIntervalText = task.recurrenceInterval.toString(),
                            recurrenceUnit = task.recurrenceUnit,
                            recurrenceDaysOfWeek = task.recurrenceDaysOfWeek,
                            recurrenceDaysOfMonth = task.recurrenceDaysOfMonth
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            loadErrorMessage = error.toUiText(R.string.error_load_task_detail)
                        )
                    }
                }
            )
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, titleError = null, saveErrorMessage = null) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, saveErrorMessage = null) }
    }

    fun onImportanceChange(value: Importance) {
        _uiState.update { it.copy(importance = value) }
    }

    fun onPriorityChange(value: Int) {
        _uiState.update {
            it.copy(
                priorityLevel = value.coerceIn(TaskConstants.MIN_PRIORITY, TaskConstants.MAX_PRIORITY)
            )
        }
    }

    fun onAreaChange(value: String) {
        _uiState.update {
            it.copy(
                area = value,
                hasInitializedDefaultArea = true,
                areaError = null,
                saveErrorMessage = null
            )
        }
    }

    fun onDateSelected(dateText: String, utcDateMillis: Long?) {
        _uiState.update {
            it.copy(
                selectedDate = dateText,
                selectedDateMillis = utcDateMillis ?: it.selectedDateMillis
            )
        }
    }

    fun onTimeSelected(timeText: String, hour: Int?, minute: Int?) {
        _uiState.update {
            it.copy(
                selectedTime = timeText,
                selectedHour = hour ?: it.selectedHour,
                selectedMinute = minute ?: it.selectedMinute
            )
        }
    }

    fun onClearDateTime() {
        _uiState.update {
            it.copy(
                selectedDate = "",
                selectedDateMillis = null,
                selectedTime = "",
                selectedHour = null,
                selectedMinute = null
            )
        }
    }

    fun onRecurringToggle(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isRecurring = enabled,
                recurrenceIntervalError = null,
                recurrenceIntervalText = if (enabled && it.recurrenceIntervalText.isBlank()) {
                    TaskConstants.DEFAULT_RECURRENCE_INTERVAL_TEXT
                } else {
                    it.recurrenceIntervalText
                }
            )
        }
    }

    fun onRecurrenceIntervalChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _uiState.update {
            it.copy(
                recurrenceIntervalText = digitsOnly,
                recurrenceIntervalError = null
            )
        }
    }

    fun onRecurrenceUnitChange(unit: RecurrenceUnit) {
        _uiState.update { it.copy(recurrenceUnit = unit) }
    }

    fun onRecurrenceDaysOfWeekChange(days: List<DayOfWeek>) {
        _uiState.update { it.copy(recurrenceDaysOfWeek = days) }
    }

    fun onRecurrenceDaysOfMonthChange(days: List<Int>) {
        _uiState.update { it.copy(recurrenceDaysOfMonth = days) }
    }

    fun onSaveTask() {
        val current = _uiState.value
        if (current.isSaving) return

        val trimmedTitle = current.title.trim()
        val trimmedArea = current.area.trim()
        val titleError = when {
            trimmedTitle.isBlank() -> UiText.StringResource(R.string.validation_title_required)
            trimmedTitle.length < TaskConstants.MIN_TITLE_LENGTH -> UiText.PluralResource(
                R.plurals.validation_title_min_length,
                TaskConstants.MIN_TITLE_LENGTH
            )
            else -> null
        }
        val areaError = if (trimmedArea.isBlank()) {
            UiText.StringResource(R.string.validation_area_required)
        } else {
            null
        }

        val intervalValue = current.recurrenceIntervalText.toIntOrNull()
        val recurrenceError = if (
            current.isRecurring &&
            (intervalValue == null || intervalValue < TaskConstants.MIN_RECURRENCE_INTERVAL)
        ) {
            UiText.StringResource(
                R.string.validation_recurrence_interval_min,
                TaskConstants.MIN_RECURRENCE_INTERVAL
            )
        } else {
            null
        }

        if (titleError != null || areaError != null || recurrenceError != null) {
            _uiState.update {
                it.copy(
                    titleError = titleError,
                    areaError = areaError,
                    recurrenceIntervalError = recurrenceError,
                    saveErrorMessage = null
                )
            }
            return
        }

        val validInterval = (intervalValue ?: TaskConstants.MIN_RECURRENCE_INTERVAL)
            .coerceAtLeast(TaskConstants.MIN_RECURRENCE_INTERVAL)
        val taskToPersist = buildTaskFromForm(current, trimmedTitle, trimmedArea, validInterval)

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveErrorMessage = null) }
            val result = if (current.isEditing) {
                taskRepository.updateTask(taskToPersist)
            } else {
                taskRepository.createTask(taskToPersist)
            }

            result.fold(
                onSuccess = {
                    val message = UiText.StringResource(
                        if (current.isEditing) R.string.msg_task_updated else R.string.msg_task_created
                    )
                    _uiState.update {
                        it.copy(isSaving = false, savedSuccessMessage = message)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveErrorMessage = error.toUiText(R.string.error_save_task)
                        )
                    }
                }
            )
        }
    }

    fun consumeSavedSuccessMessage() {
        _uiState.update { it.copy(savedSuccessMessage = null) }
    }

    private fun buildTaskFromForm(
        form: TaskFormUiState,
        trimmedTitle: String,
        trimmedArea: String,
        validInterval: Int
    ): Task {
        val zone = ZoneId.systemDefault()
        val localDate: LocalDate? = when {
            form.selectedDateMillis != null -> {
                Instant.ofEpochMilli(form.selectedDateMillis).atZone(ZoneOffset.UTC).toLocalDate()
            }
            form.selectedDate.isNotBlank() -> TaskDateFormatter.parseSpanishShortDate(form.selectedDate)
            form.selectedTime.isNotBlank() -> LocalDate.now(zone)
            else -> null
        }

        val resolvedHour = form.selectedHour
            ?: TaskDateFormatter.parseHour(form.selectedTime)
            ?: TaskConstants.DEFAULT_DUE_HOUR
        val resolvedMinute = form.selectedMinute
            ?: TaskDateFormatter.parseMinute(form.selectedTime)
            ?: TaskConstants.DEFAULT_DUE_MINUTE

        val computedDueMillis: Long? = localDate?.let { date ->
            LocalDateTime.of(date, LocalTime.of(resolvedHour, resolvedMinute))
                .atZone(zone)
                .toInstant()
                .toEpochMilli()
        }

        val combinedDueDate = TaskDateFormatter.combineDueDate(form.selectedDate, form.selectedTime)
        val todayFormatted = TaskDateFormatter.formatTodayShortDate()
        val nowMillis = System.currentTimeMillis()
        val isNowOverdue = computedDueMillis != null && computedDueMillis < nowMillis && !form.isCompleted

        return Task(
            id = form.editingTaskId.orEmpty(),
            title = trimmedTitle,
            description = form.description.trim(),
            area = trimmedArea,
            importance = form.importance,
            priorityLevel = form.priorityLevel.coerceIn(
                TaskConstants.MIN_PRIORITY,
                TaskConstants.MAX_PRIORITY
            ),
            dueDateFormatted = combinedDueDate,
            dueDateMillis = computedDueMillis,
            isOverdue = isNowOverdue,
            isCompleted = form.isCompleted,
            isRecurring = form.isRecurring,
            recurrenceInterval = validInterval,
            recurrenceUnit = form.recurrenceUnit,
            createdDateFormatted = form.createdDateFormatted?.ifBlank { null } ?: todayFormatted,
            completedAtMillis = form.completedAtMillis,
            recurrenceDaysOfWeek = form.recurrenceDaysOfWeek,
            recurrenceDaysOfMonth = form.recurrenceDaysOfMonth
        )
    }
}
