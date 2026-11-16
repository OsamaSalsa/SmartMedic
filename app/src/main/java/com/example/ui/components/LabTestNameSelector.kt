package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomTestNameEntity
import com.example.data.model.PredefinedLabTest
import com.example.data.model.PredefinedLabTestsCatalog
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.localizeCategory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LabTestNameSelector(
    selectedName: String,
    onNameChange: (String) -> Unit,
    customTestsList: List<CustomTestNameEntity> = emptyList(),
    savedDistinctNames: List<String> = emptyList(),
    onPresetSelected: ((name: String, category: String, unit: String, refRange: String) -> Unit)? = null,
    onAddNewCustomTest: ((name: String, category: String, unit: String, refRange: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var expanded by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("الكل") }
    var showAddCustomDialog by remember { mutableStateOf(false) }

    // Combine predefined + user custom tests
    val allOptions = remember(customTestsList, selectedCategoryFilter, selectedName) {
        val predefined = PredefinedLabTestsCatalog.list.filter { test ->
            (selectedCategoryFilter == "الكل" || test.category == selectedCategoryFilter) &&
                    (selectedName.isBlank() || test.matches(selectedName))
        }

        val customMatching = customTestsList.filter { custom ->
            (selectedCategoryFilter == "الكل" || custom.category == selectedCategoryFilter) &&
                    (selectedName.isBlank() || custom.name.contains(selectedName, ignoreCase = true))
        }

        Pair(predefined, customMatching)
    }

    val predefinedMatches = allOptions.first
    val customMatches = allOptions.second

    // Popular Quick Pick Chips (Top 6 most common tests)
    val quickPicks = remember {
        listOf(
            "Fasting Blood Sugar (FBS)",
            "Haemoglobin (HGB)",
            "Total Cholesterol",
            "Triglycerides",
            "Creatinine, serum",
            "25-OH-Vitamin D3"
        )
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Label and Action bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${strings.testNameLabel}:",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = { showAddCustomDialog = true },
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("add_new_test_name_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(strings.addCustomTest, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Searchable / Free typing TextField
        OutlinedTextField(
            value = selectedName,
            onValueChange = {
                onNameChange(it)
                if (!expanded) expanded = true
            },
            placeholder = { Text(strings.testNamePlaceholder) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("test_name_input_field"),
            singleLine = true,
            leadingIcon = {
                Icon(
                    Icons.Default.Science,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectedName.isNotBlank()) {
                        IconButton(onClick = { onNameChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = strings.clearAll, modifier = Modifier.size(18.dp))
                        }
                    }
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.testTag("toggle_test_names_dropdown")
                    ) {
                        Icon(
                            if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = strings.selectLabTestName
                        )
                    }
                }
            },
            shape = RoundedCornerShape(12.dp)
        )

        // Quick Suggestion Chips under the field
        if (!expanded && selectedName.isBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.popular,
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 4.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 2.dp)
                ) {
                    items(quickPicks) { pickName ->
                        val item = PredefinedLabTestsCatalog.list.firstOrNull { it.nameEn == pickName }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            modifier = Modifier.clickable {
                                if (item != null) {
                                    val testTitle = if (strings.isArabic) item.nameAr else item.nameEn
                                    onNameChange(testTitle)
                                    onPresetSelected?.invoke(
                                        testTitle,
                                        item.category,
                                        item.defaultUnit,
                                        item.defaultRefRange
                                    )
                                } else {
                                    onNameChange(pickName)
                                }
                            }
                        ) {
                            Text(
                                text = if (strings.isArabic) (item?.nameAr ?: pickName) else (item?.nameEn ?: pickName),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                    }
                }
            }
        }

        // Expandable Selection Dropdown Panel
        AnimatedVisibility(visible = expanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Filters Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 2.dp)
                    ) {
                        items(PredefinedLabTestsCatalog.categories) { cat ->
                            FilterChip(
                                selected = selectedCategoryFilter == cat,
                                onClick = { selectedCategoryFilter = cat },
                                label = { Text(strings.localizeCategory(cat), fontSize = 10.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Direct "Use entered name" option if typed something custom
                    if (selectedName.isNotBlank() &&
                        predefinedMatches.none { it.fullDisplayName.equals(selectedName, ignoreCase = true) || it.nameEn.equals(selectedName, ignoreCase = true) } &&
                        customMatches.none { it.name.equals(selectedName, ignoreCase = true) }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expanded = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${strings.useEnteredName}: \"$selectedName\"",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1D4ED8)
                                    )
                                }
                                Text(
                                    text = strings.confirmSelectTest,
                                    fontSize = 11.sp,
                                    color = Color(0xFF2563EB),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Scrollable List of Predefined & Custom Tests
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // User's custom added tests section
                        if (customMatches.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${strings.myCustomTests} (${customMatches.size}):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            }
                            items(customMatches) { custom ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onNameChange(custom.name)
                                            onPresetSelected?.invoke(
                                                custom.name,
                                                custom.category,
                                                custom.defaultUnit,
                                                custom.defaultRefRange
                                            )
                                            expanded = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(custom.name, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                            if (custom.defaultRefRange.isNotBlank() || custom.defaultUnit.isNotBlank()) {
                                                Text(
                                                    "${strings.refRangeLabel}: ${custom.defaultRefRange} ${custom.defaultUnit}",
                                                    fontSize = 10.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                strings.localizeCategory(custom.category),
                                                fontSize = 9.5.sp,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Predefined catalogue tests section
                        if (predefinedMatches.isNotEmpty()) {
                            item {
                                Text(
                                    text = "${strings.commonMedicalTests} (${predefinedMatches.size}):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                            items(predefinedMatches) { item ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val fullTitle = if (strings.isArabic) item.nameAr else item.nameEn
                                            onNameChange(fullTitle)
                                            onPresetSelected?.invoke(
                                                fullTitle,
                                                item.category,
                                                item.defaultUnit,
                                                item.defaultRefRange
                                            )
                                            expanded = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (strings.isArabic) item.nameAr else item.nameEn,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (strings.isArabic) {
                                                    "${item.nameEn} • ${strings.refRangeLabel}: ${item.defaultRefRange} ${item.defaultUnit}"
                                                } else {
                                                    "${strings.refRangeLabel}: ${item.defaultRefRange} ${item.defaultUnit}"
                                                },
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                        ) {
                                            Text(
                                                text = strings.localizeCategory(item.category),
                                                fontSize = 9.5.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (predefinedMatches.isEmpty() && customMatches.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = strings.noMatchingTests,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    TextButton(onClick = { showAddCustomDialog = true }) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${strings.add} \"$selectedName\"")
                                    }
                                }
                            }
                        }
                    }

                    // Close list button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { expanded = false }) {
                            Text(strings.closeList, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

    // Dialog to add custom test name permanently
    if (showAddCustomDialog) {
        AddNewCustomTestDialog(
            initialName = selectedName,
            onDismiss = { showAddCustomDialog = false },
            onConfirm = { name, cat, unit, ref ->
                onNameChange(name)
                onPresetSelected?.invoke(name, cat, unit, ref)
                onAddNewCustomTest?.invoke(name, cat, unit, ref)
                showAddCustomDialog = false
                expanded = false
            }
        )
    }
}

@Composable
fun AddNewCustomTestDialog(
    initialName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, unit: String, refRange: String) -> Unit
) {
    val strings = LocalAppStrings.current
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf("عام") }
    var unit by remember { mutableStateOf("mg/dL") }
    var refRange by remember { mutableStateOf("") }

    val categories = remember {
        listOf("عام", "الدم (CBC)", "السكري", "الدهون والكولسترول", "وظائف الكلى والأملاح", "وظائف الكبد", "الفيتامينات والمعادن", "الهرمونات والغدد", "القلب والأوعية")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(strings.addCustomTestTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = strings.addCustomTestHint,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.testNameInputLabel) },
                    placeholder = { Text(strings.testNameInputPlaceholder) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_test_name_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Text(strings.medicalCategory, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(strings.localizeCategory(cat), fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text(strings.defaultUnit) },
                        placeholder = { Text("mg/dL, %...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = refRange,
                        onValueChange = { refRange = it },
                        label = { Text(strings.normalRangeInput) },
                        placeholder = { Text("70 - 100") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name.trim(), category, unit.trim(), refRange.trim())
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_add_custom_test_button")
            ) {
                Text(strings.saveAndSelectTest)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
