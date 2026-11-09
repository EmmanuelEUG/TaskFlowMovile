package com.emma.proyects.taskflow.tasks.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.tasks.domain.model.AppRoute
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

@Composable
fun AppBottomBar(
    selectedRoute: AppRoute,
    onNavigate: (AppRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple(AppRoute.HOME, R.string.nav_home, Icons.Outlined.Dashboard),
        Triple(AppRoute.PRODUCTIVITY, R.string.nav_productivity, Icons.Outlined.BarChart),
        Triple(AppRoute.SETTINGS, R.string.nav_settings, Icons.Outlined.Settings)
    )

    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = TaskFlowDimens.elevationNav
    ) {
        items.forEach { (route, labelRes, icon) ->
            val isSelected = selectedRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(labelRes),
                        modifier = Modifier.size(TaskFlowDimens.iconLg)
                    )
                },
                label = {
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
