package com.emma.proyects.taskflow.tasks.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.ui.theme.TaskFlowAlphas
import com.emma.proyects.taskflow.ui.theme.TaskFlowDesign
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens
import com.emma.proyects.taskflow.ui.theme.TaskFlowTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOverdueActive = task.isOverdue && !task.isCompleted

    val containerColor = if (isOverdueActive) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val borderColor = if (isOverdueActive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.outline
    }

    val cardAlpha = if (task.isCompleted) TaskFlowAlphas.COMPLETED_CARD else TaskFlowAlphas.FULL

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(TaskFlowDimens.radiusXl),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(TaskFlowDimens.strokeThin, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = TaskFlowDimens.elevationNone)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TaskFlowDimens.spacingXl),
            horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg),
            verticalAlignment = Alignment.Top
        ) {
            val checkIcon = if (task.isCompleted) {
                Icons.Outlined.CheckCircle
            } else {
                Icons.Outlined.RadioButtonUnchecked
            }

            val checkTint = when {
                task.isCompleted -> TaskFlowDesign.extraColors.success
                isOverdueActive -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Icon(
                imageVector = checkIcon,
                contentDescription = stringResource(R.string.cd_toggle_task_completion),
                tint = checkTint,
                modifier = Modifier
                    .padding(top = TaskFlowDimens.spacingXxs)
                    .size(TaskFlowDimens.iconXl)
                    .clip(CircleShape)
                    .clickable(onClick = onToggle)
            )

            Column(
                modifier = Modifier.weight(TaskConstants.WEIGHT_FULL),
                verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textDecoration = if (task.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        },
                        modifier = Modifier
                            .weight(TaskConstants.WEIGHT_FULL)
                            .padding(end = TaskFlowDimens.spacingMd)
                    )

                    ImportanceChip(importance = task.importance)
                }

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = TaskConstants.DESCRIPTION_MAX_LINES,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingBase),
                    verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm),
                    itemVerticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(TaskFlowDimens.radiusXs)
                    ) {
                        Text(
                            text = task.area.ifBlank { stringResource(R.string.area_general) }.uppercase(),
                            style = TaskFlowTypography.badgeLabelStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                horizontal = TaskFlowDimens.spacingMd,
                                vertical = TaskFlowDimens.spacingXxs
                            )
                        )
                    }

                    if (task.dueDateFormatted != null) {
                        val dateColor = if (isOverdueActive) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXs)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = dateColor,
                                modifier = Modifier.size(TaskFlowDimens.iconXxs)
                            )
                            Text(
                                text = task.dueDateFormatted,
                                style = MaterialTheme.typography.labelSmall,
                                color = dateColor
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXs)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(TaskFlowDimens.iconXxs)
                        )
                        Text(
                            text = stringResource(R.string.task_priority_format, task.priorityLevel),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (task.isRecurring) {
                        Icon(
                            imageVector = Icons.Outlined.Autorenew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(TaskFlowDimens.iconXs)
                        )
                    }
                }
            }
        }
    }
}
