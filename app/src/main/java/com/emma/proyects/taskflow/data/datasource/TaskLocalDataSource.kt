package com.emma.proyects.taskflow.data.datasource

import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.TaskPriority
import com.emma.proyects.taskflow.data.model.ThemeMode

class TaskLocalDataSource {

    private var currentThemeMode: ThemeMode = ThemeMode.SYSTEM

    private val seedTasks = listOf(
        Task(
            id = "1",
            title = "Entregar avance de TaskFlow Mobile",
            description = "Explicar el ciclo UDF y el flujo de eventos en el ViewModel.",
            category = "Universidad",
            dueDateText = "Hoy, 16 Oct",
            priority = TaskPriority.HIGH,
            isCompleted = false,
            isRecurring = false
        ),
        Task(
            id = "2",
            title = "Diseñar mockups en Figma (P-01 a P-09)",
            description = "Definir pantallas en modo claro, oscuro y estado vacío.",
            category = "Diseño UI",
            dueDateText = "12 Oct",
            priority = TaskPriority.HIGH,
            isCompleted = true,
            isRecurring = false
        ),
        Task(
            id = "3",
            title = "Practicar ejercicios de táctica en ajedrez",
            description = "Resolver 15 problemas tácticos para mantener el ritmo.",
            category = "Personal",
            dueDateText = "Diario",
            priority = TaskPriority.MEDIUM,
            isCompleted = false,
            isRecurring = true
        ),
        Task(
            id = "4",
            title = "Revisar lecturas de literatura clásica",
            description = "Avanzar un capítulo antes de dormir.",
            category = "Lectura",
            dueDateText = "Fin de semana",
            priority = TaskPriority.LOW,
            isCompleted = false,
            isRecurring = true
        )
    )

    private val tasksInMemory = seedTasks.toMutableList()

    fun getTasks(): List<Task> = tasksInMemory.toList()

    fun toggleTaskCompletion(taskId: String): List<Task> {
        val index = tasksInMemory.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val current = tasksInMemory[index]
            tasksInMemory[index] = current.copy(isCompleted = !current.isCompleted)
        }
        return tasksInMemory.toList()
    }

    fun deleteTask(taskId: String): List<Task> {
        tasksInMemory.removeAll { it.id == taskId }
        return tasksInMemory.toList()
    }

    fun restoreSeedTasks(): List<Task> {
        tasksInMemory.clear()
        tasksInMemory.addAll(seedTasks)
        return tasksInMemory.toList()
    }

    fun getThemeMode(): ThemeMode = currentThemeMode

    fun setThemeMode(mode: ThemeMode): ThemeMode {
        currentThemeMode = mode
        return currentThemeMode
    }
}