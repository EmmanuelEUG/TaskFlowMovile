package com.emma.proyects.taskflow.tasks.domain.repository

import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode

interface TaskRepository {
    suspend fun getTasks(): Result<List<Task>>
    suspend fun getTaskById(taskId: String): Result<Task>
    suspend fun createTask(task: Task): Result<Task>
    suspend fun updateTask(task: Task): Result<Task>
    suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean): Result<Task>
    suspend fun deleteTask(taskId: String): Result<Unit>
    suspend fun restoreSeedTasks(): Result<List<Task>>
    fun getThemeMode(): ThemeMode
    fun setThemeMode(mode: ThemeMode): ThemeMode
}
