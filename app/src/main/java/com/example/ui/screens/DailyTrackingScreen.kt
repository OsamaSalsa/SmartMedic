package com.example.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.example.data.model.BloodPressureEntity
import com.example.data.model.BloodSugarEntity
import com.example.data.model.MedicationEntity
import com.example.data.model.UserProfileEntity
import com.example.data.remote.MedicationAiDetails
import com.example.data.model.WeightRecordEntity
import com.example.ui.components.AddBloodPressureDialog
import com.example.ui.components.AddBloodSugarDialog
import com.example.ui.components.AddMedicationDialog
import com.example.ui.components.AddWeightDialog
import com.example.ui.components.BloodPressureChart
import com.example.ui.components.BloodSugarChart
import com.example.ui.components.EditMedicationDialog
import com.example.ui.components.PastDatePickerRow
import com.example.ui.components.PastTimePickerRow
import com.example.ui.components.UploadPrescriptionDialog
import com.example.ui.components.WeightProgressChart
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.MedicalRedSoft
import com.example.ui.theme.PrimaryRedGradient
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.theme.StatusNormal
import com.example.ui.theme.StatusNormalDark
import com.example.ui.theme.StatusNormalContainerLight
import com.example.ui.theme.StatusNormalContainerDark
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningDark
import com.example.ui.theme.StatusWarningContainerLight
import com.example.ui.theme.StatusWarningContainerDark
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalDark
import com.example.ui.theme.StatusCriticalContainerLight
import com.example.ui.theme.StatusCriticalContainerDark
import com.example.ui.viewmodel.MedicalViewModel
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.localizeStatus
import com.example.ui.locale.localizeSugarType
import com.example.ui.locale.localizePeriod
import com.example.ui.locale.localizeFrequency
import com.example.ui.locale.localizeInstruction
import com.example.ui.locale.localizeTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTrackingScreen(
    viewModel: MedicalViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val bpList by viewModel.bpRecords.collectAsStateWithLifecycle()
    val sugarList by viewModel.sugarRecords.collectAsStateWithLifecycle()
    val weightList by viewModel.weightRecords.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showPrescriptionDialog by remember { mutableStateOf(false) }
    var medicationToEdit by remember { mutableStateOf<MedicationEntity?>(null) }

    val tabs = listOf(strings.bloodPressure, strings.bloodSugar, strings.weight, strings.medicationsTab)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryRedGradient)
                    .padding(vertical = 16.dp, horizontal = 20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = strings.dailyTrackingHeader,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = strings.dailyTrackingSubtitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Tab Navigation (4 Tabs)
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        icon = {
                            val iconVector = when (index) {
                                0 -> Icons.Default.Favorite
                                1 -> Icons.Default.WaterDrop
                                2 -> Icons.Default.MonitorWeight
                                else -> Icons.Default.Medication
                            }
                            Icon(
                                iconVector,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            // Content per Tab
            when (selectedTabIndex) {
                0 -> BloodPressureScheduleTab(
                    bpList = bpList,
                    onDelete = { viewModel.deleteBloodPressure(it) }
                )
                1 -> BloodSugarTrackingTab(
                    sugarList = sugarList,
                    onDelete = { viewModel.deleteBloodSugar(it) }
                )
                2 -> WeightTrackingTab(
                    weightList = weightList,
                    onDelete = { viewModel.deleteWeight(it) }
                )
                3 -> MedicationRemindersTab(
                    medications = medications,
                    userProfile = userProfile,
                    onToggleActive = { viewModel.toggleMedicationActive(it) },
                    onMarkTaken = { viewModel.markMedicationTaken(it) },
                    onDelete = { viewModel.deleteMedication(it) },
                    onTestNotification = { viewModel.testInstantMedicationNotification(it) },
                    onOpenAddDialog = { showAddDialog = true },
                    onOpenPrescriptionScan = { showPrescriptionDialog = true },
                    onEnrichAi = { viewModel.enrichMedicationWithAi(it) },
                    onEditMedication = { medicationToEdit = it }
                )
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 84.dp)
                .testTag("add_vitals_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = strings.add)
        }
    }

    // Add Measurement / Medication Dialogs
    if (showAddDialog) {
        when (selectedTabIndex) {
            0 -> AddBloodPressureDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { sys, dia, pulse, period, notes, date, time ->
                    viewModel.addBloodPressure(sys, dia, pulse, period, notes, date, time)
                    showAddDialog = false
                }
            )
            1 -> AddBloodSugarDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { value, type, notes, date, time ->
                    viewModel.addBloodSugar(value, type, notes, date, time)
                    showAddDialog = false
                }
            )
            2 -> AddWeightDialog(
                latestHeight = weightList.firstOrNull()?.heightCm ?: userProfile.heightCm,
                onDismiss = { showAddDialog = false },
                onAdd = { w, h, notes, date ->
                    viewModel.addWeight(w, h, notes, date)
                    showAddDialog = false
                }
            )
            3 -> AddMedicationDialog(
                userProfile = userProfile,
                onDismiss = { showAddDialog = false },
                onOpenPrescriptionScan = {
                    showAddDialog = false
                    showPrescriptionDialog = true
                },
                onFetchAiDetails = { name, dose -> viewModel.fetchMedicationAiDetails(name, dose) },
                onAdd = { name, dosage, instructions, hour, minute, frequency, notes, activeIng, uses, sideEffects, foodInteractions, importantInfo ->
                    viewModel.addMedication(
                        name = name,
                        dosage = dosage,
                        instructions = instructions,
                        hour = hour,
                        minute = minute,
                        frequency = frequency,
                        notes = notes,
                        activeIngredients = activeIng,
                        uses = uses,
                        sideEffects = sideEffects,
                        foodInteractions = foodInteractions,
                        importantInfo = importantInfo
                    )
                    showAddDialog = false
                }
            )
        }
    }

    // AI Prescription Scanner Dialog
    if (showPrescriptionDialog) {
        UploadPrescriptionDialog(
            viewModel = viewModel,
            onDismiss = { showPrescriptionDialog = false }
        )
    }

    // Edit Medication Dialog (تعديل موعد ووقت المناولة وبيانات الدواء)
    val context = LocalContext.current
    if (medicationToEdit != null) {
        EditMedicationDialog(
            initialMedication = medicationToEdit!!,
            onDismiss = { medicationToEdit = null },
            onSave = { updated ->
                viewModel.updateMedication(updated)
                val msg = if (strings.isArabic) "تم حفظ موعد دواء ${updated.name} الجديد (${updated.time}) وتحديث التنبيه بنجاح!" else if (strings.isRussian) "Время приема ${updated.name} (${updated.time}) сохранено!" else "Reminder for ${updated.name} (${updated.time}) updated successfully!"
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                medicationToEdit = null
            },
            onFetchAiDetails = { name, dosage ->
                viewModel.fetchMedicationAiDetails(name, dosage)
            }
        )
    }
}

