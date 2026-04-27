package com.example.thetaskmanagerapp.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thetaskmanagerapp.NotificationReceiver
import com.example.thetaskmanagerapp.data.AppDatabase
import com.example.thetaskmanagerapp.data.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).taskDao()

    val tasks: Flow<List<Task>> = dao.getAllTasks()
    val doneTasks: Flow<List<Task>> = dao.getDoneTasks()
    val totalNotifications: Flow<Int?> = dao.getTotalNotificationCount()

    // Logic to count pending tasks for a specific date
    fun countPendingTasks(tasks: List<Task>, date: String): Int {
        return tasks.count { it.dueDate == date && it.status != "Done" }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            val id = dao.insertTask(task).toInt()
            scheduleNotification(task.copy(id = id))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            dao.updateTask(task)
            if (task.status != "Done") {
                scheduleNotification(task)
            } else {
                cancelNotification(task.id)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
            cancelNotification(task.id)
        }
    }

    private fun scheduleNotification(task: Task) {
        val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(getApplication(), NotificationReceiver::class.java).apply {
            putExtra("task_id", task.id)
            putExtra("task_title", task.title)
            putExtra("task_description", "Reminder: ${task.title}")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            task.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val dueDate = LocalDate.parse(task.dueDate)
            val dueMillis = dueDate.atTime(23, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            
            if (System.currentTimeMillis() < dueMillis) {
                // Frequency based on priority/effort
                val interval = when (task.priority) {
                    "High" -> AlarmManager.INTERVAL_HOUR * 2
                    "Medium" -> AlarmManager.INTERVAL_HOUR * 6
                    else -> AlarmManager.INTERVAL_HOUR * 12
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                        interval,
                        pendingIntent
                    )
                } else {
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                        interval,
                        pendingIntent
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelNotification(taskId: Int) {
        val alarmManager = getApplication<Application>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(getApplication(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            getApplication(),
            taskId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    fun clearNotification(taskId: Int) {
        viewModelScope.launch {
            dao.clearNotification(taskId)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            dao.clearAllNotifications()
        }
    }

    fun simulateNotification(taskId: Int) {
        viewModelScope.launch {
            dao.increaseNotificationCount(taskId)
        }
    }
}
