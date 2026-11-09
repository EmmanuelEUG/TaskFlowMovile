package com.emma.proyects.taskflow.tasks.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.core.presentation.toUiText
import com.emma.proyects.taskflow.tasks.domain.repository.TaskRepository
import com.emma.proyects.taskflow.tasks.presentation.screens.TaskDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskId: String,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        loadTask()
    }

    fun loadTask() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            taskRepository.getTaskById(taskId).fold(
                onSuccess = { task ->
                    _uiState.update {
                        it.copy(isLoading = false, task = task, errorMessage = null)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.toUiText(R.string.error_load_task_detail)
                        )
                    }
                }
            )
        }
    }

    fun onToggleComplete() {
        val currentTask = _uiState.value.task ?: return
        if (_uiState.value.isMutating) return

        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true) }
            val nextCompleted = !currentTask.isCompleted
            taskRepository.toggleTaskCompletion(currentTask.id, nextCompleted).fold(
                onSuccess = { updated ->
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            task = updated,
                            userMessage = if (nextCompleted) {
                                UiText.StringResource(R.string.msg_task_completed)
                            } else {
                                UiText.StringResource(R.string.msg_task_reopened)
                            }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            userMessage = error.toUiText(R.string.error_update_status)
                        )
                    }
                }
            )
        }
    }

    fun onRequestDelete() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onConfirmDelete() {
        val currentTask = _uiState.value.task ?: return
        if (_uiState.value.isMutating) return

        viewModelScope.launch {
            _uiState.update { it.copy(isMutating = true, showDeleteDialog = false) }
            taskRepository.deleteTask(currentTask.id).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            deletedFeedback = UiText.StringResource(
                                R.string.msg_task_deleted_named,
                                currentTask.title
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isMutating = false,
                            userMessage = error.toUiText(R.string.error_delete_task)
                        )
                    }
                }
            )
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun consumeDeletedFeedback() {
        _uiState.update { it.copy(deletedFeedback = null) }
    }
}
