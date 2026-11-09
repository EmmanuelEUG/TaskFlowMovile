package com.emma.proyects.taskflow.auth.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String
)

data class AuthSession(
    val accessToken: String,
    val user: User
)
