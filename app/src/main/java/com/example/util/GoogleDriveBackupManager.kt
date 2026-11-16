package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.AiConsultationEntity
import com.example.data.model.BloodPressureEntity
import com.example.data.model.BloodSugarEntity
import com.example.data.model.DriveSyncFileEntity
import com.example.data.model.LabTestEntity
import com.example.data.model.MedicationEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightRecordEntity
import com.example.data.repository.MedicalRepository
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupSummary(
    val appName: String,
    val backupDate: String,
    val timestamp: Long,
    val patientName: String,
    val labTestsCount: Int,
    val bpCount: Int,
    val sugarCount: Int,
    val weightCount: Int,
    val medicationsCount: Int,
    val consultationsCount: Int,
    val fileSizeFormatted: String
) {
    val totalRecordsCount: Int
        get() = labTestsCount + bpCount + sugarCount + weightCount + medicationsCount + consultationsCount
}

data class BackupPayload(
    val summary: BackupSummary,
    val profile: UserProfileEntity?,
    val labTests: List<LabTestEntity>,
    val bpRecords: List<BloodPressureEntity>,
    val sugarRecords: List<BloodSugarEntity>,
    val weightRecords: List<WeightRecordEntity>,
    val medications: List<MedicationEntity>,
    val consultations: List<AiConsultationEntity>
)

data class RestoreResult(
    val isSuccess: Boolean,
    val message: String,
    val summary: BackupSummary? = null
)

object GoogleDriveBackupManager {

    private const val BACKUP_VERSION = 1
    private const val APP_TAG = "SmartHealth_طبيبي_الذكي"

