package com.example.fitnessapp.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.domain.model.PotentialLabel
import com.example.fitnessapp.domain.model.PredictionConfidence
import com.example.fitnessapp.domain.model.ProgressPredictionResult
import com.example.fitnessapp.ui.components.NeonCard
import com.example.fitnessapp.ui.components.ProgressLineChart
import com.example.fitnessapp.ui.components.StatCard
import com.example.fitnessapp.ui.theme.NeonCyan
import com.example.fitnessapp.ui.theme.SuccessGreen
import com.example.fitnessapp.ui.theme.NeonPurple
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val foodProgress by viewModel.foodProgress.collectAsState()
    
    var isFoodLogView by remember { mutableStateOf(false) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "PROGRESS",
                        style = MaterialTheme.typography.headlineSmall,
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

            if (!isFoodLogView) {
                // Workout Progress View
                when (val state = uiState) {
                    is ProgressUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeonCyan)
                        }
                    }
                    is ProgressUiState.Empty -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No workout data yet.\nStart a workout to track progress!",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                    is ProgressUiState.Success -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.exercises) { progress ->
                                ExerciseProgressCard(progress)
                            }
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            } else {
                // Food Progress View
                FoodProgressView(foodProgress)
            }
        }
    }
}

@Composable
fun FoodProgressView(foodProgress: com.example.fitnessapp.ui.screens.progress.FoodProgress?) {
    if (foodProgress == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No food logs or profile found.\nEnter your profile in Settings and log food to see progress.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                NeonCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "ESTIMATED WEIGHT CHANGE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        val change = foodProgress.estimatedWeightChange
                        val direction = if (change >= 0) "Gain" else "Loss"
                        Text(
                            text = "${String.format("%.2f", abs(change))} kg $direction",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (change >= 0) NeonPurple else SuccessGreen
                        )
                        Text(
                            text = "Based on ${foodProgress.totalDaysTracked} days of tracking.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        label = "AVG CALORIES",
                        value = "${foodProgress.averageDailyCalories.roundToInt()} kcal",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "TDEE (Est.)",
                        value = "${foodProgress.tdee.roundToInt()} kcal",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestionItem(title: String, description: String, isOptimized: Boolean? = null) {
    val color = when(isOptimized) {
        true -> SuccessGreen
        false -> MaterialTheme.colorScheme.error
        null -> NeonCyan
    }

    val icon = when(isOptimized) {
        true -> "✓ "
        false -> "✕ "
        null -> "• "
    }

    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = icon,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            if (isOptimized == true) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OPTIMIZED",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SuccessGreen.copy(alpha = 0.7f)
                )
            }
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun ExerciseProgressCard(progress: ExerciseProgress) {
    NeonCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = progress.exercise.name.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProgressLineChart(
                points = progress.historyPoints,
                prediction = progress.prediction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "CURRENT 1RM",
                    value = String.format("%.1f kg", progress.estimated1RM),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "TOTAL VOLUME",
                    value = String.format("%.0f kg", progress.totalVolume),
                    modifier = Modifier.weight(1f)
                )
            }
            
            progress.prediction?.let { pred ->
                Spacer(modifier = Modifier.height(24.dp))
                ProjectionSection(pred)
            } ?: run {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Not enough data to estimate future progress yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectionSection(prediction: ProgressPredictionResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(
            text = "4-WEEK ESTIMATE (NOT A GUARANTEE)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = NeonPurple,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Est. 4-week strength potential: +${prediction.strengthGainPercent4Weeks}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Predicted 1RM: ${prediction.predictedEstimated1RM4Weeks} kg",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Hypertrophy potential: ${prediction.hypertrophyPotentialLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                
                var showSuggestions by remember { mutableStateOf(false) }
                TextButton(
                    onClick = { showSuggestions = !showSuggestions },
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonPurple,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "!",
                                    color = Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Maximize suggestions",
                            style = MaterialTheme.typography.labelLarge,
                            color = NeonPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (showSuggestions) {
                    AlertDialog(
                        onDismissRequest = { showSuggestions = false },
                        title = { Text("How to maximize Hypertrophy") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                SuggestionItem(
                                    "Protein",
                                    "Optimal intake depends on calories: 1.6g/kg (surplus), 2.2g/kg (maintain), 2.6g/kg (deficit).",
                                    isOptimized = if (prediction.optimizedFactors.contains("Protein")) true else if (prediction.missingFactors.contains("Protein")) false else null
                                )
                                SuggestionItem(
                                    "Calories",
                                    "A slight surplus (250-500 kcal) is ideal for building muscle.",
                                    isOptimized = if (prediction.optimizedFactors.contains("Calories")) true else if (prediction.missingFactors.contains("Calories")) false else null
                                )
                                SuggestionItem(
                                    "Consistency",
                                    "Hit same muscle 3x/week in the last 14 days",
                                    isOptimized = if (prediction.optimizedFactors.contains("Consistency")) true else if (prediction.missingFactors.contains("Consistency")) false else null
                                )
                                SuggestionItem(
                                    "Volume",
                                    "Ensure muscle is hit with 7 sets per session",
                                    isOptimized = prediction.isVolumeOptimized
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showSuggestions = false }) {
                                Text("Got it", color = NeonCyan)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = NeonCyan,
                        textContentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "CONFIDENCE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = prediction.predictionConfidence.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = when(prediction.predictionConfidence) {
                        PredictionConfidence.HIGH -> SuccessGreen
                        PredictionConfidence.MEDIUM -> NeonCyan
                        PredictionConfidence.LOW -> MaterialTheme.colorScheme.error
                    }
                )
            }
        }

        if (prediction.explanation.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = prediction.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }

        if (prediction.warnings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                prediction.warnings.forEach { warning ->
                    SuggestionChip(
                        onClick = { },
                        label = { Text(warning, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            labelColor = MaterialTheme.colorScheme.error,
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Estimates are based on your profile, nutrition, and training history.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

