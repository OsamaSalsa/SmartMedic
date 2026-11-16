package com.example.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiConsultationEntity
import com.example.data.model.MedicationEntity
import com.example.ui.theme.MedicalRed
import com.example.ui.theme.PrimaryRedGradient
import com.example.ui.theme.isAppInDarkTheme
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.MedicalViewModel
import com.example.ui.locale.LocalAppStrings
import com.example.ui.locale.localizeTime
import com.example.ui.locale.localizeInstruction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAdvisorScreen(
    viewModel: MedicalViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val consultations by viewModel.consultations.collectAsStateWithLifecycle()
    val medications by viewModel.medications.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzingAi.collectAsStateWithLifecycle()
    val aiErrorMessage by viewModel.aiErrorMessage.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val latestConsultation = consultations.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryRedGradient)
                .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.aiAdvisorTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = strings.medicalDisclaimer,
                    fontSize = 11.5.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }

        // Tabs: التقرير الطبي الشامل vs المحادثة والاستشارات
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text(strings.fullAnalysisTab, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text(strings.chatTab, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTabIndex == 0) {
            ConsultationPlanTab(
                consultation = latestConsultation,
                isAnalyzing = isAnalyzing,
                errorMessage = aiErrorMessage,
                medications = medications,
                onReanalyze = { viewModel.runAiAnalysis() },
                onDismissError = { viewModel.clearAiError() }
            )
        } else {
            AdvisorChatTab(
                messages = chatMessages,
                isLoading = isChatLoading,
                onSendMessage = { viewModel.sendChatMessage(it) }
            )
        }
    }
}

