package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CustomTestNameEntity
import com.example.data.model.LabTestEntity
import com.example.data.model.calculateLevel
import com.example.ui.components.LabTestNameSelector
import com.example.ui.components.TestLevelBadge
import com.example.ui.components.UploadLabReportDialog
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.MedicalRedSoft
import com.example.ui.theme.PrimaryRedGradient
import com.example.ui.theme.StatusCritical
import com.example.ui.theme.StatusCriticalDark
import com.example.ui.theme.StatusNormal
import com.example.ui.theme.StatusNormalDark
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.StatusWarningDark
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.localizeStatus
import com.example.ui.locale.localizeTestName
import com.example.ui.locale.localizeCategory
import com.example.ui.viewmodel.MedicalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabTestsScreen(
    viewModel: MedicalViewModel,
    onNavigateToAiAdvisor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val tests by viewModel.labTests.collectAsStateWithLifecycle()
    val activeFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val customTests by viewModel.customTestNames.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTestForDetails by remember { mutableStateOf<LabTestEntity?>(null) }
    var showAddTestDialog by remember { mutableStateOf(false) }
    var showUploadReportDialog by remember { mutableStateOf(false) }

    val filterOptions = listOf(
        "ALL" to strings.filterAll,
        "NORMAL" to strings.normal,
        "HIGH" to strings.high,
        "LOW" to strings.low
    )

    val filteredTests = tests.filter { test ->
        val level = test.calculateLevel()
        val matchesFilter = when (activeFilter) {
            "NORMAL" -> level in listOf("normal", "طبيعي", "طبيعية", "جيد", "سليم", "норма")
            "HIGH" -> level in listOf("high", "زائد", "مرتفع", "عالي", "повышен")
            "LOW" -> level in listOf("low", "نقص", "منخفض", "انخفاض", "понижен")
            else -> true
        }
        val matchesSearch = searchQuery.isBlank() ||
                test.testName.contains(searchQuery, ignoreCase = true) ||
                test.testCategory.contains(searchQuery, ignoreCase = true) ||
                test.labName.contains(searchQuery, ignoreCase = true)

        matchesFilter && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top App Bar / Title Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryRedGradient)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.labTestsTitle,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = String.format(strings.labTestsCountFormat, tests.size),
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Quick Search & AI Analyze Header Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(strings.searchPlaceholder, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = strings.search) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = strings.close)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_lab_tests_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filterOptions) { (key, label) ->
                        val isSelected = activeFilter == key
                        val filterColor = when (key) {
                            "NORMAL" -> StatusNormal
                            "HIGH" -> StatusCritical
                            "LOW" -> StatusWarning
                            else -> MedicalRed
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setFilter(key) },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = filterColor,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            // Upload PDF / Image Report Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable { showUploadReportDialog = true }
                    .testTag("upload_report_banner"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MedicalRed.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MedicalRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = strings.uploadPdfOrImage,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = strings.extractTestsFromReport,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // AI Consultation Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable { onNavigateToAiAdvisor() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = strings.aiLabConsultation,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = strings.aiLabConsultationDesc,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Lab Tests List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredTests.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp, horizontal = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) strings.noTestsFoundSearch else strings.noTestsYet,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) strings.tryAnotherKeyword else strings.tapPlusToAdd,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { showAddTestDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("empty_state_add_test_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.addNewTestBtn)
                                }
                            }
                        }
                    }
                } else {
                    items(filteredTests, key = { it.id }) { test ->
                        LabTestItemCard(
                            test = test,
                            onClick = { selectedTestForDetails = test }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Lab Test
        FloatingActionButton(
            onClick = { showAddTestDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_lab_test_fab"),
            containerColor = MedicalRed,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = strings.addManualTest)
        }
    }

    // Detail Dialog
    selectedTestForDetails?.let { test ->
        LabTestDetailDialog(
            test = test,
            allTests = tests,
            onDismiss = { selectedTestForDetails = null },
            onSave = { updated ->
                viewModel.saveLabTest(updated)
                selectedTestForDetails = null
            },
            onDelete = {
                viewModel.deleteLabTest(test)
                selectedTestForDetails = null
            }
        )
    }

    // Add New Test Dialog
    if (showAddTestDialog) {
        AddLabTestDialog(
            customTestsList = customTests,
            onDismiss = { showAddTestDialog = false },
            onAdd = { newTest ->
                viewModel.saveLabTest(newTest)
                showAddTestDialog = false
            },
            onAddNewCustomTest = { customName, customCat, customUnit, customRef ->
                viewModel.addCustomTestName(customName, customCat, customUnit, customRef)
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
}

@Composable
fun LabTestItemCard(
    test: LabTestEntity,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lab_test_card_${test.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Test Name Header
            Text(
                text = "${strings.testNameLabel}: ${strings.localizeTestName(test.testName)}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            // Date Row: Only show if date is present
            if (test.testDate.isNotBlank() && test.testDate != "-") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${strings.testDateLabel}: ${test.testDate}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Lab Center Row: Only show if lab name is present
            if (test.labName.isNotBlank() && test.labName != "غير مسحوب" && test.labName != "-") {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = test.labName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Value preview: Only show if value is present
            if (test.value.isNotBlank() && test.value != "-") {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    val refText = if (test.referenceRange.isNotBlank()) " (${strings.normal}: ${test.referenceRange})" else ""
                    Text(
                        text = "${strings.resultLabel}: ${test.value} ${test.unit}$refText",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (test.statusFlag) {
                            "High" -> if (isAppInDarkTheme()) StatusCriticalDark else StatusCritical
                            "Low" -> if (isAppInDarkTheme()) StatusWarningDark else StatusWarning
                            else -> if (isAppInDarkTheme()) Color(0xFF64B5F6) else Color(0xFF1565C0)
                        }
                    )
                }
            }

            // Level Badge: طبيعي (أخضر)، زائد (أحمر)، نقص (برتقالي)
            val level = test.calculateLevel()
            if (level != "غير محدد") {
                Spacer(modifier = Modifier.height(6.dp))
                TestLevelBadge(level = level)
            }
        }
    }
}

@Composable
fun LabTestDetailDialog(
    test: LabTestEntity,
    allTests: List<LabTestEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (LabTestEntity) -> Unit,
    onDelete: () -> Unit
) {
    val strings = LocalAppStrings.current
    var value by remember { mutableStateOf(if (test.value == "غير مسحوب") "" else test.value) }
    var unit by remember { mutableStateOf(test.unit) }
    var refRange by remember { mutableStateOf(test.referenceRange) }
    var status by remember { mutableStateOf(test.calculateLevel()) }
    var testDate by remember { mutableStateOf(if (test.testDate == "-") "" else test.testDate) }
    var labName by remember { mutableStateOf(if (test.labName == "غير مسحوب" || test.labName == "-") "" else test.labName) }
    var notes by remember { mutableStateOf(test.notes) }
    var statusFlag by remember {
        mutableStateOf(
            when (test.calculateLevel()) {
                "زائد" -> "High"
                "نقص" -> "Low"
                else -> "Normal"
            }
        )
    }

    val historyRecords = remember(test.testName, allTests) {
        allTests.filter { it.testName.equals(test.testName, ignoreCase = true) && it.id != test.id }
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
                    Text(test.testName, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    TestLevelBadge(level = status)
                }
                Text(test.testCategory, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            value = it
                            // Auto calculate level if possible
                            val temp = test.copy(value = it, referenceRange = refRange)
                            val autoLevel = temp.calculateLevel()
                            if (autoLevel != "غير محدد") {
                                status = autoLevel
                                statusFlag = when (autoLevel) {
                                    "زائد" -> "High"
                                    "نقص" -> "Low"
                                    else -> "Normal"
                                }
                            }
                        },
                        label = { Text(strings.resultLabel) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text(strings.unit) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = refRange,
                    onValueChange = { refRange = it },
                    label = { Text(strings.refRangeLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Level Selection (طبيعي / زائد / نقص)
                Column {
                    Text(strings.levelSignificance, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("طبيعي" to "Normal", "زائد" to "High", "نقص" to "Low").forEach { (label, flag) ->
                            val isSelected = status == label || statusFlag == flag
                            val chipColor = when (flag) {
                                "Normal" -> StatusNormal
                                "High" -> StatusCritical
                                else -> StatusWarning
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    status = label
                                    statusFlag = flag
                                },
                                label = {
                                    Text(
                                        strings.localizeStatus(label),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = chipColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = testDate,
                    onValueChange = { testDate = it },
                    label = { Text(strings.testDateLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = labName,
                    onValueChange = { labName = it },
                    label = { Text(strings.labHospitalLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.medicalNotes) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Previous / Historical Tests for this test
                if (historyRecords.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "${strings.previousHistoryForTest} (${historyRecords.size}):",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            historyRecords.forEach { hist ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = hist.testDate.ifBlank { strings.twoDaysAgo },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (hist.labName.isNotBlank()) {
                                            Text(
                                                text = hist.labName,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "${hist.value} ${hist.unit}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        TestLevelBadge(level = hist.calculateLevel())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        test.copy(
                            value = value,
                            unit = unit,
                            referenceRange = refRange,
                            status = status,
                            testDate = testDate,
                            labName = labName,
                            notes = notes,
                            statusFlag = statusFlag
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
            ) {
                Text(strings.saveEdit)
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Text(strings.delete)
                }
                TextButton(onClick = onDismiss) {
                    Text(strings.cancel)
                }
            }
        }
    )
}

@Composable
fun AddLabTestDialog(
    customTestsList: List<CustomTestNameEntity> = emptyList(),
    onDismiss: () -> Unit,
    onAdd: (LabTestEntity) -> Unit,
    onAddNewCustomTest: ((name: String, category: String, unit: String, refRange: String) -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("عام") }
    var value by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var refRange by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("طبيعي") }
    var statusFlag by remember { mutableStateOf("Normal") }
    var testDate by remember { mutableStateOf("") }
    var labName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.addNewTestBtn, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LabTestNameSelector(
                    selectedName = name,
                    onNameChange = { name = it },
                    customTestsList = customTestsList,
                    onPresetSelected = { selName, selCat, selUnit, selRef ->
                        name = selName
                        if (category.isBlank() || category == "عام") category = selCat
                        if (unit.isBlank()) unit = selUnit
                        if (refRange.isBlank()) refRange = selRef
                    },
                    onAddNewCustomTest = onAddNewCustomTest
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(strings.categoryLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = {
                            value = it
                            val temp = LabTestEntity(testName = name, value = it, referenceRange = refRange)
                            val autoLevel = temp.calculateLevel()
                            if (autoLevel != "غير محدد") {
                                status = autoLevel
                                statusFlag = when (autoLevel) {
                                    "زائد" -> "High"
                                    "نقص" -> "Low"
                                    else -> "Normal"
                                }
                            }
                        },
                        label = { Text(strings.testValueLabel) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text(strings.testUnitLabel) },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = refRange,
                    onValueChange = { refRange = it },
                    label = { Text(strings.refRangeLabel) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Level Selection
                Text(strings.levelSignificance, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("طبيعي" to "Normal", "زائد" to "High", "نقص" to "Low").forEach { (label, flag) ->
                        val isSelected = status == label || statusFlag == flag
                        val chipColor = when (flag) {
                            "Normal" -> StatusNormal
                            "High" -> StatusCritical
                            else -> StatusWarning
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                status = label
                                statusFlag = flag
                            },
                            label = {
                                Text(
                                    strings.localizeStatus(label),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = chipColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = testDate,
                    onValueChange = { testDate = it },
                    label = { Text(strings.testDateLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = labName,
                    onValueChange = { labName = it },
                    label = { Text(strings.labHospitalLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            LabTestEntity(
                                testName = name,
                                testCategory = category,
                                value = value,
                                unit = unit,
                                referenceRange = refRange,
                                status = status,
                                statusFlag = statusFlag,
                                testDate = testDate,
                                labName = labName
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
            ) {
                Text(strings.add)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}
