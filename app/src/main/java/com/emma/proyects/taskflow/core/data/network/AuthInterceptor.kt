package com.emma.proyects.taskflow.core.data.network

import com.emma.proyects.taskflow.core.domain.session.SessionMonitor
import com.emma.proyects.taskflow.core.domain.session.SessionStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionStorage: SessionStorage,
    private val sessionMonitor: SessionMonitor
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val encodedPath = originalRequest.url.encodedPath
        val isPublicAuthRoute = encodedPath.endsWith(NetworkConstants.ROUTE_LOGIN_SUFFIX) ||
            encodedPath.endsWith(NetworkConstants.ROUTE_REGISTER_SUFFIX)

        val token = sessionStorage.getToken()
        val requestToProceed = if (!isPublicAuthRoute && !token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header(
                    NetworkConstants.HEADER_AUTHORIZATION,
                    "${NetworkConstants.BEARER_PREFIX}$token"
                )
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(requestToProceed)
        if (!isPublicAuthRoute &&
            response.code == NetworkConstants.HTTP_UNAUTHORIZED &&
            !token.isNullOrBlank()
        ) {
            sessionStorage.clearSession()
            sessionMonitor.notifyUnauthorized()
        }
        return response
    }
}
