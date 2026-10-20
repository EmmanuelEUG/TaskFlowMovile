package com.emma.proyects.taskflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.emma.proyects.taskflow.data.model.ThemeMode

@Immutable
data class ExtraColors(
    val success: Color,
    val altaBg: Color,
    val altaFg: Color,
    val mediaBg: Color,
    val mediaFg: Color,
    val bajaBg: Color,
    val bajaFg: Color
)

val LocalExtraColors = staticCompositionLocalOf {
    ExtraColors(
        success = SuccessLight,
        altaBg = AltaBgLight,
        altaFg = AltaFgLight,
        mediaBg = MediaBgLight,
        mediaFg = MediaFgLight,
        bajaBg = BajaBgLight,
        bajaFg = BajaFgLight
    )
}

private val LightColorScheme = lightColorScheme(
    background = BgLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceAltLight,
    onBackground = TextLight,
    onSurface = TextLight,
    onSurfaceVariant = TextMutedLight,
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    outline = OutlineLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight
)

private val DarkColorScheme = darkColorScheme(
    background = BgDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceAltDark,
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextMutedDark,
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    outline = OutlineDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark
)

private val LightExtraColors = ExtraColors(
    success = SuccessLight,
    altaBg = AltaBgLight,
    altaFg = AltaFgLight,
    mediaBg = MediaBgLight,
    mediaFg = MediaFgLight,
    bajaBg = BajaBgLight,
    bajaFg = BajaFgLight
)

private val DarkExtraColors = ExtraColors(
    success = SuccessDark,
    altaBg = AltaBgDark,
    altaFg = AltaFgDark,
    mediaBg = MediaBgDark,
    mediaFg = MediaFgDark,
    bajaBg = BajaBgDark,
    bajaFg = BajaFgDark
)

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

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val extraColors = if (isDark) DarkExtraColors else LightExtraColors

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object TaskFlowDesign {
    val extraColors: ExtraColors
        @Composable
        get() = LocalExtraColors.current
}