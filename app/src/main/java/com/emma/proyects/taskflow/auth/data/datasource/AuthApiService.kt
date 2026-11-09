package com.emma.proyects.taskflow.auth.data.datasource

import com.emma.proyects.taskflow.auth.data.dto.AuthResponseDto
import com.emma.proyects.taskflow.auth.data.dto.LoginRequestDto
import com.emma.proyects.taskflow.auth.data.dto.RegisterRequestDto
import com.emma.proyects.taskflow.auth.data.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @GET("me")
    suspend fun getMe(): UserDto
}
