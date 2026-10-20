package com.emma.proyects.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emma.proyects.taskflow.di.TaskModule
import com.emma.proyects.taskflow.presentation.screens.TaskPage
import com.emma.proyects.taskflow.presentation.viewmodels.TaskViewModel
import com.emma.proyects.taskflow.ui.theme.TaskFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: TaskViewModel = viewModel(
                factory = TaskModule.provideTaskViewModelFactory()
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            TaskFlowTheme(themeMode = uiState.themeMode) {
                TaskPage(
                    uiState = uiState,
                    onToggleTask = viewModel::onToggleTask,
                    onSearchChange = viewModel::onSearchQueryChanged,
                    onAreaSelected = viewModel::onAreaSelected,
                    onImportanceSelected = viewModel::onImportanceSelected,
                    onNavigate = viewModel::onNavigate,
                    onCycleTheme = viewModel::onCycleTheme
                )
            }
        }
    }
}