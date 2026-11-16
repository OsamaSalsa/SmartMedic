package com.example.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BentoCard
import com.example.ui.components.BpTrendSparkline
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.HomeLogBpDialog
import com.example.ui.components.HomeLogSugarDialog
import com.example.ui.components.HomeLogWeightDialog
import androidx.compose.material.icons.filled.Medication
import com.example.ui.components.PrimaryGradientButton
import com.example.ui.components.QuickActionRowCard
import com.example.ui.components.ReadingRowCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.SugarTrendSparkline
import com.example.ui.components.UploadLabReportDialog
import com.example.ui.components.UploadPrescriptionDialog
import com.example.ui.components.VitalDetailInfoBox
import com.example.ui.components.WeightTrendSparkline
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.MedicalRedDark
import com.example.ui.theme.MedicalRedSoft
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.viewmodel.MedicalViewModel
import com.example.ui.locale.LocalAppStrings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.ui.locale.localizeStatus
import com.example.ui.locale.localizeSugarType
import com.example.ui.locale.localizePeriod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class HomeVitalMetric {
    SUGAR,
    BLOOD_PRESSURE,
    WEIGHT
}

@Composable
fun HomeScreen(
    viewModel: MedicalViewModel,
    onNavigateToDailyTracking: () -> Unit,
    onNavigateToResults: () -> Unit,
    onNavigateToAiAdvisor: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLabTests: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val bpRecords by viewModel.bpRecords.collectAsStateWithLifecycle()
    val sugarRecords by viewModel.sugarRecords.collectAsStateWithLifecycle()
    val weightRecords by viewModel.weightRecords.collectAsStateWithLifecycle()

    var showAddSugarDialog by remember { mutableStateOf(false) }
    var showAddBpDialog by remember { mutableStateOf(false) }
    var showAddWeightDialog by remember { mutableStateOf(false) }
    var showUploadReportDialog by remember { mutableStateOf(false) }
    var showUploadPrescriptionDialog by remember { mutableStateOf(false) }

    val todayStr = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val latestSugar = sugarRecords.firstOrNull()
    val latestBp = bpRecords.firstOrNull()
    val latestWeight = weightRecords.firstOrNull()

    val todaySugars = remember(sugarRecords, todayStr) { sugarRecords.filter { it.date == todayStr } }
    val todayBps = remember(bpRecords, todayStr) { bpRecords.filter { it.date == todayStr } }
    val todayWeights = remember(weightRecords, todayStr) { weightRecords.filter { it.date == todayStr } }

    var selectedVitalMetric by rememberSaveable { mutableStateOf(HomeVitalMetric.SUGAR) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Greeting Header with Blood Type Badge & Drive Sync Capsule
        item {
            val isDark = isAppInDarkTheme()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar and Greeting (مرحباً / أسامة سليم)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        val homeAvatarBmp = remember(userProfile.profileImageUri) {
                            try {
                                if (userProfile.profileImageUri.isNotBlank()) {
                                    val bytes = Base64.decode(userProfile.profileImageUri, Base64.NO_WRAP)
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                } else null
                            } catch (_: Exception) { null }
                        }
                        if (homeAvatarBmp != null) {
                            androidx.compose.foundation.Image(
                                bitmap = homeAvatarBmp.asImageBitmap(),
                                contentDescription = strings.profilePhoto,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = strings.welcome,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (userProfile.name.isNotBlank()) {
                            Text(
                                text = userProfile.name,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Compact Unified Red Capsule for Blood Group and Drive Sync
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.22f else 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier.clickable { onNavigateToProfile() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (userProfile.bloodType.isNotBlank()) {
                            Icon(
                                Icons.Default.Opacity,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            androidx.compose.runtime.CompositionLocalProvider(
                                androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr
                            ) {
                                Text(
                                    text = userProfile.bloodType,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(width = 1.dp, height = 12.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                            )
                        }
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = "Google Drive",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // 2. MAIN CARD: نظرة عامة اليوم (Today's Overview with Vital Selector)
        item {
            val isDark = isAppInDarkTheme()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_vitals_summary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Header: نظرة عامة اليوم + زر السجل الكامل
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.todayOverview,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(
                            onClick = onNavigateToDailyTracking,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = strings.fullHistory,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vital Metric Selector Chips (سكر الدم / ضغط الدم / الوزن)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.35f else 0.5f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val metrics = listOf(
                            Triple(HomeVitalMetric.SUGAR, strings.bloodSugar, Icons.Default.WaterDrop),
                            Triple(HomeVitalMetric.BLOOD_PRESSURE, strings.bloodPressure, Icons.Default.Favorite),
                            Triple(HomeVitalMetric.WEIGHT, strings.weight, Icons.Default.MonitorWeight)
                        )

                        metrics.forEach { (metric, label, icon) ->
                            val isSelected = selectedVitalMetric == metric
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(9.dp))
                                    .clickable { selectedVitalMetric = metric },
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(9.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated display showing ONLY the selected vital measurement
                    AnimatedContent(
                        targetState = selectedVitalMetric,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "home_vital_metric_display"
                    ) { metric ->
                        when (metric) {
                            HomeVitalMetric.SUGAR -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Row 1: Header + Sparkline
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = strings.latestSugarReading,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                val sugarVal = if (latestSugar != null) "${latestSugar.value.toInt()}" else "--"
                                                Text(
                                                    text = sugarVal,
                                                    fontSize = 34.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "mg/dL",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(bottom = 5.dp)
                                                )
                                            }

                                            // Status and Type Badges
                                            if (latestSugar != null) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (latestSugar.status == "طبيعي" || latestSugar.status == "Normal") (if (isDark) Color(0xFF143823) else Color(0xFFDCFCE7)) else MaterialTheme.colorScheme.primaryContainer,
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                                    ) {
                                                        Text(
                                                            text = strings.localizeStatus(latestSugar.status),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (latestSugar.status == "طبيعي" || latestSugar.status == "Normal") (if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D)) else MaterialTheme.colorScheme.onPrimaryContainer,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                                    ) {
                                                        Text(
                                                            text = strings.localizeSugarType(latestSugar.type),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Trend Sparkline Curve Canvas
                                        if (sugarRecords.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .width(130.dp)
                                                    .height(54.dp)
                                            ) {
                                                SugarTrendSparkline(
                                                    records = sugarRecords,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Key Metrics Summary 3-Column Grid
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        VitalDetailInfoBox(
                                            title = if (strings.isArabic) "المعدل الطبيعي" else if (strings.isRussian) "Целевая норма" else "Normal Target",
                                            value = "70 - 99 mg/dL",
                                            modifier = Modifier.weight(1f),
                                            isDark = isDark
                                        )
                                        VitalDetailInfoBox(
                                            title = if (strings.isArabic) "آخر توقيت" else if (strings.isRussian) "Время замера" else "Reading Time",
                                            value = latestSugar?.let { "${it.time}" } ?: "--:--",
                                            modifier = Modifier.weight(1f),
                                            isDark = isDark
                                        )
                                        VitalDetailInfoBox(
                                            title = if (strings.isArabic) "قراءات اليوم" else if (strings.isRussian) "Замеров сегодня" else "Today's Logs",
                                            value = "${todaySugars.size}",
                                            modifier = Modifier.weight(1f),
                                            isDark = isDark
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Action Button: تسجيل فحص سكر جديد
                                    Button(
                                        onClick = { showAddSugarDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (strings.isArabic) "تسجيل قراءة سكر جديدة" else if (strings.isRussian) "Записать замер сахара" else "Log Blood Sugar Reading",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HomeVitalMetric.BLOOD_PRESSURE -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Row 1: Header + Sparkline
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = strings.latestBpReading,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                val bpVal = if (latestBp != null) "${latestBp.systolic}/${latestBp.diastolic}" else "--/--"
                                                Text(
                                                    text = bpVal,
                                                    fontSize = 34.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "mmHg",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(bottom = 5.dp)
                                                )
                                            }

                                            // Category and Pulse Badges
                                            if (latestBp != null) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    val bpColor = if (latestBp.category == "طبيعي" || latestBp.category == "Normal") {
                                                        if (isDark) Color(0xFF4ADE80) to Color(0xFF143823) else Color(0xFF15803D) to Color(0xFFDCFCE7)
                                                    } else if (latestBp.category.contains("مرتفع") || latestBp.category.contains("Elevated")) {
                                                        if (isDark) Color(0xFFFBBF24) to Color(0xFF422006) else Color(0xFFB45309) to Color(0xFFFEF3C7)
                                                    } else {
                                                        if (isDark) Color(0xFFF87171) to Color(0xFF450A0A) else Color(0xFFDC2626) to Color(0xFFFEE2E2)
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = bpColor.second,
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                                    ) {
                                                        Text(
                                                            text = strings.localizeStatus(latestBp.category),
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = bpColor.first,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(Icons.Default.Favorite, contentDescription = null, tint = MedicalRed, modifier = Modifier.size(11.dp))
                                                            Spacer(modifier = Modifier.width(3.dp))
                                                            Text(
                                                                text = "${latestBp.pulse} bpm",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Trend Sparkline Curve Canvas
                                        if (bpRecords.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .width(130.dp)
                                                    .height(54.dp)
                                            ) {
                                                BpTrendSparkline(
                                                    records = bpRecords,
                                                    color = MedicalRed
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Key Metrics Summary 3-Column Grid
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        VitalDetailInfoBox(
                                            title = if (strings.isArabic) "المعدل المثالي" else if (strings.isRussian) "Идеал" else "Ideal Range",
                                            value = "< 120/80",
                                            modifier = Modifier.weight(1f),
                                            isDark = isDark
                                        )
                                        VitalDetailInfoBox(
                                            title = if (strings.isArabic) "الفترة والوقت" else if (strings.isRussian) "Период" else "Period & Time",
                                            value = latestBp?.let { "${strings.localizePeriod(it.period)}" } ?: "--",
                                            modifier = Modifier.weight(1f),
                                            isDark = isDark
                                        )
                                        VitalDetailInfoBox(
                                            title = if (strings.isArabic) "قياسات اليوم" else if (strings.isRussian) "Замеров сегодня" else "Today's Logs",
                                            value = "${todayBps.size}",
                                            modifier = Modifier.weight(1f),
                                            isDark = isDark
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Action Button: تسجيل قياس ضغط جديد
                                    Button(
                                        onClick = { showAddBpDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (strings.isArabic) "تسجيل قياس ضغط جديد" else if (strings.isRussian) "Записать замер давления" else "Log Blood Pressure Reading",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            HomeVitalMetric.WEIGHT -> {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Row 1: Header + Sparkline
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = strings.latestWeightReading,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.Bottom) {
                                                val weightVal = if (latestWeight != null) "${latestWeight.weightKg.toInt()}" else "--"
                                                Text(
                                                    text = weightVal,
                                                    fontSize = 34.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = strings.kgUnit,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(bottom = 5.dp)
                                                )
                                            }

                                            // BMI and Category Badges
                                            if (latestWeight != null) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                                    ) {
                                                        Text(
                                                            text = "BMI ${latestWeight.bmi}",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (latestWeight.bmiCategory.contains("طبيعي") || latestWeight.bmiCategory.contains("Normal")) (if (isDark) Color(0xFF143823) else Color(0xFFDCFCE7)) else MaterialTheme.colorScheme.primaryContainer,
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                                                    ) {
                                                        Text(
                                                            text = latestWeight.bmiCategory,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = if (latestWeight.bmiCategory.contains("طبيعي") || latestWeight.bmiCategory.contains("Normal")) (if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D)) else MaterialTheme.colorScheme.onPrimaryContainer,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Trend Sparkline Curve Canvas
                                        if (weightRecords.isNotEmpty()) {
                                            Box(
                                                modifier = Modifier
                                                    .width(130.dp)
                                                    .height(54.dp)
                                            ) {
                                                WeightTrendSparkline(
                                                    records = weightRecords,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Key Metrics Summary 3-Column Grid
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        VitalDetailInfoBox(
                                            title = if (strings.isArabic) "مؤشر BMI المثالي" else if (strings.isRussian) "Идеальный ИМТ" else "Normal BMI",
                                            value = "18.5 - 24.9",
                                            modifier = Modifier.weight(1f),
                                            isDark = isDark
                                        )
                                        VitalDetailInfoBox(
                                            title = if (strings.isArabic) "الطول المعتمد" else if (strings.isRussian) "Рост" else "Height",
                                            value = "${latestWeight?.heightCm?.toInt() ?: userProfile.heightCm.toInt()} ${if (strings.isArabic) "سم" else "cm"}",
                                            modifier = Modifier.weight(1f),
                                            isDark = isDark
                                        )
                                        VitalDetailInfoBox(
                                            title = if (strings.isArabic) "تاريخ التسجيل" else if (strings.isRussian) "Дата замера" else "Log Date",
                                            value = latestWeight?.date ?: "--",
                                            modifier = Modifier.weight(1f),
                                            isDark = isDark
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Action Button: تسجيل وزن جديد
                                    Button(
                                        onClick = { showAddWeightDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (strings.isArabic) "تسجيل وزن وكتلة جديدة" else if (strings.isRussian) "Записать новый вес" else "Log New Weight Record",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. QUICK ACTIONS: إجراءات سريعة (3 Wide Action Cards matching the Design Board)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = strings.quickActions,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // 1. تسجيل فحص جديد (Checkmark Icon in Red Circle)
                QuickActionRowCard(
                    title = when (selectedVitalMetric) {
                        HomeVitalMetric.SUGAR -> "${strings.logNewTest} (${strings.bloodSugar})"
                        HomeVitalMetric.BLOOD_PRESSURE -> "${strings.logNewTest} (${strings.bloodPressure})"
                        HomeVitalMetric.WEIGHT -> "${strings.logNewTest} (${strings.weight})"
                    },
                    icon = Icons.Default.Check,
                    onClick = {
                        when (selectedVitalMetric) {
                            HomeVitalMetric.SUGAR -> showAddSugarDialog = true
                            HomeVitalMetric.BLOOD_PRESSURE -> showAddBpDialog = true
                            HomeVitalMetric.WEIGHT -> showAddWeightDialog = true
                        }
                    },
                    testTag = "home_quick_log_test"
                )

                // 2. تصوير فحص أو تحليل (Camera Icon in Red Circle)
                QuickActionRowCard(
                    title = strings.scanOrUploadReport,
                    icon = Icons.Default.CameraAlt,
                    onClick = { showUploadReportDialog = true },
                    testTag = "home_quick_scan_report"
                )

                // 3. إضافة دواء (Pill/Medication Icon in Red Circle)
                QuickActionRowCard(
                    title = strings.addMedicationQuick,
                    icon = Icons.Default.Medication,
                    onClick = { showUploadPrescriptionDialog = true },
                    testTag = "home_quick_add_med"
                )

                // 4. قسم الفحوصات والتحاليل الطبية (Biotech Icon)
                QuickActionRowCard(
                    title = strings.labTestsTitle,
                    icon = Icons.Default.Biotech,
                    onClick = onNavigateToLabTests,
                    testTag = "home_quick_lab_tests"
                )
            }
        }

        // 5. Today's Timeline / Log
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.todayMultipleReadings,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = todayStr,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (todaySugars.isEmpty() && todayBps.isEmpty() && todayWeights.isEmpty()) {
                    EmptyStateCard(
                        icon = Icons.Default.Favorite,
                        title = strings.noReadingsToday,
                        description = strings.tapCardsToLog,
                        actionButton = {
                            if (sugarRecords.isNotEmpty() || bpRecords.isNotEmpty() || weightRecords.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = onNavigateToDailyTracking,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "${strings.viewPastReadings} (${sugarRecords.size + bpRecords.size + weightRecords.size})",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        todaySugars.forEach { sugar ->
                            ReadingRowCard(
                                title = "${strings.bloodSugar}: ${strings.localizeSugarType(sugar.type)}",
                                value = "${sugar.value} mg/dL",
                                time = sugar.time,
                                status = strings.localizeStatus(sugar.status),
                                icon = Icons.Default.Speed
                            )
                        }
                        todayBps.forEach { bp ->
                            ReadingRowCard(
                                title = "${strings.bloodPressure}: ${strings.localizePeriod(bp.period)}",
                                value = "${bp.systolic}/${bp.diastolic} (${strings.pulse} ${bp.pulse})",
                                time = bp.time,
                                status = strings.localizeStatus(bp.category),
                                icon = Icons.Default.Favorite
                            )
                        }
                        todayWeights.forEach { weight ->
                            ReadingRowCard(
                                title = "${strings.weight}: ${weight.bmiCategory}",
                                value = "${weight.weightKg.toInt()} ${strings.kgUnit} (BMI ${weight.bmi})",
                                time = weight.date,
                                status = weight.bmiCategory,
                                icon = Icons.Default.MonitorWeight
                            )
                        }
                    }
                }
            }
        }

        // 6. Comprehensive AI Consultation Banner
        item {
            PrimaryGradientButton(
                text = strings.consultAiComprehensive,
                icon = Icons.Default.AutoAwesome,
                onClick = onNavigateToAiAdvisor,
                testTag = "home_ai_advisor_btn"
            )
        }
    }

    if (showAddSugarDialog) {
        HomeLogSugarDialog(
            onDismiss = { showAddSugarDialog = false },
            onSave = { value, timingType, selectedDate, selectedTime ->
                viewModel.addBloodSugar(value, timingType, "", selectedDate, selectedTime)
                showAddSugarDialog = false
            }
        )
    }

    if (showAddBpDialog) {
        HomeLogBpDialog(
            onDismiss = { showAddBpDialog = false },
            onSave = { sys, dia, pulse, period, selectedDate, selectedTime ->
                viewModel.addBloodPressure(sys, dia, pulse, period, "", selectedDate, selectedTime)
                showAddBpDialog = false
            }
        )
    }

    if (showAddWeightDialog) {
        HomeLogWeightDialog(
            initialWeightKg = userProfile.initialWeightKg,
            onDismiss = { showAddWeightDialog = false },
            onSave = { weight, selectedDate ->
                viewModel.addWeight(weight, userProfile.heightCm, "", selectedDate)
                showAddWeightDialog = false
            }
        )
    }

    // Upload & Extract Lab Report Dialog
    if (showUploadReportDialog) {
        UploadLabReportDialog(
            viewModel = viewModel,
            onDismiss = { showUploadReportDialog = false }
        )
    }

    // Upload & Scan Prescription Dialog
    if (showUploadPrescriptionDialog) {
        UploadPrescriptionDialog(
            viewModel = viewModel,
            onDismiss = { showUploadPrescriptionDialog = false }
        )
    }
}


