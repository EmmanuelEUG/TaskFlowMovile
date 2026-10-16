package com.emma.proyects.taskflow.presentation.screens

import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.TaskFilter
import com.emma.proyects.taskflow.data.model.ThemeMode

data class TaskUiState(
    val allTasks: List<Task> = emptyList(),
    val visibleTasks: List<Task> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val totalCount: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val completionProgress: Float = 0f,
    val completionPercentInt: Int = 0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)