package com.emma.proyects.taskflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.emma.proyects.taskflow.R
import com.emma.proyects.taskflow.auth.presentation.screens.LoginRoute
import com.emma.proyects.taskflow.auth.presentation.screens.RegisterRoute
import com.emma.proyects.taskflow.auth.presentation.viewmodels.LoginViewModel
import com.emma.proyects.taskflow.auth.presentation.viewmodels.RegisterViewModel
import com.emma.proyects.taskflow.auth.presentation.viewmodels.SessionViewModel
import com.emma.proyects.taskflow.core.presentation.UiText
import com.emma.proyects.taskflow.di.AppContainer
import com.emma.proyects.taskflow.tasks.domain.model.AppRoute
import com.emma.proyects.taskflow.tasks.presentation.screens.HomeRoute
import com.emma.proyects.taskflow.tasks.presentation.screens.ProductivityRoute
import com.emma.proyects.taskflow.tasks.presentation.screens.SettingsRoute
import com.emma.proyects.taskflow.tasks.presentation.screens.TaskDetailRoute
import com.emma.proyects.taskflow.tasks.presentation.screens.TaskFormRoute
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TaskDetailViewModel
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TaskFormViewModel
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TasksViewModel
import com.emma.proyects.taskflow.ui.theme.TaskFlowTheme

private const val KEY_DETAIL_REFRESH = "detail_refresh"

