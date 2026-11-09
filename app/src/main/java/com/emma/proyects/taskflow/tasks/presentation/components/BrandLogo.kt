package com.emma.proyects.taskflow.tasks.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import com.emma.proyects.taskflow.ui.theme.TaskFlowAlphas
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

@Composable
fun BrandLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp? = null,
    iconSize: Dp? = null,
    contentDescription: String? = null
) {
    val resolvedSize = size ?: TaskFlowDimens.errorIconBox
    val resolvedIconSize = iconSize ?: TaskFlowDimens.logoIconMd

    Box(
        modifier = modifier
            .size(resolvedSize)
            .clip(RoundedCornerShape(TaskFlowDimens.radiusXl))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = TaskFlowAlphas.LOGO_GRADIENT_END)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckBox,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(resolvedIconSize)
        )
    }
}
