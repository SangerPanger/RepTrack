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
import com.example.fitnessapp.ui.screens.progress.ProgressPoint
import com.example.fitnessapp.ui.theme.NeonCyan
import com.example.fitnessapp.ui.theme.NeonPurple

@Composable
fun ProgressLineChart(
    points: List<ProgressPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    NeonCard(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val width = size.width
            val height = size.height
            val padding = 20.dp.toPx()
            
            val minX = points.minOf { it.date }
            val maxX = points.maxOf { it.date }
            val minY = points.minOf { it.value }
            val maxY = points.maxOf { it.value }
            
            val rangeX = if (maxX > minX) maxX - minX else 1L
            val rangeY = if (maxY > minY) maxY - minY else 1.0
            
            val chartWidth = width - 2 * padding
            val chartHeight = height - 2 * padding
            
            fun getOffset(point: ProgressPoint): Offset {
                val x = padding + ((point.date - minX).toFloat() / rangeX) * chartWidth
                val y = height - (padding + ((point.value - minY).toFloat() / rangeY.toFloat()) * chartHeight)
                return Offset(x, y)
            }
            
            val chartPoints = points.map { getOffset(it) }
            
            val path = Path().apply {
                if (chartPoints.isNotEmpty()) {
                    moveTo(chartPoints.first().x, chartPoints.first().y)
                    for (i in 1 until chartPoints.size) {
                        lineTo(chartPoints[i].x, chartPoints[i].y)
                    }
                }
            }
            
            drawPath(
                path = path,
                color = NeonCyan,
                style = Stroke(width = 2.dp.toPx())
            )
            
            chartPoints.forEach { point ->
                drawCircle(
                    color = NeonPurple,
                    radius = 4.dp.toPx(),
                    center = point
                )
            }
        }
    }
}
