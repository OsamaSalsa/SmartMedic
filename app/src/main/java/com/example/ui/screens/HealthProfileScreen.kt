package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.components.ModernTextField
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.MedicalRedSoft
import com.example.ui.locale.LocalAppStrings
import com.example.ui.viewmodel.MedicalViewModel

@Composable
fun HealthProfileScreen(
    viewModel: MedicalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.healthProfileTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = strings.personalInfoSection,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showEditProfileDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalRed),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.edit, fontSize = 12.sp)
                }
            }
        }

        // Blood Group Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("blood_group_hero_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Opacity,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = strings.bloodType,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = profile.bloodType.ifEmpty { "--" },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalRed
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (profile.age > 0) "${strings.age}: ${profile.age}" else "${strings.age}: --",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (profile.heightCm > 0f) "${strings.heightCm}: ${profile.heightCm.toInt()}" else "${strings.heightCm}: --",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (profile.initialWeightKg > 0f) "${strings.weight}: ${profile.initialWeightKg.toInt()} ${strings.kgUnit}" else "${strings.weight}: --",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Health Attributes Sections
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HealthAttributeItem(
                    title = strings.allergies,
                    value = profile.notes.ifEmpty { "--" },
                    icon = Icons.Default.Coronavirus,
                    onEdit = { showEditProfileDialog = true }
                )

                HealthAttributeItem(
                    title = strings.chronicDiseases,
                    value = profile.chronicDiseases.ifEmpty { "--" },
                    icon = Icons.Default.Healing,
                    onEdit = { showEditProfileDialog = true }
                )
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var name by remember { mutableStateOf(profile.name) }
        var ageText by remember { mutableStateOf(if (profile.age > 0) profile.age.toString() else "") }
        var heightText by remember { mutableStateOf(if (profile.heightCm > 0f) profile.heightCm.toInt().toString() else "") }
        var weightText by remember { mutableStateOf(if (profile.initialWeightKg > 0f) profile.initialWeightKg.toInt().toString() else "") }
        var bloodType by remember { mutableStateOf(profile.bloodType) }
        var chronic by remember { mutableStateOf(profile.chronicDiseases) }
        var notes by remember { mutableStateOf(profile.notes) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text(strings.editBasicInfo, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ModernTextField(value = name, onValueChange = { name = it }, label = strings.fullName)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ModernTextField(value = ageText, onValueChange = { ageText = it }, label = strings.age, modifier = Modifier.weight(1f))
                        ModernTextField(value = bloodType, onValueChange = { bloodType = it }, label = strings.bloodType, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ModernTextField(value = heightText, onValueChange = { heightText = it }, label = strings.heightCm, modifier = Modifier.weight(1f))
                        ModernTextField(value = weightText, onValueChange = { weightText = it }, label = strings.initialWeightKg, modifier = Modifier.weight(1f))
                    }
                    ModernTextField(value = chronic, onValueChange = { chronic = it }, label = strings.chronicDiseases)
                    ModernTextField(value = notes, onValueChange = { notes = it }, label = strings.allergies)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveUserProfile(
                            name = name,
                            age = ageText.toIntOrNull() ?: profile.age,
                            gender = profile.gender,
                            heightCm = heightText.toFloatOrNull() ?: profile.heightCm,
                            initialWeightKg = weightText.toFloatOrNull() ?: profile.initialWeightKg,
                            bloodType = bloodType,
                            chronicDiseases = chronic,
                            notes = notes
                        )
                        showEditProfileDialog = false
                        Toast.makeText(context, strings.profileSavedSuccess, Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalRed)
                ) {
                    Text(strings.save)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text(strings.cancel) }
            }
        )
    }
}

@Composable
fun HealthAttributeItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp) )
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = value,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            val strings = com.example.ui.locale.LocalAppStrings.current
            Text(
                text = strings.edit,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MedicalRed
            )
        }
    }
}
