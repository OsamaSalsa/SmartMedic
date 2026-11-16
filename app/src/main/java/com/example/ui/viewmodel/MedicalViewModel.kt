package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DefaultData
import com.example.data.local.MedicalDatabase
import com.example.data.model.AiConsultationEntity
import com.example.data.model.BloodPressureEntity
import com.example.data.model.BloodSugarEntity
import com.example.data.model.CustomTestNameEntity
import com.example.data.model.DriveSyncFileEntity
import com.example.data.model.LabTestEntity
import com.example.data.model.calculateLevel
import com.example.data.model.MedicationEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightRecordEntity
import com.example.data.remote.GeminiService
import com.example.data.remote.MedicationAiDetails
import com.example.data.repository.MedicalRepository
import com.example.util.BackupPayload
import com.example.util.BackupSummary
import com.example.util.GoogleDriveBackupManager
import com.example.util.MedicationReminderScheduler
import com.example.util.RestoreResult
import java.io.File
import java.io.OutputStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.util.Log
import com.example.ui.locale.AppLanguage
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val time: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
)

class MedicalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MedicalRepository
    private val geminiService by lazy { GeminiService() }

    // --- Language Management (Auto-detects device language by default, supports AR, EN, RU) ---
    private val prefs = application.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)
    private val _appLanguage = MutableStateFlow(loadSavedLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private fun loadSavedLanguage(): AppLanguage {
        val code = prefs.getString("selected_language_code", AppLanguage.AUTO.code) ?: AppLanguage.AUTO.code
        return AppLanguage.values().firstOrNull { it.code == code } ?: AppLanguage.AUTO
    }

    fun setAppLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
        prefs.edit().putString("selected_language_code", lang.code).apply()
    }

    private val _darkModeOverride = MutableStateFlow(
        if (prefs.contains("dark_mode_enabled")) prefs.getBoolean("dark_mode_enabled", false) else null
    )
    val darkModeOverride: StateFlow<Boolean?> = _darkModeOverride.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _darkModeOverride.value = enabled
        prefs.edit().putBoolean("dark_mode_enabled", enabled).apply()
    }

    init {
        val db = MedicalDatabase.getDatabase(application)
        repository = MedicalRepository(db.medicalDao())
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                repository.initializeDefaultDataIfNeeded()
                val meds = repository.getAllMedicationsList()
                val user = repository.getUserProfileOnce()
                MedicationReminderScheduler.rescheduleAllActiveReminders(
                    context = application,
                    medications = meds,
                    patientName = user?.name ?: ""
                )
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    val labTests: StateFlow<List<LabTestEntity>> = repository.allLabTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bpRecords: StateFlow<List<BloodPressureEntity>> = repository.allBloodPressure
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sugarRecords: StateFlow<List<BloodSugarEntity>> = repository.allBloodSugar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weightRecords: StateFlow<List<WeightRecordEntity>> = repository.allWeight
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val consultations: StateFlow<List<AiConsultationEntity>> = repository.allConsultations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medications: StateFlow<List<MedicationEntity>> = repository.allMedications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity> = repository.userProfile
        .map { it ?: DefaultData.defaultUserProfile }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultData.defaultUserProfile)

    val driveFiles: StateFlow<List<DriveSyncFileEntity>> = repository.allDriveFiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customTestNames: StateFlow<List<CustomTestNameEntity>> = repository.allCustomTestNames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedDistinctTestNames: StateFlow<List<String>> = repository.distinctSavedTestNames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isDriveSyncing = MutableStateFlow(false)
    val isDriveSyncing: StateFlow<Boolean> = _isDriveSyncing.asStateFlow()

    private val _driveSyncMessage = MutableStateFlow<String?>(null)
    val driveSyncMessage: StateFlow<String?> = _driveSyncMessage.asStateFlow()

    private val _pendingImportPayload = MutableStateFlow<BackupPayload?>(null)
    val pendingImportPayload: StateFlow<BackupPayload?> = _pendingImportPayload.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _isAnalyzingAi = MutableStateFlow(false)
    val isAnalyzingAi: StateFlow<Boolean> = _isAnalyzingAi.asStateFlow()

    private val _aiErrorMessage = MutableStateFlow<String?>(null)
    val aiErrorMessage: StateFlow<String?> = _aiErrorMessage.asStateFlow()

    fun clearAiError() {
        _aiErrorMessage.value = null
    }

    private fun sanitizeUserError(raw: String?, fallback: String): String {
        val msg = raw?.trim().orEmpty()
        if (msg.isBlank()) return fallback
        val technicalTerms = listOf(
            "firebase", "appcheck", "app check", "gemini", "api", "token",
            "exception", "json", "http", "403", "401", "400", "404", "500", "503",
            "recaptcha", "credential", "serialization", "ktor", "okhttp"
        )
        return if (technicalTerms.any { msg.contains(it, ignoreCase = true) }) fallback else msg
    }

    fun syncSignedInUser(user: FirebaseUser?) {
        val displayName = user?.displayName?.trim().orEmpty()
        if (displayName.isBlank()) return
        viewModelScope.launch {
            val existing = repository.getUserProfileOnce()
            if (existing == null || existing.name.isBlank() || existing.name == DefaultData.defaultUserProfile.name) {
                val base = existing ?: DefaultData.defaultUserProfile
                repository.saveUserProfile(
                    base.copy(
                        id = 1,
                        name = displayName,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    private val _isExtractingReport = MutableStateFlow(false)
    val isExtractingReport: StateFlow<Boolean> = _isExtractingReport.asStateFlow()

    private val _extractedReportTests = MutableStateFlow<List<LabTestEntity>>(emptyList())
    val extractedReportTests: StateFlow<List<LabTestEntity>> = _extractedReportTests.asStateFlow()

    private val _reportExtractionErrorMessage = MutableStateFlow<String?>(null)
    val reportExtractionErrorMessage: StateFlow<String?> = _reportExtractionErrorMessage.asStateFlow()

    private val _isExtractingPrescription = MutableStateFlow(false)
    val isExtractingPrescription: StateFlow<Boolean> = _isExtractingPrescription.asStateFlow()

    private val _extractedPrescriptionMedications = MutableStateFlow<List<MedicationEntity>>(emptyList())
    val extractedPrescriptionMedications: StateFlow<List<MedicationEntity>> = _extractedPrescriptionMedications.asStateFlow()

    private val _prescriptionErrorMessage = MutableStateFlow<String?>(null)
    val prescriptionErrorMessage: StateFlow<String?> = _prescriptionErrorMessage.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isUser = false,
                text = "مرحباً بك! أنا مستشارك الطبي الذكي. سأقوم بتحليل فحوصاتك المخبرية وقراءاتك الصحية بناءً على عمرك ووزنك ومؤشراتك الحيوية لتقديم أفضل خطة غذائية وصحية مخصصة لك."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // --- User Profile Actions ---
    fun saveUserProfile(
        name: String,
        age: Int,
        gender: String,
        heightCm: Float,
        initialWeightKg: Float,
        bloodType: String,
        chronicDiseases: String,
        notes: String,
        profileImageUri: String = ""
    ) {
        viewModelScope.launch {
            val current = userProfile.value
            val profile = UserProfileEntity(
                id = 1,
                name = name,
                age = age,
                gender = gender,
                heightCm = heightCm,
                initialWeightKg = initialWeightKg,
                bloodType = bloodType,
                chronicDiseases = chronicDiseases,
                notes = notes,
                profileImageUri = profileImageUri.ifBlank { current.profileImageUri },
                updatedAt = System.currentTimeMillis()
            )
            repository.saveUserProfile(profile)

            // If user has no weight entries yet, record the initial weight
            if (weightRecords.value.isEmpty() && initialWeightKg > 0) {
                addWeight(initialWeightKg, heightCm, "الوزن الأولي المسجل في الإعدادات")
            }
        }
    }

    fun updateProfilePicture(imageUri: String) {
        viewModelScope.launch {
            val current = userProfile.value
            val updated = current.copy(
                profileImageUri = imageUri,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveUserProfile(updated)
        }
    }

    fun clearAllMeasurements(includeTests: Boolean = true) {
        viewModelScope.launch {
            repository.clearAllData(includeTests = includeTests)
            _extractedReportTests.value = emptyList()
            _extractedPrescriptionMedications.value = emptyList()
        }
    }

    fun loadBlankTemplateTests() {
        viewModelScope.launch {
            repository.loadBlankLabTestTemplates()
        }
    }

    fun deleteEmptyTests() {
        viewModelScope.launch {
            repository.deleteEmptyTests()
        }
    }

    // --- Lab Tests Actions ---
    fun saveLabTest(test: LabTestEntity) {
        viewModelScope.launch {
            if (test.id == 0L) {
                repository.insertLabTest(test)
            } else {
                repository.updateLabTest(test)
            }
        }
    }

    fun deleteLabTest(test: LabTestEntity) {
        viewModelScope.launch {
            repository.deleteLabTest(test)
        }
    }

    // --- Blood Pressure Actions ---
    fun addBloodPressure(
        systolic: Int,
        diastolic: Int,
        pulse: Int,
        period: String,
        notes: String,
        customDate: String? = null,
        customTime: String? = null
    ) {
        viewModelScope.launch {
            val category = when {
                systolic >= 180 || diastolic >= 120 -> "أزمة ارتفاع ضغط"
                systolic >= 140 || diastolic >= 90 -> "مرحلة 2"
                systolic >= 130 || diastolic >= 80 -> "مرحلة 1"
                systolic in 120..129 && diastolic < 80 -> "مرتفع طفيف"
                else -> "طبيعي"
            }
            val date = if (!customDate.isNullOrBlank()) customDate else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val time = if (!customTime.isNullOrBlank()) customTime else SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val timestamp = parseDateTimeToTimestamp(date, time)
            repository.insertBloodPressure(
                BloodPressureEntity(
                    systolic = systolic,
                    diastolic = diastolic,
                    pulse = pulse,
                    period = period,
                    date = date,
                    time = time,
                    category = category,
                    notes = notes,
                    timestamp = timestamp
                )
            )
        }
    }

    fun deleteBloodPressure(bp: BloodPressureEntity) {
        viewModelScope.launch {
            repository.deleteBloodPressure(bp)
        }
    }

    // --- Blood Sugar Actions ---
    fun addBloodSugar(
        value: Float,
        type: String,
        notes: String,
        customDate: String? = null,
        customTime: String? = null
    ) {
        viewModelScope.launch {
            val status = when (type) {
                "صائم" -> when {
                    value < 70 -> "منخفض"
                    value in 70.0..99.9 -> "طبيعي"
                    value in 100.0..125.9 -> "مرتفع (ما قبل السكري)"
                    else -> "مرتفع"
                }
                "بعد الأكل" -> when {
                    value < 80 -> "منخفض"
                    value <= 140 -> "طبيعي"
                    value in 140.1..199.9 -> "مرتفع طفيف"
                    else -> "مرتفع"
                }
                else -> when {
                    value < 70 -> "منخفض"
                    value in 70.0..125.0 -> "طبيعي"
                    else -> "مرتفع"
                }
            }
            val date = if (!customDate.isNullOrBlank()) customDate else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val time = if (!customTime.isNullOrBlank()) customTime else SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val timestamp = parseDateTimeToTimestamp(date, time)
            repository.insertBloodSugar(
                BloodSugarEntity(
                    value = value,
                    type = type,
                    date = date,
                    time = time,
                    status = status,
                    notes = notes,
                    timestamp = timestamp
                )
            )
        }
    }

    fun deleteBloodSugar(record: BloodSugarEntity) {
        viewModelScope.launch {
            repository.deleteBloodSugar(record)
        }
    }

    // --- Weight Actions ---
    fun addWeight(
        weightKg: Float,
        heightCm: Float,
        notes: String,
        customDate: String? = null
    ) {
        viewModelScope.launch {
            val heightM = heightCm / 100f
            val bmi = if (heightM > 0) weightKg / (heightM * heightM) else 0f
            val roundedBmi = (bmi * 10).toInt() / 10f
            val category = when {
                roundedBmi < 18.5 -> "نحافة"
                roundedBmi < 25.0 -> "وزن طبيعي"
                roundedBmi < 30.0 -> "زيادة وزن"
                else -> "سمنة"
            }
            val date = if (!customDate.isNullOrBlank()) customDate else SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val timestamp = parseDateTimeToTimestamp(date, null)
            repository.insertWeight(
                WeightRecordEntity(
                    weightKg = weightKg,
                    heightCm = heightCm,
                    bmi = roundedBmi,
                    bmiCategory = category,
                    date = date,
                    notes = notes,
                    timestamp = timestamp
                )
            )
        }
    }

    fun deleteWeight(record: WeightRecordEntity) {
        viewModelScope.launch {
            repository.deleteWeight(record)
        }
    }

    /**
     * تحويل التاريخ والوقت بدقة إلى طابع زمني Timestamp لتسجيل القراءات في أوقات سابقة
     */
    private fun parseDateTimeToTimestamp(dateStr: String, timeStr: String?): Long {
        return try {
            val dateParts = dateStr.split("-")
            if (dateParts.size == 3) {
                val year = dateParts[0].toIntOrNull() ?: return System.currentTimeMillis()
                val month = (dateParts[1].toIntOrNull() ?: 1) - 1
                val day = dateParts[2].toIntOrNull() ?: 1

                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }

                if (!timeStr.isNullOrBlank()) {
                    val isPm = timeStr.contains("م") || timeStr.contains("PM", ignoreCase = true)
                    val cleanTime = timeStr.replace("ص", "").replace("م", "")
                        .replace("AM", "", ignoreCase = true)
                        .replace("PM", "", ignoreCase = true).trim()
                    val timeParts = cleanTime.split(":")
                    if (timeParts.size >= 2) {
                        var hour = timeParts[0].trim().toIntOrNull() ?: 12
                        val minute = timeParts[1].trim().take(2).toIntOrNull() ?: 0
                        if (isPm && hour < 12) hour += 12
                        if (!isPm && hour == 12) hour = 0
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        cal.set(Calendar.SECOND, 0)
                        cal.set(Calendar.MILLISECOND, 0)
                    }
                }
                cal.timeInMillis
            } else {
                System.currentTimeMillis()
            }
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    // --- AI Advisor Analysis ---
    fun runAiAnalysis() {
        viewModelScope.launch {
            _isAnalyzingAi.value = true
            _aiErrorMessage.value = null
            val tests = labTests.value
            val bp = bpRecords.value
            val sugar = sugarRecords.value
            val latestW = weightRecords.value.firstOrNull()
            val profile = userProfile.value
            val meds = medications.value

            val result = geminiService.analyzeMedicalProfile(
                labTests = tests,
                bpRecords = bp,
                sugarRecords = sugar,
                latestWeight = latestW,
                profile = profile,
                medications = meds,
                language = _appLanguage.value
            )
            result.onSuccess { consultation ->
                repository.insertConsultation(consultation)
                _aiErrorMessage.value = null
            }.onFailure { err ->
                Log.e("MedicalViewModel", "runAiAnalysis failed", err)
                val fallback = when (_appLanguage.value.resolveEffectiveLanguage()) {
                    AppLanguage.EN -> "Unable to complete the medical analysis right now. Please check your internet connection."
                    AppLanguage.RU -> "Не удалось выполнить медицинский анализ. Проверьте подключение к интернету."
                    else -> "تعذر إتمام التحليل الطبي حالياً. يرجى التحقق من اتصال الإنترنت والمحاولة مجدداً."
                }
                _aiErrorMessage.value = sanitizeUserError(err.localizedMessage, fallback)
            }
            _isAnalyzingAi.value = false
        }
    }

    // --- Chat with AI Advisor (Streaming) ---
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(isUser = true, text = text)
        val assistantMsgId = java.util.UUID.randomUUID().toString()
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatLoading.value = true

        viewModelScope.launch {
            val profile = userProfile.value
            val contextData = buildString {
                append("بيانات المريض: العمر: ${profile.age} سنة، الجنس: ${profile.gender}، الطول: ${profile.heightCm} سم، الوزن: ${profile.initialWeightKg} كجم.")
                if (profile.chronicDiseases.isNotBlank()) {
                    append(" أمراض مزمنة: ${profile.chronicDiseases}.")
                }
                val abnormal = labTests.value.filter { it.statusFlag == "High" || it.statusFlag == "Low" || it.status == "زائد" || it.status == "نقص" || (it.statusFlag != "Normal" && it.statusFlag.isNotBlank()) }
                if (abnormal.isNotEmpty()) {
                    append(" فحوصات مخبرية تستدعي المتابعة: ")
                    append(abnormal.joinToString(", ") { "${it.testName}: ${it.value} (${it.calculateLevel()})" })
                }
                val latestBp = bpRecords.value.firstOrNull()
                if (latestBp != null) {
                    append(". آخر ضغط: ${latestBp.systolic}/${latestBp.diastolic} mmHg (${latestBp.category})")
                }
                val latestSugar = sugarRecords.value.firstOrNull()
                if (latestSugar != null) {
                    append(". آخر سكر: ${latestSugar.value} mg/dL (${latestSugar.status})")
                }

                val activeMeds = medications.value.filter { it.isActive }
                if (activeMeds.isNotEmpty()) {
                    append("\nأدوية المريض الحالية المجدولة وتفاصيلها الصيدلانية والغذائية:\n")
                    activeMeds.forEach { med ->
                        append("- ${med.name} (جرعة: ${med.dosage}، توقيت: ${med.time}، ${med.instructions})")
                        if (med.activeIngredients.isNotBlank()) append(" - التركيبة والمادة الفعالة: ${med.activeIngredients}")
                        if (med.uses.isNotBlank()) append(" - الاستخدامات: ${med.uses}")
                        if (med.sideEffects.isNotBlank()) append(" - الأعراض الجانبية: ${med.sideEffects}")
                        if (med.foodInteractions.isNotBlank()) append(" - الأغذية الواجب تجنبها: ${med.foodInteractions}")
                        if (med.importantInfo.isNotBlank()) append(" - إرشادات هامة: ${med.importantInfo}")
                        append("\n")
                    }
                }
            }

            try {
                var addedPlaceholder = false
                geminiService.chatWithAdvisorStream(text, contextData, _appLanguage.value).collect { partial ->
                    if (!addedPlaceholder) {
                        addedPlaceholder = true
                        _chatMessages.value = _chatMessages.value + ChatMessage(
                            id = assistantMsgId,
                            isUser = false,
                            text = partial
                        )
                    } else {
                        _chatMessages.value = _chatMessages.value.map { msg ->
                            if (msg.id == assistantMsgId) msg.copy(text = partial) else msg
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("MedicalViewModel", "Chat streaming timed out", e)
                val timeoutMsg = when (_appLanguage.value.resolveEffectiveLanguage()) {
                    AppLanguage.EN -> "AI advisor timed out (60s). Please try again."
                    AppLanguage.RU -> "Время ожидания ответа ИИ-консультанта истекло (60 сек). Повторите попытку."
                    else -> "انتهت مهلة استجابة المستشار الذكي (60 ثانية). يُرجى المحاولة مجدداً."
                }
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    isUser = false,
                    text = timeoutMsg
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("MedicalViewModel", "Chat streaming failed", e)
                val fallback = when (_appLanguage.value.resolveEffectiveLanguage()) {
                    AppLanguage.EN -> "Unable to reach the smart medical advisor. Please check your internet connection."
                    AppLanguage.RU -> "Не удалось связаться с медицинским консультантом. Проверьте подключение к интернету."
                    else -> "تعذر الاتصال بالمستشار الطبي حالياً. يرجى التحقق من اتصال الإنترنت والمحاولة مجدداً."
                }
                val detail = sanitizeUserError(e.localizedMessage, fallback)
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    isUser = false,
                    text = "⚠️ $detail"
                )
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    // --- Report PDF / Image Extraction ---
    fun processUploadedReport(
        fileBase64: String?,
        mimeType: String = "image/jpeg",
        pageImagesBase64: List<String> = emptyList(),
        reportNotes: String? = null,
        onComplete: ((Int) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isExtractingReport.value = true
            _reportExtractionErrorMessage.value = null
            val result = geminiService.extractTestsFromImageOrText(
                fileBase64 = fileBase64,
                mimeType = mimeType,
                pageImagesBase64 = pageImagesBase64,
                textNotes = reportNotes
            )
            result.onSuccess { extracted ->
                if (extracted.isNotEmpty()) {
                    val insertedIds = repository.insertAllLabTests(extracted)
                    val testsWithIds = extracted.mapIndexed { i, t ->
                        t.copy(id = insertedIds.getOrElse(i) { 0L })
                    }
                    _extractedReportTests.value = testsWithIds
                    _reportExtractionErrorMessage.value = null
                    onComplete?.invoke(testsWithIds.size)
                } else {
                    _reportExtractionErrorMessage.value = "لم يتم العثور على فحوصات واضحة في المستند المرفق. تأكد من وضوح التقرير"
                    onComplete?.invoke(0)
                }
            }.onFailure { err ->
                val fallback = when (_appLanguage.value.resolveEffectiveLanguage()) {
                    AppLanguage.EN -> "Unable to read the report right now. Please make sure the image or file is clear."
                    AppLanguage.RU -> "Не удалось прочитать отчет. Убедитесь, что файл или снимок четкий."
                    else -> "تعذر قراءة التقرير حالياً. يرجى التأكد من وضوح الملف والمحاولة مجدداً."
                }
                _reportExtractionErrorMessage.value = sanitizeUserError(err.localizedMessage, fallback)
                onComplete?.invoke(0)
            }
            _isExtractingReport.value = false
        }
    }

    fun updateExtractedTest(index: Int, test: LabTestEntity) {
        val current = _extractedReportTests.value.toMutableList()
        if (index in current.indices) {
            current[index] = test
            _extractedReportTests.value = current
            viewModelScope.launch {
                repository.updateLabTest(test)
            }
        }
    }

    fun removeExtractedTest(index: Int) {
        val current = _extractedReportTests.value.toMutableList()
        if (index in current.indices) {
            val removed = current.removeAt(index)
            _extractedReportTests.value = current
            viewModelScope.launch {
                repository.deleteLabTest(removed)
            }
        }
    }

    fun addExtractedTest(test: LabTestEntity) {
        val current = _extractedReportTests.value.toMutableList()
        current.add(test)
        _extractedReportTests.value = current
        viewModelScope.launch {
            val id = repository.insertLabTest(test)
            val updated = current.mapIndexed { i, t -> if (i == current.lastIndex) t.copy(id = id) else t }
            _extractedReportTests.value = updated
        }
    }

    fun saveAllExtractedTests(onSaved: (() -> Unit)? = null) {
        viewModelScope.launch {
            _extractedReportTests.value = emptyList()
            _reportExtractionErrorMessage.value = null
            onSaved?.invoke()
        }
    }

    fun dismissExtractedTests() {
        _extractedReportTests.value = emptyList()
        _reportExtractionErrorMessage.value = null
    }

    // --- Prescription AI Extraction ---
    fun processUploadedPrescription(
        fileBase64: String?,
        mimeType: String = "image/jpeg",
        pageImagesBase64: List<String> = emptyList(),
        notes: String? = null,
        onComplete: ((Int) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isExtractingPrescription.value = true
            _prescriptionErrorMessage.value = null
            val result = geminiService.extractMedicationsFromPrescription(
                fileBase64 = fileBase64,
                mimeType = mimeType,
                pageImagesBase64 = pageImagesBase64,
                textNotes = notes,
                language = _appLanguage.value
            )
            result.onSuccess { extracted ->
                _extractedPrescriptionMedications.value = extracted
                _prescriptionErrorMessage.value = null
                onComplete?.invoke(extracted.size)
            }.onFailure { err ->
                _prescriptionErrorMessage.value = err.message ?: "تعذر استخراج الأدوية من الوصفة الطبية"
                onComplete?.invoke(0)
            }
            _isExtractingPrescription.value = false
        }
    }

    fun updateExtractedMedication(index: Int, updated: MedicationEntity) {
        val current = _extractedPrescriptionMedications.value.toMutableList()
        if (index in current.indices) {
            current[index] = updated
            _extractedPrescriptionMedications.value = current
        }
    }

    fun removeExtractedMedication(index: Int) {
        val current = _extractedPrescriptionMedications.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _extractedPrescriptionMedications.value = current
        }
    }

    fun addExtractedMedication(med: MedicationEntity) {
        val current = _extractedPrescriptionMedications.value.toMutableList()
        current.add(med)
        _extractedPrescriptionMedications.value = current
    }

    fun clearExtractedPrescription() {
        _extractedPrescriptionMedications.value = emptyList()
        _prescriptionErrorMessage.value = null
    }

    fun saveExtractedMedications(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val list = _extractedPrescriptionMedications.value
            if (list.isNotEmpty()) {
                val app = getApplication<Application>()
                list.forEach { med ->
                    // Ensure AI details are filled
                    val enrichedMed = if (med.activeIngredients.isBlank() || med.foodInteractions.isBlank()) {
                        val ai = geminiService.fetchMedicationAiDetails(med.name, med.dosage, _appLanguage.value)
                        med.copy(
                            activeIngredients = med.activeIngredients.ifBlank { ai.activeIngredients },
                            uses = med.uses.ifBlank { ai.uses },
                            sideEffects = med.sideEffects.ifBlank { ai.sideEffects },
                            foodInteractions = med.foodInteractions.ifBlank { ai.foodInteractions },
                            importantInfo = med.importantInfo.ifBlank { ai.importantInfo }
                        )
                    } else {
                        med
                    }

                    val insertedId = repository.insertMedication(enrichedMed)
                    val scheduledMed = enrichedMed.copy(id = insertedId)
                    if (scheduledMed.isActive) {
                        MedicationReminderScheduler.scheduleReminder(
                            context = app,
                            med = scheduledMed,
                            patientName = userProfile.value.name
                        )
                    }
                }
                _extractedPrescriptionMedications.value = emptyList()
                _prescriptionErrorMessage.value = null
                onSuccess()
            }
        }
    }

    fun deleteShifaTests() {
        viewModelScope.launch {
            repository.deleteShifaTests()
        }
    }

    fun addCustomTestName(
        name: String,
        category: String = "عام",
        defaultUnit: String = "",
        defaultRefRange: String = ""
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCustomTestName(
                CustomTestNameEntity(
                    name = name.trim(),
                    category = category.ifBlank { "عام" },
                    defaultUnit = defaultUnit,
                    defaultRefRange = defaultRefRange
                )
            )
        }
    }

    fun deleteCustomTestName(name: String) {
        viewModelScope.launch {
            repository.deleteCustomTestName(name)
        }
    }

    // --- AI Medication Details Lookup & Enrichment ---
    suspend fun fetchMedicationAiDetails(name: String, dosage: String = ""): MedicationAiDetails {
        return geminiService.fetchMedicationAiDetails(name, dosage, _appLanguage.value)
    }

    fun enrichMedicationWithAi(med: MedicationEntity, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            val ai = geminiService.fetchMedicationAiDetails(med.name, med.dosage, _appLanguage.value)
            val updated = med.copy(
                activeIngredients = ai.activeIngredients,
                uses = ai.uses,
                sideEffects = ai.sideEffects,
                foodInteractions = ai.foodInteractions,
                importantInfo = ai.importantInfo
            )
            repository.updateMedication(updated)
            onComplete?.invoke()
        }
    }

    // --- Daily Medication Reminder Actions ---
    fun addMedication(
        name: String,
        dosage: String,
        instructions: String,
        hour: Int,
        minute: Int,
        frequency: String,
        notes: String,
        activeIngredients: String = "",
        uses: String = "",
        sideEffects: String = "",
        foodInteractions: String = "",
        importantInfo: String = ""
    ) {
        viewModelScope.launch {
            val timeString = formatTime(hour, minute)

            // If AI details were not provided in dialog, generate them automatically via Gemini
            val finalAi = if (activeIngredients.isBlank() && uses.isBlank() && foodInteractions.isBlank()) {
                geminiService.fetchMedicationAiDetails(name, dosage, _appLanguage.value)
            } else {
                val fallback = geminiService.generateRuleBasedMedicationDetails(name, dosage, _appLanguage.value)
                MedicationAiDetails(
                    activeIngredients = activeIngredients.ifBlank { fallback.activeIngredients },
                    uses = uses.ifBlank { fallback.uses },
                    sideEffects = sideEffects.ifBlank { fallback.sideEffects },
                    foodInteractions = foodInteractions.ifBlank { fallback.foodInteractions },
                    importantInfo = importantInfo.ifBlank { fallback.importantInfo }
                )
            }

            val med = MedicationEntity(
                name = name,
                dosage = dosage,
                instructions = instructions,
                time = timeString,
                hour = hour,
                minute = minute,
                frequency = frequency,
                isActive = true,
                notes = notes,
                activeIngredients = finalAi.activeIngredients,
                uses = finalAi.uses,
                sideEffects = finalAi.sideEffects,
                foodInteractions = finalAi.foodInteractions,
                importantInfo = finalAi.importantInfo
            )
            val insertedId = repository.insertMedication(med)
            val scheduledMed = med.copy(id = insertedId)
            val app = getApplication<Application>()
            MedicationReminderScheduler.scheduleReminder(
                context = app,
                med = scheduledMed,
                patientName = userProfile.value.name
            )
        }
    }

    fun updateMedication(updated: MedicationEntity) {
        viewModelScope.launch {
            val formattedTime = formatTime(updated.hour, updated.minute)
            val enrichedMed = if (updated.activeIngredients.isBlank() || updated.sideEffects.isBlank()) {
                val ai = geminiService.fetchMedicationAiDetails(updated.name, updated.dosage, _appLanguage.value)
                updated.copy(
                    activeIngredients = updated.activeIngredients.ifBlank { ai.activeIngredients },
                    uses = updated.uses.ifBlank { ai.uses },
                    sideEffects = updated.sideEffects.ifBlank { ai.sideEffects },
                    foodInteractions = updated.foodInteractions.ifBlank { ai.foodInteractions },
                    importantInfo = updated.importantInfo.ifBlank { ai.importantInfo }
                )
            } else {
                updated
            }
            val finalMed = enrichedMed.copy(time = formattedTime)
            repository.updateMedication(finalMed)
            val app = getApplication<Application>()
            if (finalMed.isActive) {
                MedicationReminderScheduler.cancelReminder(context = app, medId = finalMed.id)
                MedicationReminderScheduler.scheduleReminder(
                    context = app,
                    med = finalMed,
                    patientName = userProfile.value.name
                )
            } else {
                MedicationReminderScheduler.cancelReminder(context = app, medId = finalMed.id)
            }
        }
    }

    fun toggleMedicationActive(med: MedicationEntity) {
        viewModelScope.launch {
            val updated = med.copy(isActive = !med.isActive)
            repository.updateMedication(updated)
            val app = getApplication<Application>()
            if (updated.isActive) {
                MedicationReminderScheduler.scheduleReminder(
                    context = app,
                    med = updated,
                    patientName = userProfile.value.name
                )
            } else {
                MedicationReminderScheduler.cancelReminder(context = app, medId = med.id)
            }
        }
    }

    fun markMedicationTaken(med: MedicationEntity) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val updated = med.copy(lastTakenDate = todayStr)
            repository.updateMedication(updated)
        }
    }

    fun deleteMedication(med: MedicationEntity) {
        viewModelScope.launch {
            val app = getApplication<Application>()
            MedicationReminderScheduler.cancelReminder(context = app, medId = med.id)
            repository.deleteMedication(med)
        }
    }

    fun testInstantMedicationNotification(med: MedicationEntity) {
        val app = getApplication<Application>()
        MedicationReminderScheduler.triggerInstantNotification(
            context = app,
            med = med,
            patientName = userProfile.value.name
        )
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val effective = _appLanguage.value.resolveEffectiveLanguage()
        val isPm = hour >= 12
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val minStr = if (minute < 10) "0$minute" else "$minute"
        val amPm = if (effective == AppLanguage.AR) {
            if (isPm) "م" else "ص"
        } else {
            if (isPm) "PM" else "AM"
        }
        return String.format(Locale.US, "%02d:%s %s", displayHour, minStr, amPm)
    }

    // --- Google Drive Backup & Restore Operations ---

    fun exportAllDataToDrive(
        context: Context,
        onReady: (File, BackupSummary) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isDriveSyncing.value = true
            _driveSyncMessage.value = "جاري تجميع وحفظ جميع البيانات الطبية والفحوصات..."
            try {
                val (jsonString, summary) = GoogleDriveBackupManager.createFullBackupJson(repository)
                val file = GoogleDriveBackupManager.saveBackupToCacheFile(context, jsonString)
                val syncDate = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault()).format(Date())
                repository.insertDriveFile(
                    DriveSyncFileEntity(
                        fileName = file.name,
                        fileType = "DATA_BACKUP",
                        fileSizeFormatted = summary.fileSizeFormatted,
                        syncStatus = "SUCCESS",
                        syncDate = syncDate,
                        localFilePath = file.absolutePath,
                        timestamp = System.currentTimeMillis()
                    )
                )
                _isDriveSyncing.value = false
                _driveSyncMessage.value = null
                onReady(file, summary)
            } catch (e: Exception) {
                e.printStackTrace()
                _isDriveSyncing.value = false
                _driveSyncMessage.value = null
                onError("فشل تجهيز وتصدير النسخة الاحتياطية: ${e.message}")
            }
        }
    }

    fun writeBackupToStream(
        outputStream: OutputStream,
        onSuccess: (BackupSummary) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isDriveSyncing.value = true
            _driveSyncMessage.value = "جاري رفع وحفظ النسخة الاحتياطية إلى Google Drive..."
            try {
                val (jsonString, summary) = GoogleDriveBackupManager.createFullBackupJson(repository)
                GoogleDriveBackupManager.writeBackupToStream(outputStream, jsonString)
                val syncDate = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault()).format(Date())
                repository.insertDriveFile(
                    DriveSyncFileEntity(
                        fileName = "SmartHealth_Drive_Export_${summary.backupDate}.json",
                        fileType = "DATA_BACKUP",
                        fileSizeFormatted = summary.fileSizeFormatted,
                        syncStatus = "SUCCESS",
                        syncDate = syncDate,
                        timestamp = System.currentTimeMillis()
                    )
                )
                _isDriveSyncing.value = false
                _driveSyncMessage.value = null
                onSuccess(summary)
            } catch (e: Exception) {
                e.printStackTrace()
                _isDriveSyncing.value = false
                _driveSyncMessage.value = null
                onError("فشل حفظ الملف في Google Drive: ${e.message}")
            }
        }
    }

    fun inspectAndPrepareImport(context: Context, uri: Uri, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isDriveSyncing.value = true
            _driveSyncMessage.value = "جاري فحص ملف النسخة الاحتياطية من Google Drive..."
            val result = GoogleDriveBackupManager.parseBackupFromUri(context, uri)
            _isDriveSyncing.value = false
            _driveSyncMessage.value = null
            result.onSuccess { payload ->
                _pendingImportPayload.value = payload
            }.onFailure { err ->
                onError(err.message ?: "ملف النسخة الاحتياطية غير صالح أو تعذر قراءته")
            }
        }
    }

    fun dismissImportDialog() {
        _pendingImportPayload.value = null
    }

    fun executeRestoreImport(
        mergeMode: Boolean,
        context: Context,
        onComplete: (RestoreResult) -> Unit
    ) {
        val payload = _pendingImportPayload.value ?: return
        viewModelScope.launch {
            _isDriveSyncing.value = true
            _driveSyncMessage.value = "جاري استعادة ومزامنة الفحوصات والبيانات..."
            val result = GoogleDriveBackupManager.executeRestore(payload, mergeMode, repository, context)
            _pendingImportPayload.value = null
            _isDriveSyncing.value = false
            _driveSyncMessage.value = null
            onComplete(result)
        }
    }

    fun deleteDriveSyncFile(file: DriveSyncFileEntity) {
        viewModelScope.launch {
            repository.deleteDriveFile(file)
        }
    }

    fun clearDriveSyncHistory() {
        viewModelScope.launch {
            repository.clearDriveFiles()
        }
    }
}
