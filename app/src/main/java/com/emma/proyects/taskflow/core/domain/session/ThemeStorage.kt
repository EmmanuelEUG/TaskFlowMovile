package com.emma.proyects.taskflow.core.domain.session

import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode

interface ThemeStorage {
    fun getThemeMode(): ThemeMode
    fun setThemeMode(mode: ThemeMode): ThemeMode
}
