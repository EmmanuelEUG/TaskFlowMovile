package com.emma.proyects.taskflow.core.data.session

import android.content.Context
import androidx.core.content.edit
import com.emma.proyects.taskflow.core.domain.session.ThemeStorage
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode

class SharedPreferencesThemeStorage(context: Context) : ThemeStorage {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getThemeMode(): ThemeMode {
        val raw = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return runCatching { ThemeMode.valueOf(raw) }.getOrDefault(ThemeMode.SYSTEM)
    }

    override fun setThemeMode(mode: ThemeMode): ThemeMode {
        prefs.edit {
            putString(KEY_THEME_MODE, mode.name)
        }
        return mode
    }

    companion object {
        private const val PREFS_NAME = "taskflow_theme_prefs"
        private const val KEY_THEME_MODE = "app_theme_mode"
    }
}
