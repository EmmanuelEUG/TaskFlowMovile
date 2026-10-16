package com.emma.proyects.taskflow.presentation.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.data.model.TaskFilter
import com.emma.proyects.taskflow.data.model.ThemeMode
import com.emma.proyects.taskflow.presentation.components.DashboardStatsContent
import com.emma.proyects.taskflow.presentation.components.EmptyTaskContent
import com.emma.proyects.taskflow.presentation.components.TaskCardContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskPage(
    uiState: TaskUiState,
    onToggleTask: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onFilterSelected: (TaskFilter) -> Unit,
    onRestoreSeed: () -> Unit,
    onCycleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeLabelRes = when (uiState.themeMode) {
        ThemeMode.SYSTEM -> R.string.theme_mode_system
        ThemeMode.LIGHT -> R.string.theme_mode_light
        ThemeMode.DARK -> R.string.theme_mode_dark
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = stringResource(R.string.dashboard_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = onCycleTheme,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = stringResource(themeLabelRes))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            DashboardStatsContent(
                totalCount = uiState.totalCount,
                pendingCount = uiState.pendingCount,
                completedCount = uiState.completedCount,
                completionProgress = uiState.completionProgress,
                completionPercentInt = uiState.completionPercentInt
            )

            Spacer(modifier = Modifier.height(12.dp))

            FilterChipsRow(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = onFilterSelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.visibleTasks.isEmpty()) {
                EmptyTaskContent(onRestoreSeedClick = onRestoreSeed)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = uiState.visibleTasks,
                        key = { task -> task.id }
                    ) { task ->
                        TaskCardContent(
                            task = task,
                            onToggleComplete = onToggleTask,
                            onDeleteClick = onDeleteTask
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipsRow(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val filters = listOf(
            TaskFilter.ALL to R.string.filter_all,
            TaskFilter.PENDING to R.string.filter_pending,
            TaskFilter.HIGH to R.string.filter_high,
            TaskFilter.MEDIUM to R.string.filter_medium,
            TaskFilter.LOW to R.string.filter_low
        )

        filters.forEach { (filter, labelRes) ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(text = stringResource(labelRes)) }
            )
        }
    }
}