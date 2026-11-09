package com.emma.proyects.taskflow.tasks.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.tasks.domain.model.RecurrenceUnit
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.tasks.presentation.components.DeleteTaskDialog
import com.emma.proyects.taskflow.tasks.presentation.components.ErrorStateCard
import com.emma.proyects.taskflow.tasks.presentation.components.ImportanceChip
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TaskDetailViewModel
import com.emma.proyects.taskflow.ui.theme.TaskFlowDesign
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens
import com.emma.proyects.taskflow.ui.theme.TaskFlowTypography
import java.time.DayOfWeek

@Composable
fun TaskDetailRoute(
    viewModel: TaskDetailViewModel,
    refreshSignal: Boolean,
    onConsumeRefreshSignal: () -> Unit,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDeleted: (UiText) -> Unit,
    onTaskMutated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(refreshSignal) {
        if (refreshSignal) {
            viewModel.loadTask()
            onConsumeRefreshSignal()
        }
    }

    LaunchedEffect(uiState.deletedFeedback) {
        uiState.deletedFeedback?.let { feedback ->
            viewModel.consumeDeletedFeedback()
            onDeleted(feedback)
        }
    }

    TaskDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadTask,
        onEdit = {
            uiState.task?.let { onEdit(it.id) }
        },
        onToggleComplete = {
            viewModel.onToggleComplete()
            onTaskMutated()
        },
        onRequestDelete = viewModel::onRequestDelete,
        onDismissDelete = viewModel::onDismissDeleteDialog,
        onConfirmDelete = viewModel::onConfirmDelete,
        onClearUserMessage = viewModel::clearUserMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    uiState: TaskDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onEdit: () -> Unit,
    onToggleComplete: () -> Unit,
    onRequestDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onClearUserMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message.asString(context))
            onClearUserMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.detail_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    if (uiState.task != null) {
                        IconButton(onClick = onEdit, enabled = !uiState.isMutating) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = stringResource(R.string.btn_edit_task),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = onRequestDelete, enabled = !uiState.isMutating) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = stringResource(R.string.btn_delete_task),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.loading_detail),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            uiState.errorMessage != null || uiState.task == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(TaskFlowDimens.spacing4xl),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorStateCard(
                        message = uiState.errorMessage?.asString()
                            ?: stringResource(R.string.detail_not_found),
                        onRetry = onRetry
                    )
                }
            }

            else -> {
                val task = uiState.task
                val statusText = when {
                    task.isCompleted -> stringResource(R.string.detail_status_completed)
                    task.isOverdue -> stringResource(R.string.detail_status_overdue)
                    else -> stringResource(R.string.detail_status_pending)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(TaskFlowDimens.spacing4xl),
                    verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacing2xl)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(TaskFlowDimens.radiusCardLg),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(TaskFlowDimens.spacing4xl),
                            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXl)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
                            ) {
                                Icon(
                                    imageVector = if (task.isCompleted) {
                                        Icons.Outlined.CheckCircle
                                    } else {
                                        Icons.Outlined.RadioButtonUnchecked
                                    },
                                    contentDescription = stringResource(R.string.cd_toggle_task_completion),
                                    tint = if (task.isCompleted) {
                                        TaskFlowDesign.extraColors.success
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    },
                                    modifier = Modifier
                                        .size(TaskFlowDimens.icon2xl)
                                        .clip(CircleShape)
                                        .clickable(enabled = !uiState.isMutating, onClick = onToggleComplete)
                                )
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
                            ) {
                                ImportanceChip(importance = task.importance)

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

                                Text(
                                    text = stringResource(R.string.task_priority_format, task.priorityLevel),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = task.description.ifBlank {
                                    stringResource(R.string.detail_no_description)
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                            DetailInfoRow(
                                icon = Icons.Outlined.CalendarToday,
                                label = stringResource(R.string.detail_label_due_date),
                                value = task.dueDateFormatted ?: stringResource(R.string.detail_no_due_date)
                            )

                            DetailInfoRow(
                                icon = Icons.Outlined.Info,
                                label = stringResource(R.string.detail_label_status),
                                value = statusText
                            )

                            DetailInfoRow(
                                icon = Icons.Outlined.Autorenew,
                                label = stringResource(R.string.detail_label_recurrence),
                                value = formatRecurrenceDetailText(task)
                            )

                            DetailInfoRow(
                                icon = Icons.Outlined.Schedule,
                                label = stringResource(R.string.detail_label_created),
                                value = task.createdDateFormatted.ifBlank {
                                    stringResource(R.string.task_created_today)
                                }
                            )

                            DetailInfoRow(
                                icon = Icons.Outlined.Tag,
                                label = stringResource(R.string.detail_label_id),
                                value = task.id
                            )
                        }
                    }

                    Button(
                        onClick = onToggleComplete,
                        enabled = !uiState.isMutating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(TaskFlowDimens.btnHeightPrimary),
                        shape = RoundedCornerShape(TaskFlowDimens.radiusXl)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(TaskFlowDimens.iconMd)
                        )
                        Spacer(modifier = Modifier.size(TaskFlowDimens.spacingSm))
                        Text(
                            text = if (task.isCompleted) {
                                stringResource(R.string.btn_mark_pending)
                            } else {
                                stringResource(R.string.btn_mark_completed)
                            },
                            style = TaskFlowTypography.primaryCtaCompactStyle,
                            maxLines = TaskConstants.SINGLE_LINE,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = onRequestDelete,
                        enabled = !uiState.isMutating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(TaskFlowDimens.btnHeightPrimary),
                        shape = RoundedCornerShape(TaskFlowDimens.radiusXl),
                        border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.error),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = null,
                            modifier = Modifier.size(TaskFlowDimens.iconBase)
                        )
                        Spacer(modifier = Modifier.size(TaskFlowDimens.spacingMd))
                        Text(
                            text = stringResource(R.string.btn_delete_task),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }

    if (uiState.showDeleteDialog && uiState.task != null) {
        DeleteTaskDialog(
            taskTitle = uiState.task.title,
            onConfirmDelete = onConfirmDelete,
            onDismiss = onDismissDelete
        )
    }
}