@Composable
fun ConsultationPlanTab(
    consultation: AiConsultationEntity?,
    isAnalyzing: Boolean,
    errorMessage: String? = null,
    medications: List<MedicationEntity> = emptyList(),
    onReanalyze: () -> Unit,
    onDismissError: () -> Unit = {}
) {
    val strings = LocalAppStrings.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        contentPadding = PaddingValues(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Action Button: Reanalyze with AI
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MedicalRed.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.aiAdvisorTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = strings.aiAdvisorSubtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onReanalyze,
                        enabled = !isAnalyzing,
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("analyze_with_ai_btn")
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.refresh, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_error_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.aiAnalysisErrorTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Text(
                            text = errorMessage,
                            fontSize = 12.5.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            androidx.compose.material3.TextButton(onClick = onDismissError) {
                                Text(strings.dismissBtn, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onReanalyze,
                                enabled = !isAnalyzing,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(strings.retryBtn, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (consultation != null) {
            // Summary Card
            item {
                AdvisorSectionCard(
                    title = strings.fullAnalysisTab,
                    subtitle = "${strings.date}: ${consultation.date}",
                    icon = Icons.Default.Lightbulb,
                    iconTint = Color(0xFFF57F17),
                    containerColor = MaterialTheme.colorScheme.surface,
                    content = consultation.summary
                )
            }

            // Diet Plan Card
            item {
                AdvisorSectionCard(
                    title = strings.nutritionAndDiet,
                    subtitle = strings.promptDietForDiabetes,
                    icon = Icons.Default.Restaurant,
                    iconTint = Color(0xFF2E7D32),
                    containerColor = MaterialTheme.colorScheme.surface,
                    content = consultation.dietAdvice
                )
            }

            // Foods to Eat Card (Positive)
            item {
                AdvisorListCard(
                    title = strings.clinicalRecommendations,
                    subtitle = strings.nutritionAndDiet,
                    icon = Icons.Default.CheckCircle,
                    iconColor = Color(0xFF2E7D32),
                    containerColor = MaterialTheme.colorScheme.surface,
                    content = consultation.foodsToEat
                )
            }

            // Foods to Avoid Card (Warning)
            item {
                AdvisorListCard(
                    title = strings.foodInteractionsLabel,
                    subtitle = strings.promptDrugInteractions,
                    icon = Icons.Default.Block,
                    iconColor = Color(0xFFD32F2F),
                    containerColor = MaterialTheme.colorScheme.surface,
                    content = consultation.foodsToAvoid
                )
            }

            // Lifestyle & Activity Card
            item {
                AdvisorSectionCard(
                    title = strings.promptLifestyleAdvice,
                    subtitle = strings.cardiovascularStatus,
                    icon = Icons.Default.FitnessCenter,
                    iconTint = Color(0xFF0288D1),
                    containerColor = MaterialTheme.colorScheme.surface,
                    content = consultation.lifestyleAdvice
                )
            }

            // Medication & Medical Follow-up Card
            item {
                AdvisorSectionCard(
                    title = strings.activeMedicationsSection,
                    subtitle = strings.importantInfoLabel,
                    icon = Icons.Default.MedicalServices,
                    iconTint = Color(0xFF7B1FA2),
                    containerColor = MaterialTheme.colorScheme.surface,
                    content = consultation.medicationAdvice
                )
            }

            // Dedicated Clinical Pharmacology Card (Active Ingredients & Side Effects)
            val activeMeds = medications.filter { it.isActive }
            if (activeMeds.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text(strings.aiMedicationAnalysis, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                                    Text(strings.activeIngredientsLabel, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            val isDark = isAppInDarkTheme()
                            val currentLang = com.example.ui.locale.LocalAppLanguage.current

                            activeMeds.forEach { med ->
                                val ruleAi = remember(med.name, med.dosage, currentLang) {
                                    com.example.data.remote.GeminiService().generateRuleBasedMedicationDetails(med.name, med.dosage, currentLang)
                                }
                                val activeIng = med.activeIngredients.ifBlank { ruleAi.activeIngredients }
                                val sideEff = med.sideEffects.ifBlank { ruleAi.sideEffects }
                                val uses = med.uses.ifBlank { ruleAi.uses }
                                val foodInter = med.foodInteractions.ifBlank { ruleAi.foodInteractions }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF333338) else Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "💊 ${med.name} (${med.dosage})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isDark) Color(0xFF3B1E22) else Color(0xFFFEE2E2),
                                                border = androidx.compose.foundation.BorderStroke(0.5.dp, if (isDark) Color(0xFFF87171).copy(alpha = 0.4f) else Color(0xFFFCA5A5))
                                            ) {
                                                Text(
                                                    text = "${strings.localizeTime(med.time)} • ${strings.localizeInstruction(med.instructions)}",
                                                    fontSize = 10.sp,
                                                    color = if (isDark) Color(0xFFFCA5A5) else Color(0xFF991B1B),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        // Active Ingredients
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isDark) Color(0xFF221735) else Color(0xFFF5F3FF),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF7C3AED).copy(alpha = 0.45f) else Color(0xFFDDD6FE)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                                                Icon(Icons.Default.Science, contentDescription = null, tint = if (isDark) Color(0xFFA78BFA) else Color(0xFF7C3AED), modifier = Modifier.size(16.dp))
                                                Column {
                                                    Text("${strings.activeIngredientsLabel}:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFA78BFA) else Color(0xFF6D28D9))
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(activeIng, fontSize = 11.sp, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B), lineHeight = 17.sp)
                                                }
                                            }
                                        }

                                        // Side Effects
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isDark) Color(0xFF341517) else Color(0xFFFEF2F2),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFFEF4444).copy(alpha = 0.45f) else Color(0xFFFECACA)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                                                Icon(Icons.Default.Warning, contentDescription = null, tint = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                                Column {
                                                    Text("${strings.sideEffectsLabel}:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFF87171) else Color(0xFFB91C1C))
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(sideEff, fontSize = 11.sp, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF450A0A), lineHeight = 17.sp)
                                                }
                                            }
                                        }

                                        // Uses
                                        if (uses.isNotBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isDark) Color(0xFF0F291E) else Color(0xFFF0FDF4),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF22C55E).copy(alpha = 0.45f) else Color(0xFFBBF7D0)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isDark) Color(0xFF4ADE80) else Color(0xFF16A34A), modifier = Modifier.size(15.dp))
                                                    Column {
                                                        Text("${strings.usesLabel}:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF4ADE80) else Color(0xFF15803D))
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(uses, fontSize = 10.5.sp, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF14532D), lineHeight = 17.sp)
                                                    }
                                                }
                                            }
                                        }

                                        // Food Interactions
                                        if (foodInter.isNotBlank()) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isDark) Color(0xFF312108) else Color(0xFFFFFBEB),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFFF59E0B).copy(alpha = 0.45f) else Color(0xFFFDE68A)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                                                    Icon(Icons.Default.Warning, contentDescription = null, tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706), modifier = Modifier.size(15.dp))
                                                    Column {
                                                        Text("${strings.foodInteractionsLabel}:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFFFBBF24) else Color(0xFFB45309))
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(foodInter, fontSize = 10.5.sp, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF78350F), lineHeight = 17.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MedicalRed,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = strings.aiAdvisorTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.aiAdvisorSubtitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdvisorSectionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    containerColor: Color,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                fontSize = 13.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AdvisorListCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    containerColor: Color,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 13.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ==================== CHAT TAB ====================
@Composable
fun AdvisorChatTab(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val strings = LocalAppStrings.current

    val quickQuestions = listOf(
        strings.promptDietForDiabetes,
        strings.promptDrugInteractions,
        strings.promptLabInterpret,
        strings.promptLifestyleAdvice
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }
            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(strings.loading, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Quick Suggestions
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickQuestions) { q ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onSendMessage(q) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(q, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Bottom Input Row
        val strings = LocalAppStrings.current
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(strings.askDoctorPlaceholder, fontSize = 13.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MedicalRed)
                        .testTag("send_chat_msg_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = strings.send, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgColor = if (isUser) MedicalRed else MaterialTheme.colorScheme.surface
    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(bgColor)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 320.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.time,
                    color = if (isUser) Color.White.copy(alpha = 0.75f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
