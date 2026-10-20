package com.emma.proyects.taskflow.data.repository

import com.emma.proyects.taskflow.data.datasource.TaskLocalDataSource
import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.ThemeMode

class TaskRepositoryImpl(
    private val localDataSource: TaskLocalDataSource
) : TaskRepository {

    override fun getTasks(): List<Task> = localDataSource.getTasks()

    override fun toggleTask(taskId: String): List<Task> = localDataSource.toggleTask(taskId)

    override fun upsertTask(task: Task): List<Task> = localDataSource.upsertTask(task)

    override fun deleteTask(taskId: String): List<Task> = localDataSource.deleteTask(taskId)

    override fun restoreSeedTasks(): List<Task> = localDataSource.restoreSeedTasks()

    override fun getThemeMode(): ThemeMode = localDataSource.getThemeMode()

    override fun setThemeMode(mode: ThemeMode): ThemeMode = localDataSource.setThemeMode(mode)
}