package com.emma.proyects.taskflow.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.data.model.Importance
import com.emma.proyects.taskflow.ui.theme.TaskFlowDesign

@Composable
fun ImportanceChip(
    importance: Importance,
    modifier: Modifier = Modifier
) {
    val extraColors = TaskFlowDesign.extraColors

    val (bgColor, fgColor, textRes) = when (importance) {
        Importance.HIGH -> Triple(extraColors.altaBg, extraColors.altaFg, R.string.importance_high)
        Importance.MEDIUM -> Triple(extraColors.mediaBg, extraColors.mediaFg, R.string.importance_medium)
        Importance.LOW -> Triple(extraColors.bajaBg, extraColors.bajaFg, R.string.importance_low)
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = stringResource(textRes),
            style = MaterialTheme.typography.labelSmall,
            color = fgColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}