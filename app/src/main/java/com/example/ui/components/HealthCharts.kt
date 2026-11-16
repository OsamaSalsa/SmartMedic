package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodPressureEntity
import com.example.data.model.BloodSugarEntity
import com.example.data.model.WeightRecordEntity
import com.example.ui.locale.localizeSugarType
import com.example.ui.locale.localizeStatus

@Composable
fun BloodPressureChart(
    records: List<BloodPressureEntity>,
    modifier: Modifier = Modifier
) {
    val strings = com.example.ui.locale.LocalAppStrings.current

    if (records.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(strings.noReadings, style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    val displayRecords = remember(records) { records.take(10).reversed() }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.bloodPressureTrend,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFFE53935), CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${strings.systolicLabel} Systolic", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(Color(0xFF1E88E5), CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${strings.diastolicLabel} Diastolic", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val animProgress by animateFloatAsState(targetValue = 1f, animationSpec = tween(800), label = "bpAnim")

            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(displayRecords) {
                            detectTapGestures { offset ->
                                val stepX = size.width / (displayRecords.size.coerceAtLeast(2) - 1)
                                val index = (offset.x / stepX).toInt().coerceIn(0, displayRecords.size - 1)
                                selectedIndex = index
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val paddingBottom = 25f
                    val paddingTop = 15f
                    val chartHeight = height - paddingBottom - paddingTop

                    val minVal = 50f
                    val maxVal = 190f
                    val range = maxVal - minVal

                    // Draw threshold guideline (120/80 normal limit)
                    val y120 = height - paddingBottom - ((120f - minVal) / range * chartHeight)
                    val y80 = height - paddingBottom - ((80f - minVal) / range * chartHeight)

                    drawLine(
                        color = Color(0x334CAF50),
                        start = Offset(0f, y120),
                        end = Offset(width, y120),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0x334CAF50),
                        start = Offset(0f, y80),
                        end = Offset(width, y80),
                        strokeWidth = 2f
                    )

                    val count = displayRecords.size
                    if (count < 2) {
                        val record = displayRecords.first()
                        val ySys = height - paddingBottom - ((record.systolic.toFloat() - minVal) / range * chartHeight)
                        val yDia = height - paddingBottom - ((record.diastolic.toFloat() - minVal) / range * chartHeight)
                        drawCircle(Color(0xFFE53935), radius = 6.dp.toPx(), center = Offset(width / 2f, ySys))
                        drawCircle(Color(0xFF1E88E5), radius = 6.dp.toPx(), center = Offset(width / 2f, yDia))
                        return@Canvas
                    }

                    val stepX = width / (count - 1)
                    val sysPath = Path()
                    val diaPath = Path()

                    val sysPoints = mutableListOf<Offset>()
                    val diaPoints = mutableListOf<Offset>()

                    displayRecords.forEachIndexed { i, rec ->
                        val x = i * stepX
                        val ySys = height - paddingBottom - (((rec.systolic - minVal) / range * chartHeight) * animProgress)
                        val yDia = height - paddingBottom - (((rec.diastolic - minVal) / range * chartHeight) * animProgress)

                        val pSys = Offset(x, ySys)
                        val pDia = Offset(x, yDia)
                        sysPoints.add(pSys)
                        diaPoints.add(pDia)

                        if (i == 0) {
                            sysPath.moveTo(pSys.x, pSys.y)
                            diaPath.moveTo(pDia.x, pDia.y)
                        } else {
                            sysPath.lineTo(pSys.x, pSys.y)
                            diaPath.lineTo(pDia.x, pDia.y)
                        }
                    }

                    // Draw connecting lines
                    drawPath(
                        path = sysPath,
                        color = Color(0xFFE53935),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawPath(
                        path = diaPath,
                        color = Color(0xFF1E88E5),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw dots
                    sysPoints.forEachIndexed { idx, point ->
                        val isSelected = selectedIndex == idx
                        drawCircle(
                            color = Color.White,
                            radius = if (isSelected) 7.dp.toPx() else 4.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = Color(0xFFE53935),
                            radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(),
                            center = point
                        )
                    }

                    diaPoints.forEachIndexed { idx, point ->
                        val isSelected = selectedIndex == idx
                        drawCircle(
                            color = Color.White,
                            radius = if (isSelected) 7.dp.toPx() else 4.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = Color(0xFF1E88E5),
                            radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(),
                            center = point
                        )
                    }
                }
            }

            // Info bar for selected point or latest
            val activeIdx = selectedIndex ?: (displayRecords.size - 1)
            val selectedRecord = displayRecords.getOrNull(activeIdx)
            if (selectedRecord != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedRecord.date} (${selectedRecord.period})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "ضغط: ${selectedRecord.systolic}/${selectedRecord.diastolic} mmHg (نبض: ${selectedRecord.pulse}) - ${selectedRecord.category}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRecord.category == "طبيعي") Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BloodSugarChart(
    records: List<BloodSugarEntity>,
    modifier: Modifier = Modifier
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    if (records.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(strings.noDataForChart, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    val displayRecords = remember(records) { records.take(10).reversed() }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = strings.bloodSugarTrend,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${strings.targetNormalSugar}: 70 - 99 mg/dL", fontSize = 12.sp, color = Color(0xFF2E7D32))
            }

            Spacer(modifier = Modifier.height(14.dp))

            val animProgress by animateFloatAsState(targetValue = 1f, animationSpec = tween(800), label = "sugarAnim")

            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .pointerInput(displayRecords) {
                            detectTapGestures { offset ->
                                val stepX = size.width / (displayRecords.size.coerceAtLeast(2) - 1)
                                val index = (offset.x / stepX).toInt().coerceIn(0, displayRecords.size - 1)
                                selectedIndex = index
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val paddingBottom = 20f
                    val paddingTop = 15f
                    val chartHeight = height - paddingBottom - paddingTop

                    val minVal = 50f
                    val maxVal = 200f
                    val range = maxVal - minVal

                    // Draw normal zone guideline
                    val y100 = height - paddingBottom - ((100f - minVal) / range * chartHeight)
                    val y70 = height - paddingBottom - ((70f - minVal) / range * chartHeight)
                    drawRect(
                        color = Color(0x154CAF50),
                        topLeft = Offset(0f, y100),
                        size = androidx.compose.ui.geometry.Size(width, y70 - y100)
                    )

                    val count = displayRecords.size
                    if (count == 1) {
                        val rec = displayRecords[0]
                        val x = width / 2f
                        val y = height - paddingBottom - (((rec.value - minVal) / range * chartHeight) * animProgress)
                        val dotColor = when {
                            rec.value < 70 -> Color(0xFFE91E63)
                            rec.value in 70f..100f -> Color(0xFF4CAF50)
                            else -> Color(0xFFF44336)
                        }
                        drawCircle(Color.White, radius = 7.dp.toPx(), center = Offset(x, y))
                        drawCircle(dotColor, radius = 5.dp.toPx(), center = Offset(x, y))
                        return@Canvas
                    }
                    if (count < 2) return@Canvas

                    val stepX = width / (count - 1)
                    val path = Path()
                    val points = mutableListOf<Offset>()

                    displayRecords.forEachIndexed { i, rec ->
                        val x = i * stepX
                        val y = height - paddingBottom - (((rec.value - minVal) / range * chartHeight) * animProgress)
                        val p = Offset(x, y)
                        points.add(p)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFFFF9800),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    points.forEachIndexed { idx, point ->
                        val rec = displayRecords[idx]
                        val dotColor = when {
                            rec.value < 70 -> Color(0xFFE91E63)
                            rec.value in 70f..100f -> Color(0xFF4CAF50)
                            else -> Color(0xFFF44336)
                        }
                        val isSelected = selectedIndex == idx
                        drawCircle(Color.White, radius = if (isSelected) 7.dp.toPx() else 4.dp.toPx(), center = point)
                        drawCircle(dotColor, radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(), center = point)
                    }
                }
            }

            val activeIdx = selectedIndex ?: (displayRecords.size - 1)
            val selectedRecord = displayRecords.getOrNull(activeIdx)
            if (selectedRecord != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${selectedRecord.date} (${strings.localizeSugarType(selectedRecord.type)})", fontSize = 12.sp)
                    Text(
                        text = "${selectedRecord.value} mg/dL - ${strings.localizeStatus(selectedRecord.status)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedRecord.status.contains("طبيعي") || selectedRecord.status.contains("normal")) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

@Composable
fun WeightProgressChart(
    records: List<WeightRecordEntity>,
    modifier: Modifier = Modifier
) {
    val strings = com.example.ui.locale.LocalAppStrings.current
    if (records.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(strings.noDataForChart, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }
    val displayRecords = remember(records) { records.take(10).reversed() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.weightTrend,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                val latest = records.firstOrNull()
                if (latest != null) {
                    Text(
                        text = "${strings.latestWeightLabel}: ${latest.weightKg} ${strings.kgUnit} (BMI: ${latest.bmi})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val animProgress by animateFloatAsState(targetValue = 1f, animationSpec = tween(800), label = "weightAnim")

            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                    val width = size.width
                    val height = size.height
                    val paddingBottom = 20f
                    val paddingTop = 15f
                    val chartHeight = height - paddingBottom - paddingTop

                    val weights = displayRecords.map { it.weightKg }
                    val minVal = (weights.minOrNull() ?: 60f) - 2f
                    val maxVal = (weights.maxOrNull() ?: 100f) + 2f
                    val range = (maxVal - minVal).coerceAtLeast(1f)

                    val count = displayRecords.size
                    if (count < 2) return@Canvas

                    val stepX = width / (count - 1)
                    val path = Path()
                    val points = mutableListOf<Offset>()

                    displayRecords.forEachIndexed { i, rec ->
                        val x = i * stepX
                        val y = height - paddingBottom - (((rec.weightKg - minVal) / range * chartHeight) * animProgress)
                        val p = Offset(x, y)
                        points.add(p)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFF00897B),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    points.forEach { point ->
                        drawCircle(Color.White, radius = 5.dp.toPx(), center = point)
                        drawCircle(Color(0xFF00897B), radius = 3.dp.toPx(), center = point)
                    }
                }
            }
        }
    }
}
