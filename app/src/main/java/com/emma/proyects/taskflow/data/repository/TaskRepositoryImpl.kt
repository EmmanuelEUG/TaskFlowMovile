package com.emma.proyects.taskflow.data.repository

import com.emma.proyects.taskflow.data.datasource.TaskLocalDataSource
import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.ThemeMode

class TaskRepositoryImpl(
    private val localDataSource: TaskLocalDataSource
) : TaskRepository {

    override fun getTasks(): List<Task> {
        return localDataSource.getTasks()
    }

    override fun toggleTaskCompletion(taskId: String): List<Task> {
        return localDataSource.toggleTaskCompletion(taskId)
    }

    override fun deleteTask(taskId: String): List<Task> {
        return localDataSource.deleteTask(taskId)
    }

    override fun restoreSeedTasks(): List<Task> {
        return localDataSource.restoreSeedTasks()
    }

    override fun getThemeMode(): ThemeMode {
        return localDataSource.getThemeMode()
    }

    override fun setThemeMode(mode: ThemeMode): ThemeMode {
        return localDataSource.setThemeMode(mode)
    }
}