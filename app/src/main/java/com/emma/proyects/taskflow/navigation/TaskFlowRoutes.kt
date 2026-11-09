package com.emma.proyects.taskflow.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LoginRouteDest

@Serializable
data object RegisterRouteDest

@Serializable
data object AuthenticatedGraphDest

@Serializable
data object HomeRouteDest

@Serializable
data object ProductivityRouteDest

@Serializable
data object SettingsRouteDest

@Serializable
data class TaskDetailRouteDest(val taskId: String)

@Serializable
data class TaskFormRouteDest(val taskId: String? = null)
