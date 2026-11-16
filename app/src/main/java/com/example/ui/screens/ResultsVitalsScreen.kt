package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.EmptyStateCard
import com.example.data.model.LabTestEntity
import com.example.data.model.calculateLevel
import com.example.ui.components.BloodPressureChart
import com.example.ui.components.BloodSugarChart
import com.example.ui.components.LabTestNameSelector
import com.example.ui.components.PastDatePickerRow
import com.example.ui.components.PastTimePickerRow
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.components.SpeedometerGauge
import com.example.ui.components.TestLevelBadge
import com.example.ui.components.UploadLabReportDialog
import com.example.data.model.BloodSugarEntity
import com.example.data.model.BloodPressureEntity
import com.example.data.model.WeightRecordEntity
import com.example.ui.components.WeightProgressChart
import com.example.ui.theme.DangerRed
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.StatusNormal
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusCritical
import com.example.ui.viewmodel.MedicalViewModel
import com.example.util.PdfReportExporter
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.localizeStatus
import com.example.ui.locale.localizeSugarType
import com.example.ui.locale.localizeCategory
import com.example.ui.locale.localizePeriod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultsVitalsScreen(
    viewModel: MedicalViewModel,
    onNavigateToLabTests: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: السكر, 1: الوزن, 2: ضغط الدم
    var showAddSugarDialog by remember { mutableStateOf(false) }
    var showAddWeightDialog by remember { mutableStateOf(false) }
    var showAddBpDialog by remember { mutableStateOf(false) }
    var showAddCustomTestDialog by remember { mutableStateOf(false) }
    var showUploadReportDialog by remember { mutableStateOf(false) }

    // State for viewing full history of a selected test
    var selectedTestNameForHistory by remember { mutableStateOf<String?>(null) }
    var showAddPastRecordForTest by remember { mutableStateOf<String?>(null) }

    val labTests by viewModel.labTests.collectAsStateWithLifecycle()
    val bpRecords by viewModel.bpRecords.collectAsStateWithLifecycle()
    val sugarRecords by viewModel.sugarRecords.collectAsStateWithLifecycle()
    val weightRecords by viewModel.weightRecords.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val customTests by viewModel.customTestNames.collectAsStateWithLifecycle()

    val completedTests = labTests.filter { it.value.isNotBlank() && it.value != "-" }
    val latestSugar = sugarRecords.firstOrNull()

    // History search
    var historySearchQuery by remember { mutableStateOf("") }
    var historySubTab by remember { mutableIntStateOf(0) } // 0: تحاليل المختبر, 1: قراءات السكر والضغط السابقة

    // Group tests by testName for the full history log
    val testsGroupedByName = remember(completedTests) {
        completedTests.groupBy { it.testName.trim().lowercase(Locale.getDefault()) }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.labResultsTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.clickable { showUploadReportDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                            Text(
                                text = strings.smartAnalysis,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Banner for Full Lab Tests Section (قسم الفحوصات والتحاليل الطبية)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToLabTests() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Biotech, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = strings.labTestsTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = strings.addManualTest,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // 3-Segment Switcher: [السكر] [الوزن] [ضغط الدم]
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        // Tab 0: السكر
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedTab == 0) MedicalRed else Color.Transparent)
                                .clickable { selectedTab = 0 }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.bloodSugar,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }

                        // Tab 1: الوزن
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedTab == 1) MedicalRed else Color.Transparent)
                                .clickable { selectedTab = 1 }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.weight,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }

                        // Tab 2: ضغط الدم
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selectedTab == 2) MedicalRed else Color.Transparent)
                                .clickable { selectedTab = 2 }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = strings.bloodPressure,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 2) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Date Picker Row: <  18 أكتوبر 2026  >
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { /* Previous Day */ }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = strings.previous, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        val dateLocale = if (strings.isArabic) Locale.forLanguageTag("ar") else if (strings.isRussian) Locale.forLanguageTag("ru") else Locale.ENGLISH
                        val todayFormatted = remember(dateLocale) { SimpleDateFormat("dd MMMM yyyy", dateLocale).format(Date()) }
                        Text(
                            text = todayFormatted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { /* Next Day */ }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = strings.next, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Speedometer Arc Gauge for Selected Vital
            item {
                when (selectedTab) {
                    0 -> {
                        val sugarVal = latestSugar?.value ?: 0f
                        val sugarLevel = if (latestSugar != null) {
                            when {
                                sugarVal > 125f -> strings.high
                                sugarVal < 70f -> strings.low
                                else -> strings.normal
                            }
                        } else strings.noReadingsToday

                        SpeedometerGauge(
                            currentValue = sugarVal,
                            hasValue = latestSugar != null,
                            minValue = 60f,
                            maxValue = 180f,
                            unit = "mg/dL",
                            title = strings.latestSugarReading,
                            statusText = sugarLevel,
                            statusColor = when {
                                latestSugar == null -> StatusNormal
                                sugarVal > 125f -> StatusWarning
                                sugarVal < 70f -> StatusCritical
                                else -> StatusNormal
                            }
                        )
                    }
                    1 -> {
                        val latestW = weightRecords.firstOrNull()
                        val weightVal = latestW?.weightKg ?: 0f
                        SpeedometerGauge(
                            currentValue = weightVal,
                            hasValue = latestW != null,
                            minValue = 40f,
                            maxValue = 140f,
                            unit = strings.kgUnit,
                            title = strings.latestWeightReading,
                            statusText = if (latestW != null) "BMI ${latestW.bmi}" else strings.noReadingsToday,
                            statusColor = StatusNormal
                        )
                    }
                    else -> {
                        val latestB = bpRecords.firstOrNull()
                        val bpVal = latestB?.systolic?.toFloat() ?: 0f
                        val bpTitle = if (latestB != null) "${strings.latestBpReading} (${latestB.systolic}/${latestB.diastolic})" else strings.latestBpReading
                        SpeedometerGauge(
                            currentValue = bpVal,
                            hasValue = latestB != null,
                            minValue = 70f,
                            maxValue = 190f,
                            unit = "mmHg",
                            title = bpTitle,
                            statusText = latestB?.category?.let { strings.localizeStatus(it) } ?: strings.noReadingsToday,
                            statusColor = StatusNormal
                        )
                    }
                }
            }

            // Section: السجل الأخير
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.recentHistoryTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // History items
            when (selectedTab) {
                0 -> {
                    if (sugarRecords.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.Favorite,
                                title = strings.noReadingsToday,
                                description = strings.tapCardsToLog
                            )
                        }
                    } else {
                        items(sugarRecords) { s ->
                            val color = when {
                                s.value > 125f || s.status.contains("مرتفع") || s.status.contains("high", ignoreCase = true) -> StatusWarning
                                s.value < 70f || s.status.contains("منخفض") || s.status.contains("low", ignoreCase = true) -> StatusCritical
                                else -> StatusNormal
                            }
                            RecentHistoryRowCard(
                                value = "${s.value.toInt()}",
                                unit = "mg/dL",
                                date = s.date,
                                statusColor = color
                            )
                        }
                    }
                }
                1 -> {
                    if (weightRecords.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.Favorite,
                                title = strings.noReadingsToday,
                                description = strings.tapCardsToLog
                            )
                        }
                    } else {
                        items(weightRecords) { w ->
                            RecentHistoryRowCard(
                                value = "${w.weightKg.toInt()}",
                                unit = strings.kgUnit,
                                date = w.date,
                                statusColor = StatusNormal
                            )
                        }
                    }
                }
                2 -> {
                    if (bpRecords.isEmpty()) {
                        item {
                            EmptyStateCard(
                                icon = Icons.Default.Favorite,
                                title = strings.noReadingsToday,
                                description = strings.tapCardsToLog
                            )
                        }
                    } else {
                        items(bpRecords) { bp ->
                            val color = if (bp.category.contains("طبيعي") || bp.category.contains("normal", ignoreCase = true)) StatusNormal else StatusWarning
                            RecentHistoryRowCard(
                                value = "${bp.systolic}/${bp.diastolic}",
                                unit = "mmHg",
                                date = bp.date,
                                statusColor = color
                            )
                        }
                    }
                }
            }
        }

        // Centered Red Floating Action Button (+) matching Board Design
        FloatingActionButton(
            onClick = {
                when (selectedTab) {
                    0 -> showAddSugarDialog = true
                    1 -> showAddWeightDialog = true
                    2 -> showAddBpDialog = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            containerColor = MedicalRed,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = strings.add, modifier = Modifier.size(26.dp))
        }
    }
    // Dialog for adding Blood Sugar
    if (showAddSugarDialog) {
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
            onDismissRequest = { showAddSugarDialog = false },
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
                            viewModel.addBloodSugar(v, timingType, "", selectedDate, selectedTime)
                            showAddSugarDialog = false
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
                TextButton(onClick = { showAddSugarDialog = false }) { Text(strings.cancel) }
            }
        )
    }

    // Dialog for adding Daily Weight
    if (showAddWeightDialog) {
        var weightInput by remember { mutableStateOf(if (userProfile.initialWeightKg > 0f) userProfile.initialWeightKg.toInt().toString() else "") }
        val todayDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
        var selectedDate by remember { mutableStateOf(todayDate) }

        AlertDialog(
            onDismissRequest = { showAddWeightDialog = false },
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
                        placeholder = { Text("75") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightInput.toFloatOrNull()
                        if (w != null && w > 0) {
                            val userHeight = if (userProfile.heightCm > 0f) userProfile.heightCm else 170f
                            viewModel.addWeight(w, userHeight, "", selectedDate)
                            showAddWeightDialog = false
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
                TextButton(onClick = { showAddWeightDialog = false }) { Text(strings.cancel) }
            }
        )
    }

    // Dialog for adding Blood Pressure
    if (showAddBpDialog) {
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
            onDismissRequest = { showAddBpDialog = false },
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
                            label = { Text(strings.systolic) },
                            placeholder = { Text("120") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = dia,
                            onValueChange = { dia = it },
                            label = { Text(strings.diastolic) },
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
                        if (s != null && d != null) {
                            viewModel.addBloodPressure(s, d, p, period, "", selectedDate, selectedTime)
                            showAddBpDialog = false
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
                TextButton(onClick = { showAddBpDialog = false }) { Text(strings.cancel) }
            }
        )
    }

    // Dialog for viewing the full history of a selected test
    selectedTestNameForHistory?.let { testName ->
        TestHistoryDialog(
            testName = testName,
            allTests = completedTests,
            onDismiss = { selectedTestNameForHistory = null },
            onDeleteTest = { testToDelete ->
                viewModel.deleteLabTest(testToDelete)
            },
            onAddNewRecord = {
                showAddPastRecordForTest = testName
            }
        )
    }

    // Dialog for adding a past record for a specific test
    showAddPastRecordForTest?.let { testName ->
        var pastValue by remember { mutableStateOf("") }
        var pastUnit by remember { mutableStateOf(completedTests.firstOrNull { it.testName == testName }?.unit ?: "") }
        var pastRefRange by remember { mutableStateOf(completedTests.firstOrNull { it.testName == testName }?.referenceRange ?: "") }
        var pastCategory by remember { mutableStateOf(completedTests.firstOrNull { it.testName == testName }?.testCategory ?: "عام") }
        val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
        var pastDate by remember { mutableStateOf(todayStr) }
        var pastLab by remember { mutableStateOf("") }
        var pastStatus by remember { mutableStateOf("طبيعي") }
        var pastStatusFlag by remember { mutableStateOf("Normal") }

        AlertDialog(
            onDismissRequest = { showAddPastRecordForTest = null },
            title = { Text("${strings.add} $testName", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PastDatePickerRow(
                        selectedDate = pastDate,
                        onDateSelected = { pastDate = it },
                        label = "${strings.testDateLabel}:"
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = pastValue,
                            onValueChange = {
                                pastValue = it
                                val temp = LabTestEntity(testName = testName, value = it, referenceRange = pastRefRange)
                                val autoLevel = temp.calculateLevel()
                                if (autoLevel != "غير محدد") {
                                    pastStatus = autoLevel
                                    pastStatusFlag = when (autoLevel) {
                                        "زائد", "High" -> "High"
                                        "نقص", "Low" -> "Low"
                                        else -> "Normal"
                                    }
                                }
                            },
                            label = { Text(strings.resultValue) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pastUnit,
                            onValueChange = { pastUnit = it },
                            label = { Text(strings.unit) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = pastRefRange,
                        onValueChange = { pastRefRange = it },
                        label = { Text(strings.referenceRange) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Level Selection
                    Text(strings.levelSignificance, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(strings.normal to "Normal", strings.high to "High", strings.low to "Low").forEach { (label, flag) ->
                            val isSelected = pastStatus == label || strings.localizeStatus(pastStatus) == label
                            val chipColor = when (flag) {
                                "Normal" -> Color(0xFF16A34A)
                                "High" -> Color(0xFFDC2626)
                                else -> Color(0xFFEA580C)
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    pastStatus = label
                                    pastStatusFlag = flag
                                },
                                label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = pastLab,
                        onValueChange = { pastLab = it },
                        label = { Text(strings.labHospitalLabel) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (pastValue.isNotBlank()) {
                            viewModel.saveLabTest(
                                LabTestEntity(
                                    testName = testName,
                                    testCategory = pastCategory,
                                    value = pastValue,
                                    unit = pastUnit,
                                    referenceRange = pastRefRange,
                                    status = pastStatus,
                                    statusFlag = pastStatusFlag,
                                    testDate = pastDate,
                                    labName = pastLab
                                )
                            )
                            showAddPastRecordForTest = null
                            Toast.makeText(context, strings.success, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, strings.error, Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
                ) {
                    Text(strings.save)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPastRecordForTest = null }) { Text(strings.cancel) }
            }
        )
    }

    // Manual Add Custom Test Dialog
    if (showAddCustomTestDialog) {
        var testName by remember { mutableStateOf("") }
        var testCategory by remember { mutableStateOf("عام") }
        var testValue by remember { mutableStateOf("") }
        var testUnit by remember { mutableStateOf("mg/dL") }
        var refRange by remember { mutableStateOf("") }
        val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
        var selectedTestDate by remember { mutableStateOf(todayStr) }
        var testLabName by remember { mutableStateOf("") }
        var levelStatus by remember { mutableStateOf("طبيعي") }
        var levelFlag by remember { mutableStateOf("Normal") }

        AlertDialog(
            onDismissRequest = { showAddCustomTestDialog = false },
            title = { Text(strings.addManualTest, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LabTestNameSelector(
                        selectedName = testName,
                        onNameChange = { testName = it },
                        customTestsList = customTests,
                        onPresetSelected = { selName, selCat, selUnit, selRef ->
                            testName = selName
                            if (testCategory.isBlank() || testCategory == "عام") testCategory = selCat
                            if (testUnit.isBlank()) testUnit = selUnit
                            if (refRange.isBlank()) refRange = selRef
                        },
                        onAddNewCustomTest = { name, cat, unit, ref ->
                            viewModel.addCustomTestName(name, cat, unit, ref)
                        }
                    )
                    OutlinedTextField(
                        value = testCategory,
                        onValueChange = { testCategory = it },
                        label = { Text(strings.categoryLabel) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = testValue,
                            onValueChange = {
                                testValue = it
                                val temp = LabTestEntity(testName = testName, value = it, referenceRange = refRange)
                                val autoLevel = temp.calculateLevel()
                                if (autoLevel != "غير محدد") {
                                    levelStatus = autoLevel
                                    levelFlag = when (autoLevel) {
                                        "زائد", "High" -> "High"
                                        "نقص", "Low" -> "Low"
                                        else -> "Normal"
                                    }
                                }
                            },
                            label = { Text(strings.resultValue) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = testUnit,
                            onValueChange = { testUnit = it },
                            label = { Text(strings.unit) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = refRange,
                        onValueChange = { refRange = it },
                        label = { Text(strings.referenceRange) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Level Selection
                    Text(strings.levelSignificance, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(strings.normal to "Normal", strings.high to "High", strings.low to "Low").forEach { (label, flag) ->
                            val isSelected = levelStatus == label || strings.localizeStatus(levelStatus) == label
                            val chipColor = when (flag) {
                                "Normal" -> Color(0xFF16A34A)
                                "High" -> Color(0xFFDC2626)
                                else -> Color(0xFFEA580C)
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    levelStatus = label
                                    levelFlag = flag
                                },
                                label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    PastDatePickerRow(
                        selectedDate = selectedTestDate,
                        onDateSelected = { selectedTestDate = it },
                        label = "${strings.testDateLabel}:"
                    )

                    OutlinedTextField(
                        value = testLabName,
                        onValueChange = { testLabName = it },
                        label = { Text(strings.labHospitalLabel) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (testName.isNotBlank() && testValue.isNotBlank()) {
                            viewModel.saveLabTest(
                                LabTestEntity(
                                    testName = testName,
                                    testCategory = testCategory,
                                    value = testValue,
                                    unit = testUnit,
                                    referenceRange = refRange,
                                    status = levelStatus,
                                    statusFlag = levelFlag,
                                    testDate = selectedTestDate,
                                    labName = testLabName
                                )
                            )
                            showAddCustomTestDialog = false
                            Toast.makeText(context, strings.success, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, strings.error, Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
                ) {
                    Text(strings.add)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomTestDialog = false }) { Text(strings.cancel) }
            }
        )
    }

    // Upload & Extract Lab Report Dialog (PDF or Image)
    if (showUploadReportDialog) {
        UploadLabReportDialog(
            viewModel = viewModel,
            onDismiss = { showUploadReportDialog = false }
        )
    }
}

/**
 * Test history dialog for viewing full historical readings
 */
@Composable
fun TestHistoryDialog(
    testName: String,
    allTests: List<LabTestEntity>,
    onDismiss: () -> Unit,
    onDeleteTest: (LabTestEntity) -> Unit,
    onAddNewRecord: () -> Unit
) {
    val strings = LocalAppStrings.current
    val history = remember(testName, allTests) {
        allTests.filter { it.testName.trim().equals(testName.trim(), ignoreCase = true) }
            .sortedByDescending { it.testDate.ifBlank { "0" } }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$testName", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${history.size} ${strings.resultValue}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(strings.previousHistoryForTest, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddNewRecord,
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${strings.add} ${strings.testDateLabel}")
                }

                if (history.isEmpty()) {
                    Text(strings.noTestsYet, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    history.forEachIndexed { index, record ->
                        val olderRecord = history.getOrNull(index + 1)
                        val currentNum = record.value.replace(",", ".").filter { it.isDigit() || it == '.' }.toDoubleOrNull()
                        val olderNum = olderRecord?.value?.replace(",", ".")?.filter { it.isDigit() || it == '.' }?.toDoubleOrNull()

                        val trendText = if (currentNum != null && olderNum != null) {
                            val diff = currentNum - olderNum
                            when {
                                diff > 0.001 -> "+${String.format(Locale.US, "%.1f", diff)} (${olderRecord.testDate}) ↗️"
                                diff < -0.001 -> "${String.format(Locale.US, "%.1f", diff)} (${olderRecord.testDate}) ↘️"
                                else -> "➡️"
                            }
                        } else null

                        val recordLevel = record.calculateLevel()

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "📅 ${record.testDate.ifBlank { strings.testDateLabel }}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        if (record.labName.isNotBlank()) {
                                            Text(
                                                text = "${strings.labHospitalLabel}: ${record.labName}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${record.value} ${record.unit}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = when (recordLevel) {
                                                "زائد", "High", "high" -> DangerRed
                                                "نقص", "Low", "low" -> WarningAmber
                                                else -> SuccessGreen
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        TestLevelBadge(level = recordLevel)
                                    }
                                }

                                if (record.referenceRange.isNotBlank()) {
                                    Text("${strings.refRangeLabel}: ${record.referenceRange}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                if (trendText != null) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            trendText,
                                            fontSize = 10.5.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (record.notes.isNotBlank()) {
                                    Text("${strings.notes}: ${record.notes}", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = { onDeleteTest(record) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = Color(0xFFEF5350), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close)
            }
        }
    )
}

/**
 * صف سجل القياس الأخير المطابق لتصميم الفحوصات في Board UI/UX
 */
@Composable
fun RecentHistoryRowCard(
    value: String,
    unit: String,
    date: String,
    statusColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(statusColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = value,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = date,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

