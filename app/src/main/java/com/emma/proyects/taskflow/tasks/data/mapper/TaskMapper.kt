package com.emma.proyects.taskflow.tasks.data.mapper

import com.emma.proyects.taskflow.tasks.data.dto.CreateUpdateTaskDto
import com.emma.proyects.taskflow.tasks.data.dto.TaskDto
import com.emma.proyects.taskflow.tasks.domain.model.Importance
import com.emma.proyects.taskflow.tasks.domain.model.RecurrenceUnit
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import java.time.DayOfWeek

fun TaskDto.toDomain(nowMillis: Long = System.currentTimeMillis()): Task {
    val parsedImportance = runCatching { Importance.valueOf(importance) }
        .getOrDefault(Importance.MEDIUM)
    val parsedUnit = runCatching { RecurrenceUnit.valueOf(recurrenceUnit) }
        .getOrDefault(RecurrenceUnit.DAYS)
    val parsedDays = recurrenceDaysOfWeek.mapNotNull { day ->
        runCatching { DayOfWeek.valueOf(day) }.getOrNull()
    }

    val computedOverdue = !isCompleted && (
        (dueDateMillis != null && dueDateMillis < nowMillis) || isOverdue
    )

    return Task(
        id = id,
        title = title,
        description = description,
        area = area,
        importance = parsedImportance,
        priorityLevel = priorityLevel.coerceIn(TaskConstants.MIN_PRIORITY, TaskConstants.MAX_PRIORITY),
        dueDateFormatted = dueDateFormatted?.ifBlank { null },
        dueDateMillis = dueDateMillis,
        isOverdue = computedOverdue,
        isCompleted = isCompleted,
        isRecurring = isRecurring,
        recurrenceInterval = recurrenceInterval.coerceAtLeast(TaskConstants.MIN_RECURRENCE_INTERVAL),
        recurrenceUnit = parsedUnit,
        createdDateFormatted = createdDateFormatted.trim(),
        completedAtMillis = completedAtMillis,
        recurrenceDaysOfWeek = parsedDays,
        recurrenceDaysOfMonth = recurrenceDaysOfMonth
    )
}

fun List<TaskDto>.toDomainList(nowMillis: Long = System.currentTimeMillis()): List<Task> {
    return map { it.toDomain(nowMillis) }
}

fun Task.toCreateUpdateDto(): CreateUpdateTaskDto {
    return CreateUpdateTaskDto(
        title = title.trim(),
        description = description.trim(),
        area = area.trim(),
        importance = importance.name,
        priorityLevel = priorityLevel.coerceIn(TaskConstants.MIN_PRIORITY, TaskConstants.MAX_PRIORITY),
        dueDateFormatted = dueDateFormatted,
        dueDateMillis = dueDateMillis,
        isCompleted = isCompleted,
        isRecurring = isRecurring,
        recurrenceInterval = recurrenceInterval.coerceAtLeast(TaskConstants.MIN_RECURRENCE_INTERVAL),
        recurrenceUnit = recurrenceUnit.name,
        recurrenceDaysOfWeek = recurrenceDaysOfWeek.map { it.name },
        recurrenceDaysOfMonth = recurrenceDaysOfMonth,
        createdDateFormatted = createdDateFormatted.ifBlank { null },
        completedAtMillis = completedAtMillis
    )
}
