package com.example.fitnessapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.theme.NeonCyan
import com.example.fitnessapp.ui.theme.NeonPurple

@Composable
fun ProgressChartPlaceholder(
    modifier: Modifier = Modifier
) {
    NeonCard(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height
            
            // Draw a simple path to look like a chart
            val path = Path().apply {
                moveTo(0f, height * 0.8f)
                lineTo(width * 0.2f, height * 0.6f)
                lineTo(width * 0.4f, height * 0.7f)
                lineTo(width * 0.6f, height * 0.4f)
                lineTo(width * 0.8f, height * 0.5f)
                lineTo(width, height * 0.2f)
            }
            
            drawPath(
                path = path,
                color = NeonCyan,
                style = Stroke(width = 3.dp.toPx())
            )
            
            // Draw some points
            val points = listOf(
                Offset(0f, height * 0.8f),
                Offset(width * 0.2f, height * 0.6f),
                Offset(width * 0.4f, height * 0.7f),
                Offset(width * 0.6f, height * 0.4f),
                Offset(width * 0.8f, height * 0.5f),
                Offset(width, height * 0.2f)
            )
            
            points.forEach { point ->
                drawCircle(
                    color = NeonPurple,
                    radius = 5.dp.toPx(),
                    center = point
                )
            }
        }
    }
}
