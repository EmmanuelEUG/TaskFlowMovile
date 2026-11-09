package com.emma.proyects.taskflow.tasks.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.tasks.domain.model.AppRoute
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.tasks.presentation.components.AppBottomBar
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TasksViewModel
import com.emma.proyects.taskflow.ui.theme.TaskFlowAlphas
import com.emma.proyects.taskflow.ui.theme.TaskFlowDesign
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

private const val AXIS_ORIGIN = 0f
private const val HALF_DIVISOR = 2f
private const val EMPTY_TEXT = ""

@Composable
fun ProductivityRoute(
    viewModel: TasksViewModel,
    onNavigateTab: (AppRoute) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProductivityScreen(
        uiState = uiState,
        onNavigate = onNavigateTab
    )
}

@Composable
fun ProductivityScreen(
    uiState: TasksHomeUiState,
    onNavigate: (AppRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AppBottomBar(
                selectedRoute = AppRoute.PRODUCTIVITY,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + TaskFlowDimens.spacing2xl,
                bottom = innerPadding.calculateBottomPadding() + TaskFlowDimens.spacing5xl,
                start = TaskFlowDimens.spacing4xl,
                end = TaskFlowDimens.spacing4xl
            ),
            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXl)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXxs)) {
                    Text(
                        text = stringResource(R.string.prod_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.prod_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
                ) {
                    StatCard(
                        icon = Icons.AutoMirrored.Outlined.ListAlt,
                        iconTint = MaterialTheme.colorScheme.primary,
                        value = uiState.totalTasksCount.toString(),
                        label = stringResource(R.string.prod_total_tasks),
                        modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)
                    )
                    StatCard(
                        icon = Icons.Outlined.CheckCircle,
                        iconTint = TaskFlowDesign.extraColors.success,
                        value = uiState.completedTasksCount.toString(),
                        label = stringResource(R.string.prod_completed),
                        modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
                ) {
                    StatCard(
                        icon = Icons.Outlined.ErrorOutline,
                        iconTint = MaterialTheme.colorScheme.error,
                        value = uiState.overdueTasksCount.toString(),
                        label = stringResource(R.string.prod_overdue),
                        modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)
                    )
                    StatCard(
                        icon = Icons.Outlined.AccessTime,
                        iconTint = MaterialTheme.colorScheme.primary,
                        value = stringResource(R.string.prod_percent_format, uiState.punctualityRate),
                        label = stringResource(R.string.prod_punctuality),
                        modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)
                    )
                }
            }

            item {
                ChartCard(title = stringResource(R.string.prod_chart_weekly)) {
                    WeeklyBarChart(weeklyCounts = uiState.weeklyCompletedCounts)
                }
            }

            item {
                ChartCard(title = stringResource(R.string.prod_chart_punctuality)) {
                    PunctualityDonutChart(
                        punctualityRate = uiState.punctualityRate,
                        completedCount = uiState.completedTasksCount
                    )
                }
            }

            item {
                ChartCard(title = stringResource(R.string.prod_chart_areas)) {
                    AreaPieChart(distribution = uiState.areaDistribution)
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(TaskFlowDimens.radiusXl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TaskFlowDimens.spacing2xl),
            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(TaskFlowDimens.iconXl)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    content: @Composable () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacing2xl)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            content()
        }
    }
}

@Composable
private fun WeeklyBarChart(weeklyCounts: List<Int>) {
    val barColor = TaskFlowDesign.extraColors.chartBlue
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = TaskFlowAlphas.CHART_TRACK)
    val bars = if (weeklyCounts.size == TaskConstants.DAYS_IN_WEEK) {
        weeklyCounts
    } else {
        List(TaskConstants.DAYS_IN_WEEK) { TaskConstants.MIN_LIST_INDEX }
    }
    val dayLabelIds = listOf(
        R.string.day_letter_mon,
        R.string.day_letter_tue,
        R.string.day_letter_wed,
        R.string.day_letter_thu,
        R.string.day_letter_fri,
        R.string.day_letter_sat,
        R.string.day_letter_sun
    )
    val barWidthDp = TaskFlowDimens.chartBarWidth
    val barRadiusDp = TaskFlowDimens.radiusXs

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            bars.forEach { count ->
                Text(
                    text = if (count > TaskConstants.MIN_LIST_INDEX) count.toString() else EMPTY_TEXT,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = barColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)
                )
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(TaskFlowDimens.chartBarHeight)
        ) {
            val maxVal = maxOf(
                bars.maxOrNull()?.toFloat() ?: TaskConstants.MIN_CHART_SUM,
                TaskConstants.MIN_BAR_CHART_SCALE
            )
            val slotWidth = size.width / bars.size
            val barWidth = barWidthDp.toPx()
            val radiusPx = barRadiusDp.toPx()
            val corner = CornerRadius(radiusPx, radiusPx)

            bars.forEachIndexed { index, value ->
                val left = index * slotWidth + (slotWidth - barWidth) / HALF_DIVISOR
                drawRoundRect(
                    color = trackColor,
                    topLeft = Offset(left, AXIS_ORIGIN),
                    size = Size(barWidth, size.height),
                    cornerRadius = corner
                )
                if (value > TaskConstants.MIN_LIST_INDEX) {
                    val barHeight = (value / maxVal) * size.height
                    val top = size.height - barHeight
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barHeight),
                        cornerRadius = corner
                    )
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabelIds.forEach { dayRes ->
                Text(
                    text = stringResource(dayRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)
                )
            }
        }
    }
}

