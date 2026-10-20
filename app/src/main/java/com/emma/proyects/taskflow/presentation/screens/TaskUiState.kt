package com.emma.proyects.taskflow.presentation.screens

import com.emma.proyects.taskflow.data.model.AppRoute
import com.emma.proyects.taskflow.data.model.Importance
import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.ThemeMode

data class TaskUiState(
    val allTasks: List<Task> = emptyList(),
    val overdueTasks: List<Task> = emptyList(),
    val pendingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val searchQuery: String = "",
    val selectedArea: String = "Todas",
    val selectedImportance: Importance? = null,
    val currentRoute: AppRoute = AppRoute.HOME,
    val themeMode: ThemeMode = ThemeMode.DARK
)