@Composable
fun TaskFlowApp(
    appContainer: AppContainer
) {
    val sessionViewModel: SessionViewModel = viewModel(
        factory = appContainer.provideSessionViewModelFactory()
    )
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val startDestination: Any = remember {
        if (sessionState.isAuthenticated) AuthenticatedGraphDest else LoginRouteDest
    }

    var authBannerMessage by remember { mutableStateOf<UiText?>(null) }

    LaunchedEffect(sessionState.isAuthenticated, sessionState.sessionExpiredNotice) {
        if (!sessionState.isAuthenticated) {
            if (sessionState.sessionExpiredNotice) {
                authBannerMessage = UiText.StringResource(R.string.auth_session_expired)
                sessionViewModel.consumeSessionExpiredNotice()
            }
            val currentDestination = navController.currentDestination
            val currentRouteIsAuth = currentDestination?.hasRoute<LoginRouteDest>() == true ||
                currentDestination?.hasRoute<RegisterRouteDest>() == true

            if (!currentRouteIsAuth && currentDestination != null) {
                navController.navigate(LoginRouteDest) {
                    popUpTo(navController.graph.id) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    TaskFlowTheme(themeMode = sessionState.themeMode) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable<LoginRouteDest> {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = appContainer.provideLoginViewModelFactory()
                )
                LoginRoute(
                    viewModel = loginViewModel,
                    themeMode = sessionState.themeMode,
                    bannerMessage = authBannerMessage,
                    onConsumeBannerMessage = { authBannerMessage = null },
                    onCycleTheme = sessionViewModel::onCycleTheme,
                    onNavigateToRegister = {
                        navController.navigate(RegisterRouteDest)
                    },
                    onLoginSuccess = { user ->
                        sessionViewModel.onAuthenticated(user)
                        navController.navigate(AuthenticatedGraphDest) {
                            popUpTo(LoginRouteDest) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<RegisterRouteDest> {
                val registerViewModel: RegisterViewModel = viewModel(
                    factory = appContainer.provideRegisterViewModelFactory()
                )
                RegisterRoute(
                    viewModel = registerViewModel,
                    onNavigateBackToLogin = { navController.popBackStack() },
                    onRegisterSuccess = { user ->
                        sessionViewModel.onAuthenticated(user)
                        navController.navigate(AuthenticatedGraphDest) {
                            popUpTo(LoginRouteDest) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            navigation<AuthenticatedGraphDest>(startDestination = HomeRouteDest) {
                composable<HomeRouteDest> { backStackEntry ->
                    val graphEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<AuthenticatedGraphDest>()
                    }
                    val tasksViewModel: TasksViewModel = viewModel(
                        viewModelStoreOwner = graphEntry,
                        factory = appContainer.provideTasksViewModelFactory()
                    )

                    HomeRoute(
                        viewModel = tasksViewModel,
                        onNavigateTab = { tab ->
                            val targetDest: Any = when (tab) {
                                AppRoute.HOME -> HomeRouteDest
                                AppRoute.PRODUCTIVITY -> ProductivityRouteDest
                                AppRoute.SETTINGS -> SettingsRouteDest
                            }
                            navController.navigate(targetDest) {
                                popUpTo(HomeRouteDest) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onOpenTaskDetail = { taskId ->
                            navController.navigate(TaskDetailRouteDest(taskId = taskId))
                        },
                        onOpenNewTaskForm = {
                            navController.navigate(TaskFormRouteDest(taskId = null))
                        }
                    )
                }

                composable<ProductivityRouteDest> { backStackEntry ->
                    val graphEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<AuthenticatedGraphDest>()
                    }
                    val tasksViewModel: TasksViewModel = viewModel(
                        viewModelStoreOwner = graphEntry,
                        factory = appContainer.provideTasksViewModelFactory()
                    )

                    ProductivityRoute(
                        viewModel = tasksViewModel,
                        onNavigateTab = { tab ->
                            val targetDest: Any = when (tab) {
                                AppRoute.HOME -> HomeRouteDest
                                AppRoute.PRODUCTIVITY -> ProductivityRouteDest
                                AppRoute.SETTINGS -> SettingsRouteDest
                            }
                            navController.navigate(targetDest) {
                                popUpTo(HomeRouteDest) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                composable<SettingsRouteDest> { backStackEntry ->
                    val graphEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<AuthenticatedGraphDest>()
                    }
                    val tasksViewModel: TasksViewModel = viewModel(
                        viewModelStoreOwner = graphEntry,
                        factory = appContainer.provideTasksViewModelFactory()
                    )

                    SettingsRoute(
                        viewModel = tasksViewModel,
                        currentUser = sessionState.currentUser,
                        themeMode = sessionState.themeMode,
                        onNavigateTab = { tab ->
                            val targetDest: Any = when (tab) {
                                AppRoute.HOME -> HomeRouteDest
                                AppRoute.PRODUCTIVITY -> ProductivityRouteDest
                                AppRoute.SETTINGS -> SettingsRouteDest
                            }
                            navController.navigate(targetDest) {
                                popUpTo(HomeRouteDest) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onThemeModeSelected = sessionViewModel::onThemeModeSelected,
                        onLogout = {
                            authBannerMessage = UiText.StringResource(R.string.auth_logout_success)
                            sessionViewModel.onLogout()
                        }
                    )
                }

                composable<TaskDetailRouteDest> { backStackEntry ->
                    val route = backStackEntry.toRoute<TaskDetailRouteDest>()
                    val graphEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<AuthenticatedGraphDest>()
                    }
                    val tasksViewModel: TasksViewModel = viewModel(
                        viewModelStoreOwner = graphEntry,
                        factory = appContainer.provideTasksViewModelFactory()
                    )
                    val detailViewModel: TaskDetailViewModel = viewModel(
                        factory = appContainer.provideTaskDetailViewModelFactory(route.taskId)
                    )
                    val refreshSignal by backStackEntry.savedStateHandle
                        .getStateFlow(KEY_DETAIL_REFRESH, false)
                        .collectAsStateWithLifecycle()

                    TaskDetailRoute(
                        viewModel = detailViewModel,
                        refreshSignal = refreshSignal,
                        onConsumeRefreshSignal = {
                            backStackEntry.savedStateHandle[KEY_DETAIL_REFRESH] = false
                        },
                        onBack = { navController.popBackStack() },
                        onEdit = { taskId ->
                            navController.navigate(TaskFormRouteDest(taskId = taskId))
                        },
                        onDeleted = { feedback ->
                            tasksViewModel.loadTasks(isPullRefresh = true)
                            tasksViewModel.showUserMessage(feedback)
                            navController.popBackStack()
                        },
                        onTaskMutated = {
                            tasksViewModel.loadTasks(isPullRefresh = true)
                        }
                    )
                }

                composable<TaskFormRouteDest> { backStackEntry ->
                    val route = backStackEntry.toRoute<TaskFormRouteDest>()
                    val graphEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<AuthenticatedGraphDest>()
                    }
                    val tasksViewModel: TasksViewModel = viewModel(
                        viewModelStoreOwner = graphEntry,
                        factory = appContainer.provideTasksViewModelFactory()
                    )
                    val formViewModel: TaskFormViewModel = viewModel(
                        factory = appContainer.provideTaskFormViewModelFactory(route.taskId)
                    )

                    TaskFormRoute(
                        viewModel = formViewModel,
                        onClose = { navController.popBackStack() },
                        onSaved = { feedback ->
                            tasksViewModel.loadTasks(isPullRefresh = true)
                            tasksViewModel.showUserMessage(feedback)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(KEY_DETAIL_REFRESH, true)
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
