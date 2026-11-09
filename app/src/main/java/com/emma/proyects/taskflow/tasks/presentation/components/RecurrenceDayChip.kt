package com.emma.proyects.taskflow.tasks.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

@Composable
fun RecurrenceDayChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    minWidth: Dp,
    shape: Shape,
    modifier: Modifier = Modifier,
    unselectedFontWeight: FontWeight = FontWeight.Medium
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val border = if (selected) {
        null
    } else {
        BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.outline)
    }

    Surface(
        modifier = modifier
            .widthIn(min = minWidth)
            .height(TaskFlowDimens.recurrenceDayHeight)
            .clip(shape)
            .toggleable(
                value = selected,
                role = Role.Checkbox,
                onValueChange = { onClick() }
            ),
        shape = shape,
        color = containerColor,
        border = border
    ) {
        Box(
            modifier = Modifier.padding(horizontal = TaskFlowDimens.spacingXs),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (selected) FontWeight.SemiBold else unselectedFontWeight
                ),
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = TaskConstants.SINGLE_LINE,
                softWrap = false
            )
        }
    }
}
