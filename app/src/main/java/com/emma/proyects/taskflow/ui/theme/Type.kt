package com.emma.proyects.taskflow.ui.theme

import androidx.annotation.DimenRes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.emma.proyects.taskflow.R

@Composable
private fun spResource(@DimenRes id: Int): TextUnit {
    val density = LocalDensity.current
    return with(density) { dimensionResource(id).toSp() }
}

object TaskFlowTypography {
    val badgeLabelStyle: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = spResource(R.dimen.text_badge_label),
            lineHeight = spResource(R.dimen.line_badge_label)
        )

    val compactButtonTextStyle: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = spResource(R.dimen.text_compact_button),
            lineHeight = spResource(R.dimen.line_compact_button)
        )

    val primaryCtaCompactStyle: TextStyle
        @Composable get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = spResource(R.dimen.text_primary_cta_compact),
            lineHeight = spResource(R.dimen.line_primary_cta_compact)
        )
}

@Composable
fun taskFlowTypography(): Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = spResource(R.dimen.text_headline_large),
        lineHeight = spResource(R.dimen.line_headline_large),
        letterSpacing = spResource(R.dimen.letter_spacing_headline_large)
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = spResource(R.dimen.text_headline_medium),
        lineHeight = spResource(R.dimen.line_headline_medium),
        letterSpacing = spResource(R.dimen.letter_spacing_headline_medium)
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = spResource(R.dimen.text_title_large),
        lineHeight = spResource(R.dimen.line_title_large)
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = spResource(R.dimen.text_title_medium),
        lineHeight = spResource(R.dimen.line_title_medium)
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = spResource(R.dimen.text_title_small),
        lineHeight = spResource(R.dimen.line_title_small)
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = spResource(R.dimen.text_body_large),
        lineHeight = spResource(R.dimen.line_body_large)
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = spResource(R.dimen.text_body_medium),
        lineHeight = spResource(R.dimen.line_body_medium)
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = spResource(R.dimen.text_label_medium),
        lineHeight = spResource(R.dimen.line_label_medium)
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = spResource(R.dimen.text_label_small),
        lineHeight = spResource(R.dimen.line_label_small)
    )
)
