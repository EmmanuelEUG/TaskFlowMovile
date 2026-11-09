package com.emma.proyects.taskflow.core.domain.network

import androidx.annotation.StringRes

sealed class AppException(message: String? = null) : Exception(message) {
    data class ServerMessage(val detail: String) : AppException(detail)

    data class ResourceMessage(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : AppException()
}
