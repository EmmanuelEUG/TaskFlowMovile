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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.auth.presentation.screens.AuthErrorBanner
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.tasks.domain.model.Importance
import com.emma.proyects.taskflow.tasks.domain.model.RecurrenceUnit
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.tasks.domain.util.TaskDateFormatter
import com.emma.proyects.taskflow.tasks.presentation.components.AppFilterChip
import com.emma.proyects.taskflow.tasks.presentation.components.ErrorStateCard
import com.emma.proyects.taskflow.tasks.presentation.components.RecurrenceDayChip
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TaskFormViewModel
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens
import com.emma.proyects.taskflow.ui.theme.TaskFlowTypography
import java.time.DayOfWeek
import kotlin.math.roundToInt

@Composable
fun TaskFormRoute(
    viewModel: TaskFormViewModel,
    onClose: () -> Unit,
    onSaved: (UiText) -> Unit
) {
    val formState by viewModel.uiState.collectAsStateWithLifecycle()
    val defaultArea = stringResource(R.string.area_school)

    LaunchedEffect(defaultArea) {
        viewModel.initializeDefaultArea(defaultArea)
    }

    LaunchedEffect(formState.savedSuccessMessage) {
        formState.savedSuccessMessage?.let { message ->
            viewModel.consumeSavedSuccessMessage()
            onSaved(message)
        }
    }

    TaskFormScreen(
        formState = formState,
        onRetryLoad = viewModel::loadTaskForEdit,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onImportanceChange = viewModel::onImportanceChange,
        onPriorityChange = viewModel::onPriorityChange,
        onAreaChange = viewModel::onAreaChange,
        onDateSelected = viewModel::onDateSelected,
        onTimeSelected = viewModel::onTimeSelected,
        onClearDateTime = viewModel::onClearDateTime,
        onRecurringToggle = viewModel::onRecurringToggle,
        onRecurrenceIntervalChange = viewModel::onRecurrenceIntervalChange,
        onRecurrenceUnitChange = viewModel::onRecurrenceUnitChange,
        onRecurrenceDaysOfWeekChange = viewModel::onRecurrenceDaysOfWeekChange,
        onRecurrenceDaysOfMonthChange = viewModel::onRecurrenceDaysOfMonthChange,
        onSave = viewModel::onSaveTask,
        onClose = onClose
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskFormScreen(
    formState: TaskFormUiState,
    onRetryLoad: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImportanceChange: (Importance) -> Unit,
    onPriorityChange: (Int) -> Unit,
    onAreaChange: (String) -> Unit,
    onDateSelected: (String, Long?) -> Unit,
    onTimeSelected: (String, Int?, Int?) -> Unit,
    onClearDateTime: () -> Unit,
    onRecurringToggle: (Boolean) -> Unit,
    onRecurrenceIntervalChange: (String) -> Unit,
    onRecurrenceUnitChange: (RecurrenceUnit) -> Unit,
    onRecurrenceDaysOfWeekChange: (List<DayOfWeek>) -> Unit,
    onRecurrenceDaysOfMonthChange: (List<Int>) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var unitDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.cd_close),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(
                            if (formState.isEditing) R.string.form_edit_title else R.string.form_new_title
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (!formState.isLoadingInitial && formState.loadErrorMessage == null) {
                Button(
                    onClick = onSave,
                    enabled = !formState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = TaskFlowDimens.spacing4xl,
                            vertical = TaskFlowDimens.spacingXl
                        )
                        .height(TaskFlowDimens.btnHeightPrimary),
                    shape = RoundedCornerShape(TaskFlowDimens.radiusXl)
                ) {
                    if (formState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(TaskFlowDimens.iconBase),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = TaskFlowDimens.strokeDashed
                        )
                        Spacer(modifier = Modifier.width(TaskFlowDimens.spacingBase))
                    } else {
                        Icon(
                            imageVector = if (formState.isEditing) Icons.Outlined.Check else Icons.Outlined.Add,
                            contentDescription = null,
                            modifier = Modifier.size(TaskFlowDimens.iconBase)
                        )
                        Spacer(modifier = Modifier.width(TaskFlowDimens.spacingMd))
                    }
                    Text(
                        text = stringResource(
                            if (formState.isEditing) R.string.btn_save_changes else R.string.btn_create_task
                        ),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    ) { innerPadding ->
        when {
            formState.isLoadingInitial -> {
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
                            text = stringResource(R.string.loading_form),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            formState.loadErrorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(TaskFlowDimens.spacing4xl),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorStateCard(
                        message = formState.loadErrorMessage.asString(),
                        onRetry = onRetryLoad
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = TaskFlowDimens.spacing4xl,
                            vertical = TaskFlowDimens.spacingMd
                        ),
                    verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXl)
                ) {
                    if (formState.saveErrorMessage != null) {
                        AuthErrorBanner(message = formState.saveErrorMessage)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXs)) {
                        Text(
                            text = stringResource(R.string.form_label_title),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = formState.title,
                            onValueChange = onTitleChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.form_placeholder_title)) },
                            isError = formState.titleError != null,
                            trailingIcon = {
                                if (formState.titleError != null) {
                                    Icon(
                                        imageVector = Icons.Outlined.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            supportingText = formState.titleError?.let { errorText ->
                                { Text(text = errorText.asString(), color = MaterialTheme.colorScheme.error) }
                            },
                            shape = RoundedCornerShape(TaskFlowDimens.radiusMd),
                            singleLine = true
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXs)) {
                        Text(
                            text = stringResource(R.string.form_label_description),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = formState.description,
                            onValueChange = onDescriptionChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.form_placeholder_description)) },
                            minLines = TaskConstants.FORM_DESCRIPTION_MIN_LINES,
                            shape = RoundedCornerShape(TaskFlowDimens.radiusMd)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)) {
                        Text(
                            text = stringResource(R.string.importance_label),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
                        ) {
                            val items = listOf(
                                Importance.HIGH to R.string.importance_high,
                                Importance.MEDIUM to R.string.importance_medium,
                                Importance.LOW to R.string.importance_low
                            )
                            items.forEach { (imp, labelRes) ->
                                AppFilterChip(
                                    label = stringResource(labelRes),
                                    selected = formState.importance == imp,
                                    onClick = { onImportanceChange(imp) },
                                    modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXxs)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.form_label_priority),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(
                                    R.string.form_priority_value,
                                    formState.priorityLevel,
                                    TaskConstants.MAX_PRIORITY
                                ),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = formState.priorityLevel.toFloat(),
                            onValueChange = { onPriorityChange(it.roundToInt()) },
                            valueRange = TaskConstants.MIN_PRIORITY.toFloat()..TaskConstants.MAX_PRIORITY.toFloat(),
                            steps = TaskConstants.PRIORITY_SLIDER_STEPS
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = TaskFlowDimens.spacingXs),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            (TaskConstants.MIN_PRIORITY..TaskConstants.MAX_PRIORITY).forEach { level ->
                                val isCurrent = formState.priorityLevel == level
                                Text(
                                    text = level.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCurrent) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)) {
                        Text(
                            text = stringResource(R.string.form_label_area),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = formState.area,
                            onValueChange = onAreaChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.form_placeholder_area)) },
                            isError = formState.areaError != null,
                            supportingText = formState.areaError?.let { errorText ->
                                { Text(text = errorText.asString(), color = MaterialTheme.colorScheme.error) }
                            },
                            shape = RoundedCornerShape(TaskFlowDimens.radiusMd),
                            singleLine = true
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
                        ) {
                            val suggestedAreas = listOf(
                                stringResource(R.string.area_school),
                                stringResource(R.string.area_home),
                                stringResource(R.string.area_health),
                                stringResource(R.string.area_project)
                            )
                            suggestedAreas.forEach { suggested ->
                                AppFilterChip(
                                    label = suggested,
                                    selected = formState.area.equals(suggested, ignoreCase = true),
                                    onClick = { onAreaChange(suggested) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.form_label_due_date),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (formState.selectedDate.isNotBlank() || formState.selectedTime.isNotBlank()) {
                                TextButton(
                                    onClick = onClearDateTime,
                                    contentPadding = PaddingValues(
                                        horizontal = TaskFlowDimens.spacingMd,
                                        vertical = TaskFlowDimens.spacingXxs
                                    )
                                ) {
                                    Text(
                                        text = stringResource(R.string.form_clear_datetime),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingBase)
                        ) {
                            OutlinedButton(
                                onClick = { showDatePicker = true },
                                modifier = Modifier
                                    .weight(TaskConstants.WEIGHT_FULL)
                                    .height(TaskFlowDimens.btnHeightSecondary),
                                shape = RoundedCornerShape(TaskFlowDimens.radiusMd),
                                contentPadding = PaddingValues(horizontal = TaskFlowDimens.spacingSm)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.size(TaskFlowDimens.iconSm)
                                )
                                Spacer(modifier = Modifier.width(TaskFlowDimens.spacingXs))
                                Text(
                                    text = formState.selectedDate.ifBlank {
                                        stringResource(R.string.form_pick_date)
                                    },
                                    style = TaskFlowTypography.compactButtonTextStyle,
                                    maxLines = TaskConstants.SINGLE_LINE,
                                    softWrap = false
                                )
                            }

                            OutlinedButton(
                                onClick = { showTimePicker = true },
                                modifier = Modifier
                                    .weight(TaskConstants.WEIGHT_FULL)
                                    .height(TaskFlowDimens.btnHeightSecondary),
                                shape = RoundedCornerShape(TaskFlowDimens.radiusMd),
                                contentPadding = PaddingValues(horizontal = TaskFlowDimens.spacingSm)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(TaskFlowDimens.iconSm)
                                )
                                Spacer(modifier = Modifier.width(TaskFlowDimens.spacingXs))
                                Text(
                                    text = formState.selectedTime.ifBlank {
                                        stringResource(R.string.form_pick_time)
                                    },
                                    style = TaskFlowTypography.compactButtonTextStyle,
                                    maxLines = TaskConstants.SINGLE_LINE,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(TaskFlowDimens.radiusXl),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(TaskFlowDimens.spacingXl),
                            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Autorenew,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = stringResource(R.string.form_recurring_question),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                Switch(
                                    checked = formState.isRecurring,
                                    onCheckedChange = onRecurringToggle
                                )
                            }

                            if (formState.isRecurring) {
                                val unitMap = mapOf(
                                    RecurrenceUnit.HOURS to stringResource(R.string.unit_hours),
                                    RecurrenceUnit.DAYS to stringResource(R.string.unit_days),
                                    RecurrenceUnit.WEEKS to stringResource(R.string.unit_weeks),
                                    RecurrenceUnit.MONTHS to stringResource(R.string.unit_months),
                                    RecurrenceUnit.YEARS to stringResource(R.string.unit_years)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingBase),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    OutlinedTextField(
                                        value = formState.recurrenceIntervalText,
                                        onValueChange = onRecurrenceIntervalChange,
                                        label = { Text(stringResource(R.string.form_recurring_every)) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        isError = formState.recurrenceIntervalError != null,
                                        supportingText = formState.recurrenceIntervalError?.let { errorText ->
                                            { Text(text = errorText.asString(), color = MaterialTheme.colorScheme.error) }
                                        },
                                        modifier = Modifier.weight(TaskConstants.WEIGHT_RECURRENCE_INTERVAL),
                                        shape = RoundedCornerShape(TaskFlowDimens.radiusMd),
                                        singleLine = true
                                    )

                                    Box(
                                        modifier = Modifier
                                            .weight(TaskConstants.WEIGHT_RECURRENCE_UNIT)
                                            .padding(top = TaskFlowDimens.spacingMd)
                                    ) {
                                        OutlinedButton(
                                            onClick = { unitDropdownExpanded = true },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(TaskFlowDimens.btnHeightDropdown),
                                            shape = RoundedCornerShape(TaskFlowDimens.radiusMd),
                                            border = BorderStroke(
                                                TaskFlowDimens.strokeThin,
                                                MaterialTheme.colorScheme.outline
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = unitMap[formState.recurrenceUnit].orEmpty(),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Icon(
                                                    imageVector = Icons.Outlined.ArrowDropDown,
                                                    contentDescription = stringResource(R.string.cd_recurrence_unit),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = unitDropdownExpanded,
                                            onDismissRequest = { unitDropdownExpanded = false }
                                        ) {
                                            unitMap.forEach { (unit, label) ->
                                                DropdownMenuItem(
                                                    text = { Text(label) },
                                                    onClick = {
                                                        onRecurrenceUnitChange(unit)
                                                        unitDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                if (formState.recurrenceUnit == RecurrenceUnit.WEEKS) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = TaskFlowDimens.spacingXs)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.form_repeat_days_label),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(bottom = TaskFlowDimens.spacingMd)
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)
                                        ) {
                                            val daysOfWeek = listOf(
                                                DayOfWeek.MONDAY to R.string.day_letter_mon,
                                                DayOfWeek.TUESDAY to R.string.day_letter_tue,
                                                DayOfWeek.WEDNESDAY to R.string.day_letter_wed,
                                                DayOfWeek.THURSDAY to R.string.day_letter_thu,
                                                DayOfWeek.FRIDAY to R.string.day_letter_fri,
                                                DayOfWeek.SATURDAY to R.string.day_letter_sat,
                                                DayOfWeek.SUNDAY to R.string.day_letter_sun
                                            )
                                            daysOfWeek.forEach { (day, labelRes) ->
                                                val isSelected = formState.recurrenceDaysOfWeek.contains(day)
                                                RecurrenceDayChip(
                                                    label = stringResource(labelRes),
                                                    selected = isSelected,
                                                    onClick = {
                                                        val newDays = if (isSelected) {
                                                            formState.recurrenceDaysOfWeek - day
                                                        } else {
                                                            formState.recurrenceDaysOfWeek + day
                                                        }
                                                        onRecurrenceDaysOfWeekChange(newDays)
                                                    },
                                                    minWidth = TaskFlowDimens.weekDayChipWidth,
                                                    shape = RoundedCornerShape(TaskFlowDimens.radiusPill),
                                                    unselectedFontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }

                                if (formState.recurrenceUnit == RecurrenceUnit.MONTHS) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = TaskFlowDimens.spacingXs)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.form_repeat_month_days_label),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(bottom = TaskFlowDimens.spacingMd)
                                        )
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm),
                                            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)
                                        ) {
                                            (TaskConstants.FIRST_DAY_OF_MONTH..TaskConstants.MAX_MONTH_DAYS).forEach { day ->
                                                val isSelected = formState.recurrenceDaysOfMonth.contains(day)
                                                RecurrenceDayChip(
                                                    label = day.toString(),
                                                    selected = isSelected,
                                                    onClick = {
                                                        val newDays = if (isSelected) {
                                                            formState.recurrenceDaysOfMonth - day
                                                        } else {
                                                            formState.recurrenceDaysOfMonth + day
                                                        }
                                                        onRecurrenceDaysOfMonthChange(newDays)
                                                    },
                                                    minWidth = TaskFlowDimens.monthDayChipWidth,
                                                    shape = RoundedCornerShape(TaskFlowDimens.radiusDayCell)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(TaskFlowDimens.spacing5xl))
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.selectedDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(
                                TaskDateFormatter.formatShortDateFromUtcMillis(millis),
                                millis
                            )
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.btn_save_changes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = formState.selectedHour ?: TaskConstants.DEFAULT_TIME_PICKER_HOUR,
            initialMinute = formState.selectedMinute ?: TaskConstants.DEFAULT_TIME_PICKER_MINUTE,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val formatted = TaskDateFormatter.formatTime24h(timeState.hour, timeState.minute)
                        onTimeSelected(formatted, timeState.hour, timeState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text(stringResource(R.string.btn_save_changes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            },
            text = {
                TimePicker(state = timeState)
            }
        )
    }
}
