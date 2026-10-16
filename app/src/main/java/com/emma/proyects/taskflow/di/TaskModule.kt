package com.emma.proyects.taskflow.di

import com.emma.proyects.taskflow.data.datasource.TaskLocalDataSource
import com.emma.proyects.taskflow.data.repository.TaskRepository
import com.emma.proyects.taskflow.data.repository.TaskRepositoryImpl
import com.emma.proyects.taskflow.presentation.viewmodels.TaskViewModelFactory

object TaskModule {

    private val localDataSource: TaskLocalDataSource by lazy {
        TaskLocalDataSource()
    }

    private val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(localDataSource)
    }

    fun provideTaskViewModelFactory(): TaskViewModelFactory {
        return TaskViewModelFactory(taskRepository)
    }
}