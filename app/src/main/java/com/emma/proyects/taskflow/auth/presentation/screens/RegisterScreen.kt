package com.emma.proyects.taskflow.auth.presentation.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.auth.domain.model.User
import com.emma.proyects.taskflow.auth.presentation.viewmodels.RegisterViewModel
import com.emma.proyects.taskflow.tasks.presentation.components.BrandLogoBadge
import com.emma.proyects.taskflow.ui.theme.TaskFlowDimens

@Composable
fun RegisterRoute(
    viewModel: RegisterViewModel,
    onNavigateBackToLogin: () -> Unit,
    onRegisterSuccess: (User) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.registeredUser) {
        uiState.registeredUser?.let { user ->
            viewModel.consumeRegisterSuccess()
            onRegisterSuccess(user)
        }
    }

    RegisterScreen(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onTogglePasswordVisibility = viewModel::onTogglePasswordVisibility,
        onToggleConfirmPasswordVisibility = viewModel::onToggleConfirmPasswordVisibility,
        onSubmitRegister = viewModel::onSubmitRegister,
        onNavigateBackToLogin = onNavigateBackToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onToggleConfirmPasswordVisibility: () -> Unit,
    onSubmitRegister: () -> Unit,
    onNavigateBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBackToLogin) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = {},
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
                .padding(horizontal = TaskFlowDimens.spacing5xl, vertical = TaskFlowDimens.spacingMd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacing3xl)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingMd)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingLg)
                ) {
                    BrandLogoBadge(
                        size = TaskFlowDimens.logoBadgeMd,
                        iconSize = TaskFlowDimens.logoIconMd,
                        contentDescription = stringResource(R.string.app_tagline)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.register_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(R.string.register_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
                    verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingXl)
                ) {
                    if (uiState.generalError != null) {
                        AuthErrorBanner(message = uiState.generalError)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)) {
                        Text(
                            text = stringResource(R.string.auth_label_name),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = uiState.name,
                            onValueChange = onNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.auth_placeholder_name)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Outlined.Person, contentDescription = null)
                            },
                            isError = uiState.nameError != null,
                            supportingText = uiState.nameError?.let { errorText ->
                                { Text(text = errorText.asString()) }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(TaskFlowDimens.radiusLg)
                        )
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
                                Icon(imageVector = Icons.Outlined.Email, contentDescription = null)
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
                                Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
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
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(TaskFlowDimens.radiusLg)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(TaskFlowDimens.spacingSm)) {
                        Text(
                            text = stringResource(R.string.auth_label_confirm_password),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = uiState.confirmPassword,
                            onValueChange = onConfirmPasswordChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.auth_placeholder_confirm_password)) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = onToggleConfirmPasswordVisibility) {
                                    Icon(
                                        imageVector = if (uiState.isConfirmPasswordVisible) {
                                            Icons.Outlined.VisibilityOff
                                        } else {
                                            Icons.Outlined.Visibility
                                        },
                                        contentDescription = stringResource(R.string.cd_toggle_password)
                                    )
                                }
                            },
                            visualTransformation = if (uiState.isConfirmPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            isError = uiState.confirmPasswordError != null,
                            supportingText = uiState.confirmPasswordError?.let { errorText ->
                                { Text(text = errorText.asString()) }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { onSubmitRegister() }
                            ),
                            shape = RoundedCornerShape(TaskFlowDimens.radiusLg)
                        )
                    }

                    Button(
                        onClick = onSubmitRegister,
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
                            text = stringResource(R.string.auth_btn_register),
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
                    text = stringResource(R.string.auth_go_to_login_prefix),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onNavigateBackToLogin) {
                    Text(
                        text = stringResource(R.string.auth_go_to_login_cta),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
