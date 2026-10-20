package com.emma.proyects.taskflow.data.datasource

import android.content.Context
import androidx.core.content.edit
import com.emma.proyects.taskflow.data.model.Importance
import com.emma.proyects.taskflow.data.model.RecurrenceUnit
import com.emma.proyects.taskflow.data.model.Task
import com.emma.proyects.taskflow.data.model.ThemeMode
import org.json.JSONArray
import org.json.JSONObject

class TaskLocalDataSource(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val tasks = mutableListOf<Task>()

    init {
        loadFromJsonStorage()
    }

    private fun getSeedTasks(): List<Task> = listOf(
        Task(
            id = "1",
            title = "Pagar el internet",
            description = "Pagar antes del corte para evitar la suspensión del servicio.",
            area = "Casa",
            importance = Importance.HIGH,
            priorityLevel = 4,
            dueDateFormatted = "9 de octubre, 18:00",
            isOverdue = true,
            isCompleted = false,
            isRecurring = false,
            createdDateFormatted = "5 de octubre"
        ),
        Task(
            id = "2",
            title = "Hacer ejercicio",
            description = "30 minutos de cardio en casa.",
            area = "Salud",
            importance = Importance.MEDIUM,
            priorityLevel = 3,
            dueDateFormatted = "11 de octubre, 06:00",
            isOverdue = false,
            isCompleted = false,
            isRecurring = true,
            recurrenceInterval = 1,
            recurrenceUnit = RecurrenceUnit.DAYS,
            createdDateFormatted = "6 de octubre"
        ),
        Task(
            id = "3",
            title = "Repasar Probabilidad y Estadística",
            description = "Repasar distribuciones y ejercicios para el examen.",
            area = "Escuela",
            importance = Importance.MEDIUM,
            priorityLevel = 3,
            dueDateFormatted = "11 de octubre, 20:00",
            isOverdue = false,
            isCompleted = false,
            isRecurring = false,
            createdDateFormatted = "7 de octubre"
        ),
        Task(
            id = "4",
            title = "Comprar mandado",
            description = "Frutas, verduras, leche y avena para la semana.",
            area = "Casa",
            importance = Importance.LOW,
            priorityLevel = 2,
            dueDateFormatted = "12 de octubre, 17:00",
            isOverdue = false,
            isCompleted = false,
            isRecurring = false,
            createdDateFormatted = "8 de octubre"
        ),
        Task(
            id = "5",
            title = "Entregar avance del proyecto",
            description = "Mostrar arquitectura MVVM, StateFlow y pantallas en Compose.",
            area = "Escuela",
            importance = Importance.HIGH,
            priorityLevel = 5,
            dueDateFormatted = "16 de octubre, 10:00",
            isOverdue = false,
            isCompleted = false,
            isRecurring = false,
            createdDateFormatted = "10 de octubre"
        ),
        Task(
            id = "6",
            title = "Revisar Classroom",
            description = "Verificar anuncios y rúbrica de evaluación de la unidad.",
            area = "Escuela",
            importance = Importance.LOW,
            priorityLevel = 2,
            dueDateFormatted = null,
            isOverdue = false,
            isCompleted = false,
            isRecurring = false,
            createdDateFormatted = "10 de octubre"
        ),
        Task(
            id = "7",
            title = "Crear repositorio en GitHub",
            description = "Inicializar repositorio con commits incrementales.",
            area = "Proyecto",
            importance = Importance.HIGH,
            priorityLevel = 4,
            dueDateFormatted = "8 de octubre, 14:00",
            isOverdue = false,
            isCompleted = true,
            isRecurring = false,
            createdDateFormatted = "4 de octubre"
        ),
        Task(
            id = "8",
            title = "Respaldar archivos",
            description = "Subir copia de seguridad de los proyectos del cuatrimestre.",
            area = "Casa",
            importance = Importance.MEDIUM,
            priorityLevel = 3,
            dueDateFormatted = "7 de octubre, 21:00",
            isOverdue = true,
            isCompleted = true,
            isRecurring = false,
            createdDateFormatted = "3 de octubre"
        ),
        Task(
            id = "9",
            title = "Preparar mockups en Figma",
            description = "Diseñar las vistas P-01 a P-09 con tokens de color.",
            area = "Proyecto",
            importance = Importance.HIGH,
            priorityLevel = 5,
            dueDateFormatted = "9 de octubre, 22:00",
            isOverdue = false,
            isCompleted = true,
            isRecurring = false,
            createdDateFormatted = "5 de octubre"
        )
    )

    private fun loadFromJsonStorage() {
        val rawJson = prefs.getString(KEY_TASKS_JSON, null)
        tasks.clear()
        if (rawJson.isNullOrBlank()) {
            tasks.addAll(getSeedTasks())
            saveToJsonStorage()
        } else {
            try {
                val jsonArray = JSONArray(rawJson)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val dueRaw = obj.optString("dueDateFormatted", "")
                    tasks.add(
                        Task(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            description = obj.optString("description", ""),
                            area = obj.optString("area", "Escuela"),
                            importance = Importance.valueOf(
                                obj.optString("importance", Importance.MEDIUM.name)
                            ),
                            priorityLevel = obj.optInt("priorityLevel", 3),
                            dueDateFormatted = dueRaw.ifBlank { null },
                            isOverdue = obj.optBoolean("isOverdue", false),
                            isCompleted = obj.optBoolean("isCompleted", false),
                            isRecurring = obj.optBoolean("isRecurring", false),
                            recurrenceInterval = obj.optInt("recurrenceInterval", 1),
                            recurrenceUnit = RecurrenceUnit.valueOf(
                                obj.optString("recurrenceUnit", RecurrenceUnit.DAYS.name)
                            ),
                            createdDateFormatted = obj.optString("createdDateFormatted", "Hoy")
                        )
                    )
                }
            } catch (_: Exception) {
                tasks.addAll(getSeedTasks())
                saveToJsonStorage()
            }
        }
    }

    private fun saveToJsonStorage() {
        val jsonArray = JSONArray()
        tasks.forEach { task ->
            val obj = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("area", task.area)
                put("importance", task.importance.name)
                put("priorityLevel", task.priorityLevel)
                put("dueDateFormatted", task.dueDateFormatted ?: "")
                put("isOverdue", task.isOverdue)
                put("isCompleted", task.isCompleted)
                put("isRecurring", task.isRecurring)
                put("recurrenceInterval", task.recurrenceInterval)
                put("recurrenceUnit", task.recurrenceUnit.name)
                put("createdDateFormatted", task.createdDateFormatted)
            }
            jsonArray.put(obj)
        }
        prefs.edit {
            putString(KEY_TASKS_JSON, jsonArray.toString())
        }
    }

    fun getTasks(): List<Task> = tasks.toList()

    fun toggleTask(taskId: String): List<Task> {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val current = tasks[index]
            tasks[index] = current.copy(isCompleted = !current.isCompleted)
            saveToJsonStorage()
        }
        return tasks.toList()
    }

    fun upsertTask(task: Task): List<Task> {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
        } else {
            tasks.add(0, task)
        }
        saveToJsonStorage()
        return tasks.toList()
    }

    fun deleteTask(taskId: String): List<Task> {
        tasks.removeAll { it.id == taskId }
        saveToJsonStorage()
        return tasks.toList()
    }

    fun restoreSeedTasks(): List<Task> {
        tasks.clear()
        tasks.addAll(getSeedTasks())
        saveToJsonStorage()
        return tasks.toList()
    }

    fun getThemeMode(): ThemeMode {
        val saved = prefs.getString(KEY_THEME_MODE, ThemeMode.DARK.name) ?: ThemeMode.DARK.name
        return try {
            ThemeMode.valueOf(saved)
        } catch (_: Exception) {
            ThemeMode.DARK
        }
    }

    fun setThemeMode(mode: ThemeMode): ThemeMode {
        prefs.edit {
            putString(KEY_THEME_MODE, mode.name)
        }
        return mode
    }

    companion object {
        private const val PREFS_NAME = "taskflow_json_store"
        private const val KEY_TASKS_JSON = "tasks_json_array"
        private const val KEY_THEME_MODE = "app_theme_mode"
    }
}