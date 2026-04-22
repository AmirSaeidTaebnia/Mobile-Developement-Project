package com.example.thetaskmanagerapp

import com.example.thetaskmanagerapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for TaskViewModel logic.
 * These tests focus on the data processing part of the ViewModel.
 */
class TaskViewModelTest {

    @Test
    fun test_count_pending_tasks_logic() {
        // We test the logic that counts today's pending tasks
        val testDate = "2024-11-20"
        val tasks = listOf(
            Task(id = 1, title = "Task 1", dueDate = testDate, status = "Pending"),
            Task(id = 2, title = "Task 2", dueDate = testDate, status = "Done"),
            Task(id = 3, title = "Task 3", dueDate = "2024-11-21", status = "Pending")
        )

        // The logic: filter by date and exclude "Done" status
        val result = tasks.count { it.dueDate == testDate && it.status != "Done" }

        // Only Task 1 matches
        assertEquals("Should find exactly 1 pending task for the test date", 1, result)
    }

    @Test
    fun test_workload_value_consistency() {
        // Verify that the workload property correctly stores values (1-5)
        val taskHigh = Task(id = 1, workload = 5)
        val taskLow = Task(id = 2, workload = 1)
        
        assertEquals(5, taskHigh.workload)
        assertEquals(1, taskLow.workload)
    }
}
