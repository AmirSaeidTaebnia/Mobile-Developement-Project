package com.example.thetaskmanagerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val dueDate: String = "",
    val status: String = "Pending",

    // notification fields
    val hasUnreadNotification: Boolean = false,
    val notificationCount: Int = 0,
    val reminderEnabled: Boolean = true,

    // Workload field (from 1 to 5)
    val workload: Int = 1
)
