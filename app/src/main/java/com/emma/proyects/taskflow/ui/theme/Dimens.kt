package com.emma.proyects.taskflow.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.emma.proyects.taskflow.R

object TaskFlowDimens {
    val spacingXxs: Dp @Composable get() = dimensionResource(R.dimen.spacing_xxs)
    val pillPaddingVertical: Dp @Composable get() = dimensionResource(R.dimen.pill_padding_vertical)
    val spacingXs: Dp @Composable get() = dimensionResource(R.dimen.spacing_xs)
    val spacingSm: Dp @Composable get() = dimensionResource(R.dimen.spacing_sm)
    val spacingMd: Dp @Composable get() = dimensionResource(R.dimen.spacing_md)
    val spacingBase: Dp @Composable get() = dimensionResource(R.dimen.spacing_base)
    val spacingLg: Dp @Composable get() = dimensionResource(R.dimen.spacing_lg)
    val spacingXl: Dp @Composable get() = dimensionResource(R.dimen.spacing_xl)
    val spacing2xl: Dp @Composable get() = dimensionResource(R.dimen.spacing_2xl)
    val spacing3xl: Dp @Composable get() = dimensionResource(R.dimen.spacing_3xl)
    val spacing4xl: Dp @Composable get() = dimensionResource(R.dimen.spacing_4xl)
    val spacing5xl: Dp @Composable get() = dimensionResource(R.dimen.spacing_5xl)
    val spacing6xl: Dp @Composable get() = dimensionResource(R.dimen.spacing_6xl)
    val bottomListPadding: Dp @Composable get() = dimensionResource(R.dimen.bottom_list_padding)

    val radiusXs: Dp @Composable get() = dimensionResource(R.dimen.radius_xs)
    val radiusDayCell: Dp @Composable get() = dimensionResource(R.dimen.radius_day_cell)
    val radiusSm: Dp @Composable get() = dimensionResource(R.dimen.radius_sm)
    val radiusMd: Dp @Composable get() = dimensionResource(R.dimen.radius_md)
    val radiusLg: Dp @Composable get() = dimensionResource(R.dimen.radius_lg)
    val radiusXl: Dp @Composable get() = dimensionResource(R.dimen.radius_xl)
    val radiusFab: Dp @Composable get() = dimensionResource(R.dimen.radius_fab)
    val radiusCardLg: Dp @Composable get() = dimensionResource(R.dimen.radius_card_lg)
    val radius2xl: Dp @Composable get() = dimensionResource(R.dimen.radius_2xl)
    val radiusDialog: Dp @Composable get() = dimensionResource(R.dimen.radius_dialog)
    val radiusPill: Dp @Composable get() = dimensionResource(R.dimen.radius_pill)

    val strokeThin: Dp @Composable get() = dimensionResource(R.dimen.stroke_thin)
    val strokeDashed: Dp @Composable get() = dimensionResource(R.dimen.stroke_dashed)
    val elevationNone: Dp @Composable get() = dimensionResource(R.dimen.elevation_none)
    val elevationNav: Dp @Composable get() = dimensionResource(R.dimen.elevation_nav)

    val btnHeightPrimary: Dp @Composable get() = dimensionResource(R.dimen.btn_height_primary)
    val btnHeightSecondary: Dp @Composable get() = dimensionResource(R.dimen.btn_height_secondary)
    val btnHeightDropdown: Dp @Composable get() = dimensionResource(R.dimen.btn_height_dropdown)
    val chipHeight: Dp @Composable get() = dimensionResource(R.dimen.chip_height)
    val recurrenceDayHeight: Dp @Composable get() = dimensionResource(R.dimen.recurrence_day_height)
    val weekDayChipWidth: Dp @Composable get() = dimensionResource(R.dimen.week_day_chip_width)
    val monthDayChipWidth: Dp @Composable get() = dimensionResource(R.dimen.month_day_chip_width)
    val iconBtnCompact: Dp @Composable get() = dimensionResource(R.dimen.icon_btn_compact)
    val avatarSize: Dp @Composable get() = dimensionResource(R.dimen.avatar_size)
    val settingsIconBox: Dp @Composable get() = dimensionResource(R.dimen.settings_icon_box)
    val detailIconBox: Dp @Composable get() = dimensionResource(R.dimen.detail_icon_box)
    val emptyIconBox: Dp @Composable get() = dimensionResource(R.dimen.empty_icon_box)
    val errorIconBox: Dp @Composable get() = dimensionResource(R.dimen.error_icon_box)
    val logoBadgeLg: Dp @Composable get() = dimensionResource(R.dimen.logo_badge_lg)
    val logoBadgeMd: Dp @Composable get() = dimensionResource(R.dimen.logo_badge_md)
    val logoBadgeSm: Dp @Composable get() = dimensionResource(R.dimen.logo_badge_sm)
    val logoIconLg: Dp @Composable get() = dimensionResource(R.dimen.logo_icon_lg)
    val logoIconMd: Dp @Composable get() = dimensionResource(R.dimen.logo_icon_md)
    val logoIconSm: Dp @Composable get() = dimensionResource(R.dimen.logo_icon_sm)

    val iconXxs: Dp @Composable get() = dimensionResource(R.dimen.icon_xxs)
    val iconXs: Dp @Composable get() = dimensionResource(R.dimen.icon_xs)
    val iconSm: Dp @Composable get() = dimensionResource(R.dimen.icon_sm)
    val iconMd: Dp @Composable get() = dimensionResource(R.dimen.icon_md)
    val iconBase: Dp @Composable get() = dimensionResource(R.dimen.icon_base)
    val iconLg: Dp @Composable get() = dimensionResource(R.dimen.icon_lg)
    val iconXl: Dp @Composable get() = dimensionResource(R.dimen.icon_xl)
    val icon2xl: Dp @Composable get() = dimensionResource(R.dimen.icon_2xl)
    val icon3xl: Dp @Composable get() = dimensionResource(R.dimen.icon_3xl)

    val chartBarHeight: Dp @Composable get() = dimensionResource(R.dimen.chart_bar_height)
    val chartBarWidth: Dp @Composable get() = dimensionResource(R.dimen.chart_bar_width)
    val chartDonutSize: Dp @Composable get() = dimensionResource(R.dimen.chart_donut_size)
    val chartDonutStroke: Dp @Composable get() = dimensionResource(R.dimen.chart_donut_stroke)
    val chartLegendDot: Dp @Composable get() = dimensionResource(R.dimen.chart_legend_dot)
}

object TaskFlowAlphas {
    const val FULL = 1f
    const val ERROR_BORDER_SUBTLE = 0.50f
    const val ERROR_ICON_BG = 0.14f
    const val EMPTY_ICON_BG = 0.15f
    const val LOGO_GRADIENT_END = 0.82f
    const val COMPLETED_CARD = 0.78f
    const val CHART_TRACK = 0.50f
    const val CHART_NEUTRAL_ARC = 0.35f
}
