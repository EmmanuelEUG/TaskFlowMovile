package com.emma.proyects.taskflow.tasks.domain.model

object TaskConstants {
    const val MIN_TITLE_LENGTH = 2
    const val MIN_PRIORITY = 1
    const val MAX_PRIORITY = 5
    const val DEFAULT_PRIORITY = 3
    const val PRIORITY_SLIDER_STEPS = 3

    const val MIN_RECURRENCE_INTERVAL = 1
    const val DEFAULT_RECURRENCE_INTERVAL_TEXT = "1"

    const val RECENT_COMPLETED_LIMIT = 8
    const val DAYS_IN_WEEK = 7
    const val WEEK_LAST_DAY_OFFSET = 6L
    const val WEEK_MAX_DAY_INDEX = 6
    const val MIN_LIST_INDEX = 0
    const val FIRST_DAY_OF_MONTH = 1
    const val MAX_MONTH_DAYS = 31

    const val PERCENT_MAX = 100
    const val PERCENT_MIN = 0
    const val MIN_BAR_CHART_SCALE = 4f
    const val MIN_CHART_SUM = 1f
    const val CHART_START_ANGLE = -90f
    const val CHART_ZERO_ANGLE = 0f
    const val CHART_FULL_CIRCLE = 360f

    const val SINGLE_LINE = 1
    const val DESCRIPTION_MAX_LINES = 2
    const val DETAIL_VALUE_MAX_LINES = 2
    const val FORM_DESCRIPTION_MIN_LINES = 3

    const val DEFAULT_DUE_HOUR = 23
    const val DEFAULT_DUE_MINUTE = 59
    const val DEFAULT_TIME_PICKER_HOUR = 18
    const val DEFAULT_TIME_PICKER_MINUTE = 0

    const val WEIGHT_FULL = 1f
    const val WEIGHT_RECURRENCE_INTERVAL = 0.42f
    const val WEIGHT_RECURRENCE_UNIT = 0.58f
    const val PRIORITY_FACTOR_ONE_WEEK = 1L

    const val LIST_SEPARATOR = ", "
}
