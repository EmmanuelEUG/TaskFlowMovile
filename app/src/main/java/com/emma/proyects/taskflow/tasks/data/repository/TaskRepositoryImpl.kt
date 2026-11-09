package com.emma.proyects.taskflow.tasks.data.repository

import com.emma.proyects.taskflow.core.data.network.ApiErrorMapper
import com.emma.proyects.taskflow.core.domain.session.ThemeStorage
import com.emma.proyects.taskflow.tasks.data.datasource.TaskApiService
import com.emma.proyects.taskflow.tasks.data.dto.PatchTaskDto
import com.emma.proyects.taskflow.tasks.data.mapper.toCreateUpdateDto
import com.emma.proyects.taskflow.tasks.data.mapper.toDomain
import com.emma.proyects.taskflow.tasks.data.mapper.toDomainList
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode
import com.emma.proyects.taskflow.tasks.domain.repository.TaskRepository

class TaskRepositoryImpl(
    private val apiService: TaskApiService,
    private val themeStorage: ThemeStorage,
    private val errorMapper: ApiErrorMapper
) : TaskRepository {

    override suspend fun getTasks(): Result<List<Task>> {
        return runCatching {
            apiService.getTasks().toDomainList()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override suspend fun getTaskById(taskId: String): Result<Task> {
        return runCatching {
            apiService.getTaskById(taskId).toDomain()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override suspend fun createTask(task: Task): Result<Task> {
        return runCatching {
            apiService.createTask(task.toCreateUpdateDto()).toDomain()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override suspend fun updateTask(task: Task): Result<Task> {
        return runCatching {
            apiService.updateTask(task.id, task.toCreateUpdateDto()).toDomain()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override suspend fun toggleTaskCompletion(taskId: String, isCompleted: Boolean): Result<Task> {
        return runCatching {
            apiService.patchTask(taskId, PatchTaskDto(isCompleted = isCompleted)).toDomain()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return runCatching {
            apiService.deleteTask(taskId)
            Unit
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override suspend fun restoreSeedTasks(): Result<List<Task>> {
        return runCatching {
            apiService.restoreSeedTasks().toDomainList()
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(errorMapper.map(it)) }
        )
    }

    override fun getThemeMode(): ThemeMode = themeStorage.getThemeMode()

    override fun setThemeMode(mode: ThemeMode): ThemeMode = themeStorage.setThemeMode(mode)
}