@Composable
private fun formatRecurrenceDetailText(task: Task): String {
    if (!task.isRecurring) {
        return stringResource(R.string.detail_no_recurrence)
    }

    val unitLabel = when (task.recurrenceUnit) {
        RecurrenceUnit.HOURS -> stringResource(R.string.unit_hours)
        RecurrenceUnit.DAYS -> stringResource(R.string.unit_days)
        RecurrenceUnit.WEEKS -> stringResource(R.string.unit_weeks)
        RecurrenceUnit.MONTHS -> stringResource(R.string.unit_months)
        RecurrenceUnit.YEARS -> stringResource(R.string.unit_years)
    }

    val baseText = stringResource(
        R.string.detail_recurrence_format,
        task.recurrenceInterval,
        unitLabel
    )

    return when (task.recurrenceUnit) {
        RecurrenceUnit.WEEKS -> {
            val days = task.recurrenceDaysOfWeek
            if (days.isNotEmpty()) {
                val dayLabels = days.sortedBy { it.value }.map { day ->
                    val labelRes = when (day) {
                        DayOfWeek.MONDAY -> R.string.day_short_mon
                        DayOfWeek.TUESDAY -> R.string.day_short_tue
                        DayOfWeek.WEDNESDAY -> R.string.day_short_wed
                        DayOfWeek.THURSDAY -> R.string.day_short_thu
                        DayOfWeek.FRIDAY -> R.string.day_short_fri
                        DayOfWeek.SATURDAY -> R.string.day_short_sat
                        DayOfWeek.SUNDAY -> R.string.day_short_sun
                    }
                    stringResource(labelRes)
                }
                stringResource(
                    R.string.detail_recurrence_days_format,
                    baseText,
                    dayLabels.joinToString(TaskConstants.LIST_SEPARATOR)
                )
            } else {
                baseText
            }
        }

        RecurrenceUnit.MONTHS -> {
            val days = task.recurrenceDaysOfMonth
            if (days.isNotEmpty()) {
                val sortedDays = days.sorted().joinToString(TaskConstants.LIST_SEPARATOR)
                stringResource(R.string.detail_recurrence_month_days_format, baseText, sortedDays)
            } else {
                baseText
            }
        }

        else -> baseText
    }
}

@Composable
private fun DetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
    ) {
        Box(
            modifier = Modifier
                .size(TaskFlowDimens.detailIconBox)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(TaskFlowDimens.radiusSm)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(TaskFlowDimens.iconMd)
            )
        }
        Column(modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = TaskConstants.DETAIL_VALUE_MAX_LINES,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
