package com.emma.proyects.taskflow.presentation.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.data.model.AppRoute
import com.emma.proyects.taskflow.data.model.Importance
import com.emma.proyects.taskflow.presentation.components.AppBottomBar
import com.emma.proyects.taskflow.presentation.components.AppFilterChip
import com.emma.proyects.taskflow.presentation.components.EmptyState
import com.emma.proyects.taskflow.presentation.components.SectionHeader
import com.emma.proyects.taskflow.presentation.components.TaskCard
import com.emma.proyects.taskflow.ui.theme.TaskFlowDesign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPage(
    uiState: TaskUiState,
    onToggleTask: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onAreaSelected: (String) -> Unit,
    onImportanceSelected: (Importance?) -> Unit,
    onNavigate: (AppRoute) -> Unit,
    onCycleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckBox,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCycleTheme) {
                        Icon(
                            imageVector = Icons.Outlined.SwapVert,
                            contentDescription = stringResource(R.string.cd_sort_tasks),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                selectedRoute = uiState.currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp,
                start = 20.dp,
                end = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(R.string.dashboard_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.dashboard_subtitle, uiState.pendingTaskCount()),
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
                    shape = RoundedCornerShape(14.dp),
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val areas = listOf(
                        R.string.area_all to "Todas",
                        R.string.area_school to "Escuela",
                        R.string.area_home to "Casa",
                        R.string.area_health to "Salud",
                        R.string.area_project to "Proyecto"
                    )
                    areas.forEach { (resId, areaName) ->
                        AppFilterChip(
                            label = stringResource(resId),
                            selected = uiState.selectedArea == areaName,
                            onClick = { onAreaSelected(areaName) }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.importance_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val importances = listOf(
                        Triple(Importance.HIGH, R.string.importance_high, false),
                        Triple(Importance.MEDIUM, R.string.importance_medium, false),
                        Triple(Importance.LOW, R.string.importance_low, false)
                    )
                    importances.forEach { (imp, resId, _) ->
                        AppFilterChip(
                            label = stringResource(resId),
                            selected = uiState.selectedImportance == imp,
                            onClick = { onImportanceSelected(imp) },
                            showCheckIcon = false
                        )
                    }
                }
            }

            if (uiState.overdueTasks.isNotEmpty()) {
                item {
                    SectionHeader(
                        icon = Icons.Outlined.ErrorOutline,
                        title = stringResource(R.string.section_overdue),
                        count = uiState.overdueTasks.size,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                items(uiState.overdueTasks, key = { "overdue_${it.id}" }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { onToggleTask(task.id) },
                        onClick = {}
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

            if (uiState.pendingTasks.isEmpty() && uiState.overdueTasks.isEmpty()) {
                item {
                    EmptyState(
                        title = stringResource(R.string.empty_state_title),
                        hint = stringResource(R.string.empty_state_hint)
                    )
                }
            } else {
                items(uiState.pendingTasks, key = { "pending_${it.id}" }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { onToggleTask(task.id) },
                        onClick = {}
                    )
                }
            }

            if (uiState.completedTasks.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    SectionHeader(
                        icon = Icons.Outlined.CheckBox,
                        title = stringResource(R.string.section_completed),
                        count = null,
                        color = TaskFlowDesign.extraColors.success
                    )
                }
                items(uiState.completedTasks, key = { "completed_${it.id}" }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { onToggleTask(task.id) },
                        onClick = {}
                    )
                }
            }
        }
    }
}

private fun TaskUiState.pendingTaskCount(): Int = pendingTasks.size + overdueTasks.size