// ==================== MEDICATION REMINDERS TAB ====================
@Composable
fun MedicationRemindersTab(
    medications: List<MedicationEntity>,
    userProfile: UserProfileEntity,
    onToggleActive: (MedicationEntity) -> Unit,
    onMarkTaken: (MedicationEntity) -> Unit,
    onDelete: (MedicationEntity) -> Unit,
    onTestNotification: (MedicationEntity) -> Unit,
    onOpenAddDialog: () -> Unit,
    onOpenPrescriptionScan: () -> Unit,
    onEnrichAi: ((MedicationEntity) -> Unit)? = null,
    onEditMedication: (MedicationEntity) -> Unit = {}
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                com.example.util.MedicationReminderScheduler.areNotificationsEnabled(context)
            }
        )
    }

    var canScheduleExactAlarms by remember {
        mutableStateOf(com.example.util.MedicationReminderScheduler.canScheduleExactAlarms(context))
    }

    var isBatteryOptimized by remember {
        mutableStateOf(!com.example.util.MedicationReminderScheduler.isIgnoringBatteryOptimizations(context))
    }

    // Auto-refresh permission and exact alarm states when returning from system settings
    androidx.lifecycle.compose.LifecycleEventEffect(androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
        hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            com.example.util.MedicationReminderScheduler.areNotificationsEnabled(context)
        }
        canScheduleExactAlarms = com.example.util.MedicationReminderScheduler.canScheduleExactAlarms(context)
        isBatteryOptimized = !com.example.util.MedicationReminderScheduler.isIgnoringBatteryOptimizations(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, strings.notifActiveTitle, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, strings.notifActiveSubtitle, Toast.LENGTH_LONG).show()
        }
    }

    val todayDateStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Notification & Android 12 Exact Alarms Permission Card
        val needsPermissionNotice = !hasNotificationPermission || !canScheduleExactAlarms
        if (needsPermissionNotice) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (!hasNotificationPermission) Icons.Default.NotificationsActive else Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                val titleText = if (!hasNotificationPermission) {
                                    strings.notifActiveTitle
                                } else {
                                    if (strings.isArabic) "تفعيل التنبيهات الدقيقة (أندرويد 12)"
                                    else if (strings.isRussian) "Точные напоминания (Android 12+)"
                                    else "Enable Exact Alarms (Android 12+)"
                                }
                                val subtitleText = if (!hasNotificationPermission) {
                                    strings.notifActiveSubtitle
                                } else {
                                    if (strings.isArabic) "يتطلب نظام أندرويد 12 تفعيل إذن المواعيد الدقيقة لتصلك تنبيهات الأدوية في وقتها"
                                    else if (strings.isRussian) "Для точных напоминаний о лекарствах на Android 12+ необходимо разрешение точных будильников"
                                    else "Android 12+ requires exact alarm permission so medication reminders arrive on time"
                                }
                                Text(titleText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(subtitleText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 15.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (!hasNotificationPermission) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                        androidx.core.content.ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                                    ) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        com.example.util.MedicationReminderScheduler.openAppNotificationSettings(context)
                                    }
                                } else if (!canScheduleExactAlarms) {
                                    com.example.util.MedicationReminderScheduler.openExactAlarmSettings(context)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            val btnLabel = if (!hasNotificationPermission) {
                                strings.enableNotifBtn
                            } else {
                                if (strings.isArabic) "تفعيل الآن"
                                else if (strings.isRussian) "Включить"
                                else "Enable Now"
                            }
                            Text(btnLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Background Execution Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = strings.backgroundAlarmsInfo,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = strings.notifActiveSubtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Profile Integration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${strings.activeMedsHeader}: ${userProfile.name.ifEmpty { strings.welcomeUser }}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val chronicText = if (userProfile.chronicDiseases.isNotBlank()) "${strings.chronicDiseases}: ${userProfile.chronicDiseases}" else "${strings.age}: ${userProfile.age}"
                        Text(chronicText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // AI Prescription Scanning Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPrescriptionScan() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = strings.scanPrescriptionTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "AI",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.scanPrescriptionSubtitle,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = onOpenPrescriptionScan,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.scanPrescriptionBtn, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Header with count
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${strings.activeMedsHeader} (${medications.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onOpenPrescriptionScan) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.scanPrescriptionBtn, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    TextButton(onClick = onOpenAddDialog) {
                        Text("+ ${strings.addMedication}", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (medications.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Medication, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(strings.noMedsTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(strings.noMedsSubtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onOpenPrescriptionScan,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.scanPrescriptionBtn, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(onClick = onOpenAddDialog, shape = RoundedCornerShape(10.dp)) {
                                Text(strings.addMedBtn)
                            }
                        }
                    }
                }
            }
        }

        // Medication List Cards
        items(medications, key = { it.id }) { med ->
            val isTakenToday = med.lastTakenDate == todayDateStr

            // Pre-calculate fallback clinical details so fields like active ingredients and side effects are NEVER empty
            val currentLang = com.example.ui.locale.LocalAppLanguage.current
            val ruleAi = remember(med.name, med.dosage, currentLang) {
                com.example.data.remote.GeminiService().generateRuleBasedMedicationDetails(med.name, med.dosage, currentLang)
            }
            val displayActiveIngredients = med.activeIngredients.ifBlank { ruleAi.activeIngredients }
            val displayUses = med.uses.ifBlank { ruleAi.uses }
            val displaySideEffects = med.sideEffects.ifBlank { ruleAi.sideEffects }
            val displayFoodInteractions = med.foodInteractions.ifBlank { ruleAi.foodInteractions }
            val displayImportantInfo = med.importantInfo.ifBlank { ruleAi.importantInfo }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("med_item_${med.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isTakenToday) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (med.isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Medication,
                                    contentDescription = null,
                                    tint = if (med.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = med.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(med.dosage, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                // Time badge - Clickable to edit time
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                        .clickable { onEditMedication(med) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${strings.localizeTime(med.time)} • ${strings.localizeFrequency(med.frequency)} • ${strings.localizeInstruction(med.instructions)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Icon(Icons.Default.Edit, contentDescription = strings.edit, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), modifier = Modifier.size(12.dp))
                                }

                                // Quick active ingredients chip
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Science, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(13.dp))
                                    Text(
                                        text = "${strings.activeIngredientsLabel}: $displayActiveIngredients",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Toggle Active Switch
                        Switch(
                            checked = med.isActive,
                            onCheckedChange = { onToggleActive(med) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("switch_med_${med.id}")
                        )
                    }

                    if (med.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${strings.notes}: ${med.notes}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 52.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // AI Medication Details Expandable Section
                    var showAiDetails by remember { mutableStateOf(false) }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAiDetails = !showAiDetails },
                        shape = RoundedCornerShape(8.dp),
                        color = if (showAiDetails) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (showAiDetails) strings.hideAiDetails else strings.aiMedicationAnalysis,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Icon(
                                imageVector = if (showAiDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    if (showAiDetails) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val isDark = isAppInDarkTheme()
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Text(strings.aiMedicationAnalysis, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ) {
                                        Text("AI", fontSize = 9.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                // 1. Active ingredients (التركيبة الأساسية والمادة الفعالة)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) Color(0xFF221735) else Color(0xFFF5F3FF),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF7C3AED).copy(alpha = 0.45f) else Color(0xFFDDD6FE)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(Icons.Default.Science, contentDescription = null, tint = if (isDark) Color(0xFFA78BFA) else Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                                        Column {
                                            Text("${strings.activeIngredientsLabel}:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFA78BFA) else Color(0xFF6D28D9))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = displayActiveIngredients,
                                                fontSize = 11.sp,
                                                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B),
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                // 2. Side Effects (الأعراض الجانبية والمحاذير)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) Color(0xFF341517) else Color(0xFFFEF2F2),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFFEF4444).copy(alpha = 0.45f) else Color(0xFFFECACA)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                        Column {
                                            Text("${strings.sideEffectsLabel}:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFF87171) else Color(0xFFB91C1C))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = displaySideEffects,
                                                fontSize = 11.sp,
                                                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF450A0A),
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                // 3. Uses (دواعي واستخدامات الدواء)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) Color(0xFF0F291E) else Color(0xFFF0FDF4),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF22C55E).copy(alpha = 0.45f) else Color(0xFFBBF7D0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A), modifier = Modifier.size(17.dp))
                                        Column {
                                            Text("${strings.usesLabel}:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = displayUses,
                                                fontSize = 11.sp,
                                                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF14532D),
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                // 4. Food Interactions (الأطعمة والمشروبات الممنوعة)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) Color(0xFF312108) else Color(0xFFFFFBEB),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFFF59E0B).copy(alpha = 0.45f) else Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706), modifier = Modifier.size(17.dp))
                                        Column {
                                            Text("${strings.foodInteractionsLabel}:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = displayFoodInteractions,
                                                fontSize = 11.sp,
                                                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF78350F),
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                // 5. Important Info (إرشادات صيدلانية)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) Color(0xFF182232) else Color(0xFFF0F9FF),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF38BDF8).copy(alpha = 0.45f) else Color(0xFFBAE6FD)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7), modifier = Modifier.size(17.dp))
                                        Column {
                                            Text("${strings.importantInfoLabel}:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0369A1))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = displayImportantInfo,
                                                fontSize = 11.sp,
                                                color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0C4A6E),
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                // Action Buttons inside AI card
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { onEditMedication(med) }) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(strings.editMedication, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.primary)
                                    }

                                    if (onEnrichAi != null) {
                                        Button(
                                            onClick = { onEnrichAi(med) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), contentColor = MaterialTheme.colorScheme.primary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(strings.fetchAiDetails, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons row (Taken today, Test notification, Delete)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mark Taken Button (Most prominent action)
                        val isDark = isAppInDarkTheme()
                        Button(
                            onClick = {
                                onMarkTaken(med)
                                Toast.makeText(context, "${strings.takenTodayBadge}: ${med.name}", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTakenToday) (if (isDark) StatusNormalDark else StatusNormal) else MaterialTheme.colorScheme.primary,
                                contentColor = if (isTakenToday) (if (isDark) Color.Black else Color.White) else Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("mark_taken_btn_${med.id}")
                        ) {
                            Icon(
                                if (isTakenToday) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isTakenToday) strings.takenTodayBadge else strings.markTaken,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Edit Medication Time & Info Button
                            IconButton(
                                onClick = { onEditMedication(med) },
                                modifier = Modifier.size(40.dp).testTag("edit_med_${med.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = strings.editMedication, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                            }

                            // Test Instant Reminder
                            IconButton(
                                onClick = {
                                    onTestNotification(med)
                                    Toast.makeText(context, strings.testAlarm, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(40.dp).testTag("test_notif_${med.id}")
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = strings.testAlarm, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }

                            // Delete
                            IconButton(
                                onClick = { onDelete(med) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.75f), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}


// ==================== BLOOD PRESSURE SCHEDULE TAB ====================
@Composable
fun BloodPressureScheduleTab(
    bpList: List<BloodPressureEntity>,
    onDelete: (BloodPressureEntity) -> Unit
) {
    val strings = LocalAppStrings.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Chart Summary
        item {
            BloodPressureChart(records = bpList)
        }

        // Target Reference Guide Card
        item {
            val isDark = isAppInDarkTheme()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "${strings.bpScheduleTitle}:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• ${strings.normal}: ${strings.targetNormalBp}", fontSize = 11.sp, color = if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                    Text("• ${strings.high}: 120-129 / <80 mmHg", fontSize = 11.sp, color = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706), fontWeight = FontWeight.SemiBold)
                    Text("• ${strings.high} 1: 130-139 / 80-89 mmHg", fontSize = 11.sp, color = if (isDark) Color(0xFFFB923C) else Color(0xFFEA580C), fontWeight = FontWeight.SemiBold)
                    Text("• ${strings.critical} 2: 140+ / 90+ mmHg", fontSize = 11.sp, color = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Daily Schedule Table Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${strings.bpScheduleTitle} (${bpList.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (bpList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(strings.noReadings, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(strings.tapCardsToLog, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Table Rows
        items(bpList, key = { it.id }) { bp ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bp_row_${bp.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val categoryColor = when {
                        bp.category.contains("طبيعي") || bp.category.contains("normal") -> Color(0xFF2E7D32)
                        bp.category.contains("مرتفع طفيف") || bp.category.contains("elevated") -> Color(0xFFF57F17)
                        bp.category.contains("مرحلة 1") || bp.category.contains("stage 1") -> Color(0xFFE65100)
                        else -> Color(0xFFC62828)
                    }

                    Box(
                        modifier = Modifier
                            .size(12.dp, 40.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(categoryColor)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${bp.systolic} / ${bp.diastolic}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("mmHg", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${strings.pulse}: ${bp.pulse} bpm",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${bp.date} • ${bp.time} (${strings.localizePeriod(bp.period)})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (bp.notes.isNotBlank()) {
                                Text(" • ${bp.notes}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(categoryColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = strings.localizeStatus(bp.category),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor
                        )
                    }

                    IconButton(
                        onClick = { onDelete(bp) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ==================== BLOOD SUGAR TAB ====================
@Composable
fun BloodSugarTrackingTab(
    sugarList: List<BloodSugarEntity>,
    onDelete: (BloodSugarEntity) -> Unit
) {
    val strings = LocalAppStrings.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BloodSugarChart(records = sugarList)
        }

        item {
            Text(
                text = "${strings.sugarScheduleTitle} (${sugarList.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        if (sugarList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(strings.noReadings, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(strings.tapCardsToLog, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(sugarList, key = { it.id }) { s ->
            val statusColor = when {
                s.status.contains("طبيعي") || s.status.contains("normal") -> Color(0xFF2E7D32)
                s.status.contains("منخفض") || s.status.contains("low") -> Color(0xFF0288D1)
                else -> Color(0xFFC62828)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = statusColor, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${s.value}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("mg/dL", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(strings.localizeSugarType(s.type), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text(
                            text = "${s.date} ${s.time} ${if (s.notes.isNotBlank()) "• ${s.notes}" else ""}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(strings.localizeStatus(s.status), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = statusColor)
                    }

                    IconButton(onClick = { onDelete(s) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ==================== WEIGHT & BMI TAB ====================
@Composable
fun WeightTrackingTab(
    weightList: List<WeightRecordEntity>,
    onDelete: (WeightRecordEntity) -> Unit
) {
    val strings = LocalAppStrings.current
    val latest = weightList.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (latest != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(strings.latestWeightLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${latest.weightKg} ${strings.kgUnit}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(strings.bmiLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${latest.bmi}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(strings.categoryLabel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(strings.localizeStatus(latest.bmiCategory), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        item {
            WeightProgressChart(records = weightList)
        }

        item {
            Text(
                text = "${strings.weightScheduleTitle} (${weightList.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        if (weightList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(strings.noReadings, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(strings.tapCardsToLog, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(weightList, key = { it.id }) { w ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${w.weightKg} ${strings.kgUnit}", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("BMI: ${w.bmi} (${strings.localizeStatus(w.bmiCategory)})", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Text("${w.date} • ${strings.heightCmLabel}: ${w.heightCm}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    IconButton(onClick = { onDelete(w) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = strings.delete, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

