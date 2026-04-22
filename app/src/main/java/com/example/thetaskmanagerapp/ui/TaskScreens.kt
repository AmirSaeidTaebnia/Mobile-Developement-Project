package com.example.thetaskmanagerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thetaskmanagerapp.data.Task
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    notificationCount: Int,
    onAddTask: () -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToDone: () -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    var showTaskMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Task Manager", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Future Scanner */ }) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Scan Task")
                    }
                },
                actions = {
                    NotificationIcon(notificationCount, onNavigateToNotifications)
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { 
                        Box {
                            Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Tasks")
                            
                            DropdownMenu(
                                expanded = showTaskMenu,
                                onDismissRequest = { showTaskMenu = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Camera Scanner") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null) },
                                    onClick = { showTaskMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Settings") },
                                    leadingIcon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null) },
                                    onClick = { showTaskMenu = false }
                                )
                            }
                        }
                    },
                    label = { Text("Tasks", fontSize = 10.sp) },
                    selected = true,
                    onClick = { showTaskMenu = !showTaskMenu }
                )
                
                NavigationBarItem(
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") },
                    label = { Text("Add", fontSize = 10.sp) },
                    selected = false,
                    onClick = onAddTask
                )
                NavigationBarItem(
                    icon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = "Calendar") },
                    label = { Text("Calendar", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavigateToCalendar
                )
                NavigationBarItem(
                    icon = { Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Done") },
                    label = { Text("Done", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavigateToDone
                )
                NavigationBarItem(
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Schedule") },
                    label = { Text("Schedule", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavigateToTimetable
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            TaskHeader()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(tasks) { task ->
                    TaskCard(task, onEditTask, onDeleteTask)
                }
            }
        }
    }
}

@Composable
fun NotificationIcon(notificationCount: Int, onNavigateToNotifications: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(48.dp)
            .clickable { onNavigateToNotifications() }, 
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
        if (notificationCount > 0) {
            Badge(
                modifier = Modifier.align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp),
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Text(notificationCount.toString())
            }
        }
    }
}

@Composable
fun TaskHeader() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Your Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TaskCard(task: Task, onEditTask: (Task) -> Unit, onDeleteTask: (Task) -> Unit) {
    val effort = calculateEffort(task)
    
    val cardColor = when {
        task.status == "Done" -> MaterialTheme.colorScheme.surface
        effort != null && effort > 10 -> Color(0xFFFFCDD2)
        effort != null && effort > 5 -> Color(0xFFFFEBEE)
        effort != null && effort > 2 -> Color(0xFFFFF3E0)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onEditTask(task) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (effort != null && effort > 10 && task.status != "Done") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("⚠️", fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Due: ${task.dueDate}", style = MaterialTheme.typography.bodySmall)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Total: ${task.workload}h", style = MaterialTheme.typography.labelSmall)
                    
                    if (effort != null && task.status != "Done") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f h/day", effort),
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                effort > 10 -> Color(0xFFB71C1C)
                                effort > 5 -> Color(0xFFC62828)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            StatusBadge(task.status)
            
            IconButton(onClick = { onDeleteTask(task) }) {
                Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}

fun calculateEffort(task: Task): Double? {
    return try {
        val today = LocalDate.now()
        val due = LocalDate.parse(task.dueDate)
        val days = ChronoUnit.DAYS.between(today, due)
        if (days >= 0) task.workload / (days + 1) else null
    } catch (e: Exception) { null }
}

@Composable
fun StatusBadge(status: String) {
    Surface(
        color = when(status) {
            "Done" -> Color(0xFFE8F5E9)
            "In Progress" -> Color(0xFFE3F2FD)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = when(status) {
                "Done" -> Color(0xFF2E7D32)
                "In Progress" -> Color(0xFF1565C0)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(task: Task?, onSave: (Task) -> Unit, onCancel: () -> Unit) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: LocalDate.now().toString()) }
    var status by remember { mutableStateOf(task?.status ?: "Pending") }
    var workloadText by remember { mutableStateOf(task?.workload?.toString() ?: "1.0") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try {
            LocalDate.parse(dueDate).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    )

    val effort = remember(dueDate, workloadText) {
        try {
            val w = workloadText.toDoubleOrNull() ?: 0.0
            val d = LocalDate.parse(dueDate)
            val days = ChronoUnit.DAYS.between(LocalDate.now(), d)
            if (days >= 0) w / (days + 1) else null
        } catch (e: Exception) { null }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        dueDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text(if (task == null) "New Task" else "Edit Task", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onCancel) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Cancel") } }
            ) 
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(Icons.Default.Edit, null) }
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = workloadText,
                    onValueChange = { 
                        if (it.isEmpty() || it.toDoubleOrNull() != null || it == ".") {
                            workloadText = it 
                        }
                    },
                    label = { Text("Total Duration (Hours)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    leadingIcon = { Icon(Icons.Default.Speed, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                if (effort != null) {
                    val days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(dueDate)) + 1
                    Text(
                        text = "Calculation: %.1f hours / %d days = %.1f h/day".format(Locale.getDefault(), workloadText.toDoubleOrNull() ?: 0.0, days, effort),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (effort > 5) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            OutlinedTextField(
                value = dueDate,
                onValueChange = { },
                readOnly = true,
                label = { Text("Due Date") },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(Icons.Default.DateRange, null) },
                enabled = false, // Use clickable container instead
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            // Note: Since enabled=false makes it non-clickable, we wrap it or use a Box
            Box(modifier = Modifier.fillMaxWidth().height(64.dp).clickable { showDatePicker = true }) {
                // The TextField is already there, this Box just catches the click
            }

            StatusSelector(status) { status = it }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSave(Task(task?.id ?: 0, title, description, dueDate, status, workload = workloadText.toDoubleOrNull() ?: 0.0)) },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Task", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusSelector(currentStatus: String, onStatusChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("Pending", "In Progress", "Done")
    
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = currentStatus, onValueChange = {}, readOnly = true,
            label = { Text("Status") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            statuses.forEach { s ->
                DropdownMenuItem(text = { Text(s) }, onClick = { onStatusChange(s); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(tasks: List<Task>, onBack: () -> Unit, onClearAllNotifications: () -> Unit) {
    val today = LocalDate.now().toString()
    val dueTodayTasks = tasks.filter { it.dueDate == today && it.status != "Done" }
    val highWorkloadTasks = tasks.filter { 
        val effort = calculateEffort(it)
        effort != null && effort > 5 && it.status != "Done" && it.dueDate != today
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications & Alerts") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            if (dueTodayTasks.isNotEmpty()) {
                item { Text("Due Today", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp)) }
                items(dueTodayTasks) { task ->
                    NotificationCard(task, "Is due today!", MaterialTheme.colorScheme.errorContainer)
                }
            }
            
            if (highWorkloadTasks.isNotEmpty()) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { Text("High Workload Alerts", style = MaterialTheme.typography.titleMedium, color = Color(0xFFC62828), modifier = Modifier.padding(vertical = 8.dp)) }
                items(highWorkloadTasks) { task ->
                    val effort = calculateEffort(task) ?: 0.0
                    NotificationCard(task, "Requires %.1f h/day!".format(Locale.getDefault(), effort), Color(0xFFFFEBEE))
                }
            }

            if (dueTodayTasks.isEmpty() && highWorkloadTasks.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No urgent notifications", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(task: Task, message: String, backgroundColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, null, tint = Color(0xFFB71C1C))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(task.title, fontWeight = FontWeight.Bold)
                Text(message, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneTasksScreen(tasks: List<Task>, onBack: () -> Unit, onEditTask: (Task) -> Unit, onDeleteTask: (Task) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Done Tasks") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(tasks.filter { it.status == "Done" }) { task ->
                    TaskCard(task, onEditTask, onDeleteTask)
                }
            }
        }
    }
}
