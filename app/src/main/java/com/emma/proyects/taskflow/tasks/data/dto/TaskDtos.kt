package com.emma.proyects.taskflow.tasks.data.dto

import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private object TaskDtoDefaults {
    const val EMPTY_TEXT = ""
    const val DEFAULT_IMPORTANCE = "MEDIUM"
    const val DEFAULT_RECURRENCE_UNIT = "DAYS"
}

@Serializable
data class TaskDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String = TaskDtoDefaults.EMPTY_TEXT,
    @SerialName("area") val area: String = TaskDtoDefaults.EMPTY_TEXT,
    @SerialName("importance") val importance: String = TaskDtoDefaults.DEFAULT_IMPORTANCE,
    @SerialName("priority_level") val priorityLevel: Int = TaskConstants.DEFAULT_PRIORITY,
    @SerialName("due_date_formatted") val dueDateFormatted: String? = null,
    @SerialName("due_date_millis") val dueDateMillis: Long? = null,
    @SerialName("is_overdue") val isOverdue: Boolean = false,
    @SerialName("is_completed") val isCompleted: Boolean = false,
    @SerialName("is_recurring") val isRecurring: Boolean = false,
    @SerialName("recurrence_interval") val recurrenceInterval: Int = TaskConstants.MIN_RECURRENCE_INTERVAL,
    @SerialName("recurrence_unit") val recurrenceUnit: String = TaskDtoDefaults.DEFAULT_RECURRENCE_UNIT,
    @SerialName("created_date_formatted") val createdDateFormatted: String = TaskDtoDefaults.EMPTY_TEXT,
    @SerialName("completed_at_millis") val completedAtMillis: Long? = null,
    @SerialName("recurrence_days_of_week") val recurrenceDaysOfWeek: List<String> = emptyList(),
    @SerialName("recurrence_days_of_month") val recurrenceDaysOfMonth: List<Int> = emptyList()
)

@Serializable
data class CreateUpdateTaskDto(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("area") val area: String,
    @SerialName("importance") val importance: String,
    @SerialName("priority_level") val priorityLevel: Int,
    @SerialName("due_date_formatted") val dueDateFormatted: String? = null,
    @SerialName("due_date_millis") val dueDateMillis: Long? = null,
    @SerialName("is_completed") val isCompleted: Boolean = false,
    @SerialName("is_recurring") val isRecurring: Boolean = false,
    @SerialName("recurrence_interval") val recurrenceInterval: Int = TaskConstants.MIN_RECURRENCE_INTERVAL,
    @SerialName("recurrence_unit") val recurrenceUnit: String = TaskDtoDefaults.DEFAULT_RECURRENCE_UNIT,
    @SerialName("recurrence_days_of_week") val recurrenceDaysOfWeek: List<String> = emptyList(),
    @SerialName("recurrence_days_of_month") val recurrenceDaysOfMonth: List<Int> = emptyList(),
    @SerialName("created_date_formatted") val createdDateFormatted: String? = null,
    @SerialName("completed_at_millis") val completedAtMillis: Long? = null
)

@Serializable
data class PatchTaskDto(
    @SerialName("is_completed") val isCompleted: Boolean? = null
)

@Serializable
data class DeleteTaskResponseDto(
    @SerialName("deleted") val deleted: Boolean = true,
    @SerialName("id") val id: String = TaskDtoDefaults.EMPTY_TEXT
)
