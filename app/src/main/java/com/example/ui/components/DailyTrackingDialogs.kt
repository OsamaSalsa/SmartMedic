package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicationEntity
import com.example.data.model.UserProfileEntity
import com.example.data.remote.GeminiService
import com.example.data.remote.MedicationAiDetails
import com.example.ui.locale.LocalAppLanguage
import com.example.ui.locale.LocalAppStrings
import com.example.ui.theme.MedicalRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationDialog(
    userProfile: UserProfileEntity,
    onDismiss: () -> Unit,
    onOpenPrescriptionScan: () -> Unit = {},
    onFetchAiDetails: (suspend (String, String) -> MedicationAiDetails)? = null,
    onAdd: (
        name: String,
        dosage: String,
        instructions: String,
        hour: Int,
        minute: Int,
        frequency: String,
        notes: String,
        activeIngredients: String,
        uses: String,
        sideEffects: String,
        foodInteractions: String,
        importantInfo: String
    ) -> Unit
) {
    val strings = LocalAppStrings.current
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf(strings.afterFood) }
    var hour by remember { mutableIntStateOf(8) }
    var minute by remember { mutableIntStateOf(0) }
    var frequency by remember { mutableStateOf(strings.dailyFreq) }
    var notes by remember { mutableStateOf("") }

    var activeIngredients by remember { mutableStateOf("") }
    var uses by remember { mutableStateOf("") }
    var sideEffects by remember { mutableStateOf("") }
    var foodInteractions by remember { mutableStateOf("") }
    var importantInfo by remember { mutableStateOf("") }
    var isFetchingAi by remember { mutableStateOf(false) }
    var showAiDetailsCard by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val instructionOptions = listOf(strings.afterFood, strings.beforeFood, strings.withFood, strings.beforeSleep)

    val chronicSuggestions = remember(userProfile.chronicDiseases) {
        val list = mutableListOf<Pair<String, String>>()
        val lower = userProfile.chronicDiseases.lowercase(Locale.getDefault())
        if (lower.contains("سكر") || lower.contains("diabetes")) {
            list.add("Metformin" to "500 mg")
        }
        if (lower.contains("ضغط") || lower.contains("pressure")) {
            list.add("Amlodipine" to "5 mg")
            list.add("Concor" to "2.5 mg")
        }
        list.add("Vitamin D3" to "5000 IU")
        list.add("Omega 3" to "1000 mg")
        list
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Medication, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(strings.addMedication, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenPrescriptionScan() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(strings.scanPrescriptionTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text(strings.scanPrescriptionSubtitle, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(strings.scanPrescriptionBtn, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                if (chronicSuggestions.isNotEmpty()) {
                    Text(strings.activeMedsHeader, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        chronicSuggestions.take(3).forEach { (sugName, sugDose) ->
                            FilterChip(
                                selected = name == sugName,
                                onClick = {
                                    name = sugName
                                    dosage = sugDose
                                },
                                label = { Text(sugName, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.medicationName) },
                    modifier = Modifier.fillMaxWidth().testTag("med_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text(strings.dosage) },
                    modifier = Modifier.fillMaxWidth().testTag("med_dosage_input"),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (name.isNotBlank() && onFetchAiDetails != null) {
                            coroutineScope.launch {
                                isFetchingAi = true
                                try {
                                    val ai = onFetchAiDetails(name, dosage)
                                    activeIngredients = ai.activeIngredients
                                    uses = ai.uses
                                    sideEffects = ai.sideEffects
                                    foodInteractions = ai.foodInteractions
                                    importantInfo = ai.importantInfo
                                    showAiDetailsCard = true
                                } catch (_: Exception) {
                                } finally {
                                    isFetchingAi = false
                                }
                            }
                        }
                    },
                    enabled = name.isNotBlank() && !isFetchingAi,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isFetchingAi) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.fetchAiDetails, fontSize = 11.5.sp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (showAiDetailsCard) strings.fetchAiDetails else "✨ ${strings.fetchAiDetails}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (showAiDetailsCard || activeIngredients.isNotBlank() || foodInteractions.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Text(strings.aiMedicationAnalysis, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }

                            OutlinedTextField(
                                value = activeIngredients,
                                onValueChange = { activeIngredients = it },
                                label = { Text(strings.activeIngredientsLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp)
                            )

                            OutlinedTextField(
                                value = uses,
                                onValueChange = { uses = it },
                                label = { Text(strings.usesLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp),
                                maxLines = 3
                            )

                            OutlinedTextField(
                                value = sideEffects,
                                onValueChange = { sideEffects = it },
                                label = { Text(strings.sideEffectsLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp),
                                maxLines = 3
                            )

                            OutlinedTextField(
                                value = foodInteractions,
                                onValueChange = { foodInteractions = it },
                                label = { Text(strings.foodInteractionsLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp),
                                maxLines = 3
                            )

                            OutlinedTextField(
                                value = importantInfo,
                                onValueChange = { importantInfo = it },
                                label = { Text(strings.importantInfoLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp),
                                maxLines = 3
                            )
                        }
                    }
                }

                Text(strings.doseTimeLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    var hourStr by remember(hour) { mutableStateOf(hour.toString()) }
                    var minuteStr by remember(minute) { mutableStateOf(if (minute < 10) "0$minute" else "$minute") }

                    OutlinedTextField(
                        value = hourStr,
                        onValueChange = {
                            hourStr = it
                            val parsed = it.toIntOrNull()
                            if (parsed != null && parsed in 0..23) hour = parsed
                        },
                        label = { Text("0-23") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = minuteStr,
                        onValueChange = {
                            minuteStr = it
                            val parsed = it.toIntOrNull()
                            if (parsed != null && parsed in 0..59) minute = parsed
                        },
                        label = { Text("0-59") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("${strings.morningOption} 08:00" to (8 to 0), "${strings.noonOption} 13:00" to (13 to 0), "${strings.eveningOption} 20:00" to (20 to 0)).forEach { (label, time) ->
                        FilterChip(
                            selected = hour == time.first && minute == time.second,
                            onClick = {
                                hour = time.first
                                minute = time.second
                            },
                            label = { Text(label, fontSize = 10.sp) }
                        )
                    }
                }

                Text(strings.instructions, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    instructionOptions.take(3).forEach { ins ->
                        FilterChip(
                            selected = instructions == ins,
                            onClick = { instructions = ins },
                            label = { Text(ins, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.notes) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            name,
                            dosage,
                            instructions,
                            hour,
                            minute,
                            frequency,
                            notes,
                            activeIngredients,
                            uses,
                            sideEffects,
                            foodInteractions,
                            importantInfo
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("confirm_add_med_btn")
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicationDialog(
    initialMedication: MedicationEntity,
    onDismiss: () -> Unit,
    onSave: (MedicationEntity) -> Unit,
    onFetchAiDetails: (suspend (String, String) -> MedicationAiDetails)? = null
) {
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    var isFetchingAi by remember { mutableStateOf(false) }

    val currentLang = LocalAppLanguage.current
    val ruleAi = remember(initialMedication.name, initialMedication.dosage, currentLang) {
        GeminiService().generateRuleBasedMedicationDetails(initialMedication.name, initialMedication.dosage, currentLang)
    }

    var name by remember { mutableStateOf(initialMedication.name) }
    var dosage by remember { mutableStateOf(initialMedication.dosage) }
    var instructions by remember { mutableStateOf(initialMedication.instructions) }
    var hour by remember { mutableIntStateOf(initialMedication.hour) }
    var minute by remember { mutableIntStateOf(initialMedication.minute) }
    var frequency by remember { mutableStateOf(initialMedication.frequency) }
    var notes by remember { mutableStateOf(initialMedication.notes) }

    var activeIngredients by remember { mutableStateOf(initialMedication.activeIngredients.ifBlank { ruleAi.activeIngredients }) }
    var uses by remember { mutableStateOf(initialMedication.uses.ifBlank { ruleAi.uses }) }
    var sideEffects by remember { mutableStateOf(initialMedication.sideEffects.ifBlank { ruleAi.sideEffects }) }
    var foodInteractions by remember { mutableStateOf(initialMedication.foodInteractions.ifBlank { ruleAi.foodInteractions }) }
    var importantInfo by remember { mutableStateOf(initialMedication.importantInfo.ifBlank { ruleAi.importantInfo }) }
    var showAiDetailsCard by remember { mutableStateOf(true) }

    val instructionOptions = listOf(strings.afterFood, strings.beforeFood, strings.withFood, strings.beforeSleep)
    val frequencyOptions = listOf(strings.dailyFreq, strings.twiceDailyFreq, strings.weeklyFreq, strings.asNeededFreq)

    val period = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val displayMinute = if (minute < 10) "0$minute" else "$minute"
    val formattedCurrentTime = String.format(Locale.getDefault(), "%02d:%s %s", displayHour, displayMinute, period)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(strings.editMedicationTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                            Column {
                                Text(strings.doseTimeLabel, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(formattedCurrentTime, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = frequency,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Text(strings.doseTimeLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    var hourStr by remember(hour) { mutableStateOf(hour.toString()) }
                    var minuteStr by remember(minute) { mutableStateOf(if (minute < 10) "0$minute" else "$minute") }

                    OutlinedTextField(
                        value = hourStr,
                        onValueChange = {
                            hourStr = it
                            val parsed = it.toIntOrNull()
                            if (parsed != null && parsed in 0..23) hour = parsed
                        },
                        label = { Text("0-23") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = minuteStr,
                        onValueChange = {
                            minuteStr = it
                            val parsed = it.toIntOrNull()
                            if (parsed != null && parsed in 0..59) minute = parsed
                        },
                        label = { Text("0-59") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                val timingPresets = listOf(
                    "${strings.morningOption} 07:00" to (7 to 0),
                    "${strings.morningOption} 08:30" to (8 to 30),
                    "${strings.noonOption} 14:00" to (14 to 0),
                    "${strings.eveningOption} 17:00" to (17 to 0),
                    "${strings.eveningOption} 20:30" to (20 to 30),
                    "${strings.beforeBed} 23:00" to (23 to 0)
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        timingPresets.take(3).forEach { (label, t) ->
                            FilterChip(
                                selected = hour == t.first && minute == t.second,
                                onClick = {
                                    hour = t.first
                                    minute = t.second
                                },
                                label = { Text(label, fontSize = 9.5.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        timingPresets.drop(3).forEach { (label, t) ->
                            FilterChip(
                                selected = hour == t.first && minute == t.second,
                                onClick = {
                                    hour = t.first
                                    minute = t.second
                                },
                                label = { Text(label, fontSize = 9.5.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(strings.medicationName) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text(strings.dosage) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (name.isNotBlank() && onFetchAiDetails != null) {
                            coroutineScope.launch {
                                isFetchingAi = true
                                try {
                                    val ai = onFetchAiDetails(name, dosage)
                                    activeIngredients = ai.activeIngredients
                                    uses = ai.uses
                                    sideEffects = ai.sideEffects
                                    foodInteractions = ai.foodInteractions
                                    importantInfo = ai.importantInfo
                                    showAiDetailsCard = true
                                } catch (_: Exception) {
                                } finally {
                                    isFetchingAi = false
                                }
                            }
                        }
                    },
                    enabled = name.isNotBlank() && !isFetchingAi,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isFetchingAi) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.fetchAiDetails, fontSize = 11.5.sp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("✨ ${strings.fetchAiDetails}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(strings.instructions, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    instructionOptions.forEach { ins ->
                        FilterChip(
                            selected = instructions == ins,
                            onClick = { instructions = ins },
                            label = { Text(ins, fontSize = 9.5.sp) }
                        )
                    }
                }

                Text(strings.frequency, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    frequencyOptions.take(3).forEach { freq ->
                        FilterChip(
                            selected = frequency == freq,
                            onClick = { frequency = freq },
                            label = { Text(freq, fontSize = 9.5.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.notes) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAiDetailsCard = !showAiDetailsCard },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Text(strings.aiMedicationAnalysis, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Icon(if (showAiDetailsCard) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }

                if (showAiDetailsCard) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = activeIngredients,
                                onValueChange = { activeIngredients = it },
                                label = { Text(strings.activeIngredientsLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp)
                            )

                            OutlinedTextField(
                                value = sideEffects,
                                onValueChange = { sideEffects = it },
                                label = { Text(strings.sideEffectsLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp),
                                maxLines = 3
                            )

                            OutlinedTextField(
                                value = uses,
                                onValueChange = { uses = it },
                                label = { Text(strings.usesLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp),
                                maxLines = 3
                            )

                            OutlinedTextField(
                                value = foodInteractions,
                                onValueChange = { foodInteractions = it },
                                label = { Text(strings.foodInteractionsLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp),
                                maxLines = 3
                            )

                            OutlinedTextField(
                                value = importantInfo,
                                onValueChange = { importantInfo = it },
                                label = { Text(strings.importantInfoLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 11.5.sp),
                                maxLines = 3
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val formattedTimeStr = String.format(Locale.getDefault(), "%02d:%s %s", displayHour, displayMinute, period)
                        val updated = initialMedication.copy(
                            name = name,
                            dosage = dosage,
                            instructions = instructions,
                            time = formattedTimeStr,
                            hour = hour,
                            minute = minute,
                            frequency = frequency,
                            notes = notes,
                            activeIngredients = activeIngredients,
                            uses = uses,
                            sideEffects = sideEffects,
                            foodInteractions = foodInteractions,
                            importantInfo = importantInfo
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("confirm_edit_med_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBloodPressureDialog(
    onDismiss: () -> Unit,
    onAdd: (Int, Int, Int, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    var systolic by remember { mutableStateOf("") }
    var diastolic by remember { mutableStateOf("") }
    var pulse by remember { mutableStateOf("") }
    var period by remember { mutableStateOf(strings.morningOption) }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val nowTimeStr = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) }
    var selectedDate by remember { mutableStateOf(todayStr) }
    var selectedTime by remember { mutableStateOf(nowTimeStr) }

    val periods = listOf(strings.morningOption, strings.noonOption, strings.eveningOption, strings.nightOption)

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
                    label = "${strings.testDateLabel}:"
                )

                PastTimePickerRow(
                    selectedTime = selectedTime,
                    onTimeSelected = { selectedTime = it },
                    label = "${strings.testDateLabel}:"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = systolic,
                        onValueChange = { systolic = it },
                        label = { Text(strings.systolicLabel) },
                        placeholder = { Text("120") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = diastolic,
                        onValueChange = { diastolic = it },
                        label = { Text(strings.diastolicLabel) },
                        placeholder = { Text("80") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                OutlinedTextField(
                    value = pulse,
                    onValueChange = { pulse = it },
                    label = { Text("${strings.pulseLabel} (bpm)") },
                    placeholder = { Text("72") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = period,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.measurementPeriod) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        periods.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p) },
                                onClick = {
                                    period = p
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.notes) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sysInt = systolic.toIntOrNull()
                    val diaInt = diastolic.toIntOrNull()
                    val pulseInt = pulse.toIntOrNull() ?: 72
                    if (sysInt != null && diaInt != null && sysInt > 0 && diaInt > 0) {
                        onAdd(sysInt, diaInt, pulseInt, period, notes, selectedDate, selectedTime)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBloodSugarDialog(
    onDismiss: () -> Unit,
    onAdd: (Float, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    var value by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(strings.fastingOption) }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val nowTimeStr = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) }
    var selectedDate by remember { mutableStateOf(todayStr) }
    var selectedTime by remember { mutableStateOf(nowTimeStr) }

    val types = listOf(
        strings.fastingOption, strings.postBreakfast, strings.beforeLunch,
        strings.afterLunch, strings.beforeDinner, strings.afterDinner,
        strings.beforeBed, strings.randomOption
    )

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
                    label = "${strings.testDateLabel}:"
                )

                PastTimePickerRow(
                    selectedTime = selectedTime,
                    onTimeSelected = { selectedTime = it },
                    label = "${strings.testDateLabel}:"
                )

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("${strings.bloodSugar} (mg/dL)") },
                    placeholder = { Text("95") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(strings.measurementPeriod) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        types.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    type = t
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.notes) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val floatVal = value.toFloatOrNull()
                    if (floatVal != null && floatVal > 0f) {
                        onAdd(floatVal, type, notes, selectedDate, selectedTime)
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
fun AddWeightDialog(
    latestHeight: Float,
    onDismiss: () -> Unit,
    onAdd: (Float, Float, String, String) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf(if (latestHeight > 0f) latestHeight.toInt().toString() else "") }
    var notes by remember { mutableStateOf("") }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedDate by remember { mutableStateOf(todayStr) }

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
                    label = "${strings.testDateLabel}:"
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("${strings.weightKgLabel} (${strings.kgUnit})") },
                    placeholder = { Text("75.0") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text(strings.heightCmLabel) },
                    placeholder = { Text("175") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(strings.notes) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weight.toFloatOrNull()
                    val h = height.toFloatOrNull() ?: 170f
                    if (w != null && w > 0f) {
                        onAdd(w, h, notes, selectedDate)
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
