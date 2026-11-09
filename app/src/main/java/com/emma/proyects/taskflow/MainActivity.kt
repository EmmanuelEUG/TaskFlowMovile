package com.emma.proyects.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.emma.proyects.taskflow.navigation.TaskFlowApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as TaskFlowApplication).appContainer
        setContent {
            TaskFlowApp(appContainer = appContainer)
        }
    }
}
