package com.emma.proyects.taskflow.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.emma.proyects.taskflow.BuildConfig
import com.emma.proyects.taskflow.auth.data.datasource.AuthApiService
import com.emma.proyects.taskflow.auth.data.repository.AuthRepositoryImpl
import com.emma.proyects.taskflow.auth.domain.repository.AuthRepository
import com.emma.proyects.taskflow.auth.presentation.viewmodels.LoginViewModel
import com.emma.proyects.taskflow.auth.presentation.viewmodels.RegisterViewModel
import com.emma.proyects.taskflow.auth.presentation.viewmodels.SessionViewModel
import com.emma.proyects.taskflow.core.data.network.ApiErrorMapper
import com.emma.proyects.taskflow.core.data.network.AuthInterceptor
import com.emma.proyects.taskflow.core.data.network.NetworkConstants
import com.emma.proyects.taskflow.core.data.session.SessionMonitorImpl
import com.emma.proyects.taskflow.core.data.session.SharedPreferencesSessionStorage
import com.emma.proyects.taskflow.core.data.session.SharedPreferencesThemeStorage
import com.emma.proyects.taskflow.core.domain.session.SessionMonitor
import com.emma.proyects.taskflow.core.domain.session.SessionStorage
import com.emma.proyects.taskflow.core.domain.session.ThemeStorage
import com.emma.proyects.taskflow.tasks.data.datasource.TaskApiService
import com.emma.proyects.taskflow.tasks.data.repository.TaskRepositoryImpl
import com.emma.proyects.taskflow.tasks.domain.repository.TaskRepository
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TaskDetailViewModel
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TaskFormViewModel
import com.emma.proyects.taskflow.tasks.presentation.viewmodels.TasksViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val authRepository: AuthRepository
    val taskRepository: TaskRepository
    val sessionMonitor: SessionMonitor

    fun provideSessionViewModelFactory(): ViewModelProvider.Factory
    fun provideLoginViewModelFactory(): ViewModelProvider.Factory
    fun provideRegisterViewModelFactory(): ViewModelProvider.Factory
    fun provideTasksViewModelFactory(): ViewModelProvider.Factory
    fun provideTaskDetailViewModelFactory(taskId: String): ViewModelProvider.Factory
    fun provideTaskFormViewModelFactory(taskId: String?): ViewModelProvider.Factory
}

class DefaultAppContainer(context: Context) : AppContainer {

    private val appContext = context.applicationContext

    private val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
        }
    }

    private val errorMapper: ApiErrorMapper by lazy {
        ApiErrorMapper(json)
    }

    private val sessionStorage: SessionStorage by lazy {
        SharedPreferencesSessionStorage(appContext)
    }

    private val themeStorage: ThemeStorage by lazy {
        SharedPreferencesThemeStorage(appContext)
    }

    override val sessionMonitor: SessionMonitor by lazy {
        SessionMonitorImpl()
    }

    private val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(
            sessionStorage = sessionStorage,
            sessionMonitor = sessionMonitor
        )
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(NetworkConstants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NetworkConstants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NetworkConstants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        builder.build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(NetworkConstants.MEDIA_TYPE_JSON.toMediaType()))
            .build()
    }

    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    private val taskApiService: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            apiService = authApiService,
            sessionStorage = sessionStorage,
            errorMapper = errorMapper
        )
    }

    override val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(
            apiService = taskApiService,
            themeStorage = themeStorage,
            errorMapper = errorMapper
        )
    }

    override fun provideSessionViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            SessionViewModel(
                authRepository = authRepository,
                taskRepository = taskRepository,
                sessionMonitor = sessionMonitor
            )
        }
    }

    override fun provideLoginViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            LoginViewModel(authRepository = authRepository)
        }
    }

    override fun provideRegisterViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            RegisterViewModel(authRepository = authRepository)
        }
    }

    override fun provideTasksViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            TasksViewModel(
                taskRepository = taskRepository,
                authRepository = authRepository
            )
        }
    }

    override fun provideTaskDetailViewModelFactory(taskId: String): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                TaskDetailViewModel(
                    taskId = taskId,
                    taskRepository = taskRepository
                )
            }
        }

    override fun provideTaskFormViewModelFactory(taskId: String?): ViewModelProvider.Factory =
        viewModelFactory {
            initializer {
                TaskFormViewModel(
                    taskId = taskId,
                    taskRepository = taskRepository
                )
            }
        }
}
