package com.example.thetaskmanagerapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thetaskmanagerapp.data.AppDatabase
import com.example.thetaskmanagerapp.data.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).taskDao()

    val tasks: Flow<List<Task>> = dao.getAllTasks()
    val doneTasks: Flow<List<Task>> = dao.getDoneTasks()
    val totalNotifications: Flow<Int?> = dao.getTotalNotificationCount()

    fun insertTask(task: Task) {
        viewModelScope.launch {
            dao.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            dao.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
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
