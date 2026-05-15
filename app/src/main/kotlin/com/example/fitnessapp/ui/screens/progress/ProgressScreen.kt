package com.example.fitnessapp.ui.screens.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.components.NeonCard
import com.example.fitnessapp.ui.components.ProgressLineChart
import com.example.fitnessapp.ui.components.StatCard
import com.example.fitnessapp.ui.theme.NeonCyan
import com.example.fitnessapp.ui.theme.SuccessGreen
import com.example.fitnessapp.ui.theme.NeonPurple

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "PERFORMANCE",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

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
                projection = progress.projection,
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
            
            progress.projection?.let { proj ->
                Spacer(modifier = Modifier.height(24.dp))
                ProjectionSection(proj)
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

@Composable
fun ProjectionSection(projection: ProgressProjection) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Text(
            text = "4-WEEK PROJECTION",
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
                ProjectionStatRow("Est. 1RM", projection.predictedEstimated1RMIn4Weeks, projection.estimated1RMChangePercent, "kg")
                ProjectionStatRow("Weekly Vol", projection.predictedWeeklyVolumeIn4Weeks, projection.volumeChangePercent, "kg")
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
                    text = projection.confidence.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = when(projection.confidence) {
                        ProjectionConfidence.HIGH -> SuccessGreen
                        ProjectionConfidence.MEDIUM -> NeonCyan
                        ProjectionConfidence.LOW -> MaterialTheme.colorScheme.error
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Projection is based on recent logged training history and may be inaccurate.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
fun ProjectionStatRow(label: String, value: Double, change: Double, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = String.format("%.1f %s", value, unit),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("(%+.1f%%)", change),
                style = MaterialTheme.typography.labelSmall,
                color = if (change >= 0) SuccessGreen else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
