package com.emma.proyects.taskflow.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.emma.proyects.taskflow.R

@Composable
fun taskFlowLightColorScheme(): ColorScheme = lightColorScheme(
    background = colorResource(R.color.tf_bg_light),
    surface = colorResource(R.color.tf_surface_light),
    surfaceVariant = colorResource(R.color.tf_surface_alt_light),
    onBackground = colorResource(R.color.tf_text_light),
    onSurface = colorResource(R.color.tf_text_light),
    onSurfaceVariant = colorResource(R.color.tf_text_muted_light),
    primary = colorResource(R.color.tf_primary_light),
    onPrimary = colorResource(R.color.tf_on_primary_light),
    primaryContainer = colorResource(R.color.tf_primary_container_light),
    onPrimaryContainer = colorResource(R.color.tf_on_primary_container_light),
    outline = colorResource(R.color.tf_outline_light),
    error = colorResource(R.color.tf_error_light),
    onError = colorResource(R.color.tf_on_error_light),
    errorContainer = colorResource(R.color.tf_error_container_light)
)

@Composable
fun taskFlowDarkColorScheme(): ColorScheme = darkColorScheme(
    background = colorResource(R.color.tf_bg_dark),
    surface = colorResource(R.color.tf_surface_dark),
    surfaceVariant = colorResource(R.color.tf_surface_alt_dark),
    onBackground = colorResource(R.color.tf_text_dark),
    onSurface = colorResource(R.color.tf_text_dark),
    onSurfaceVariant = colorResource(R.color.tf_text_muted_dark),
    primary = colorResource(R.color.tf_primary_dark),
    onPrimary = colorResource(R.color.tf_on_primary_dark),
    primaryContainer = colorResource(R.color.tf_primary_container_dark),
    onPrimaryContainer = colorResource(R.color.tf_on_primary_container_dark),
    outline = colorResource(R.color.tf_outline_dark),
    error = colorResource(R.color.tf_error_dark),
    onError = colorResource(R.color.tf_on_error_dark),
    errorContainer = colorResource(R.color.tf_error_container_dark)
)

@Composable
fun taskFlowLightExtraColors(): ExtraColors = ExtraColors(
    success = colorResource(R.color.tf_success_light),
    altaBg = colorResource(R.color.tf_alta_bg_light),
    altaFg = colorResource(R.color.tf_alta_fg_light),
    mediaBg = colorResource(R.color.tf_media_bg_light),
    mediaFg = colorResource(R.color.tf_media_fg_light),
    bajaBg = colorResource(R.color.tf_baja_bg_light),
    bajaFg = colorResource(R.color.tf_baja_fg_light),
    chartBlue = colorResource(R.color.tf_chart_blue),
    chartGreen = colorResource(R.color.tf_chart_green),
    chartRed = colorResource(R.color.tf_chart_red),
    chartAmber = colorResource(R.color.tf_chart_amber),
    chartPurple = colorResource(R.color.tf_chart_purple)
)

@Composable
fun taskFlowDarkExtraColors(): ExtraColors = ExtraColors(
    success = colorResource(R.color.tf_success_dark),
    altaBg = colorResource(R.color.tf_alta_bg_dark),
    altaFg = colorResource(R.color.tf_alta_fg_dark),
    mediaBg = colorResource(R.color.tf_media_bg_dark),
    mediaFg = colorResource(R.color.tf_media_fg_dark),
    bajaBg = colorResource(R.color.tf_baja_bg_dark),
    bajaFg = colorResource(R.color.tf_baja_fg_dark),
    chartBlue = colorResource(R.color.tf_chart_blue),
    chartGreen = colorResource(R.color.tf_chart_green),
    chartRed = colorResource(R.color.tf_chart_red),
    chartAmber = colorResource(R.color.tf_chart_amber),
    chartPurple = colorResource(R.color.tf_chart_purple)
)
