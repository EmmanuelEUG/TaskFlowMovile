package com.emma.proyects.taskflow.tasks.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emma.proyects.taskflow.BuildConfig
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.tasks.domain.model.AppRoute
import com.emma.proyects.taskflow.tasks.domain.model.TaskConstants
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode
import com.emma.proyects.taskflow.tasks.presentation.components.AppBottomBar
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TasksViewModel
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

@Composable
fun SettingsRoute(
    viewModel: TasksViewModel,
    currentUser: User?,
    themeMode: ThemeMode,
    onNavigateTab: (AppRoute) -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        currentUser = currentUser,
        themeMode = themeMode,
        onNavigate = onNavigateTab,
        onThemeModeSelected = onThemeModeSelected,
        onRestoreSeed = viewModel::onRestoreSeedTasks,
        onLogout = onLogout,
        onClearUserMessage = viewModel::clearUserMessage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: TasksHomeUiState,
    currentUser: User?,
    themeMode: ThemeMode,
    onNavigate: (AppRoute) -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onRestoreSeed: () -> Unit,
    onLogout: () -> Unit,
    onClearUserMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg.asString(context))
            onClearUserMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AppBottomBar(
                selectedRoute = AppRoute.SETTINGS,
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
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.settings_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.settings_account_header),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                val fallbackName = stringResource(R.string.default_user_name)
                val fallbackInitial = stringResource(R.string.default_avatar_initial)
                val displayName = currentUser?.name?.takeIf { it.isNotBlank() }
                    ?: uiState.userName.ifBlank { fallbackName }
                val displayEmail = currentUser?.email?.takeIf { it.isNotBlank() }
                    ?: uiState.userEmail
                val initial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: fallbackInitial

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(TaskFlowDimens.radiusCardLg),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(TaskFlowDimens.spacing2xl),
                            horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXl),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(TaskFlowDimens.avatarSize)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initial,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Column(modifier = Modifier.weight(TaskConstants.WEIGHT_FULL)) {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = displayEmail,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                        SettingsRow(
                            icon = Icons.AutoMirrored.Outlined.Logout,
                            title = stringResource(R.string.auth_logout),
                            subtitle = stringResource(R.string.auth_logout_subtitle),
                            onClick = onLogout,
                            iconContainerColor = MaterialTheme.colorScheme.errorContainer,
                            iconTint = MaterialTheme.colorScheme.error,
                            titleColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.settings_appearance_header),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(TaskFlowDimens.radiusCardLg),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(TaskFlowDimens.spacing2xl),
                        verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_theme_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            val options = listOf(
                                ThemeMode.LIGHT to R.string.theme_light,
                                ThemeMode.DARK to R.string.theme_dark,
                                ThemeMode.SYSTEM to R.string.theme_system
                            )
                            options.forEachIndexed { index, (mode, labelRes) ->
                                SegmentedButton(
                                    selected = themeMode == mode,
                                    onClick = { onThemeModeSelected(mode) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = options.size
                                    )
                                ) {
                                    Text(text = stringResource(labelRes))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.settings_data_header),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(TaskFlowDimens.radiusCardLg),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsRow(
                            icon = Icons.Outlined.Refresh,
                            title = stringResource(R.string.settings_restore_seed),
                            subtitle = stringResource(R.string.settings_restore_subtitle),
                            onClick = onRestoreSeed
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        SettingsRow(
                            icon = Icons.AutoMirrored.Outlined.ListAlt,
                            title = stringResource(R.string.settings_saved_tasks),
                            subtitle = pluralStringResource(
                                R.plurals.settings_saved_count,
                                uiState.totalTasksCount,
                                uiState.totalTasksCount
                            ),
                            trailingText = uiState.totalTasksCount.toString(),
                            onClick = {}
                        )
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.settings_about_header),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(TaskFlowDimens.radiusCardLg),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.outline)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SettingsRow(
                            icon = Icons.Outlined.Info,
                            title = stringResource(R.string.settings_version_title),
                            subtitle = stringResource(R.string.settings_version_subtitle),
                            onClick = {}
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        SettingsRow(
                            icon = Icons.Outlined.CloudDone,
                            title = stringResource(R.string.settings_api_title),
                            subtitle = BuildConfig.BASE_URL,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    trailingText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(TaskFlowDimens.spacing2xl),
        horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacing2xl),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(TaskFlowDimens.settingsIconBox)
                .background(
                    color = iconContainerColor,
                    shape = RoundedCornerShape(TaskFlowDimens.radiusMd)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(TaskFlowDimens.iconBase)
            )
        }

        Column(
            modifier = Modifier.weight(TaskConstants.WEIGHT_FULL),
            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXxs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
