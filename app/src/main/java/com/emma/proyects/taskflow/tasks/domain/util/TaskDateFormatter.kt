package com.emma.proyects.taskflow.tasks.domain.util

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TaskDateFormatter {
    private const val SPANISH_LANGUAGE_TAG = "es-MX"
    private const val SHORT_DATE_PATTERN = "d MMM"
    private const val TIME_ZONE_UTC = "UTC"
    private const val TIME_24H_FORMAT = "%02d:%02d"
    private const val DATE_TIME_SEPARATOR = ", "
    private const val SPACE_DELIMITER = " "
    private const val TIME_DELIMITER = ":"
    private const val INDEX_HOUR = 0
    private const val INDEX_MINUTE = 1
    private const val TRAILING_DOT = '.'

    private val spanishLocale: Locale = Locale.forLanguageTag(SPANISH_LANGUAGE_TAG)

    fun formatShortDateFromUtcMillis(utcMillis: Long): String {
        val formatter = SimpleDateFormat(SHORT_DATE_PATTERN, spanishLocale).apply {
            timeZone = TimeZone.getTimeZone(TIME_ZONE_UTC)
        }
        return formatter.format(Date(utcMillis))
    }

    fun formatTodayShortDate(): String {
        val formatter = SimpleDateFormat(SHORT_DATE_PATTERN, spanishLocale)
        return formatter.format(Date())
    }

    fun formatTime24h(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), TIME_24H_FORMAT, hour, minute)
    }

    fun combineDueDate(selectedDate: String, selectedTime: String): String? {
        return when {
            selectedDate.isNotBlank() && selectedTime.isNotBlank() ->
                "$selectedDate$DATE_TIME_SEPARATOR$selectedTime"
            selectedDate.isNotBlank() -> selectedDate
            selectedTime.isNotBlank() ->
                "${formatTodayShortDate()}$DATE_TIME_SEPARATOR$selectedTime"
            else -> null
        }
    }

    fun splitDueDateParts(dueDateFormatted: String?): Pair<String, String> {
        val parts = dueDateFormatted?.split(DATE_TIME_SEPARATOR.trim())
            ?.map { it.trim() }
            .orEmpty()
        return parts.getOrNull(0).orEmpty() to parts.getOrNull(1).orEmpty()
    }

    fun parseHour(timeText: String): Int? =
        timeText.split(TIME_DELIMITER).getOrNull(INDEX_HOUR)?.trim()?.toIntOrNull()

    fun parseMinute(timeText: String): Int? =
        timeText.split(TIME_DELIMITER).getOrNull(INDEX_MINUTE)?.trim()?.toIntOrNull()

    fun parseSpanishShortDate(dateText: String): LocalDate? {
        return try {
            val tokens = dateText.lowercase(spanishLocale)
                .trim()
                .split(SPACE_DELIMITER)
                .filter { it.isNotBlank() }
            val day = tokens.firstOrNull()?.toIntOrNull() ?: return null
            val rawMonth = tokens.lastOrNull()?.trimEnd(TRAILING_DOT) ?: return null
            val matchedMonth = Month.entries.firstOrNull { month ->
                val full = month.getDisplayName(TextStyle.FULL, spanishLocale).lowercase(spanishLocale)
                val short = month.getDisplayName(TextStyle.SHORT, spanishLocale)
                    .lowercase(spanishLocale)
                    .trimEnd(TRAILING_DOT)
                rawMonth == full || rawMonth == short
            } ?: return null

            LocalDate.of(LocalDate.now().year, matchedMonth.value, day)
        } catch (_: Exception) {
            null
        }
    }
}
