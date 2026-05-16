package com.example.fitnessapp.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.fitnessapp.ui.components.PrimaryNeonButton
import com.example.fitnessapp.ui.components.StatCard
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onWorkoutClick: (Long) -> Unit
) {
    val latestWorkout by viewModel.latestWorkout.collectAsState()
    val workoutCount by viewModel.workoutCount.collectAsState()
    val uniqueTitles by viewModel.uniqueWorkoutTitles.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val foodLogForDate by viewModel.foodLogForDate.collectAsState()
    val lastAddedFoodLogId by viewModel.lastAddedFoodLogId.collectAsState()
    val scope = rememberCoroutineScope()

    var showStartDialog by remember { mutableStateOf(false) }
    var workoutTitle by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    var isFoodLogView by remember { mutableStateOf(false) }

    val filteredTitles = remember(workoutTitle, uniqueTitles) {
        if (workoutTitle.isEmpty()) {
            uniqueTitles
        } else {
            uniqueTitles.filter { it.contains(workoutTitle, ignoreCase = true) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .draggable(
                state = rememberDraggableState { delta ->
                    if (delta < -20 && !isFoodLogView) isFoodLogView = true
                    if (delta > 20 && isFoodLogView) isFoodLogView = false
                },
                orientation = Orientation.Horizontal
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "DASHBOARD",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFoodLogView) "(Food-log)" else "(Workouts)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = if (isFoodLogView) "Swipe -> for Workouts" else "Swipe <- for Food-log",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            if (!isFoodLogView) {
                // Workout View
                item {
                    PrimaryNeonButton(
                        text = "Start Workout",
                        onClick = {
                            showStartDialog = true
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            label = "TOTAL WORKOUTS",
                            value = workoutCount.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "LATEST PR",
                            value = "85 kg", // Placeholder
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (latestWorkout != null) {
                    item {
                        Text(
                            text = "LATEST WORKOUT",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val workout = latestWorkout!!
                        val dateStr = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(workout.startedAt))
                        val duration = if (workout.finishedAt != null) {
                            val diff = workout.manualDurationMinutes ?: (((workout.finishedAt - workout.startedAt) - workout.durationOffsetMs) / (1000 * 60))
                            " ($diff min)"
                        } else ""
                        
                        StatCard(
                            label = "${workout.title} - $dateStr$duration",
                            value = if (workout.finishedAt != null) "Completed" else "In Progress",
                            modifier = Modifier.clickable { onWorkoutClick(workout.id) }
                        )
                    }
                }
            } else {
                // Food Log View
                item {
                    FoodLogEntrySection(
                        selectedDate = selectedDate,
                        onDateChange = { viewModel.setSelectedDate(it) },
                        onAddLog = { c, f, p, cal, date ->
                            viewModel.addFoodLog(c, f, p, cal, date)
                        },
                        showUndo = lastAddedFoodLogId != null,
                        onUndo = { viewModel.undoLastFoodLog() }
                    )
                }

                item {
                    val isToday = remember(selectedDate) {
                        val cal1 = Calendar.getInstance()
                        val cal2 = Calendar.getInstance().apply { timeInMillis = selectedDate }
                        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                    }
                    val dateStr = remember(selectedDate) {
                        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(selectedDate))
                    }
                
                    Text(
                        text = if (isToday) "TODAY'S TOTALS" else "$dateStr TOTALS",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            label = "CALORIES",
                            value = "${foodLogForDate?.calories?.roundToInt() ?: 0} kcal",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "PROTEIN",
                            value = "${foodLogForDate?.protein?.roundToInt() ?: 0} g",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            label = "CARBS",
                            value = "${foodLogForDate?.carbs?.roundToInt() ?: 0} g",
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "FATS",
                            value = "${foodLogForDate?.fats?.roundToInt() ?: 0} g",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { isFoodLogView = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("FINISH LOGGING FOOD")
                    }
                }
            }
        }
    }

    if (showStartDialog) {
        AlertDialog(
            onDismissRequest = { 
                showStartDialog = false
                expanded = false
            },
            title = { Text("Start Workout") },
            text = {
                Box {
                    OutlinedTextField(
                        value = workoutTitle,
                        onValueChange = { 
                            workoutTitle = it
                            expanded = true
                        },
                        label = { Text("Workout Title (e.g. Chest Day)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (expanded && filteredTitles.isNotEmpty()) {
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f),
                            properties = PopupProperties(focusable = false)
                        ) {
                            filteredTitles.forEach { title ->
                                DropdownMenuItem(
                                    text = { Text(title) },
                                    onClick = {
                                        workoutTitle = title
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (workoutTitle.isNotBlank()) {
                        scope.launch {
                            val id = viewModel.startNewWorkout(workoutTitle)
                            onWorkoutClick(id)
                            showStartDialog = false
                            workoutTitle = ""
                        }
                    }
                }) {
                    Text("Start")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showStartDialog = false
                    expanded = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogEntrySection(
    selectedDate: Long,
    onDateChange: (Long) -> Unit,
    onAddLog: (Double, Double, Double, Double, Long) -> Unit,
    showUndo: Boolean = false,
    onUndo: () -> Unit = {}
) {
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateStr = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(selectedDate))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "LOG FOOD",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Date: $dateStr")
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val newDate = datePickerState.selectedDateMillis ?: selectedDate
                        onDateChange(newDate)
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = carbs,
                onValueChange = { carbs = it },
                label = { Text("Carbs") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = fats,
                onValueChange = { fats = it },
                label = { Text("Fats") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text("Protein") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calories") },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showUndo) {
                TextButton(
                    onClick = onUndo
                ) {
                    Text("UNDO RECENT ADDITION", color = MaterialTheme.colorScheme.error)
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            TextButton(
                onClick = {
                    val c = carbs.toDoubleOrNull() ?: 0.0
                    val f = fats.toDoubleOrNull() ?: 0.0
                    val p = protein.toDoubleOrNull() ?: 0.0
                    // (protein*4, Carbs*4, Fats*8) = calories
                    val cal = (p * 4) + (c * 4) + (f * 8)
                    calories = cal.toString()
                }
            ) {
                Text("Auto calculate calories")
            }
        }
        
        PrimaryNeonButton(
            text = "Add to Log",
            onClick = {
                val c = carbs.toDoubleOrNull() ?: 0.0
                val f = fats.toDoubleOrNull() ?: 0.0
                val p = protein.toDoubleOrNull() ?: 0.0
                val cal = calories.toDoubleOrNull() ?: 0.0
                onAddLog(c, f, p, cal, selectedDate)
                carbs = ""
                fats = ""
                protein = ""
                calories = ""
            }
        )
    }
}
