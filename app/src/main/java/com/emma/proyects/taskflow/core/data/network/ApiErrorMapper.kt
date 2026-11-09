package com.emma.proyects.taskflow.core.data.network

import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.core.domain.network.AppException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class ApiErrorMapper(private val json: Json) {

    fun map(throwable: Throwable): Throwable {
        return when (throwable) {
            is HttpException -> parseHttpException(throwable)
            is SocketTimeoutException -> AppException.ResourceMessage(R.string.error_timeout)
            is IOException -> AppException.ResourceMessage(R.string.error_connection)
            is SerializationException -> AppException.ResourceMessage(R.string.error_serialization)
            else -> throwable
        }
    }

    private fun parseHttpException(exception: HttpException): AppException {
        val rawBody = exception.response()?.errorBody()?.string()
        val extracted = extractDetail(rawBody)
        if (!extracted.isNullOrBlank()) {
            return AppException.ServerMessage(extracted)
        }

        return when (val code = exception.code()) {
            NetworkConstants.HTTP_BAD_REQUEST ->
                AppException.ResourceMessage(R.string.error_http_bad_request)
            NetworkConstants.HTTP_UNAUTHORIZED ->
                AppException.ResourceMessage(R.string.error_http_unauthorized)
            NetworkConstants.HTTP_NOT_FOUND ->
                AppException.ResourceMessage(R.string.error_http_not_found)
            NetworkConstants.HTTP_CONFLICT ->
                AppException.ResourceMessage(R.string.error_http_conflict)
            NetworkConstants.HTTP_UNPROCESSABLE_ENTITY ->
                AppException.ResourceMessage(R.string.error_http_validation)
            in NetworkConstants.HTTP_SERVER_ERROR_MIN..NetworkConstants.HTTP_SERVER_ERROR_MAX ->
                AppException.ResourceMessage(R.string.error_http_server, listOf(code))
            else ->
                AppException.ResourceMessage(
                    R.string.error_http_generic,
                    listOf(code, exception.message())
                )
        }
    }

    private fun extractDetail(rawBody: String?): String? {
        if (rawBody.isNullOrBlank()) return null
        return try {
            val element = json.parseToJsonElement(rawBody)
            if (element !is JsonObject) return null
            val detail = element[NetworkConstants.JSON_KEY_DETAIL] ?: return null
            when (detail) {
                is JsonPrimitive -> detail.content
                is JsonArray -> detail.firstOrNull()
                    ?.jsonObject
                    ?.get(NetworkConstants.JSON_KEY_MESSAGE)
                    ?.jsonPrimitive
                    ?.content
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }
}
