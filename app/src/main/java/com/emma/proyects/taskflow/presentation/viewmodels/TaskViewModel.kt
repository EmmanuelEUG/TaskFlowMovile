package com.emma.proyects.taskflow.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.emma.proyects.taskflow.data.model.AppRoute
import com.emma.proyects.taskflow.data.model.Importance
import com.emma.proyects.taskflow.data.model.RecurrenceUnit
import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.ThemeMode
import com.emma.proyects.taskflow.data.repository.TaskRepository
import com.emma.proyects.taskflow.presentation.screens.TaskFormUiState
import com.emma.proyects.taskflow.presentation.screens.TaskUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        val next = if (_uiState.value.selectedImportance == importance) null else importance
        _uiState.update { it.copy(selectedImportance = next) }
        updateState(tasks = _uiState.value.allTasks)
    }

    fun onToggleSortOrder() {
        _uiState.update { it.copy(sortDescendingByPriority = !it.sortDescendingByPriority) }
        updateState(tasks = _uiState.value.allTasks)
    }

    fun onNavigate(route: AppRoute) {
        _uiState.update {
            it.copy(
                currentRoute = route,
                selectedTaskForDetail = null,
                isFormOpen = false
            )
        }
    }


    fun onOpenTaskDetail(task: Task) {
        _uiState.update { it.copy(selectedTaskForDetail = task) }
    }

    fun onCloseTaskDetail() {
        _uiState.update { it.copy(selectedTaskForDetail = null) }
    }

    fun onOpenNewTaskForm() {
        _uiState.update {
            it.copy(
                isFormOpen = true,
                formState = TaskFormUiState()
            )
        }
    }

    fun onOpenEditTaskForm(task: Task) {
        val parts = task.dueDateFormatted?.split(",")?.map { it.trim() } ?: emptyList()
        val datePart = parts.getOrNull(0) ?: ""
        val timePart = parts.getOrNull(1) ?: ""

        _uiState.update {
            it.copy(
                isFormOpen = true,
                formState = TaskFormUiState(
                    editingTaskId = task.id,
                    title = task.title,
                    description = task.description,
                    importance = task.importance,
                    priorityLevel = task.priorityLevel,
                    area = task.area,
                    selectedDate = datePart,
                    selectedTime = timePart,
                    isRecurring = task.isRecurring,
                    recurrenceIntervalText = task.recurrenceInterval.toString(),
                    recurrenceUnit = task.recurrenceUnit
                )
            )
        }
    }

    fun onCloseForm() {
        _uiState.update { it.copy(isFormOpen = false) }
    }


    fun onFormTitleChange(value: String) {
        _uiState.update {
            it.copy(
                formState = it.formState.copy(
                    title = value,
                    showTitleError = false
                )
            )
        }
    }

    fun onFormDescriptionChange(value: String) {
        _uiState.update { it.copy(formState = it.formState.copy(description = value)) }
    }

    fun onFormImportanceChange(value: Importance) {
        _uiState.update { it.copy(formState = it.formState.copy(importance = value)) }
    }

    fun onFormPriorityChange(value: Int) {
        _uiState.update { it.copy(formState = it.formState.copy(priorityLevel = value)) }
    }

    fun onFormAreaChange(value: String) {
        _uiState.update { it.copy(formState = it.formState.copy(area = value)) }
    }

    fun onFormDateSelected(date: String) {
        _uiState.update { it.copy(formState = it.formState.copy(selectedDate = date)) }
    }

    fun onFormTimeSelected(time: String) {
        _uiState.update { it.copy(formState = it.formState.copy(selectedTime = time)) }
    }

    fun onFormRecurringToggle(enabled: Boolean) {
        _uiState.update { it.copy(formState = it.formState.copy(isRecurring = enabled)) }
    }

    fun onFormRecurrenceIntervalChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _uiState.update {
            it.copy(formState = it.formState.copy(recurrenceIntervalText = digitsOnly))
        }
    }

    fun onFormRecurrenceUnitChange(unit: RecurrenceUnit) {
        _uiState.update { it.copy(formState = it.formState.copy(recurrenceUnit = unit)) }
    }

    fun onSaveTaskForm() {
        val form = _uiState.value.formState
        if (form.title.isBlank()) {
            _uiState.update {
                it.copy(formState = form.copy(showTitleError = true))
            }
            return
        }

        val combinedDueDate = when {
            form.selectedDate.isNotBlank() && form.selectedTime.isNotBlank() ->
                "${form.selectedDate}, ${form.selectedTime}"
            form.selectedDate.isNotBlank() -> form.selectedDate
            form.selectedTime.isNotBlank() -> form.selectedTime
            else -> null
        }

        val existingTask = _uiState.value.allTasks.find { it.id == form.editingTaskId }
        val todayFormatted = SimpleDateFormat("d 'de' MMMM", Locale.forLanguageTag("es-MX")).format(Date())

        val taskToSave = Task(
            id = existingTask?.id ?: System.currentTimeMillis().toString(),
            title = form.title.trim(),
            description = form.description.trim(),
            area = form.area.trim().ifBlank { "General" },
            importance = form.importance,
            priorityLevel = form.priorityLevel,
            dueDateFormatted = combinedDueDate,
            isOverdue = existingTask?.isOverdue ?: false,
            isCompleted = existingTask?.isCompleted ?: false,
            isRecurring = form.isRecurring,
            recurrenceInterval = form.recurrenceIntervalText.toIntOrNull()?.coerceAtLeast(1) ?: 1,
            recurrenceUnit = form.recurrenceUnit,
            createdDateFormatted = existingTask?.createdDateFormatted ?: todayFormatted
        )

        val updatedTasks = repository.upsertTask(taskToSave)
        _uiState.update { it.copy(isFormOpen = false) }
        updateState(tasks = updatedTasks)
    }


    fun onRequestDeleteTask(task: Task) {
        _uiState.update { it.copy(taskPendingDeletion = task) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(taskPendingDeletion = null) }
    }

    fun onConfirmDeleteTask() {
        val target = _uiState.value.taskPendingDeletion ?: return
        val updatedTasks = repository.deleteTask(target.id)
        _uiState.update {
            it.copy(
                taskPendingDeletion = null,
                selectedTaskForDetail = null,
                isFormOpen = false
            )
        }
        updateState(tasks = updatedTasks)
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

    fun onRestoreSeed() {
        val restored = repository.restoreSeedTasks()
        updateState(tasks = restored)
    }

    fun onThemeModeSelected(mode: ThemeMode) {
        val saved = repository.setThemeMode(mode)
        _uiState.update { it.copy(themeMode = saved) }
    }

    private fun updateState(
        tasks: List<Task>,
        themeMode: ThemeMode = _uiState.value.themeMode
    ) {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val area = _uiState.value.selectedArea
        val importance = _uiState.value.selectedImportance
        val sortDesc = _uiState.value.sortDescendingByPriority

        val filtered = tasks.filter { task ->
            val matchesQuery = query.isEmpty() ||
                    task.title.lowercase().contains(query) ||
                    task.description.lowercase().contains(query)
            val matchesArea = area == "Todas" || task.area.equals(area, ignoreCase = true)
            val matchesImportance = importance == null || task.importance == importance

            matchesQuery && matchesArea && matchesImportance
        }.let { list ->
            if (sortDesc) {
                list.sortedWith(compareByDescending<Task> { it.priorityLevel }.thenBy { it.title })
            } else {
                list.sortedWith(compareBy<Task> { it.priorityLevel }.thenByDescending { it.title })
            }
        }

        val overdue = filtered.filter { it.isOverdue && !it.isCompleted }
        val pending = filtered.filter { !it.isOverdue && !it.isCompleted }
        val completed = filtered.filter { it.isCompleted }.take(6)

        val refreshedDetail = _uiState.value.selectedTaskForDetail?.let { currentDetail ->
            tasks.find { it.id == currentDetail.id }
        }

        _uiState.update {
            it.copy(
                allTasks = tasks,
                overdueTasks = overdue,
                pendingTasks = pending,
                completedTasks = completed,
                themeMode = themeMode,
                selectedTaskForDetail = refreshedDetail
            )
        }
    }
}