package com.emma.proyects.taskflow.di

import android.content.Context
import com.emma.proyects.taskflow.data.datasource.TaskLocalDataSource
import com.emma.proyects.taskflow.data.repository.TaskRepository
import com.emma.proyects.taskflow.data.repository.TaskRepositoryImpl
import com.emma.proyects.taskflow.presentation.viewmodels.TaskViewModelFactory

object TaskModule {

    @Volatile
    private var repositoryInstance: TaskRepository? = null

    fun provideTaskViewModelFactory(context: Context): TaskViewModelFactory {
        val repo = repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: TaskRepositoryImpl(
                TaskLocalDataSource(context.applicationContext)
            ).also { repositoryInstance = it }
        }
        return TaskViewModelFactory(repo)
    }
}