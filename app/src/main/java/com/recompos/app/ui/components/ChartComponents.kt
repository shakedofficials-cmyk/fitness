package com.recompos.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun LineChart(values: List<Double>, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    if (values.size < 2) {
        Box(modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
            Text("Log more data to draw this trend.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }
    Canvas(modifier.fillMaxWidth().height(150.dp).padding(8.dp)) {
        val min = values.minOrNull() ?: 0.0
        val max = values.maxOrNull() ?: 1.0
        val range = (max - min).takeIf { it > 0.0 } ?: 1.0
        val stepX = size.width / (values.lastIndex.coerceAtLeast(1))
        val path = Path()
        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - (((value - min) / range).toFloat() * size.height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawLine(Color.Gray.copy(alpha = 0.35f), Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 2f)
        drawPath(path, color = color, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5f))
    }
}

@Composable
fun BarChart(values: List<Pair<String, Double>>, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.secondary) {
    if (values.isEmpty()) {
        Box(modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
            Text("No graph data yet.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }
    Canvas(modifier.fillMaxWidth().height(150.dp).padding(8.dp)) {
        val max = values.maxOf { it.second }.coerceAtLeast(1.0)
        val barWidth = size.width / (values.size * 1.6f)
        values.forEachIndexed { index, (_, value) ->
            val left = index * (barWidth * 1.6f) + barWidth * 0.3f
            val height = (value / max).toFloat() * size.height
            drawRoundRect(color, topLeft = Offset(left, size.height - height), size = androidx.compose.ui.geometry.Size(barWidth, height), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
        }
    }
}