    /**
     * Builds a comprehensive JSON string of all user data.
     */
    suspend fun createFullBackupJson(repository: MedicalRepository): Pair<String, BackupSummary> {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val timestamp = System.currentTimeMillis()

        val profile = repository.getUserProfileOnce()
        val labTests = repository.getAllLabTestsList()
        val bpRecords = repository.getAllBloodPressureList()
        val sugarRecords = repository.getAllBloodSugarList()
        val weightRecords = repository.getAllWeightList()
        val medications = repository.getAllMedicationsList()
        val consultations = repository.getAllConsultationsList()

        val root = JSONObject()
        root.put("app", APP_TAG)
        root.put("version", BACKUP_VERSION)
        root.put("exportDate", dateStr)
        root.put("timestamp", timestamp)

        // Metadata / Summary
        val meta = JSONObject()
        meta.put("patientName", profile?.name ?: "")
        meta.put("labTestsCount", labTests.size)
        meta.put("bpCount", bpRecords.size)
        meta.put("sugarCount", sugarRecords.size)
        meta.put("weightCount", weightRecords.size)
        meta.put("medicationsCount", medications.size)
        meta.put("consultationsCount", consultations.size)
        root.put("metadata", meta)

        // User Profile
        if (profile != null) {
            val profJson = JSONObject().apply {
                put("name", profile.name)
                put("age", profile.age)
                put("gender", profile.gender)
                put("heightCm", profile.heightCm.toDouble())
                put("initialWeightKg", profile.initialWeightKg.toDouble())
                put("bloodType", profile.bloodType)
                put("chronicDiseases", profile.chronicDiseases)
                put("notes", profile.notes)
            }
            root.put("userProfile", profJson)
        }

        // Lab Tests
        val labArray = JSONArray()
        labTests.forEach { t ->
            val obj = JSONObject().apply {
                put("testName", t.testName)
                put("testCategory", t.testCategory)
                put("testDate", t.testDate)
                put("labName", t.labName)
                put("status", t.status)
                put("value", t.value)
                put("unit", t.unit)
                put("referenceRange", t.referenceRange)
                put("statusFlag", t.statusFlag)
                put("notes", t.notes)
                put("timestamp", t.timestamp)
            }
            labArray.put(obj)
        }
        root.put("labTests", labArray)

        // Blood Pressure
        val bpArray = JSONArray()
        bpRecords.forEach { b ->
            val obj = JSONObject().apply {
                put("systolic", b.systolic)
                put("diastolic", b.diastolic)
                put("pulse", b.pulse)
                put("period", b.period)
                put("date", b.date)
                put("time", b.time)
                put("category", b.category)
                put("notes", b.notes)
                put("timestamp", b.timestamp)
            }
            bpArray.put(obj)
        }
        root.put("bloodPressureRecords", bpArray)

        // Blood Sugar
        val sugarArray = JSONArray()
        sugarRecords.forEach { s ->
            val obj = JSONObject().apply {
                put("value", s.value.toDouble())
                put("type", s.type)
                put("date", s.date)
                put("time", s.time)
                put("status", s.status)
                put("notes", s.notes)
                put("timestamp", s.timestamp)
            }
            sugarArray.put(obj)
        }
        root.put("bloodSugarRecords", sugarArray)

        // Weight
        val weightArray = JSONArray()
        weightRecords.forEach { w ->
            val obj = JSONObject().apply {
                put("weightKg", w.weightKg.toDouble())
                put("heightCm", w.heightCm.toDouble())
                put("bmi", w.bmi.toDouble())
                put("bmiCategory", w.bmiCategory)
                put("date", w.date)
                put("notes", w.notes)
                put("timestamp", w.timestamp)
            }
            weightArray.put(obj)
        }
        root.put("weightRecords", weightArray)

        // Medications
        val medArray = JSONArray()
        medications.forEach { m ->
            val obj = JSONObject().apply {
                put("name", m.name)
                put("dosage", m.dosage)
                put("instructions", m.instructions)
                put("time", m.time)
                put("hour", m.hour)
                put("minute", m.minute)
                put("frequency", m.frequency)
                put("isActive", m.isActive)
                put("lastTakenDate", m.lastTakenDate)
                put("notes", m.notes)
                put("activeIngredients", m.activeIngredients)
                put("uses", m.uses)
                put("sideEffects", m.sideEffects)
                put("foodInteractions", m.foodInteractions)
                put("importantInfo", m.importantInfo)
                put("timestamp", m.timestamp)
            }
            medArray.put(obj)
        }
        root.put("medications", medArray)

        // Consultations
        val consultArray = JSONArray()
        consultations.forEach { c ->
            val obj = JSONObject().apply {
                put("title", c.title)
                put("summary", c.summary)
                put("dietAdvice", c.dietAdvice)
                put("foodsToEat", c.foodsToEat)
                put("foodsToAvoid", c.foodsToAvoid)
                put("lifestyleAdvice", c.lifestyleAdvice)
                put("medicationAdvice", c.medicationAdvice)
                put("fullResponse", c.fullResponse)
                put("date", c.date)
                put("timestamp", c.timestamp)
            }
            consultArray.put(obj)
        }
        root.put("aiConsultations", consultArray)

        val jsonString = root.toString(2)
        val fileSizeBytes = jsonString.toByteArray(Charsets.UTF_8).size.toLong()
        val sizeFormatted = formatFileSize(fileSizeBytes)

        val summary = BackupSummary(
            appName = APP_TAG,
            backupDate = dateStr,
            timestamp = timestamp,
            patientName = profile?.name ?: "المستخدم",
            labTestsCount = labTests.size,
            bpCount = bpRecords.size,
            sugarCount = sugarRecords.size,
            weightCount = weightRecords.size,
            medicationsCount = medications.size,
            consultationsCount = consultations.size,
            fileSizeFormatted = sizeFormatted
        )

        return Pair(jsonString, summary)
    }

