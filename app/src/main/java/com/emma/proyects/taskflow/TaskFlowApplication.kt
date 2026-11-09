package com.emma.proyects.taskflow

import android.app.Application
import com.emma.proyects.taskflow.di.AppContainer
import com.emma.proyects.taskflow.di.DefaultAppContainer

class TaskFlowApplication : Application() {
    val appContainer: AppContainer by lazy { DefaultAppContainer(this) }
}
