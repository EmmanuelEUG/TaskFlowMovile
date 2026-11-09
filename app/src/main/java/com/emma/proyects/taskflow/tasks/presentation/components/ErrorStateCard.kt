package com.emma.proyects.taskflow.tasks.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.ui.theme.TaskFlowAlphas
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

@Composable
fun ErrorStateCard(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.error_title)
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(TaskFlowDimens.radius2xl),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        border = BorderStroke(
            TaskFlowDimens.strokeThin,
            MaterialTheme.colorScheme.error.copy(alpha = TaskFlowAlphas.ERROR_BORDER_SUBTLE)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TaskFlowDimens.spacing5xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
        ) {
            Box(
                modifier = Modifier
                    .size(TaskFlowDimens.errorIconBox)
                    .background(
                        color = MaterialTheme.colorScheme.error.copy(alpha = TaskFlowAlphas.ERROR_ICON_BG),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(TaskFlowDimens.icon2xl)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(TaskFlowDimens.spacingXs))

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(TaskFlowDimens.radiusLg),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(TaskFlowDimens.iconMd)
                )
                Spacer(modifier = Modifier.width(TaskFlowDimens.spacingMd))
                Text(text = stringResource(R.string.btn_retry))
            }
        }
    }
}
