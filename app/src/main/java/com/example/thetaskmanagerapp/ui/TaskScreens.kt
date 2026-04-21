package com.example.thetaskmanagerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thetaskmanagerapp.data.Task
import java.time.LocalDate

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
    // Determine card background color based on workload
    val cardColor = when (task.workload) {
        5 -> Color(0xFFFFEBEE) // Very soft red for high difficulty
        4 -> Color(0xFFFFF3E0) // Very soft orange
        3 -> Color(0xFFFFFDE7) // Very soft yellow
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onEditTask(task) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange, 
                        contentDescription = null, 
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Due: ${task.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    // Load indicator with color logic
                    Text(
                        text = "Load: ${task.workload}/5",
                        style = MaterialTheme.typography.labelSmall,
                        color = when(task.workload) {
                            5 -> Color(0xFFC62828) // Stronger red text for high load
                            4 -> Color(0xFFEF6C00) // Stronger orange text
                            else -> MaterialTheme.colorScheme.secondary
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Surface(
                color = when(task.status) {
                    "Done" -> Color(0xFFE8F5E9)
                    "In Progress" -> Color(0xFFE3F2FD)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = task.status,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = when(task.status) {
                        "Done" -> Color(0xFF2E7D32)
                        "In Progress" -> Color(0xFF1565C0)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(onClick = { onDeleteTask(task) }) {
                Icon(
                    imageVector = Icons.Default.Delete, 
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(tasks: List<Task>, onBack: () -> Unit, onClearAllNotifications: () -> Unit) {
    val today = LocalDate.now().toString()
    val dueTodayTasks = tasks.filter { it.dueDate == today }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = { 
                        onClearAllNotifications()
                        onBack() 
                    }) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            items(dueTodayTasks) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Task Due Today", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("'${task.title}' is due today", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            if (dueTodayTasks.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No notifications for today", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
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
                items(tasks) { task ->
                    TaskCard(task, onEditTask, onDeleteTask)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(task: Task?, onSave: (Task) -> Unit, onCancel: () -> Unit) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: LocalDate.now().toString()) }
    var status by remember { mutableStateOf(task?.status ?: "Pending") }
    var workload by remember { mutableIntStateOf(task?.workload ?: 1) }

    val statuses = listOf("Pending", "In Progress", "Done")
    val workloads = listOf(1, 2, 3, 4, 5)
    
    var expandedStatus by remember { mutableStateOf(false) }
    var expandedWorkload by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text(if (task == null) "New Task" else "Edit Task", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onCancel) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel") }
                }
            ) 
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("What needs to be done?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(imageVector = Icons.Default.Edit, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { /* Future Scanner */ }) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Scan")
                    }
                },
                singleLine = true
            )

            // Workload Selector (Dropdown Menu)
            ExposedDropdownMenuBox(
                expanded = expandedWorkload,
                onExpandedChange = { expandedWorkload = !expandedWorkload },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = "Workload: $workload/5",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Workload") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedWorkload) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Speed, contentDescription = null) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedWorkload,
                    onDismissRequest = { expandedWorkload = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                ) {
                    workloads.forEach { level ->
                        DropdownMenuItem(
                            text = { Text("Level $level") },
                            onClick = { 
                                workload = level
                                expandedWorkload = false 
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Add more details...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null) },
                minLines = 3
            )

            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Due Date") },
                placeholder = { Text("YYYY-MM-DD") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) },
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedStatus) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = when(status) {
                            "Done" -> Color(0xFFE8F5E9)
                            "In Progress" -> Color(0xFFE3F2FD)
                            else -> Color.Transparent
                        },
                        unfocusedContainerColor = when(status) {
                            "Done" -> Color(0xFFF1F8E9)
                            "In Progress" -> Color(0xFFE3F2FD)
                            else -> Color.Transparent
                        }
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                ) {
                    statuses.forEach { item ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    item,
                                    color = when(item) {
                                        "Done" -> Color(0xFF2E7D32)
                                        "In Progress" -> Color(0xFF1565C0)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                ) 
                            },
                            onClick = { 
                                status = item
                                expandedStatus = false 
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSave(Task(task?.id ?: 0, title, description, dueDate, status, workload = workload)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Task", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
