package com.example.thetaskmanagerapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thetaskmanagerapp.data.UIReservation
import com.example.thetaskmanagerapp.viewmodel.TimetableViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

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
