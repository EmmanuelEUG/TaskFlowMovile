package com.emma.proyects.taskflow.data.model

enum class Importance {
    HIGH, MEDIUM, LOW
}

enum class RecurrenceUnit {
    HOURS, DAYS, WEEKS, MONTHS, YEARS
}

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class AppRoute {
    HOME, PRODUCTIVITY, SETTINGS
}

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val area: String,
    val importance: Importance,
    val priorityLevel: Int, // 1..5
    val dueDateFormatted: String?,
    val isOverdue: Boolean = false,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrenceInterval: Int = 1,
    val recurrenceUnit: RecurrenceUnit = RecurrenceUnit.DAYS,
    val createdDateFormatted: String = "Octubre 2026"
)