package com.emma.proyects.taskflow.tasks.domain.model

import java.time.DayOfWeek

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

enum class DueDateFilter {
    ALL, TODAY, THIS_WEEK, NEXT_WEEK, LATER, NO_DATE
}

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val area: String,
    val importance: Importance,
    val priorityLevel: Int,
    val dueDateFormatted: String?,
    val dueDateMillis: Long? = null,
    val isOverdue: Boolean = false,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrenceInterval: Int = TaskConstants.MIN_RECURRENCE_INTERVAL,
    val recurrenceUnit: RecurrenceUnit = RecurrenceUnit.DAYS,
    val createdDateFormatted: String = "",
    val completedAtMillis: Long? = null,
    val recurrenceDaysOfWeek: List<DayOfWeek> = emptyList(),
    val recurrenceDaysOfMonth: List<Int> = emptyList()
)
