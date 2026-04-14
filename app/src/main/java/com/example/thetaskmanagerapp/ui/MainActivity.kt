package com.example.thetaskmanagerapp.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thetaskmanagerapp.data.Screen
import com.example.thetaskmanagerapp.ui.theme.TheTaskManagerAppTheme
import com.example.thetaskmanagerapp.viewmodel.TaskViewModel
import com.example.thetaskmanagerapp.viewmodel.TimetableViewModel
import java.time.LocalDate

// Custom saver to handle orientation changes for Screen state
val ScreenSaver = Saver<MutableState<Screen>, String>(
    save = { state ->
        when (state.value) {
            is Screen.TaskList -> "TaskList"
            is Screen.DoneTasks -> "DoneTasks"
            is Screen.Calendar -> "Calendar"
            is Screen.Timetable -> "Timetable"
            is Screen.AddEditTask -> "TaskList"
            is Screen.Notifications -> "Notifications"
        }
    },
    restore = { value ->
        mutableStateOf(
            when (value) {
                "TaskList" -> Screen.TaskList
                "DoneTasks" -> Screen.DoneTasks
                "Calendar" -> Screen.Calendar
                "Timetable" -> Screen.Timetable
                "Notifications" -> Screen.Notifications
                else -> Screen.TaskList
            }
        )
    }
)

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null
    private var isDarkBySensor by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize sensor manager and light sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            TheTaskManagerAppTheme(darkTheme = isDarkBySensor) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TaskApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Register sensor listener
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister sensor listener to save battery
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lux = event.values[0]
            // Lowered threshold to 1 lux to prevent constant dark mode in emulators
            isDarkBySensor = lux < 1f
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun TaskApp(
    taskViewModel: TaskViewModel = viewModel(),
    timetableViewModel: TimetableViewModel = viewModel()
) {
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())
    
    // Automatic counter for today's unfinished tasks
    val today = LocalDate.now().toString()
    val notificationCount = tasks.count { it.dueDate == today && it.status != "Done" }

    // Navigation state
    var currentScreen by rememberSaveable(saver = ScreenSaver) { mutableStateOf(Screen.TaskList) }

    when (val screen = currentScreen) {
        is Screen.TaskList -> {
            TaskListScreen(
                tasks = tasks,
                notificationCount = notificationCount,
                onAddTask = { currentScreen = Screen.AddEditTask() },
                onEditTask = { task -> currentScreen = Screen.AddEditTask(task) },
                onDeleteTask = { task -> taskViewModel.deleteTask(task) },
                onNavigateToCalendar = { currentScreen = Screen.Calendar },
                onNavigateToDone = { currentScreen = Screen.DoneTasks },
                onNavigateToTimetable = { currentScreen = Screen.Timetable },
                onNavigateToNotifications = { currentScreen = Screen.Notifications }
            )
        }
        is Screen.AddEditTask -> {
            AddEditTaskScreen(
                task = screen.task,
                onSave = { newTask ->
                    if (screen.task == null) taskViewModel.insertTask(newTask)
                    else taskViewModel.updateTask(newTask)
                    currentScreen = Screen.TaskList
                },
                onCancel = { currentScreen = Screen.TaskList }
            )
        }
        is Screen.Notifications -> {
            NotificationsScreen(
                tasks = tasks,
                onBack = { currentScreen = Screen.TaskList },
                onClearAllNotifications = { taskViewModel.clearAllNotifications() }
            )
        }
        is Screen.DoneTasks -> {
            DoneTasksScreen(
                tasks = tasks.filter { it.status == "Done" },
                onBack = { currentScreen = Screen.TaskList },
                onEditTask = { task -> currentScreen = Screen.AddEditTask(task) },
                onDeleteTask = { task -> taskViewModel.deleteTask(task) }
            )
        }
        is Screen.Calendar -> {
            CalendarScreen(tasks = tasks, onBack = { currentScreen = Screen.TaskList })
        }
        is Screen.Timetable -> {
            TimetableScreen(onBack = { currentScreen = Screen.TaskList }, viewModel = timetableViewModel)
        }
    }
}