@Composable
private fun PunctualityDonutChart(punctualityRate: Int, completedCount: Int) {
    val extraColors = TaskFlowDesign.extraColors
    val greenColor = extraColors.chartGreen
    val redColor = extraColors.chartRed
    val neutralTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = TaskFlowAlphas.CHART_NEUTRAL_ARC)

    val hasData = completedCount > TaskConstants.MIN_LIST_INDEX
    val displayRate = if (hasData) punctualityRate else TaskConstants.PERCENT_MIN
    val lateRate = if (hasData) {
        (TaskConstants.PERCENT_MAX - punctualityRate).coerceIn(
            TaskConstants.PERCENT_MIN,
            TaskConstants.PERCENT_MAX
        )
    } else {
        TaskConstants.PERCENT_MIN
    }
    val strokeWidthDp = TaskFlowDimens.chartDonutStroke

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TaskFlowDimens.spacingXs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.size(TaskFlowDimens.chartDonutSize),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(TaskFlowDimens.spacingSm)
            ) {
                val stroke = strokeWidthDp.toPx()

                if (!hasData) {
                    drawArc(
                        color = neutralTrackColor,
                        startAngle = TaskConstants.CHART_ZERO_ANGLE,
                        sweepAngle = TaskConstants.CHART_FULL_CIRCLE,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                } else {
                    val onTimeSweep = (punctualityRate / TaskConstants.PERCENT_MAX.toFloat()) *
                        TaskConstants.CHART_FULL_CIRCLE
                    drawArc(
                        color = redColor,
                        startAngle = TaskConstants.CHART_ZERO_ANGLE,
                        sweepAngle = TaskConstants.CHART_FULL_CIRCLE,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Butt)
                    )
                    if (punctualityRate > TaskConstants.PERCENT_MIN) {
                        drawArc(
                            color = greenColor,
                            startAngle = TaskConstants.CHART_START_ANGLE,
                            sweepAngle = onTimeSweep,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.prod_percent_format, displayRate),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .weight(TaskConstants.WEIGHT_FULL)
                .padding(start = TaskFlowDimens.spacing2xl),
            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingBase)
        ) {
            ChartLegendRow(
                color = greenColor,
                label = stringResource(R.string.prod_on_time),
                valueText = stringResource(R.string.prod_percent_format, displayRate)
            )
            ChartLegendRow(
                color = redColor,
                label = stringResource(R.string.prod_late),
                valueText = stringResource(R.string.prod_percent_format, lateRate)
            )
        }
    }
}

@Composable
private fun AreaPieChart(distribution: Map<String, Int>) {
    if (distribution.isEmpty()) {
        Text(
            text = stringResource(R.string.prod_no_areas),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    val palette = TaskFlowDesign.extraColors.chartPalette

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TaskFlowDimens.spacingXs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Canvas(
            modifier = Modifier
                .size(TaskFlowDimens.chartDonutSize)
                .padding(TaskFlowDimens.spacingXs)
        ) {
            val total = distribution.values.sum().toFloat().coerceAtLeast(TaskConstants.MIN_CHART_SUM)
            var startAngle = TaskConstants.CHART_START_ANGLE
            distribution.entries.forEachIndexed { index, entry ->
                val sweep = (entry.value / total) * TaskConstants.CHART_FULL_CIRCLE
                drawArc(
                    color = palette[index % palette.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
        }

        Column(
            modifier = Modifier
                .weight(TaskConstants.WEIGHT_FULL)
                .padding(start = TaskFlowDimens.spacing2xl),
            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
        ) {
            distribution.entries.forEachIndexed { index, (area, count) ->
                ChartLegendRow(
                    color = palette[index % palette.size],
                    label = area.ifBlank { stringResource(R.string.area_general) },
                    valueText = count.toString()
                )
            }
        }
    }
}

@Composable
private fun ChartLegendRow(
    color: Color,
    label: String,
    valueText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(TaskConstants.WEIGHT_FULL, fill = false)
        ) {
            Box(
                modifier = Modifier
                    .size(TaskFlowDimens.chartLegendDot)
                    .background(color = color, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(TaskFlowDimens.spacingSm))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = TaskConstants.SINGLE_LINE
            )
        }
        Spacer(modifier = Modifier.width(TaskFlowDimens.spacingMd))
        Text(
            text = valueText,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = color,
            maxLines = TaskConstants.SINGLE_LINE
        )
    }
}