    /**
     * Saves backup JSON to a local cache file for sharing / saving to Google Drive.
     */
    fun saveBackupToCacheFile(context: Context, jsonString: String): File {
        val backupDir = File(context.cacheDir, "backup").apply { if (!exists()) mkdirs() }
        val dateSuffix = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(backupDir, "SmartHealth_GoogleDrive_Backup_$dateSuffix.json")
        FileOutputStream(file).use { out ->
            out.write(jsonString.toByteArray(Charsets.UTF_8))
        }
        return file
    }

    /**
     * Writes backup JSON directly into an output stream (e.g. from Storage Access Framework Uri).
     */
    fun writeBackupToStream(outputStream: OutputStream, jsonString: String) {
        outputStream.use { out ->
            out.write(jsonString.toByteArray(Charsets.UTF_8))
            out.flush()
        }
    }

    /**
     * Opens Android's share chooser targeting Google Drive / Cloud storage.
     */
    fun shareFileToGoogleDrive(context: Context, file: File, summary: BackupSummary) {
        val uri: Uri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(
                Intent.EXTRA_SUBJECT,
                "نسخة احتياطية طبية شاملة - Google Drive (${summary.backupDate})"
            )
            putExtra(
                Intent.EXTRA_TEXT,
                "مرفق ملف النسخة الاحتياطية الطبية الشاملة لجميع الفحوصات والتحاليل والقراءات الحيوية (${summary.totalRecordsCount} سجل مسجل).\nيمكنك حفظه مباشرة في Google Drive واستعادته في أي وقت."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "حفظ النسخة الاحتياطية في Google Drive / مشاركة").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    /**
     * Reads and parses a backup file from a selected Uri (Google Drive or local storage).
     */
    fun parseBackupFromUri(context: Context, uri: Uri): Result<BackupPayload> {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("تعذر فتح ملف النسخة الاحتياطية من Google Drive"))

            val jsonString = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use {
                it.readText()
            }

            if (jsonString.isBlank()) {
                return Result.failure(Exception("الملف فارغ أو غير صالح"))
            }

            val root = JSONObject(jsonString)

            // Validate root attributes or entities
            val app = root.optString("app", "")
            val hasEntities = root.has("labTests") || root.has("bloodPressureRecords") ||
                    root.has("bloodSugarRecords") || root.has("userProfile") || root.has("weightRecords")

            if (!hasEntities && !app.contains("SmartHealth", ignoreCase = true)) {
                return Result.failure(Exception("الملف المحدد ليس ملف نسخة احتياطية صالحاً لتطبيق طبيبي الذكي"))
            }

            val timestamp = root.optLong("timestamp", System.currentTimeMillis())
            val exportDate = root.optString("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp)))

            // User Profile
            var profile: UserProfileEntity? = null
            if (root.has("userProfile")) {
                val pObj = root.getJSONObject("userProfile")
                profile = UserProfileEntity(
                    id = 1,
                    name = pObj.optString("name", ""),
                    age = pObj.optInt("age", 30),
                    gender = pObj.optString("gender", "ذكر"),
                    heightCm = pObj.optDouble("heightCm", 175.0).toFloat(),
                    initialWeightKg = pObj.optDouble("initialWeightKg", 75.0).toFloat(),
                    bloodType = pObj.optString("bloodType", ""),
                    chronicDiseases = pObj.optString("chronicDiseases", ""),
                    notes = pObj.optString("notes", ""),
                    updatedAt = System.currentTimeMillis()
                )
            }

            // Lab Tests
            val labTests = mutableListOf<LabTestEntity>()
            if (root.has("labTests")) {
                val array = root.getJSONArray("labTests")
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    labTests.add(
                        LabTestEntity(
                            id = 0,
                            testName = item.optString("testName", "فحص مخبري"),
                            testCategory = item.optString("testCategory", "عام"),
                            testDate = item.optString("testDate", ""),
                            labName = item.optString("labName", ""),
                            status = item.optString("status", ""),
                            value = item.optString("value", ""),
                            unit = item.optString("unit", ""),
                            referenceRange = item.optString("referenceRange", ""),
                            statusFlag = item.optString("statusFlag", "Normal"),
                            notes = item.optString("notes", ""),
                            timestamp = item.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            // Blood Pressure
            val bpRecords = mutableListOf<BloodPressureEntity>()
            if (root.has("bloodPressureRecords")) {
                val array = root.getJSONArray("bloodPressureRecords")
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    bpRecords.add(
                        BloodPressureEntity(
                            id = 0,
                            systolic = item.optInt("systolic", 120),
                            diastolic = item.optInt("diastolic", 80),
                            pulse = item.optInt("pulse", 72),
                            period = item.optString("period", "صباحاً"),
                            date = item.optString("date", ""),
                            time = item.optString("time", ""),
                            category = item.optString("category", "طبيعي"),
                            notes = item.optString("notes", ""),
                            timestamp = item.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            // Blood Sugar
            val sugarRecords = mutableListOf<BloodSugarEntity>()
            if (root.has("bloodSugarRecords")) {
                val array = root.getJSONArray("bloodSugarRecords")
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    sugarRecords.add(
                        BloodSugarEntity(
                            id = 0,
                            value = item.optDouble("value", 100.0).toFloat(),
                            type = item.optString("type", "صائم"),
                            date = item.optString("date", ""),
                            time = item.optString("time", ""),
                            status = item.optString("status", "طبيعي"),
                            notes = item.optString("notes", ""),
                            timestamp = item.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            // Weight
            val weightRecords = mutableListOf<WeightRecordEntity>()
            if (root.has("weightRecords")) {
                val array = root.getJSONArray("weightRecords")
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    weightRecords.add(
                        WeightRecordEntity(
                            id = 0,
                            weightKg = item.optDouble("weightKg", 70.0).toFloat(),
                            heightCm = item.optDouble("heightCm", 175.0).toFloat(),
                            bmi = item.optDouble("bmi", 22.8).toFloat(),
                            bmiCategory = item.optString("bmiCategory", "وزن طبيعي"),
                            date = item.optString("date", ""),
                            notes = item.optString("notes", ""),
                            timestamp = item.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            // Medications
            val medications = mutableListOf<MedicationEntity>()
            if (root.has("medications")) {
                val array = root.getJSONArray("medications")
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    medications.add(
                        MedicationEntity(
                            id = 0,
                            name = item.optString("name", "دواء"),
                            dosage = item.optString("dosage", "1 قرص"),
                            instructions = item.optString("instructions", "بعد الأكل"),
                            time = item.optString("time", "08:00 ص"),
                            hour = item.optInt("hour", 8),
                            minute = item.optInt("minute", 0),
                            frequency = item.optString("frequency", "يومياً"),
                            isActive = item.optBoolean("isActive", true),
                            lastTakenDate = item.optString("lastTakenDate", ""),
                            notes = item.optString("notes", ""),
                            activeIngredients = item.optString("activeIngredients", ""),
                            uses = item.optString("uses", ""),
                            sideEffects = item.optString("sideEffects", ""),
                            foodInteractions = item.optString("foodInteractions", ""),
                            importantInfo = item.optString("importantInfo", ""),
                            timestamp = item.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            // AI Consultations
            val consultations = mutableListOf<AiConsultationEntity>()
            if (root.has("aiConsultations")) {
                val array = root.getJSONArray("aiConsultations")
                for (i in 0 until array.length()) {
                    val item = array.getJSONObject(i)
                    consultations.add(
                        AiConsultationEntity(
                            id = 0,
                            title = item.optString("title", "استشارة طبية"),
                            summary = item.optString("summary", ""),
                            dietAdvice = item.optString("dietAdvice", ""),
                            foodsToEat = item.optString("foodsToEat", ""),
                            foodsToAvoid = item.optString("foodsToAvoid", ""),
                            lifestyleAdvice = item.optString("lifestyleAdvice", ""),
                            medicationAdvice = item.optString("medicationAdvice", ""),
                            fullResponse = item.optString("fullResponse", ""),
                            date = item.optString("date", ""),
                            timestamp = item.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            val fileSizeBytes = jsonString.toByteArray(Charsets.UTF_8).size.toLong()
            val summary = BackupSummary(
                appName = app.ifEmpty { "طبيبي الذكي" },
                backupDate = exportDate,
                timestamp = timestamp,
                patientName = profile?.name ?: "المستخدم",
                labTestsCount = labTests.size,
                bpCount = bpRecords.size,
                sugarCount = sugarRecords.size,
                weightCount = weightRecords.size,
                medicationsCount = medications.size,
                consultationsCount = consultations.size,
                fileSizeFormatted = formatFileSize(fileSizeBytes)
            )

            Result.success(
                BackupPayload(
                    summary = summary,
                    profile = profile,
                    labTests = labTests,
                    bpRecords = bpRecords,
                    sugarRecords = sugarRecords,
                    weightRecords = weightRecords,
                    medications = medications,
                    consultations = consultations
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("خطأ أثناء قراءة ملف النسخة الاحتياطية: ${e.message}"))
        }
    }

    /**
     * Executes the restoration of the parsed payload into Room database.
     */
    suspend fun executeRestore(
        payload: BackupPayload,
        mergeMode: Boolean,
        repository: MedicalRepository,
        context: Context
    ): RestoreResult {
        return try {
            if (!mergeMode) {
                // Clear existing records before restoring
                repository.clearAllData(includeTests = true)
                repository.clearMedications()
            }

            // Restore user profile if present
            payload.profile?.let { prof ->
                repository.saveUserProfile(prof)
            }

            // Restore lab tests
            if (payload.labTests.isNotEmpty()) {
                repository.insertAllLabTests(payload.labTests)
            }

            // Restore blood pressure
            if (payload.bpRecords.isNotEmpty()) {
                repository.insertAllBloodPressure(payload.bpRecords)
            }

            // Restore blood sugar
            if (payload.sugarRecords.isNotEmpty()) {
                repository.insertAllBloodSugar(payload.sugarRecords)
            }

            // Restore weight
            if (payload.weightRecords.isNotEmpty()) {
                repository.insertAllWeight(payload.weightRecords)
            }

            // Restore medications & schedule reminders
            if (payload.medications.isNotEmpty()) {
                payload.medications.forEach { med ->
                    val newId = repository.insertMedication(med)
                    if (med.isActive) {
                        MedicationReminderScheduler.scheduleReminder(
                            context = context,
                            med = med.copy(id = newId),
                            patientName = payload.profile?.name ?: ""
                        )
                    }
                }
            }

            // Restore AI consultations
            if (payload.consultations.isNotEmpty()) {
                repository.insertAllConsultations(payload.consultations)
            }

            // Log this sync operation in DriveSyncFileEntity
            val syncDate = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault()).format(Date())
            repository.insertDriveFile(
                DriveSyncFileEntity(
                    fileName = "GoogleDrive_Restore_${payload.summary.backupDate}.json",
                    fileType = "DATA_BACKUP",
                    fileSizeFormatted = payload.summary.fileSizeFormatted,
                    syncStatus = "SUCCESS",
                    syncDate = syncDate,
                    timestamp = System.currentTimeMillis()
                )
            )

            val modeText = if (mergeMode) "دمج" else "استعادة كاملة لـ"
            RestoreResult(
                isSuccess = true,
                message = "تم $modeText ${payload.summary.totalRecordsCount} سجلاً بنجاح من Google Drive!",
                summary = payload.summary
            )
        } catch (e: Exception) {
            e.printStackTrace()
            RestoreResult(
                isSuccess = false,
                message = "فشل استعادة البيانات: ${e.message}",
                summary = payload.summary
            )
        }
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format(Locale.US, "%.1f KB", bytes / 1024.0)
            else -> String.format(Locale.US, "%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }
}
