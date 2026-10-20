package com.emma.proyects.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emma.proyects.taskflow.data.model.AppRoute
import com.emma.proyects.taskflow.di.TaskModule
import com.emma.proyects.taskflow.presentation.components.DeleteTaskDialog
import com.emma.proyects.taskflow.presentation.screens.ProductivityScreen
import com.emma.proyects.taskflow.presentation.screens.SettingsScreen
import com.emma.proyects.taskflow.presentation.screens.TaskDetailScreen
import com.emma.proyects.taskflow.presentation.screens.TaskFormScreen
import com.emma.proyects.taskflow.presentation.screens.TaskPage
import com.emma.proyects.taskflow.presentation.viewmodels.TaskViewModel
import com.emma.proyects.taskflow.ui.theme.TaskFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TaskViewModel = viewModel(
                factory = TaskModule.provideTaskViewModelFactory(applicationContext)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            TaskFlowTheme(themeMode = uiState.themeMode) {
                when {
                    uiState.isFormOpen -> {
                        TaskFormScreen(
                            formState = uiState.formState,
                            onTitleChange = viewModel::onFormTitleChange,
                            onDescriptionChange = viewModel::onFormDescriptionChange,
                            onImportanceChange = viewModel::onFormImportanceChange,
                            onPriorityChange = viewModel::onFormPriorityChange,
                            onAreaChange = viewModel::onFormAreaChange,
                            onDateSelected = viewModel::onFormDateSelected,
                            onTimeSelected = viewModel::onFormTimeSelected,
                            onRecurringToggle = viewModel::onFormRecurringToggle,
                            onRecurrenceIntervalChange = viewModel::onFormRecurrenceIntervalChange,
                            onRecurrenceUnitChange = viewModel::onFormRecurrenceUnitChange,
                            onSave = viewModel::onSaveTaskForm,
                            onClose = viewModel::onCloseForm,
                            onDeleteFromEdit = {
                                uiState.selectedTaskForDetail?.let {
                                    viewModel.onRequestDeleteTask(it)
                                }
                            }
                        )
                    }

                    uiState.selectedTaskForDetail != null -> {
                        val detailTask = uiState.selectedTaskForDetail!!
                        TaskDetailScreen(
                            task = detailTask,
                            onBack = viewModel::onCloseTaskDetail,
                            onEdit = { viewModel.onOpenEditTaskForm(detailTask) },
                            onToggleComplete = { viewModel.onToggleTask(detailTask.id) },
                            onRequestDelete = { viewModel.onRequestDeleteTask(detailTask) }
                        )
                    }

                    else -> {
                        when (uiState.currentRoute) {
                            AppRoute.HOME -> {
                                TaskPage(
                                    uiState = uiState,
                                    onToggleTask = viewModel::onToggleTask,
                                    onTaskClick = viewModel::onOpenTaskDetail,
                                    onNewTaskClick = viewModel::onOpenNewTaskForm,
                                    onSearchChange = viewModel::onSearchQueryChanged,
                                    onAreaSelected = viewModel::onAreaSelected,
                                    onImportanceSelected = viewModel::onImportanceSelected,
                                    onNavigate = viewModel::onNavigate,
                                    onToggleSortOrder = viewModel::onToggleSortOrder,
                                    onCycleTheme = viewModel::onCycleTheme
                                )
                            }

                            AppRoute.PRODUCTIVITY -> {
                                ProductivityScreen(
                                    uiState = uiState,
                                    onNavigate = viewModel::onNavigate
                                )
                            }

                            AppRoute.SETTINGS -> {
                                SettingsScreen(
                                    uiState = uiState,
                                    onNavigate = viewModel::onNavigate,
                                    onThemeModeSelected = viewModel::onThemeModeSelected,
                                    onRestoreSeed = viewModel::onRestoreSeed
                                )
                            }
                        }
                    }
                }

                
                uiState.taskPendingDeletion?.let { taskToDelete ->
                    DeleteTaskDialog(
                        taskTitle = taskToDelete.title,
                        onConfirmDelete = viewModel::onConfirmDeleteTask,
                        onDismiss = viewModel::onDismissDeleteDialog
                    )
                }
            }
        }
    }
}