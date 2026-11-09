package com.emma.proyects.taskflow.core.data.network

object NetworkConstants {
    const val MEDIA_TYPE_JSON = "application/json; charset=UTF-8"
    const val HEADER_AUTHORIZATION = "Authorization"
    const val BEARER_PREFIX = "Bearer "

    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 20L
    const val WRITE_TIMEOUT_SECONDS = 20L

    const val ROUTE_LOGIN_SUFFIX = "/login"
    const val ROUTE_REGISTER_SUFFIX = "/register"

    const val JSON_KEY_DETAIL = "detail"
    const val JSON_KEY_MESSAGE = "msg"

    const val HTTP_BAD_REQUEST = 400
    const val HTTP_UNAUTHORIZED = 401
    const val HTTP_NOT_FOUND = 404
    const val HTTP_CONFLICT = 409
    const val HTTP_UNPROCESSABLE_ENTITY = 422
    const val HTTP_SERVER_ERROR_MIN = 500
    const val HTTP_SERVER_ERROR_MAX = 599
}
