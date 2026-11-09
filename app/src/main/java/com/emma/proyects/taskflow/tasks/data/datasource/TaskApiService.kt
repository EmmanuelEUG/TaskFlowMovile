package com.emma.proyects.taskflow.tasks.data.datasource

import com.emma.proyects.taskflow.tasks.data.dto.CreateUpdateTaskDto
import com.emma.proyects.taskflow.tasks.data.dto.DeleteTaskResponseDto
import com.emma.proyects.taskflow.tasks.data.dto.PatchTaskDto
import com.emma.proyects.taskflow.tasks.data.dto.TaskDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskApiService {

    @GET("tasks")
    suspend fun getTasks(): List<TaskDto>

    @GET("tasks/{id}")
    suspend fun getTaskById(@Path("id") taskId: String): TaskDto

    @POST("tasks")
    suspend fun createTask(@Body request: CreateUpdateTaskDto): TaskDto

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: String,
        @Body request: CreateUpdateTaskDto
    ): TaskDto

    @PATCH("tasks/{id}")
    suspend fun patchTask(
        @Path("id") taskId: String,
        @Body request: PatchTaskDto
    ): TaskDto

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") taskId: String): DeleteTaskResponseDto

    @POST("tasks/seed")
    suspend fun restoreSeedTasks(): List<TaskDto>
}
