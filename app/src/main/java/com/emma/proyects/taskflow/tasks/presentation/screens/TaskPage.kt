package com.emma.proyects.taskflow.tasks.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.tasks.domain.model.AppRoute
import com.emma.proyects.taskflow.tasks.domain.model.DueDateFilter
import com.emma.proyects.taskflow.tasks.domain.model.Importance
import com.emma.proyects.taskflow.tasks.domain.model.Task
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.tasks.presentation.components.AppBottomBar
import com.emma.proyects.taskflow.tasks.presentation.components.AppFilterChip
import com.emma.proyects.taskflow.tasks.presentation.components.BrandLogoBadge
import com.emma.proyects.taskflow.tasks.presentation.components.EmptyState
import com.emma.proyects.taskflow.tasks.presentation.components.ErrorStateCard
import com.emma.proyects.taskflow.tasks.presentation.components.SectionHeader
import com.emma.proyects.taskflow.tasks.presentation.components.TaskCard
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TasksViewModel
import com.emma.proyects.taskflow.ui.theme.TaskFlowDesign
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

private object TaskListItemKeyPrefix {
    const val OVERDUE = "overdue_"
    const val PENDING = "pending_"
    const val COMPLETED = "completed_"
}

@Composable
fun HomeRoute(
    viewModel: TasksViewModel,
    onNavigateTab: (AppRoute) -> Unit,
    onOpenTaskDetail: (String) -> Unit,
    onOpenNewTaskForm: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TaskPage(
        uiState = uiState,
        onToggleTask = viewModel::onToggleTask,
        onTaskClick = { task -> onOpenTaskDetail(task.id) },
        onNewTaskClick = onOpenNewTaskForm,
        onSearchChange = viewModel::onSearchQueryChanged,
        onAreaSelected = viewModel::onAreaSelected,
        onImportanceSelected = viewModel::onImportanceSelected,
        onDueDateFilterSelected = viewModel::onDueDateFilterSelected,
        onRecurrenceFilterSelected = viewModel::onRecurrenceFilterSelected,
        onNavigate = onNavigateTab,
        onToggleSortOrder = viewModel::onToggleSortOrder,
        onRefresh = { viewModel.loadTasks(isPullRefresh = true) },
        onRetry = { viewModel.loadTasks(isPullRefresh = false) },
        onClearFilters = viewModel::onClearFilters,
        onLoadSeedTasks = viewModel::onRestoreSeedTasks,
        onClearUserMessage = viewModel::clearUserMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskPage(
    uiState: TasksHomeUiState,
    onToggleTask: (Task) -> Unit,
    onTaskClick: (Task) -> Unit,
    onNewTaskClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onAreaSelected: (String?) -> Unit,
    onImportanceSelected: (Importance?) -> Unit,
    onDueDateFilterSelected: (DueDateFilter) -> Unit,
    onRecurrenceFilterSelected: (RecurrenceFilter) -> Unit,
    onNavigate: (AppRoute) -> Unit,
    onToggleSortOrder: () -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onClearFilters: () -> Unit,
    onLoadSeedTasks: () -> Unit,
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
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingBase)
                    ) {
                        BrandLogoBadge(
                            size = TaskFlowDimens.logoBadgeSm,
                            iconSize = TaskFlowDimens.logoIconSm
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = TaskConstants.SINGLE_LINE,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXxs),
                        modifier = Modifier.padding(end = TaskFlowDimens.spacingMd)
                    ) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier.size(TaskFlowDimens.iconBtnCompact)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = stringResource(R.string.cd_refresh_tasks),
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(TaskFlowDimens.iconBase)
                            )
                        }
                        IconButton(
                            onClick = onToggleSortOrder,
                            modifier = Modifier.size(TaskFlowDimens.iconBtnCompact)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SwapVert,
                                contentDescription = stringResource(R.string.cd_sort_tasks),
                                tint = if (uiState.sortDescendingByPriority) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier.size(TaskFlowDimens.iconBase)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewTaskClick,
                shape = RoundedCornerShape(TaskFlowDimens.radiusFab),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        modifier = Modifier.size(TaskFlowDimens.iconLg)
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.fab_new_task),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedRoute = AppRoute.HOME,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = stringResource(R.string.loading_tasks),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                uiState.errorMessage != null && uiState.allTasks.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(TaskFlowDimens.spacing4xl),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorStateCard(
                            message = uiState.errorMessage.asString(),
                            onRetry = onRetry
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = TaskFlowDimens.spacingMd,
                            bottom = TaskFlowDimens.bottomListPadding,
                            start = TaskFlowDimens.spacing4xl,
                            end = TaskFlowDimens.spacing4xl
                        ),
                        verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXl)
                    ) {
                        if (uiState.errorMessage != null) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(TaskFlowDimens.radiusLg),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    ),
                                    border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.error)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = TaskFlowDimens.spacingXl,
                                                vertical = TaskFlowDimens.spacingBase
                                            ),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = uiState.errorMessage.asString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)
                                        )
                                        TextButton(onClick = onRetry) {
                                            Text(text = stringResource(R.string.btn_retry))
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXs)) {
                                if (uiState.userName.isNotBlank()) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(TaskFlowDimens.radiusPill)
                                    ) {
                                        Text(
                                            text = stringResource(
                                                R.string.dashboard_greeting,
                                                uiState.userName
                                            ),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(
                                                horizontal = TaskFlowDimens.spacingBase,
                                                vertical = TaskFlowDimens.pillPaddingVertical
                                            )
                                        )
                                    }
                                }
                                Text(
                                    text = stringResource(R.string.dashboard_title),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = pluralStringResource(
                                        R.plurals.dashboard_subtitle,
                                        uiState.activeTodoCount,
                                        uiState.activeTodoCount
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.search_placeholder),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                shape = RoundedCornerShape(TaskFlowDimens.radiusLg),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                singleLine = true
                            )
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
                            ) {
                                val areaOptions = listOf(
                                    null to R.string.area_all,
                                    stringResource(R.string.area_school) to R.string.area_school,
                                    stringResource(R.string.area_home) to R.string.area_home,
                                    stringResource(R.string.area_health) to R.string.area_health,
                                    stringResource(R.string.area_project) to R.string.area_project
                                )
                                areaOptions.forEach { (areaFilterValue, resId) ->
                                    val isSelected = if (areaFilterValue == null) {
                                        uiState.selectedArea == null
                                    } else {
                                        uiState.selectedArea.equals(areaFilterValue, ignoreCase = true)
                                    }
                                    AppFilterChip(
                                        label = stringResource(resId),
                                        selected = isSelected,
                                        onClick = { onAreaSelected(areaFilterValue) }
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
                            ) {
                                Text(
                                    text = stringResource(R.string.due_filter_label),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val dateFilters = listOf(
                                    DueDateFilter.ALL to R.string.due_filter_all,
                                    DueDateFilter.TODAY to R.string.due_filter_today,
                                    DueDateFilter.THIS_WEEK to R.string.due_filter_this_week,
                                    DueDateFilter.NEXT_WEEK to R.string.due_filter_next_week,
                                    DueDateFilter.LATER to R.string.due_filter_later,
                                    DueDateFilter.NO_DATE to R.string.due_filter_no_date
                                )
                                dateFilters.forEach { (filter, labelRes) ->
                                    AppFilterChip(
                                        label = stringResource(labelRes),
                                        selected = uiState.selectedDueDateFilter == filter,
                                        onClick = { onDueDateFilterSelected(filter) }
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
                            ) {
                                Text(
                                    text = stringResource(R.string.recurrence_filter_label),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val recFilters = listOf(
                                    RecurrenceFilter.ALL to R.string.recurrence_all,
                                    RecurrenceFilter.RECURRING_ONLY to R.string.recurrence_only,
                                    RecurrenceFilter.NON_RECURRING_ONLY to R.string.recurrence_none
                                )
                                recFilters.forEach { (filter, labelRes) ->
                                    AppFilterChip(
                                        label = stringResource(labelRes),
                                        selected = uiState.selectedRecurrenceFilter == filter,
                                        onClick = { onRecurrenceFilterSelected(filter) }
                                    )
                                }
                            }
                        }

                        item {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd),
                                verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm),
                                itemVerticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.importance_label),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                val importances = listOf(
                                    Importance.HIGH to R.string.importance_high,
                                    Importance.MEDIUM to R.string.importance_medium,
                                    Importance.LOW to R.string.importance_low
                                )
                                importances.forEach { (imp, resId) ->
                                    AppFilterChip(
                                        label = stringResource(resId),
                                        selected = uiState.selectedImportance == imp,
                                        onClick = { onImportanceSelected(imp) },
                                        showCheckIcon = false
                                    )
                                }
                                if (uiState.hasActiveFilters) {
                                    TextButton(
                                        onClick = onClearFilters,
                                        contentPadding = PaddingValues(
                                            horizontal = TaskFlowDimens.spacingMd,
                                            vertical = TaskFlowDimens.elevationNone
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(R.string.btn_clear_filters),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        if (uiState.allTasks.isEmpty()) {
                            item {
                                EmptyState(
                                    title = stringResource(R.string.empty_state_title),
                                    hint = stringResource(R.string.empty_state_hint),
                                    primaryActionLabel = stringResource(R.string.fab_new_task),
                                    onPrimaryAction = onNewTaskClick,
                                    secondaryActionLabel = stringResource(R.string.btn_load_seed),
                                    onSecondaryAction = onLoadSeedTasks
                                )
                            }
                        } else if (uiState.isFilteredEmpty) {
                            item {
                                EmptyState(
                                    title = stringResource(R.string.empty_filtered_title),
                                    hint = stringResource(R.string.empty_filtered_hint),
                                    primaryActionLabel = stringResource(R.string.btn_clear_filters),
                                    onPrimaryAction = onClearFilters
                                )
                            }
                        } else {
                            if (uiState.overdueTasks.isNotEmpty()) {
                                item {
                                    SectionHeader(
                                        icon = Icons.Outlined.ErrorOutline,
                                        title = stringResource(R.string.section_overdue),
                                        count = uiState.overdueTasks.size,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                items(
                                    uiState.overdueTasks,
                                    key = { "${TaskListItemKeyPrefix.OVERDUE}${it.id}" }
                                ) { task ->
                                    TaskCard(
                                        task = task,
                                        onToggle = { onToggleTask(task) },
                                        onClick = { onTaskClick(task) },
                                        modifier = Modifier.animateItem()
                                    )
                                }
                            }

                            item {
                                SectionHeader(
                                    icon = Icons.AutoMirrored.Outlined.ListAlt,
                                    title = stringResource(R.string.section_pending),
                                    count = uiState.pendingTasks.size,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (uiState.pendingTasks.isEmpty()) {
                                item {
                                    EmptyState(
                                        title = stringResource(R.string.empty_state_title),
                                        hint = stringResource(R.string.empty_state_hint)
                                    )
                                }
                            } else {
                                items(
                                    uiState.pendingTasks,
                                    key = { "${TaskListItemKeyPrefix.PENDING}${it.id}" }
                                ) { task ->
                                    TaskCard(
                                        task = task,
                                        onToggle = { onToggleTask(task) },
                                        onClick = { onTaskClick(task) },
                                        modifier = Modifier.animateItem()
                                    )
                                }
                            }

                            if (uiState.completedTasks.isNotEmpty()) {
                                item {
                                    Spacer(modifier = Modifier.height(TaskFlowDimens.spacingSm))
                                    SectionHeader(
                                        icon = Icons.Outlined.CheckBox,
                                        title = stringResource(R.string.section_completed),
                                        count = null,
                                        color = TaskFlowDesign.extraColors.success
                                    )
                                }
                                items(
                                    uiState.completedTasks,
                                    key = { "${TaskListItemKeyPrefix.COMPLETED}${it.id}" }
                                ) { task ->
                                    TaskCard(
                                        task = task,
                                        onToggle = { onToggleTask(task) },
                                        onClick = { onTaskClick(task) },
                                        modifier = Modifier.animateItem()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
