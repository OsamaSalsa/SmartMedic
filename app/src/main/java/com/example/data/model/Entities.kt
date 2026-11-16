package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lab_tests")
data class LabTestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testName: String,
    val testCategory: String = "عام",
    val testDate: String = "",
    val labName: String = "",
    val status: String = "", // طبيعي, زائد, نقص
    val value: String = "",
    val unit: String = "",
    val referenceRange: String = "",
    val statusFlag: String = "Normal", // Normal, High, Low, Unknown
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

fun LabTestEntity.calculateLevel(): String {
    val flagLower = statusFlag.trim().lowercase()
    val statusLower = status.trim().lowercase()

    if (flagLower == "high" || flagLower == "مرتفع" || flagLower == "زائد" ||
        statusLower == "high" || statusLower == "مرتفع" || statusLower == "زائد") {
        return "زائد"
    }
    if (flagLower == "low" || flagLower == "منخفض" || flagLower == "نقص" ||
        statusLower == "low" || statusLower == "منخفض" || statusLower == "نقص") {
        return "نقص"
    }
    if (flagLower == "normal" || flagLower == "طبيعي" ||
        statusLower == "normal" || statusLower == "طبيعي") {
        return "طبيعي"
    }

    val cleanVal = value.replace(",", ".").filter { it.isDigit() || it == '.' }.toDoubleOrNull()
    if (cleanVal != null && referenceRange.isNotBlank()) {
        val numbers = Regex("""\d+(\.\d+)?""").findAll(referenceRange).mapNotNull { it.value.toDoubleOrNull() }.toList()
        if (numbers.size >= 2) {
            val min = numbers[0]
            val max = numbers[1]
            return when {
                cleanVal < min -> "نقص"
                cleanVal > max -> "زائد"
                else -> "طبيعي"
            }
        }
        if (referenceRange.contains("<")) {
            val limit = numbers.firstOrNull()
            if (limit != null) return if (cleanVal > limit) "زائد" else "طبيعي"
        }
        if (referenceRange.contains(">")) {
            val limit = numbers.firstOrNull()
            if (limit != null) return if (cleanVal < limit) "نقص" else "طبيعي"
        }
    }

    return if (value.isNotBlank() && value != "-") "طبيعي" else "غير محدد"
}

@Entity(tableName = "blood_pressure_records")
data class BloodPressureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int = 72,
    val period: String = "صباحاً", // صباحاً, ظهراً, مساءً, ليلاً
    val date: String,
    val time: String,
    val category: String, // طبيعي, مرتفع طفيف, مرحلة 1, مرحلة 2, أزمة
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "blood_sugar_records")
data class BloodSugarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val value: Float,
    val type: String, // صائم, بعد الأكل, عشوائي, قبل النوم
    val date: String,
    val time: String,
    val status: String, // طبيعي, مرتفع, منخفض
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weight_records")
data class WeightRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float,
    val heightCm: Float = 175f,
    val bmi: Float,
    val bmiCategory: String, // وزن طبيعي, زيادة وزن, سمنة, نحافة
    val date: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_consultations")
data class AiConsultationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val summary: String,
    val dietAdvice: String,
    val foodsToEat: String,
    val foodsToAvoid: String,
    val lifestyleAdvice: String,
    val medicationAdvice: String,
    val fullResponse: String,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Long = 1,
    val name: String = "",
    val age: Int = 30,
    val gender: String = "ذكر", // ذكر / أنثى
    val heightCm: Float = 175f,
    val initialWeightKg: Float = 75f,
    val bloodType: String = "",
    val chronicDiseases: String = "",
    val notes: String = "",
    val profileImageUri: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String = "1 قرص",
    val instructions: String = "بعد الأكل", // قبل الأكل، بعد الأكل، مع الطعام، قبل النوم
    val time: String = "08:00 ص",
    val hour: Int = 8,
    val minute: Int = 0,
    val frequency: String = "يومياً", // يومياً، مرتين يومياً، عند اللزوم، أسبوعياً
    val isActive: Boolean = true,
    val lastTakenDate: String = "",
    val notes: String = "",
    val activeIngredients: String = "", // التركيبة الأساسية / المادة الفعالة
    val uses: String = "",              // دواعي واستخدامات الدواء
    val sideEffects: String = "",       // الأعراض الجانبية والمحاذير
    val foodInteractions: String = "",  // الغذاء والأطعمة الواجب تجنبها والمشروبات
    val importantInfo: String = "",     // معلومات هامة وإرشادات صيدلانية
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "drive_synced_files")
data class DriveSyncFileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val driveFileId: String = "",
    val fileName: String,
    val fileType: String, // "PDF_REPORT", "DATA_BACKUP", "LAB_TEST"
    val fileSizeFormatted: String = "",
    val syncStatus: String = "SUCCESS", // "SUCCESS", "PENDING", "FAILED"
    val syncDate: String,
    val driveViewUrl: String = "",
    val localFilePath: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_test_names")
data class CustomTestNameEntity(
    @PrimaryKey val name: String,
    val category: String = "عام",
    val defaultUnit: String = "",
    val defaultRefRange: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
