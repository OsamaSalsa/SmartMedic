package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodPressureEntity
import com.example.data.model.BloodSugarEntity
import com.example.data.model.WeightRecordEntity
import com.example.ui.locale.LocalAppStrings
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.isAppInDarkTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeLogSugarDialog(
    onDismiss: () -> Unit,
    onSave: (Float, String, String, String) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    var sugarValue by remember { mutableStateOf("") }
    var timingType by remember { mutableStateOf("صائم") }
    val timingOptions = listOf(
        "صائم" to strings.fastingOption,
        "بعد الإفطار" to strings.postBreakfast,
        "قبل الغداء" to strings.beforeLunch,
        "بعد الغداء" to strings.afterLunch,
        "قبل العشاء" to strings.beforeDinner,
        "بعد العشاء" to strings.afterDinner,
        "قبل النوم" to strings.beforeBed,
        "عشوائي" to strings.randomOption
    )

    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val nowTime = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) }
    var selectedDate by remember { mutableStateOf(todayDate) }
    var selectedTime by remember { mutableStateOf(nowTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.logSugarTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PastDatePickerRow(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    label = "${strings.date}:"
                )

                PastTimePickerRow(
                    selectedTime = selectedTime,
                    onTimeSelected = { selectedTime = it },
                    label = "${strings.time}:"
                )

                OutlinedTextField(
                    value = sugarValue,
                    onValueChange = { sugarValue = it },
                    label = { Text("${strings.bloodSugar} (mg/dL)") },
                    placeholder = { Text("95 / 120") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(strings.measurementPeriod, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(timingOptions) { (key, display) ->
                        FilterChip(
                            selected = timingType == key,
                            onClick = { timingType = key },
                            label = { Text(display, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedicalRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = sugarValue.toFloatOrNull()
                    if (v != null && v > 0) {
                        onSave(v, timingType, selectedDate, selectedTime)
                        Toast.makeText(context, strings.sugarSavedSuccess, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, strings.pleaseEnterValidNumber, Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

@Composable
fun HomeLogBpDialog(
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, String, String, String) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    var sys by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf("") }
    var pulse by remember { mutableStateOf("") }
    var period by remember { mutableStateOf("صباحاً") }
    val periodOptions = listOf(
        "صباحاً" to strings.morningOption,
        "ظهراً" to strings.noonOption,
        "مساءً" to strings.eveningOption,
        "ليلاً" to strings.nightOption,
        "قبل النوم" to strings.beforeBed
    )

    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val nowTime = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) }
    var selectedDate by remember { mutableStateOf(todayDate) }
    var selectedTime by remember { mutableStateOf(nowTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.logBpTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PastDatePickerRow(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    label = "${strings.date}:"
                )

                PastTimePickerRow(
                    selectedTime = selectedTime,
                    onTimeSelected = { selectedTime = it },
                    label = "${strings.time}:"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sys,
                        onValueChange = { sys = it },
                        label = { Text("${strings.systolic} (SYS)") },
                        placeholder = { Text("120") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = dia,
                        onValueChange = { dia = it },
                        label = { Text("${strings.diastolic} (DIA)") },
                        placeholder = { Text("80") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = pulse,
                    onValueChange = { pulse = it },
                    label = { Text("${strings.pulse} (bpm)") },
                    placeholder = { Text("72") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text(strings.measurementPeriod, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(periodOptions) { (key, display) ->
                        FilterChip(
                            selected = period == key,
                            onClick = { period = key },
                            label = { Text(display, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedicalRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s = sys.toIntOrNull()
                    val d = dia.toIntOrNull()
                    val p = pulse.toIntOrNull() ?: 72
                    if (s != null && d != null && s > 0 && d > 0) {
                        onSave(s, d, p, period, selectedDate, selectedTime)
                        Toast.makeText(context, strings.bpSavedSuccess, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, strings.pleaseEnterValidNumber, Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

@Composable
fun HomeLogWeightDialog(
    initialWeightKg: Float,
    onDismiss: () -> Unit,
    onSave: (Float, String) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    var weightInput by remember { mutableStateOf(if (initialWeightKg > 0f) initialWeightKg.toInt().toString() else "") }
    val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedDate by remember { mutableStateOf(todayDate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.logWeightTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PastDatePickerRow(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    label = "${strings.date}:"
                )

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("${strings.weight} (${strings.kgUnit})") },
                    placeholder = { Text("72.5") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Text(strings.normalBmiRange, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightInput.toFloatOrNull()
                    if (w != null && w > 0) {
                        onSave(w, selectedDate)
                        Toast.makeText(context, strings.weightSavedSuccess, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, strings.pleaseEnterValidNumber, Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

@Composable
fun VitalSummaryBox(
    title: String,
    value: String,
    unit: String,
    status: String,
    modifier: Modifier = Modifier
) {
    val isDark = isAppInDarkTheme()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.35f else 0.55f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (value != "--") {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = unit,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            StatusBadge(status = status)
        }
    }
}

@Composable
fun AiActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ReadingRowCard(
    title: String,
    value: String,
    time: String,
    status: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text(time, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(3.dp))
                StatusBadge(status = status)
            }
        }
    }
}

@Composable
fun QuickActionRowCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SugarTrendSparkline(
    records: List<BloodSugarEntity>,
    color: Color = MedicalRed,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val values = records.take(6).map { it.value }.reversed()

        if (values.size == 1) {
            drawCircle(color = color, radius = 4.dp.toPx(), center = Offset(width / 2f, height / 2f))
            return@Canvas
        }

        val minVal = (values.minOrNull() ?: 70f) - 10f
        val maxVal = (values.maxOrNull() ?: 180f) + 10f
        val range = if (maxVal == minVal) 1f else (maxVal - minVal)

        val points = values.mapIndexed { index, v ->
            val x = (index.toFloat() / (values.size - 1)) * width
            val y = height - ((v - minVal) / range) * (height * 0.8f) - (height * 0.1f)
            Offset(x, y)
        }

        val path = Path()
        path.moveTo(points.first().x, points.first().y)

        for (i in 0 until points.size - 1) {
            val pPrev = points[i]
            val pNext = points[i + 1]
            val control1 = Offset((pPrev.x + pNext.x) / 2f, pPrev.y)
            val control2 = Offset((pPrev.x + pNext.x) / 2f, pNext.y)
            path.cubicTo(control1.x, control1.y, control2.x, control2.y, pNext.x, pNext.y)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        points.forEachIndexed { idx, p ->
            val isLast = idx == points.size - 1
            val radius = if (isLast) 4.5.dp.toPx() else 3.dp.toPx()
            drawCircle(color = color, radius = radius, center = p)
            if (isLast) {
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = p)
            }
        }
    }
}

@Composable
fun BpTrendSparkline(
    records: List<BloodPressureEntity>,
    color: Color = MedicalRed,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val values = records.take(6).map { it.systolic.toFloat() }.reversed()

        if (values.size == 1) {
            drawCircle(color = color, radius = 4.dp.toPx(), center = Offset(width / 2f, height / 2f))
            return@Canvas
        }

        val minVal = (values.minOrNull() ?: 90f) - 10f
        val maxVal = (values.maxOrNull() ?: 160f) + 10f
        val range = if (maxVal == minVal) 1f else (maxVal - minVal)

        val points = values.mapIndexed { index, v ->
            val x = (index.toFloat() / (values.size - 1)) * width
            val y = height - ((v - minVal) / range) * (height * 0.8f) - (height * 0.1f)
            Offset(x, y)
        }

        val path = Path()
        path.moveTo(points.first().x, points.first().y)

        for (i in 0 until points.size - 1) {
            val pPrev = points[i]
            val pNext = points[i + 1]
            val control1 = Offset((pPrev.x + pNext.x) / 2f, pPrev.y)
            val control2 = Offset((pPrev.x + pNext.x) / 2f, pNext.y)
            path.cubicTo(control1.x, control1.y, control2.x, control2.y, pNext.x, pNext.y)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        points.forEachIndexed { idx, p ->
            val isLast = idx == points.size - 1
            val radius = if (isLast) 4.5.dp.toPx() else 3.dp.toPx()
            drawCircle(color = color, radius = radius, center = p)
            if (isLast) {
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = p)
            }
        }
    }
}

@Composable
fun WeightTrendSparkline(
    records: List<WeightRecordEntity>,
    color: Color = MedicalRed,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val values = records.take(6).map { it.weightKg }.reversed()

        if (values.size == 1) {
            drawCircle(color = color, radius = 4.dp.toPx(), center = Offset(width / 2f, height / 2f))
            return@Canvas
        }

        val minVal = (values.minOrNull() ?: 50f) - 2f
        val maxVal = (values.maxOrNull() ?: 100f) + 2f
        val range = if (maxVal == minVal) 1f else (maxVal - minVal)

        val points = values.mapIndexed { index, v ->
            val x = (index.toFloat() / (values.size - 1)) * width
            val y = height - ((v - minVal) / range) * (height * 0.8f) - (height * 0.1f)
            Offset(x, y)
        }

        val path = Path()
        path.moveTo(points.first().x, points.first().y)

        for (i in 0 until points.size - 1) {
            val pPrev = points[i]
            val pNext = points[i + 1]
            val control1 = Offset((pPrev.x + pNext.x) / 2f, pPrev.y)
            val control2 = Offset((pPrev.x + pNext.x) / 2f, pNext.y)
            path.cubicTo(control1.x, control1.y, control2.x, control2.y, pNext.x, pNext.y)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        points.forEachIndexed { idx, p ->
            val isLast = idx == points.size - 1
            val radius = if (isLast) 4.5.dp.toPx() else 3.dp.toPx()
            drawCircle(color = color, radius = radius, center = p)
            if (isLast) {
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = p)
            }
        }
    }
}

@Composable
fun VitalDetailInfoBox(
    title: String,
    value: String,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.35f else 0.55f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
