package com.emma.proyects.taskflow.data.model

enum class TaskPriority {
    HIGH, MEDIUM, LOW
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class TaskFilter {
    ALL, PENDING, HIGH, MEDIUM, LOW
}

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val dueDateText: String,
    val priority: TaskPriority,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false
)