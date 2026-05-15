package com.example.fitnessapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.screens.progress.ProgressPoint
import com.example.fitnessapp.ui.screens.progress.ProgressProjection
import com.example.fitnessapp.ui.theme.NeonCyan
import com.example.fitnessapp.ui.theme.NeonPurple
import com.example.fitnessapp.ui.theme.SuccessGreen

@Composable
fun ProgressLineChart(
    points: List<ProgressPoint>,
    projection: ProgressProjection? = null,
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
            val padding = 24.dp.toPx()
            
            val lastPoint = points.last()
            val projectionDate = lastPoint.date + (28L * 24 * 60 * 60 * 1000)
            val projectionValue = projection?.predictedEstimated1RMIn4Weeks ?: lastPoint.value

            val minX = points.minOf { it.date }
            val maxX = if (projection != null) projectionDate else points.maxOf { it.date }
            val minY = (points.map { it.value } + (projection?.predictedEstimated1RMIn4Weeks ?: lastPoint.value)).minOrNull() ?: 0.0
            val maxY = (points.map { it.value } + (projection?.predictedEstimated1RMIn4Weeks ?: lastPoint.value)).maxOrNull() ?: 1.0
            
            val rangeX = if (maxX > minX) maxX - minX else 1L
            val rangeY = if (maxY > minY) (maxY - minY).coerceAtLeast(1.0) else 1.0
            
            val chartWidth = width - 2 * padding
            val chartHeight = height - 2 * padding
            
            fun getOffset(date: Long, value: Double): Offset {
                val x = padding + ((date - minX).toFloat() / rangeX) * chartWidth
                val y = height - (padding + ((value - minY).toFloat() / rangeY.toFloat()) * chartHeight)
                return Offset(x, y)
            }
            
            val chartPoints = points.map { getOffset(it.date, it.value) }
            
            // Draw historical path
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

            // Draw projection
            projection?.let {
                val projOffset = getOffset(projectionDate, projectionValue)
                val lastOffset = chartPoints.last()
                
                drawPath(
                    path = Path().apply {
                        moveTo(lastOffset.x, lastOffset.y)
                        lineTo(projOffset.x, projOffset.y)
                    },
                    color = SuccessGreen,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
                
                drawCircle(
                    color = SuccessGreen,
                    radius = 5.dp.toPx(),
                    center = projOffset
                )
            }
            
            // Draw historical points
            chartPoints.forEach { point ->
                drawCircle(
                    color = NeonPurple,
                    radius = 3.dp.toPx(),
                    center = point
                )
            }
        }
    }
}
