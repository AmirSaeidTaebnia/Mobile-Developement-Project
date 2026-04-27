package com.example.thetaskmanagerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Task Manager", 
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E).copy(alpha = 0.7f) // Soft Navy
                    ) 
                },
                actions = { NotificationIcon(notificationCount, onNavigateToNotifications) }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer, tonalElevation = 8.dp) {
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, "Tasks", tint = Color(0xFF64B5F6).copy(alpha = 0.8f)) },
                    label = { Text("Tasks", fontSize = 10.sp, color = Color(0xFF1976D2)) },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFFBBDEFB).copy(alpha = 0.3f)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, "Add", tint = Color(0xFF81C784).copy(alpha = 0.8f)) },
                    label = { Text("Add", fontSize = 10.sp) },
                    selected = false,
                    onClick = onAddTask
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, "Calendar", tint = Color(0xFFBA68C8).copy(alpha = 0.8f)) },
                    label = { Text("Calendar", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavigateToCalendar
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.CheckCircle, "Done", tint = Color(0xFF4DB6AC).copy(alpha = 0.8f)) },
                    label = { Text("Done", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavigateToDone
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, "Schedule", tint = Color(0xFFFFB74D).copy(alpha = 0.8f)) },
                    label = { Text("Schedule", fontSize = 10.sp) },
                    selected = false,
                    onClick = onNavigateToTimetable
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            TaskHeader()
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                items(tasks) { task -> TaskCard(task, onEditTask, onDeleteTask) }
            }
        }
    }
}

@Composable
fun NotificationIcon(notificationCount: Int, onNavigateToNotifications: () -> Unit) {
    Box(modifier = Modifier.padding(end = 8.dp).size(48.dp).clickable { onNavigateToNotifications() }, contentAlignment = Alignment.Center) {
        Icon(Icons.Default.Notifications, "Notifications", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
        if (notificationCount > 0) {
            Badge(modifier = Modifier.align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp), containerColor = MaterialTheme.colorScheme.error) {
                Text(notificationCount.toString())
            }
        }
    }
}

@Composable
fun TaskHeader() {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                "Your Tasks", 
                style = MaterialTheme.typography.titleMedium, 
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF455A64).copy(alpha = 0.8f) // Soft Slate
            )
        }
    }
}

@Composable
fun TaskCard(task: Task, onEditTask: (Task) -> Unit, onDeleteTask: (Task) -> Unit) {
    // More vibrant colors for the background
    val cardColor = when (task.priority) {
        "High" -> Color(0xFFFFF2F2) // Very soft red
        "Medium" -> Color(0xFFFFF9E1) // Very soft yellow
        "Low" -> Color(0xFFEBF5FF)    // Very soft blue
        else -> MaterialTheme.colorScheme.surface
    }

    // Accent color for the sidebar (also slightly softened)
    val accentColor = when (task.priority) {
        "High" -> Color(0xFFFFAB91) // Soft Coral
        "Medium" -> Color(0xFFFFE082) // Soft Amber
        "Low" -> Color(0xFF90CAF9)    // Soft Blue
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min), // To make the sidebar take full height
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sidebar color for "more color" and hierarchy
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(8.dp)
                    .background(accentColor)
            )

            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp), tint = Color(0xFFBA68C8).copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(task.dueDate, style = MaterialTheme.typography.bodySmall, color = Color.Black.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(Icons.Default.Speed, null, modifier = Modifier.size(14.dp), tint = Color(0xFF4DB6AC).copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${task.workload}h", style = MaterialTheme.typography.bodySmall, color = Color.Black.copy(alpha = 0.7f))
                    }
                }
                StatusBadge(task.status)
                IconButton(onClick = { onEditTask(task) }) { Icon(Icons.Default.Edit, "Edit", tint = Color(0xFF64B5F6).copy(alpha = 0.7f)) }
                IconButton(onClick = { onDeleteTask(task) }) { Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFEF5350).copy(alpha = 0.7f)) }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (backgroundColor, contentColor) = when(status) {
        "Done" -> Color(0xFFC8E6C9) to Color(0xFF1B5E20) // Stronger green
        "In Progress" -> Color(0xFFBBDEFB) to Color(0xFF0D47A1) // Stronger blue
        else -> Color(0xFFEEEEEE) to Color(0xFF424242) // Gray for Pending
    }
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
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
    var priority by remember { mutableStateOf(task?.priority ?: "Medium") }
    var workloadText by remember { mutableStateOf(task?.workload?.toString() ?: "1.0") }

    var statusExpanded by remember { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        focusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (task == null) "New Task" else "Edit Task", 
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E).copy(alpha = 0.7f) // Consistent soft Navy
                    ) 
                },
                navigationIcon = { 
                    IconButton(onClick = onCancel) { 
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            "Cancel",
                            tint = Color(0xFF1A237E).copy(alpha = 0.6f)
                        ) 
                    } 
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(24.dp).fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = title, 
                onValueChange = { title = it }, 
                label = { Text("Title") }, 
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(20.dp),
                colors = textFieldColors
            )
            OutlinedTextField(
                value = description, 
                onValueChange = { description = it }, 
                label = { Text("Description") }, 
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(20.dp), 
                minLines = 2,
                colors = textFieldColors
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = dueDate, 
                    onValueChange = { dueDate = it }, 
                    label = { Text("Due Date") }, 
                    modifier = Modifier.weight(1f), 
                    shape = RoundedCornerShape(20.dp),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = workloadText, 
                    onValueChange = { workloadText = it }, 
                    label = { Text("Hours") }, 
                    modifier = Modifier.weight(0.7f), 
                    shape = RoundedCornerShape(20.dp), 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = textFieldColors
                )
            }

            // Visual Effort Selector (Priority) with Colors
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Effort Degree", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Low" to Color(0xFFEBF5FF),    // Very soft blue
                        "Medium" to Color(0xFFFFF9E1), // Very soft yellow
                        "High" to Color(0xFFFFF2F2)    // Very soft red
                    ).forEach { (level, color) ->
                        val isSelected = priority == level
                        val accentColor = when(level) {
                            "High" -> Color(0xFFFFAB91) // Soft Coral
                            "Medium" -> Color(0xFFFFE082) // Soft Amber
                            "Low" -> Color(0xFF90CAF9)    // Soft Blue
                            else -> Color.Gray.copy(alpha = 0.4f)
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .background(color, RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) accentColor else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { priority = level },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = level,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (isSelected) accentColor.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Selector de Estado
            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = !statusExpanded }, modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = status, onValueChange = {}, readOnly = true, label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(statusExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                    listOf("Pending", "In Progress", "Done").forEach { s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = { status = s; statusExpanded = false })
                    }
                }
            }

            Button(
                onClick = { onSave(Task(task?.id ?: 0, title, description, dueDate, status, priority, workload = workloadText.toDoubleOrNull() ?: 1.0)) },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF64B5F6).copy(alpha = 0.7f), // Soft Blue Button
                    contentColor = Color.White.copy(alpha = 0.9f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
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
fun NotificationsScreen(tasks: List<Task>, onBack: () -> Unit, onClearAllNotifications: () -> Unit) {
    val today = LocalDate.now().toString()
    val dueTodayTasks = tasks.filter { it.dueDate == today }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Notifications") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } })
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            items(dueTodayTasks) { task ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Due Today", fontWeight = FontWeight.Bold)
                        }
                        Text("'${task.title}' is due today", style = MaterialTheme.typography.bodyMedium)
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
            TopAppBar(title = { Text("Done Tasks") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(tasks) { task -> TaskCard(task, onEditTask, onDeleteTask) }
        }
    }
}
