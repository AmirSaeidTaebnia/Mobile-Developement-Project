package com.example.thetaskmanagerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

// Custom saver to handle orientation changes for Screen state
val ScreenSaver = Saver<MutableState<Screen>, String>(
    save = { state ->
        when (state.value) {
            is Screen.TaskList -> "TaskList"
            is Screen.DoneTasks -> "DoneTasks"
            is Screen.Calendar -> "Calendar"
            is Screen.Timetable -> "Timetable"
            is Screen.AddEditTask -> "TaskList"
        }
    },
    restore = { value ->
        mutableStateOf(
            when (value) {
                "TaskList" -> Screen.TaskList
                "DoneTasks" -> Screen.DoneTasks
                "Calendar" -> Screen.Calendar
                "Timetable" -> Screen.Timetable
                else -> Screen.TaskList
            }
        )
    }
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TaskApp()
                }
            }
        }
    }
}

@Composable
fun TaskApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val dao = database.taskDao()
    val tasks by dao.getAllTasks().collectAsState(initial = emptyList())
    val doneTasks by dao.getDoneTasks().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Screen state survives the rotation
    var currentScreen by rememberSaveable(saver = ScreenSaver) { mutableStateOf(Screen.TaskList) }

    when (val screen = currentScreen) {
        is Screen.TaskList -> {
            TaskListScreen(
                tasks = tasks,
                onAddTask = { currentScreen = Screen.AddEditTask() },
                onEditTask = { task -> currentScreen = Screen.AddEditTask(task) },
                onDeleteTask = { task -> scope.launch { dao.deleteTask(task) } },
                onNavigateToCalendar = { currentScreen = Screen.Calendar },
                onNavigateToDone = { currentScreen = Screen.DoneTasks },
                onNavigateToTimetable = { currentScreen = Screen.Timetable }
            )
        }
        is Screen.AddEditTask -> {
            AddEditTaskScreen(
                task = screen.task,
                onSave = { newTask ->
                    scope.launch {
                        if (screen.task == null) dao.insertTask(newTask)
                        else dao.updateTask(newTask)
                    }
                    currentScreen = Screen.TaskList
                },
                onCancel = { currentScreen = Screen.TaskList }
            )
        }
        is Screen.DoneTasks -> {
            DoneTasksScreen(
                tasks = doneTasks,
                onBack = { currentScreen = Screen.TaskList },
                onEditTask = { task -> currentScreen = Screen.AddEditTask(task) },
                onDeleteTask = { task -> scope.launch { dao.deleteTask(task) } }
            )
        }
        is Screen.Calendar -> {
            CalendarScreen(tasks = tasks, onBack = { currentScreen = Screen.TaskList })
        }
        is Screen.Timetable -> {
            TimetableScreen(onBack = { currentScreen = Screen.TaskList })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onAddTask: () -> Unit,
    onEditTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToDone: () -> Unit,
    onNavigateToTimetable: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    Button(onClick = onAddTask) { Text("Add Task") }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = onNavigateToCalendar) { Text("Calendar") }
                    TextButton(onClick = onNavigateToDone) { Text("Done") }
                    TextButton(onClick = onNavigateToTimetable) { Text("Schedule") }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            TaskHeader()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tasks) { task ->
                    TaskRow(task, onEditTask, onDeleteTask)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun TaskHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Title", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelLarge)
        Text("Due", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.labelLarge)
        Text("Status", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.width(96.dp))
    }
    HorizontalDivider()
}

