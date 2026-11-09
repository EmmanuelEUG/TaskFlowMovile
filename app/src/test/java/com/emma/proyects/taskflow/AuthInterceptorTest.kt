package com.emma.proyects.taskflow

import com.emma.proyects.taskflow.auth.domain.model.AuthSession
import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.core.data.network.AuthInterceptor
import com.emma.proyects.taskflow.core.data.session.SessionMonitorImpl
import com.emma.proyects.taskflow.core.domain.session.SessionStorage
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {

    private class InMemorySessionStorage : SessionStorage {
        var session: AuthSession? = AuthSession("secret-jwt", User("1", "Test", "t@t.com"))
        override fun saveSession(session: AuthSession) { this.session = session }
        override fun getToken(): String? = session?.accessToken
        override fun getCurrentUser(): User? = session?.user
        override fun hasActiveSession(): Boolean = session != null
        override fun clearSession() { session = null }
    }

    @Test
    fun interceptor_addsBearerHeaderForTasks_andSkipsLogin() {
        val storage = InMemorySessionStorage()
        val monitor = SessionMonitorImpl()
        val capturedHeaders = mutableListOf<String?>()

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(storage, monitor))
            .addInterceptor { chain ->
                capturedHeaders.add(chain.request().header("Authorization"))
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body("{}".toResponseBody())
                    .build()
            }
            .build()

        client.newCall(Request.Builder().url("http://localhost:8000/tasks").build()).execute().close()
        client.newCall(Request.Builder().url("http://localhost:8000/login").build()).execute().close()

        assertEquals("Bearer secret-jwt", capturedHeaders[0])
        assertNull(capturedHeaders[1])
    }

    @Test
    fun interceptor_clearsSessionOn401FromProtectedRoute() {
        val storage = InMemorySessionStorage()
        val monitor = SessionMonitorImpl()

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(storage, monitor))
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(401)
                    .message("Unauthorized")
                    .body("{}".toResponseBody())
                    .build()
            }
            .build()

        client.newCall(Request.Builder().url("http://localhost:8000/tasks").build()).execute().close()
        assertNull(storage.getToken())
    }
}
