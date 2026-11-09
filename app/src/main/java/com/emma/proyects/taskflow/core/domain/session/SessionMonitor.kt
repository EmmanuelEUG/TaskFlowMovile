package com.emma.proyects.taskflow.core.domain.session

import kotlinx.coroutines.flow.SharedFlow

interface SessionMonitor {
    val unauthorizedEvents: SharedFlow<Unit>
    fun notifyUnauthorized()
}
