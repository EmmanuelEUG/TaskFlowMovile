package com.emma.proyects.taskflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode

@Immutable
data class ExtraColors(
    val success: Color,
    val altaBg: Color,
    val altaFg: Color,
    val mediaBg: Color,
    val mediaFg: Color,
    val bajaBg: Color,
    val bajaFg: Color,
    val chartBlue: Color,
    val chartGreen: Color,
    val chartRed: Color,
    val chartAmber: Color,
    val chartPurple: Color
) {
    val chartPalette: List<Color>
        get() = listOf(chartBlue, chartGreen, chartRed, chartAmber, chartPurple)
}

val LocalExtraColors = staticCompositionLocalOf<ExtraColors> {
    error("TaskFlowTheme must provide LocalExtraColors")
}

@Composable
fun TaskFlowTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (isDark) taskFlowDarkColorScheme() else taskFlowLightColorScheme()
    val extraColors = if (isDark) taskFlowDarkExtraColors() else taskFlowLightExtraColors()

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = taskFlowTypography(),
            content = content
        )
    }
}

object TaskFlowDesign {
    val extraColors: ExtraColors
        @Composable
        get() = LocalExtraColors.current
}
