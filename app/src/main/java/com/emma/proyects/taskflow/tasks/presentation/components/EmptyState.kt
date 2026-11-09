package com.emma.proyects.taskflow.tasks.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import com.emma.proyects.taskflow.ui.theme.TaskFlowAlphas
import com.emma.proyects.taskflow.ui.theme.TaskFlowDesign
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

private const val DASH_PHASE = 0f

@Composable
fun EmptyState(
    title: String,
    hint: String,
    modifier: Modifier = Modifier,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null
) {
    val outlineColor = MaterialTheme.colorScheme.outline
    val strokeWidthDp = TaskFlowDimens.strokeDashed
    val cornerRadiusDp = TaskFlowDimens.radius2xl
    val dashOnDp = TaskFlowDimens.spacing2xl
    val dashOffDp = TaskFlowDimens.spacingLg

    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = strokeWidthDp.toPx()
                val cornerRadius = cornerRadiusDp.toPx()
                drawRoundRect(
                    color = outlineColor,
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(dashOnDp.toPx(), dashOffDp.toPx()),
                            DASH_PHASE
                        )
                    )
                )
            }
            .padding(
                vertical = TaskFlowDimens.spacing6xl,
                horizontal = TaskFlowDimens.spacing4xl
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingBase)
    ) {
        Box(
            modifier = Modifier
                .size(TaskFlowDimens.emptyIconBox)
                .background(
                    color = TaskFlowDesign.extraColors.success.copy(alpha = TaskFlowAlphas.EMPTY_ICON_BG),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = TaskFlowDesign.extraColors.success,
                modifier = Modifier.size(TaskFlowDimens.icon3xl)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = hint,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (primaryActionLabel != null && onPrimaryAction != null) {
            Spacer(modifier = Modifier.height(TaskFlowDimens.spacingXs))
            Button(
                onClick = onPrimaryAction,
                shape = RoundedCornerShape(TaskFlowDimens.radiusMd)
            ) {
                Text(text = primaryActionLabel)
            }
        }

        if (secondaryActionLabel != null && onSecondaryAction != null) {
            OutlinedButton(
                onClick = onSecondaryAction,
                shape = RoundedCornerShape(TaskFlowDimens.radiusMd)
            ) {
                Text(text = secondaryActionLabel)
            }
        }
    }
}
