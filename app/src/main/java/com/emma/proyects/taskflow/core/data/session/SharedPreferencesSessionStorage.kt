package com.emma.proyects.taskflow.core.data.session

import android.content.Context
import androidx.core.content.edit
import com.emma.proyects.taskflow.auth.domain.model.AuthSession
import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.core.domain.session.SessionStorage

class SharedPreferencesSessionStorage(context: Context) : SessionStorage {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveSession(session: AuthSession) {
        prefs.edit {
            putString(KEY_TOKEN, session.accessToken)
            putString(KEY_USER_ID, session.user.id)
            putString(KEY_USER_NAME, session.user.name)
            putString(KEY_USER_EMAIL, session.user.email)
        }
    }

    override fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() }
    }

    override fun getCurrentUser(): User? {
        val id = prefs.getString(KEY_USER_ID, null)?.takeIf { it.isNotBlank() } ?: return null
        val name = prefs.getString(KEY_USER_NAME, null)?.takeIf { it.isNotBlank() } ?: return null
        val email = prefs.getString(KEY_USER_EMAIL, null)?.takeIf { it.isNotBlank() } ?: return null
        return User(id = id, name = name, email = email)
    }

    override fun hasActiveSession(): Boolean {
        return getToken() != null && getCurrentUser() != null
    }

    override fun clearSession() {
        prefs.edit {
            remove(KEY_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
        }
    }

    companion object {
        private const val PREFS_NAME = "taskflow_session_prefs"
        private const val KEY_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
    }
}
