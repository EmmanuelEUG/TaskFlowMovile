package com.emma.proyects.taskflow.core.data.session

import com.emma.proyects.taskflow.core.domain.session.SessionMonitor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SessionMonitorImpl : SessionMonitor {
    private val _unauthorizedEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val unauthorizedEvents: SharedFlow<Unit> = _unauthorizedEvents.asSharedFlow()

    override fun notifyUnauthorized() {
        _unauthorizedEvents.tryEmit(Unit)
    }
}
