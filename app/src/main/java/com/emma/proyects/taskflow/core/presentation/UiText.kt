package com.emma.proyects.taskflow.core.presentation

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.emma.proyects.taskflow.core.domain.network.AppException

sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    data class StringResource(
        @param:StringRes val resId: Int,
        val args: List<Any> = emptyList()
    ) : UiText {
        constructor(@StringRes resId: Int, vararg args: Any) : this(resId, args.toList())
    }

    data class PluralResource(
        @param:PluralsRes val resId: Int,
        val count: Int,
        val args: List<Any> = listOf(count)
    ) : UiText

    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
        is StringResource -> stringResource(resId, *args.toTypedArray())
        is PluralResource -> pluralStringResource(resId, count, *args.toTypedArray())
    }

    fun asString(context: Context): String = when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(resId, *args.toTypedArray())
        is PluralResource -> context.resources.getQuantityString(resId, count, *args.toTypedArray())
    }
}

fun Throwable.toUiText(@StringRes fallbackResId: Int): UiText = when (this) {
    is AppException.ServerMessage -> UiText.DynamicString(detail)
    is AppException.ResourceMessage -> UiText.StringResource(resId, args)
    else -> localizedMessage?.takeIf { it.isNotBlank() }
        ?.let { UiText.DynamicString(it) }
        ?: UiText.StringResource(fallbackResId)
}
