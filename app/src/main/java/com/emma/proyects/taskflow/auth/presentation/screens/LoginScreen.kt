package com.emma.proyects.taskflow.auth.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.auth.presentation.viewmodels.LoginViewModel
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.tasks.domain.model.ThemeMode
import com.emma.proyects.taskflow.tasks.presentation.components.BrandLogoBadge
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    themeMode: ThemeMode,
    bannerMessage: UiText?,
    onConsumeBannerMessage: () -> Unit,
    onCycleTheme: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (User) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.authenticatedUser) {
        uiState.authenticatedUser?.let { user ->
            viewModel.consumeLoginSuccess()
            onLoginSuccess(user)
        }
    }

    LoginScreen(
        uiState = uiState,
        themeMode = themeMode,
        bannerMessage = bannerMessage,
        onConsumeBannerMessage = onConsumeBannerMessage,
        onCycleTheme = onCycleTheme,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onSubmitLogin = viewModel::onSubmitLogin,
        onNavigateToRegister = onNavigateToRegister
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    themeMode: ThemeMode,
    bannerMessage: UiText?,
    onConsumeBannerMessage: () -> Unit,
    onCycleTheme: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onSubmitLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(bannerMessage) {
        bannerMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg.asString(context))
            onConsumeBannerMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = onCycleTheme) {
                        Icon(
                            imageVector = if (themeMode == ThemeMode.LIGHT) {
                                Icons.Outlined.DarkMode
                            } else {
                                Icons.Outlined.LightMode
                            },
                            contentDescription = stringResource(R.string.cd_toggle_theme),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = TaskFlowDimens.spacing5xl, vertical = TaskFlowDimens.spacingLg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacing4xl)
        ) {
            Spacer(modifier = Modifier.height(TaskFlowDimens.spacingMd))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingBase)
            ) {
                BrandLogoBadge(
                    size = TaskFlowDimens.logoBadgeLg,
                    iconSize = TaskFlowDimens.logoIconLg,
                    contentDescription = stringResource(R.string.app_tagline)
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(TaskFlowDimens.radiusPill)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(
                            horizontal = TaskFlowDimens.spacingXl,
                            vertical = TaskFlowDimens.spacingXs
                        )
                    )
                }

                Text(
                    text = stringResource(R.string.login_welcome_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(R.string.login_welcome_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(TaskFlowDimens.radius2xl),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(TaskFlowDimens.strokeThin, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(TaskFlowDimens.spacing4xl),
                    verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacing2xl)
                ) {
                    if (uiState.generalError != null) {
                        AuthErrorBanner(message = uiState.generalError)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)) {
                        Text(
                            text = stringResource(R.string.auth_label_email),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = onEmailChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.auth_placeholder_email)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Email,
                                    contentDescription = null
                                )
                            },
                            isError = uiState.emailError != null,
                            supportingText = uiState.emailError?.let { errorText ->
                                { Text(text = errorText.asString()) }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(TaskFlowDimens.radiusLg)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)) {
                        Text(
                            text = stringResource(R.string.auth_label_password),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = onPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.auth_placeholder_password)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = onTogglePasswordVisibility) {
                                    Icon(
                                        imageVector = if (uiState.isPasswordVisible) {
                                            Icons.Outlined.VisibilityOff
                                        } else {
                                            Icons.Outlined.Visibility
                                        },
                                        contentDescription = stringResource(R.string.cd_toggle_password)
                                    )
                                }
                            },
                            visualTransformation = if (uiState.isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            isError = uiState.passwordError != null,
                            supportingText = uiState.passwordError?.let { errorText ->
                                { Text(text = errorText.asString()) }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { onSubmitLogin() }
                            ),
                            shape = RoundedCornerShape(TaskFlowDimens.radiusLg)
                        )
                    }

                    Button(
                        onClick = onSubmitLogin,
                        enabled = !uiState.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(TaskFlowDimens.btnHeightPrimary),
                        shape = RoundedCornerShape(TaskFlowDimens.radiusXl)
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(TaskFlowDimens.iconBase),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = TaskFlowDimens.strokeDashed
                            )
                            Spacer(modifier = Modifier.width(TaskFlowDimens.spacingBase))
                        }
                        Text(
                            text = stringResource(R.string.auth_btn_login),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.auth_go_to_register_prefix),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = stringResource(R.string.auth_go_to_register_cta),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
internal fun AuthErrorBanner(
    message: UiText,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(TaskFlowDimens.radiusMd)
            )
            .padding(horizontal = TaskFlowDimens.spacingLg, vertical = TaskFlowDimens.spacingBase),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingBase)
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(TaskFlowDimens.iconBase)
        )
        Text(
            text = message.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}
