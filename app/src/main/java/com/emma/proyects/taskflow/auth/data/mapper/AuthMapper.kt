package com.emma.proyects.taskflow.auth.data.mapper

import com.emma.proyects.taskflow.auth.data.dto.AuthResponseDto
import com.emma.proyects.taskflow.auth.data.dto.UserDto
import com.emma.proyects.taskflow.auth.domain.model.AuthSession
import com.emma.proyects.taskflow.auth.domain.model.User

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email
)

fun AuthResponseDto.toDomain(): AuthSession = AuthSession(
    accessToken = accessToken,
    user = user.toDomain()
)
