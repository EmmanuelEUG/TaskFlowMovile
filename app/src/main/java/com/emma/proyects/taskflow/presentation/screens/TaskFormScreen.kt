package com.emma.proyects.taskflow.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.data.model.Importance
import com.emma.proyects.taskflow.data.model.RecurrenceUnit
import com.emma.proyects.taskflow.presentation.components.AppFilterChip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    formState: TaskFormUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImportanceChange: (Importance) -> Unit,
    onPriorityChange: (Int) -> Unit,
    onAreaChange: (String) -> Unit,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit,
    onRecurringToggle: (Boolean) -> Unit,
    onRecurrenceIntervalChange: (String) -> Unit,
    onRecurrenceUnitChange: (RecurrenceUnit) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit,
    onDeleteFromEdit: () -> Unit,
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
                            contentDescription = null,
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
                actions = {
                    if (formState.isEditing) {
                        IconButton(onClick = onDeleteFromEdit) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = if (formState.isEditing) Icons.Outlined.Check else Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        if (formState.isEditing) R.string.btn_save_changes else R.string.btn_create_task
                    ),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Campo Título con validación (P-04)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                    isError = formState.showTitleError,
                    trailingIcon = {
                        if (formState.showTitleError) {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    supportingText = if (formState.showTitleError) {
                        {
                            Text(
                                text = stringResource(R.string.form_error_title_empty),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // 2. Campo Descripción
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // 3. Importancia (3 chips iguales)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.importance_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 4. Slider de Prioridad (1 a 5)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                        text = stringResource(R.string.form_priority_value, formState.priorityLevel),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = formState.priorityLevel.toFloat(),
                    onValueChange = { onPriorityChange(it.roundToInt()) },
                    valueRange = 1f..5f,
                    steps = 3
                )
            }

            // 5. Área + Chips de áreas sugeridas
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.form_label_area),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = formState.area,
                    onValueChange = onAreaChange,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Escuela", "Casa", "Salud", "Proyecto").forEach { suggested ->
                        AppFilterChip(
                            label = suggested,
                            selected = formState.area.equals(suggested, ignoreCase = true),
                            onClick = { onAreaChange(suggested) }
                        )
                    }
                }
            }

            // 6. Fecha y Hora
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.form_label_due_date),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formState.selectedDate.ifBlank {
                                stringResource(R.string.form_pick_date)
                            }
                        )
                    }

                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formState.selectedTime.ifBlank {
                                stringResource(R.string.form_pick_time)
                            }
                        )
                    }
                }
            }

            // 7. Sección de Recurrencia (P-05)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = formState.recurrenceIntervalText,
                                onValueChange = onRecurrenceIntervalChange,
                                label = { Text(stringResource(R.string.form_recurring_every)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(0.4f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Box(modifier = Modifier.weight(0.6f)) {
                                OutlinedButton(
                                    onClick = { unitDropdownExpanded = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = unitMap[formState.recurrenceUnit] ?: "",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Icon(
                                            imageVector = Icons.Outlined.ArrowDropDown,
                                            contentDescription = null,
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
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val sdf = SimpleDateFormat("d 'de' MMMM", Locale("es", "MX")).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            onDateSelected(sdf.format(Date(millis)))
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
        val timeState = rememberTimePickerState(initialHour = 18, initialMinute = 0, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val formatted = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            timeState.hour,
                            timeState.minute
                        )
                        onTimeSelected(formatted)
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