@Composable
fun TaskRow(task: Task, onEditTask: (Task) -> Unit, onDeleteTask: (Task) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onEditTask(task) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(task.title, modifier = Modifier.weight(1.5f))
        Text(task.dueDate, modifier = Modifier.weight(1.2f))
        Text(task.status, modifier = Modifier.weight(1f))
        Row {
            IconButton(onClick = { onEditTask(task) }) { Icon(Icons.Default.Edit, "Edit") }
            IconButton(onClick = { onDeleteTask(task) }) { Icon(Icons.Default.Delete, "Delete") }
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
            TaskHeader()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tasks) { task ->
                    TaskRow(task, onEditTask, onDeleteTask)
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(tasks: List<Task>, onBack: () -> Unit) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Prev") }
                Text("${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}", style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                }
            }
            val totalCells = ((daysInMonth + firstDayOfMonth + 6) / 7) * 7
            Column {
                for (row in 0 until totalCells / 7) {
                    Row(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                        for (col in 0 until 7) {
                            val dayIndex = row * 7 + col - firstDayOfMonth + 1
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.5.dp, Color.LightGray).padding(2.dp)) {
                                if (dayIndex in 1..daysInMonth) {
                                    val dateStr = currentMonth.atDay(dayIndex).toString()
                                    val tasksForDay = tasks.filter { it.dueDate == dateStr }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(dayIndex.toString(), fontSize = 12.sp)
                                        if (tasksForDay.isNotEmpty()) {
                                            Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                                            Text("${tasksForDay.size} tasks", fontSize = 8.sp, textAlign = TextAlign.Center)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(onBack: () -> Unit, viewModel: TimetableViewModel = viewModel()) {
    // Initial fetch if data is empty
    LaunchedEffect(Unit) { if (viewModel.uiData.isEmpty()) viewModel.fetchTimetable() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Schedule") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = viewModel.classCode,
                    onValueChange = { viewModel.updateClassCode(it) },
                    label = { Text("Class Code") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.fetchTimetable() }) { Icon(Icons.Default.Search, "Search") }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.changeWeek(-1) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Prev Week") }
                Text("Week starting ${viewModel.currentWeekStart.format(DateTimeFormatter.ofPattern("dd.MM."))}", fontWeight = FontWeight.Bold)
                IconButton(onClick = { viewModel.changeWeek(1) }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next Week") }
            }

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
            } else if (viewModel.errorMessage != null) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.fetchTimetable() }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Retry")
                    }
                }
            } else {
                ScheduleGrid(viewModel.uiData, viewModel.currentWeekStart)
            }
        }
    }
}

@Composable
fun ScheduleGrid(reservations: List<UIReservation>, weekStart: LocalDate) {
    val startHour = 7
    val endHour = 21
    val hours = (startHour..endHour).toList()
    val days = (0..6).map { weekStart.plusDays(it.toLong()) }

    Row(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(modifier = Modifier.width(45.dp)) {
            Spacer(modifier = Modifier.height(30.dp))
            hours.forEach { hour ->
                Text("$hour:00", fontSize = 10.sp, modifier = Modifier.height(60.dp).padding(4.dp), textAlign = TextAlign.End)
            }
        }

        days.forEach { day ->
            Column(modifier = Modifier.weight(1f).border(0.5.dp, Color.LightGray.copy(0.3f))) {
                Text(day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.fillMaxWidth().height(30.dp).background(MaterialTheme.colorScheme.primaryContainer),
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                Box(modifier = Modifier.fillMaxWidth().height(((endHour - startHour + 1) * 60).dp)) {
                    hours.forEachIndexed { index, _ ->
                        HorizontalDivider(modifier = Modifier.offset(y = (index * 60).dp), thickness = 0.5.dp, color = Color.LightGray.copy(0.3f))
                    }

                    reservations.filter { it.date == day }.forEach { res ->
                        val startMin = (res.startTime.hour - startHour) * 60 + res.startTime.minute
                        val duration = (res.endTime.hour * 60 + res.endTime.minute) - (res.startTime.hour * 60 + res.startTime.minute)

                        if (duration > 0 && startMin >= 0) {
                            LessonCard(res, startMin, duration)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCard(res: UIReservation, startOffset: Int, duration: Int) {
    Card(
        modifier = Modifier.padding(1.dp).fillMaxWidth().offset(y = startOffset.dp).height(duration.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
    ) {
        Column(modifier = Modifier.padding(2.dp)) {
            Text(res.subject, fontSize = 8.sp, fontWeight = FontWeight.Bold, lineHeight = 9.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (res.location.isNotEmpty()) {
                Text(res.location, fontSize = 7.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Text("${res.startTime} - ${res.endTime}", fontSize = 7.sp, lineHeight = 8.sp)
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

    val statuses = listOf("Pending", "In Progress", "Done")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text(if (task == null) "Add Task" else "Edit Task") }) }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            TextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            TextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                TextField(
                    value = status,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    statuses.forEach { item -> DropdownMenuItem(text = { Text(item) }, onClick = { status = item; expanded = false }) }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onSave(Task(task?.id ?: 0, title, description, dueDate, status)) }) { Text("Save") }
            }
        }
    }
}
