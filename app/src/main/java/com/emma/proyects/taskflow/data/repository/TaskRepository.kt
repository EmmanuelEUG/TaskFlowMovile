package com.emma.proyects.taskflow.data.repository

import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.ThemeMode

interface TaskRepository {
    fun getTasks(): List<Task>
    fun toggleTask(taskId: String): List<Task>
    fun upsertTask(task: Task): List<Task>
    fun deleteTask(taskId: String): List<Task>
    fun restoreSeedTasks(): List<Task>
    fun getThemeMode(): ThemeMode
    fun setThemeMode(mode: ThemeMode): ThemeMode
}