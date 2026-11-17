package com.example.data.remote

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.data.model.AiConsultationEntity
import com.example.data.model.BloodPressureEntity
import com.example.data.model.BloodSugarEntity
import com.example.data.model.LabTestEntity
import com.example.data.model.MedicationEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightRecordEntity
import com.example.ui.locale.AppLanguage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MedicationAiDetails(
    val activeIngredients: String = "",
    val uses: String = "",
    val sideEffects: String = "",
    val foodInteractions: String = "",
    val importantInfo: String = ""
)

class GeminiService {

    companion object {
        private const val TAG = "GeminiService"
        private const val DEFAULT_MODEL = "gemini-3.8-flash"
        private const val REQUEST_TIMEOUT_MS = 45_000L
        private const val MULTIMODAL_TIMEOUT_MS = 90_000L
        private const val MAX_INLINE_MEDIA_BYTES = 19 * 1024 * 1024 // Keep under 20MB limit
    }

    private val medicalAnalysisSchema by lazy {
        Schema.obj(
            properties = mapOf(
                "title" to Schema.string(description = "Summary title of the medical consultation report"),
                "summary" to Schema.string(description = "Comprehensive medical summary interpreting labs, vitals, and medications"),
                "dietAdvice" to Schema.string(description = "Detailed nutrition and meal plan advice considering medications"),
                "foodsToEat" to Schema.string(description = "Bulleted list of recommended foods and nutrients"),
                "foodsToAvoid" to Schema.string(description = "Bulleted list of foods and beverages to avoid including drug interactions"),
                "lifestyleAdvice" to Schema.string(description = "Exercise, sleep, hydration, and daily routine advice"),
                "medicationAdvice" to Schema.string(description = "Clinical medication guidance, timing, interactions, and doctor reminder")
            )
        )
    }

    private val analysisModel by lazy {
        Firebase.ai.generativeModel(
            modelName = DEFAULT_MODEL,
            generationConfig = generationConfig {
                temperature = 0.3f
                topP = 0.95f
                responseMimeType = "application/json"
                responseSchema = medicalAnalysisSchema
            }
        )
    }

    private val jsonExtractionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = DEFAULT_MODEL,
            generationConfig = generationConfig {
                temperature = 0.1f
                topP = 0.95f
                responseMimeType = "application/json"
            }
        )
    }

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = DEFAULT_MODEL,
            generationConfig = generationConfig {
                temperature = 0.3f
                topP = 0.95f
            }
        )
    }

    suspend fun analyzeMedicalProfile(
        labTests: List<LabTestEntity>,
        bpRecords: List<BloodPressureEntity>,
        sugarRecords: List<BloodSugarEntity>,
        latestWeight: WeightRecordEntity?,
        profile: UserProfileEntity? = null,
        medications: List<MedicationEntity> = emptyList(),
        language: AppLanguage = AppLanguage.AR
    ): Result<AiConsultationEntity> = withContext(Dispatchers.IO) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val effective = language.resolveEffectiveLanguage()

        val completedTests = labTests.filter { it.value.isNotBlank() && it.value != "غير مسحوب" && it.value != "-" }
        val activeMeds = medications.filter { it.isActive }

        val prompt = buildString {
            when (effective) {
                AppLanguage.EN -> append("You are a clinical medical consultant, nutritionist, and pharmacist. Analyze the patient data below and return a concise, actionable medical report in English.\n\n")
                AppLanguage.RU -> append("Вы врач-консультант, клинический диетолог и фармацевт. Проанализируйте медицинские данные пациента и составьте краткий, практичный медицинский отчет на русском языке.\n\n")
                else -> append("أنت استشاري طبي وأخصائي تغذية إكلينيكية وصيدلي سريري. حلل البيانات الطبية والأدوية التالية وقدّم تقريراً طبياً وغذائياً مختصراً وواضحاً باللغة العربية.\n\n")
            }
            if (profile != null) {
                append("=== معلومات المريض ===\n")
                append("- الاسم: ${profile.name.ifEmpty { "غير محدد" }} | العمر: ${profile.age} | الجنس: ${profile.gender}\n")
                append("- الطول: ${profile.heightCm} سم | الوزن: ${profile.initialWeightKg} كجم | فصيلة الدم: ${profile.bloodType}\n")
                if (profile.chronicDiseases.isNotBlank()) append("- الأمراض المزمنة: ${profile.chronicDiseases}\n")
            }

            if (completedTests.isNotEmpty()) {
                append("\n=== الفحوصات المخبرية ===\n")
                completedTests.take(30).forEach { test ->
                    append("- ${test.testName}: ${test.value} ${test.unit} (الطبيعي: ${test.referenceRange}) [${test.status}]\n")
                }
            }

            if (bpRecords.isNotEmpty()) {
                append("\n=== آخر قراءات ضغط الدم ===\n")
                bpRecords.take(5).forEach { bp ->
                    append("- ${bp.date} (${bp.period}): ${bp.systolic}/${bp.diastolic} mmHg (نبض ${bp.pulse}) [${bp.category}]\n")
                }
            }

            if (sugarRecords.isNotEmpty()) {
                append("\n=== آخر قراءات سكر الدم ===\n")
                sugarRecords.take(5).forEach { s ->
                    append("- ${s.date} (${s.type}): ${s.value} mg/dL [${s.status}]\n")
                }
            }

            if (latestWeight != null) {
                append("\n=== الوزن ومؤشر الكتلة ===\n")
                append("- الوزن: ${latestWeight.weightKg} كجم، BMI: ${latestWeight.bmi} (${latestWeight.bmiCategory})\n")
            }

            if (activeMeds.isNotEmpty()) {
                append("\n=== الأدوية الحالية ===\n")
                activeMeds.forEach { med ->
                    append("- ${med.name} (${med.dosage}، ${med.time}، ${med.instructions}، ${med.frequency})")
                    if (med.activeIngredients.isNotBlank()) append(" | المادة الفعالة: ${med.activeIngredients}")
                    if (med.foodInteractions.isNotBlank()) append(" | تعارض غذائي: ${med.foodInteractions}")
                    append("\n")
                }
            }
        }

        try {
            val response = withTimeout(REQUEST_TIMEOUT_MS) {
                analysisModel.generateContent(prompt)
            }
            val responseText = response.text?.trim().orEmpty()
            if (responseText.isNotBlank()) {
                val json = JSONObject(cleanJsonOutput(responseText))
                return@withContext Result.success(
                    AiConsultationEntity(
                        title = json.optString("title", "تحليل الفحوصات والأدوية بالذكاء الاصطناعي"),
                        summary = json.optString("summary", "تم تحليل الفحوصات الطبية والأدوية بنجاح."),
                        dietAdvice = json.optString("dietAdvice", "اتباع نظام غذائي متوازن متوافق مع الأدوية."),
                        foodsToEat = json.optString("foodsToEat", "الخضروات الطازجة والأسماك والحبوب الكاملة."),
                        foodsToAvoid = json.optString("foodsToAvoid", "السكريات الزائدة والدهون المشبعة وتجنب ما يتعارض مع الأدوية."),
                        lifestyleAdvice = json.optString("lifestyleAdvice", "المشي يومياً وشرب الماء الكافي."),
                        medicationAdvice = json.optString("medicationAdvice", "الالتزام بمواعيد الجرعات واستشارة الطبيب."),
                        fullResponse = responseText,
                        date = currentDate
                    )
                )
            }
            Result.success(
                generateRuleBasedMedicalConsultation(
                    labTests = completedTests,
                    bpRecords = bpRecords,
                    sugarRecords = sugarRecords,
                    latestWeight = latestWeight,
                    profile = profile,
                    medications = activeMeds,
                    language = effective,
                    currentDate = currentDate
                )
            )
        } catch (e: CancellationException) {
            if (e is TimeoutCancellationException) {
                Log.w(TAG, "Medical profile cloud analysis timed out; building clinical consultation from local patient data", e)
                return@withContext Result.success(
                    generateRuleBasedMedicalConsultation(
                        labTests = completedTests,
                        bpRecords = bpRecords,
                        sugarRecords = sugarRecords,
                        latestWeight = latestWeight,
                        profile = profile,
                        medications = activeMeds,
                        language = effective,
                        currentDate = currentDate
                    )
                )
            }
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Medical profile cloud analysis unavailable; building clinical consultation from local patient data", e)
            Result.success(
                generateRuleBasedMedicalConsultation(
                    labTests = completedTests,
                    bpRecords = bpRecords,
                    sugarRecords = sugarRecords,
                    latestWeight = latestWeight,
                    profile = profile,
                    medications = activeMeds,
                    language = effective,
                    currentDate = currentDate
                )
            )
        }
    }

    suspend fun extractTestsFromImageOrText(
        fileBase64: String?,
        mimeType: String = "image/jpeg",
        pageImagesBase64: List<String> = emptyList(),
        textNotes: String? = null
    ): Result<List<LabTestEntity>> = withContext(Dispatchers.IO) {
        val prompt = """
            أنت طبيب استشاري ومختص خبير في قراءة وتحليل تقارير الفحوصات المخبرية الطبية بدقة متناهية.
            المهمة: قم بقراءة مستند الفحص الطبي المرفق (سواء كان صور صفحات أو مستند PDF لتحاليل مخبرية) واستخراج كافة الفحوصات المخبرية الموجودة بجميع الصفحات بدقة وتفصيل دون استثناء أي فحص.
            ${if (!textNotes.isNullOrBlank()) "ملاحظات المستخدم: $textNotes" else ""}

            لكل فحص تم العثور عليه في التقرير، استخرج:
            1. testName: اسم الفحص الطبي بالإنجليزية ومقابله بالعربية إن وجد (مثل: Fasting Blood Sugar (FBS)، Hemoglobin، Creatinine).
            2. value: القيمة الرقمية الناتجة للفحص بدقة كما هي مسجلة في الورقة.
            3. unit: وحدة القياس (مثل mg/dL, g/dL, U/L, %, mmol/L).
            4. referenceRange: المعدل المرجعي الطبيعي المذكور في التقرير (مثل 70 - 100).
            5. testCategory: تصنيف الفحص (مثل: صورة الدم CBC، وظائف الكلى، وظائف الكبد، دهون الدم Lipid Profile، سكر الدم، فيتامينات ومعادن، هرمونات).
            6. statusFlag: "Normal" إذا كانت القيمة ضمن المعدل، "High" إذا كانت مرتفعة، "Low" إذا كانت منخفضة.
            7. status: "طبيعي" إذا كانت القيمة طبيعية، "زائد" إذا كانت مرتفعة/زائدة، "نقص" إذا كانت منخفضة/ناقصة.
            8. labName: اسم المختبر أو المركز الطبي إن وجد في الترويسة.
            9. testDate: تاريخ إجراء الفحص إن وجد بالتقرير (صيغة YYYY-MM-DD).
            10. notes: ملاحظة طبية وجيزة عن النتيجة.

            أعد النتيجة حصرياً على شكل مصفوفة JSON تحتوي على كافة الفحوصات المستخرجة من كل الصفحات:
            [
              {
                "testName": "Fasting Blood Sugar (FBS)",
                "testCategory": "سكر الدم",
                "testDate": "2026-10-06",
                "labName": "المختبر الطبي",
                "status": "طبيعي",
                "value": "95",
                "unit": "mg/dL",
                "referenceRange": "70 - 100",
                "statusFlag": "Normal",
                "notes": "النتيجة ضمن المعدل الطبيعي"
              }
            ]
        """.trimIndent()

        try {
            val responseText = executeGeminiMultimodal(prompt, fileBase64, mimeType, pageImagesBase64)
            val jsonCleaned = cleanJsonOutput(responseText)
            val jsonArray = try {
                JSONArray(jsonCleaned)
            } catch (_: Exception) {
                try {
                    val obj = JSONObject(jsonCleaned)
                    obj.optJSONArray("tests")
                        ?: obj.optJSONArray("labTests")
                        ?: obj.optJSONArray("results")
                        ?: obj.optJSONArray("data")
                        ?: JSONArray()
                } catch (_: Exception) {
                    JSONArray()
                }
            }
            val extracted = mutableListOf<LabTestEntity>()
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val testVal = obj.optString("value", "").trim()
                if (testVal.isNotBlank() && testVal != "-" && testVal != "غير مسحوب") {
                    val sFlag = obj.optString("statusFlag", "Normal")
                    val defaultStatus = when (sFlag.lowercase()) {
                        "high" -> "زائد"
                        "low" -> "نقص"
                        else -> "طبيعي"
                    }
                    val rawStatus = obj.optString("status", defaultStatus)
                    val cleanStatus = when (rawStatus.lowercase()) {
                        "approve", "approved", "normal" -> "طبيعي"
                        "high", "مرتفع" -> "زائد"
                        "low", "منخفض" -> "نقص"
                        else -> rawStatus.ifBlank { defaultStatus }
                    }
                    extracted.add(
                        LabTestEntity(
                            testName = obj.optString("testName", "فحص مخبري"),
                            testCategory = obj.optString("testCategory", "عام"),
                            testDate = obj.optString("testDate", todayStr).ifBlank { todayStr },
                            labName = obj.optString("labName", "المختبر الطبي"),
                            status = cleanStatus,
                            value = testVal,
                            unit = obj.optString("unit", ""),
                            referenceRange = obj.optString("referenceRange", ""),
                            statusFlag = sFlag,
                            notes = obj.optString("notes", "مستخرج آلياً بواسطة الذكاء الاصطناعي")
                        )
                    )
                }
            }
            if (extracted.isNotEmpty()) {
                Result.success(extracted)
            } else {
                Result.failure(Exception("لم نتمكن من قراءة فحوصات مخبرية من المستند المرفق. يرجى التأكد من وضوح التقرير"))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Lab report extraction timed out", e)
            Result.failure(Exception("انتهت مهلة تحليل التقرير المخبري (90 ثانية). يُرجى المحاولة مجدداً أو تقليل حجم الملف"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Extraction failed", e)
            Result.failure(Exception("تعذر استخراج الفحوصات. يرجى التأكد من وضوح الصورة أو الملف والاتصال بالإنترنت."))
        }
    }

    suspend fun extractMedicationsFromPrescription(
        fileBase64: String?,
        mimeType: String = "image/jpeg",
        pageImagesBase64: List<String> = emptyList(),
        textNotes: String? = null,
        language: AppLanguage = AppLanguage.AR
    ): Result<List<MedicationEntity>> = withContext(Dispatchers.IO) {
        val effectiveLang = language.resolveEffectiveLanguage()

        val prompt = buildString {
            when (effectiveLang) {
                AppLanguage.RU -> {
                    append("Вы профессиональный клинический фармаколог и врач-консультант. Проанализируйте прикрепленный рецепт.\n")
                    append("Задача: внимательно изучите документ и извлеките ВСЕ назначенные лекарства.\n")
                    if (!textNotes.isNullOrBlank()) append("Дополнительные примечания пользователя: $textNotes\n")
                    append("Для каждого препарата извлеките следующие данные НА РУССКОМ ЯЗЫКЕ:\n")
                    append("1. name: название препарата (латиницей или кириллицей).\n")
                    append("2. dosage: дозировка и форма (например: 20 мг, 1 таб., 5 мг).\n")
                    append("3. instructions: инструкции по приему на русском языке ('После еды', 'До еды', 'Во время еды', 'Перед сном', 'Натощак').\n")
                    append("4. frequency: частота приема на русском языке ('Ежедневно', '2 раза в день', '3 раза в день', 'По необходимости').\n")
                    append("5. hour: час напоминания (0-23).\n")
                    append("6. minute: минута напоминания (0-59).\n")
                    append("7. time: время напоминания (например: '08:00', '20:00').\n")
                    append("8. activeIngredients: действующее вещество и состав на русском языке.\n")
                    append("9. uses: показания к применению на русском языке.\n")
                    append("10. sideEffects: побочные эффекты и предостережения на русском языке.\n")
                    append("11. foodInteractions: взаимодействие с пищей и напитками на русском языке.\n")
                    append("12. importantInfo: особые указания по применению на русском языке.\n")
                    append("13. notes: примечания на русском языке.\n\n")
                    append("Верните результат ИСКЛЮЧИТЕЛЬНО в виде JSON массива.\n")
                }
                AppLanguage.EN -> {
                    append("You are a professional clinical pharmacist and medical consultant. Analyze the attached medical prescription image/document.\n")
                    append("Task: Read the document carefully and extract ALL prescribed medications.\n")
                    if (!textNotes.isNullOrBlank()) append("User notes: $textNotes\n")
                    append("For each medication, extract the following data IN ENGLISH:\n")
                    append("1. name: scientific or brand name.\n")
                    append("2. dosage: dosage and form (e.g., 20 mg, 1 tablet, 5 ml).\n")
                    append("3. instructions: taking instructions in English ('After meal', 'Before meal', 'With food', 'Before bed').\n")
                    append("4. frequency: frequency in English ('Daily', 'Twice daily', 'Three times daily', 'As needed').\n")
                    append("5. hour: hour for daily alarm (0 to 23).\n")
                    append("6. minute: minute (0 to 59).\n")
                    append("7. time: 12-hour formatted time (e.g. '08:00 AM' or '08:00 PM').\n")
                    append("8. activeIngredients: active ingredient in English.\n")
                    append("9. uses: main indications and uses in English.\n")
                    append("10. sideEffects: common side effects and warnings in English.\n")
                    append("11. foodInteractions: food/beverage interactions in English.\n")
                    append("12. importantInfo: important pharmaceutical instructions in English.\n")
                    append("13. notes: notes in English.\n\n")
                    append("Return EXCLUSIVELY a JSON array.\n")
                }
                else -> {
                    append("أنت طبيب استشاري وصيدلي سريري خبير ومحترف في قراءة الروشتات والوصفات الطبية (Medical Prescriptions) سواء كانت مطبوعة أو مكتوبة بخط اليد.\n")
                    append("المهمة: قم بقراءة صورة أو مستند الوصفة الطبية المرفقة بجميع صفحاتها بتمعن ودقة متناهية واستخراج كافة الأدوية الموصوفة فيها بدون استثناء أي دواء.\n")
                    if (!textNotes.isNullOrBlank()) {
                        append("ملاحظات إضافية وتوضيحات مكتوبة من المستخدم: $textNotes\n")
                    }
                    append("\nتنبيه هام جداً: استخرج جميع الأدوية المذكورة في المستند بالكامل وأدرج كل دواء كعنصر مستقل داخل مصفوفة JSON.\n")
                    append("لكل دواء مستخرج، استخرج البيانات التالية بدقة:\n")
                    append("1. name: الاسم العلمي أو التجاري للدواء.\n")
                    append("2. dosage: الجرعة والتركيز والشكل الصيدلاني (مثل: 20 mg، 1 قرص، كبسولة واحدة).\n")
                    append("3. instructions: إرشادات التناول باللغة العربية (\"بعد الأكل\"، \"قبل الأكل\"، \"مع الطعام\"، \"قبل النوم\").\n")
                    append("4. frequency: تكرار أخذ الدواء باللغة العربية (\"يومياً\"، \"مرتين يومياً\"، \"3 مرات يومياً\"، \"عند اللزوم\").\n")
                    append("5. hour: ساعة التنبيه (0 إلى 23).\n")
                    append("6. minute: دقيقة التنبيه (0 إلى 59).\n")
                    append("7. time: نص وقت التنبيه بصيغة 12 ساعة بالعربية (مثل: \"08:00 ص\" أو \"08:00 م\").\n")
                    append("8. activeIngredients: التركيبة الأساسية والمادة الفعالة.\n")
                    append("9. uses: دواعي واستخدامات الدواء الرئيسية.\n")
                    append("10. sideEffects: أهم الأعراض الجانبية الشائعة والمحاذير.\n")
                    append("11. foodInteractions: التداخلات الغذائية والأطعمة التي يجب تجنبها.\n")
                    append("12. importantInfo: معلومات صيدلانية وإرشادات هامة.\n")
                    append("13. notes: أي ملاحظات طبية أخرى مذكورة بالروشتة.\n\n")
                }
            }
            append("Return ONLY a valid JSON array.")
        }

        try {
            val responseText = executeGeminiMultimodal(prompt, fileBase64, mimeType, pageImagesBase64)
            val jsonCleaned = cleanJsonOutput(responseText)
            val jsonArray = try {
                JSONArray(jsonCleaned)
            } catch (_: Exception) {
                try {
                    val obj = JSONObject(jsonCleaned)
                    obj.optJSONArray("medications")
                        ?: obj.optJSONArray("medicines")
                        ?: obj.optJSONArray("drugs")
                        ?: obj.optJSONArray("prescriptions")
                        ?: obj.optJSONArray("data")
                        ?: JSONArray()
                } catch (_: Exception) {
                    JSONArray()
                }
            }
            val extracted = mutableListOf<MedicationEntity>()

            val defaultDosage = if (effectiveLang == AppLanguage.RU) "1 таб." else if (effectiveLang == AppLanguage.EN) "1 tablet" else "1 قرص"
            val defaultInst = if (effectiveLang == AppLanguage.RU) "После еды" else if (effectiveLang == AppLanguage.EN) "After meal" else "بعد الأكل"
            val defaultFreq = if (effectiveLang == AppLanguage.RU) "Ежедневно" else if (effectiveLang == AppLanguage.EN) "Daily" else "يومياً"
            val defaultNotesStr = if (effectiveLang == AppLanguage.RU) "Автоматически извлечено ИИ из рецепта" else if (effectiveLang == AppLanguage.EN) "Auto-extracted by AI from prescription" else "مستخرج آلياً بواسطة الذكاء الاصطناعي من الوصفة الطبية"

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val medName = obj.optString("name", "").trim()
                if (medName.isNotBlank()) {
                    val dosage = obj.optString("dosage", defaultDosage).trim()
                    val instructions = obj.optString("instructions", defaultInst).trim()
                    val frequency = obj.optString("frequency", defaultFreq).trim()
                    val hour = obj.optInt("hour", 8).coerceIn(0, 23)
                    val minute = obj.optInt("minute", 0).coerceIn(0, 59)
                    val defaultTime = if (effectiveLang == AppLanguage.AR) {
                        if (hour < 12) {
                            String.format(Locale.getDefault(), "%02d:%02d ص", if (hour == 0) 12 else hour, minute)
                        } else {
                            val h12 = if (hour == 12) 12 else hour - 12
                            String.format(Locale.getDefault(), "%02d:%02d م", h12, minute)
                        }
                    } else if (effectiveLang == AppLanguage.RU) {
                        String.format(Locale.US, "%02d:%02d", hour, minute)
                    } else {
                        val isPm = hour >= 12
                        val h12 = when { hour == 0 -> 12; hour > 12 -> hour - 12; else -> hour }
                        String.format(Locale.US, "%02d:%02d %s", h12, minute, if (isPm) "PM" else "AM")
                    }
                    val timeStr = obj.optString("time", defaultTime).ifBlank { defaultTime }
                    val notes = obj.optString("notes", defaultNotesStr)

                    val ruleFallback = generateRuleBasedMedicationDetails(medName, dosage, effectiveLang)
                    val activeIng = obj.optString("activeIngredients", "").trim().ifBlank { ruleFallback.activeIngredients }
                    val uses = obj.optString("uses", "").trim().ifBlank { ruleFallback.uses }
                    val sideEffects = obj.optString("sideEffects", "").trim().ifBlank { ruleFallback.sideEffects }
                    val foodInteractions = obj.optString("foodInteractions", "").trim().ifBlank { ruleFallback.foodInteractions }
                    val importantInfo = obj.optString("importantInfo", "").trim().ifBlank { ruleFallback.importantInfo }

                    extracted.add(
                        MedicationEntity(
                            name = medName,
                            dosage = dosage,
                            instructions = instructions,
                            time = timeStr,
                            hour = hour,
                            minute = minute,
                            frequency = frequency,
                            isActive = true,
                            notes = notes,
                            activeIngredients = activeIng,
                            uses = uses,
                            sideEffects = sideEffects,
                            foodInteractions = foodInteractions,
                            importantInfo = importantInfo
                        )
                    )
                }
            }

            if (extracted.isNotEmpty()) {
                Result.success(extracted)
            } else {
                val errStr = if (effectiveLang == AppLanguage.RU) "Не удалось распознать лекарства на фото рецепта. Проверьте четкость снимка."
                else if (effectiveLang == AppLanguage.EN) "Could not recognize medications in attached prescription image."
                else "لم نتمكن من التعرف على أدوية في الصورة/الوصفة المرفقة. يُرجى التأكد من وضوح الخط"
                Result.failure(Exception(errStr))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Prescription extraction timed out", e)
            Result.failure(Exception(localizedTimeoutError(effectiveLang)))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Prescription extraction failed", e)
            val errStr = when (effectiveLang) {
                AppLanguage.RU -> "Не удалось распознать лекарства. Убедитесь, что снимок четкий и есть подключение к интернету."
                AppLanguage.EN -> "Unable to read medications. Please make sure the image is clear and you are connected to the internet."
                else -> "تعذر قراءة الأدوية من الوصفة. يرجى التأكد من وضوح الصورة والاتصال بالإنترنت."
            }
            Result.failure(Exception(errStr))
        }
    }

    suspend fun fetchMedicationAiDetails(
        medicationName: String,
        dosage: String = "",
        language: AppLanguage = AppLanguage.AR
    ): MedicationAiDetails = withContext(Dispatchers.IO) {
        val effectiveLang = language.resolveEffectiveLanguage()
        if (medicationName.isBlank()) {
            return@withContext generateRuleBasedMedicationDetails(medicationName, dosage, effectiveLang)
        }

        val prompt = when (effectiveLang) {
            AppLanguage.RU -> """
                Вы квалифицированный клинический фармаколог. Предоставьте подробный медицинский анализ следующего препарата НА РУССКОМ ЯЗЫКЕ:
                Название препарата: $medicationName
                ${if (dosage.isNotBlank()) "Дозировка: $dosage" else ""}

                Укажите следующие данные НА РУССКОМ ЯЗЫКЕ в формате JSON:
                - activeIngredients: действующее вещество и химический состав.
                - uses: показания к применению и назначение.
                - sideEffects: основные побочные эффекты и предостережения.
                - foodInteractions: взаимодействие с пищей, напитками и алкоголем.
                - importantInfo: особые указания и правила приема.
            """.trimIndent()
            AppLanguage.EN -> """
                You are a clinical pharmacist. Provide a detailed medical analysis for the following medication IN ENGLISH:
                Medication Name: $medicationName
                ${if (dosage.isNotBlank()) "Dosage: $dosage" else ""}

                Provide JSON with:
                - activeIngredients: active ingredient and chemical compound.
                - uses: main clinical indications and uses.
                - sideEffects: common side effects and warnings.
                - foodInteractions: food, beverage, and alcohol interactions.
                - importantInfo: key pharmaceutical instructions for patient.
            """.trimIndent()
            else -> """
                أنت استشاري صيدلة سريرية وطبيب خبير في علم الأدوية (Pharmacology).
                المطلوب تقديم تحليل طبي ودقيق وشامل للدواء التالي باللغة العربية:
                اسم الدواء: $medicationName
                ${if (dosage.isNotBlank()) "الجرعة المقترحة: $dosage" else ""}

                قم باستخراج وتحديد المعلومات التالية بتنسيق JSON باللغة العربية بدقة وموضوعية:
                - activeIngredients: التركيبة الأساسية والمادة الفعالة بالإنجليزية والعربية.
                - uses: دواعي واستخدامات الدواء الرئيسية بنقاط واضحة ومحددة.
                - sideEffects: أهم الأعراض الجانبية الشائعة والمحاذير الصحية التي يجب الانتباه لها.
                - foodInteractions: الأغذية والأطعمة والمشروبات أو الأعشاب التي يجب تجنبها تماماً أو الحذر الشديد عند تناولها.
                - importantInfo: معلومات صيدلانية مهمة وإرشادات الاستعمال السليمة.
            """.trimIndent()
        }

        try {
            val response = withTimeout(REQUEST_TIMEOUT_MS) {
                jsonExtractionModel.generateContent(prompt)
            }
            val jsonCleaned = cleanJsonOutput(response.text.orEmpty())
            val json = JSONObject(jsonCleaned)
            val details = MedicationAiDetails(
                activeIngredients = json.optString("activeIngredients", "").trim(),
                uses = json.optString("uses", "").trim(),
                sideEffects = json.optString("sideEffects", "").trim(),
                foodInteractions = json.optString("foodInteractions", "").trim(),
                importantInfo = json.optString("importantInfo", "").trim()
            )
            val fallback = generateRuleBasedMedicationDetails(medicationName, dosage, effectiveLang)
            details.copy(
                activeIngredients = details.activeIngredients.ifBlank { fallback.activeIngredients },
                uses = details.uses.ifBlank { fallback.uses },
                sideEffects = details.sideEffects.ifBlank { fallback.sideEffects },
                foodInteractions = details.foodInteractions.ifBlank { fallback.foodInteractions },
                importantInfo = details.importantInfo.ifBlank { fallback.importantInfo }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch AI medication details, using local pharmacology reference", e)
            generateRuleBasedMedicationDetails(medicationName, dosage, effectiveLang)
        }
    }

    fun generateRuleBasedMedicationDetails(
        medicationName: String,
        dosage: String = "",
        language: AppLanguage = AppLanguage.AR
    ): MedicationAiDetails {
        val effectiveLang = language.resolveEffectiveLanguage()
        val nameLower = medicationName.lowercase(Locale.getDefault())

        val isMetformin = nameLower.contains("metformin") || nameLower.contains("glucophage") ||
                nameLower.contains("سيدوفاج") || nameLower.contains("ميتفورمين") || nameLower.contains("منظم")
        val isStatin = nameLower.contains("atorvastatin") || nameLower.contains("lipitor") ||
                nameLower.contains("rosuvastatin") || nameLower.contains("crestor") ||
                nameLower.contains("ليبيتور") || nameLower.contains("كريستور") ||
                nameLower.contains("ستاتين") || nameLower.contains("كولسترول") || nameLower.contains("rosavel")
        val isAmlodipine = nameLower.contains("amlodipine") || nameLower.contains("norvasc") ||
                nameLower.contains("أملوديبين") || nameLower.contains("نورفاسك") || nameLower.contains("amicor")
        val isConcor = nameLower.contains("concor") || nameLower.contains("bisoprolol") ||
                nameLower.contains("كونكور") || nameLower.contains("بيسوبرولول")
        val isAspirin = nameLower.contains("aspirin") || nameLower.contains("أسبرين") || nameLower.contains("ecosprin")
        val isOmeprazole = nameLower.contains("nexium") || nameLower.contains("losec") ||
                nameLower.contains("omeprazole") || nameLower.contains("pantoprazole") ||
                nameLower.contains("نكسيوم") || nameLower.contains("أوميبرازول") || nameLower.contains("حموضة")

        return when (effectiveLang) {
            AppLanguage.RU -> when {
                isMetformin -> MedicationAiDetails(
                    activeIngredients = "Метформина гидрохлорид (Metformin Hydrochloride)",
                    uses = "Регулирование уровня сахара в крови, повышение чувствительности к инсулину, лечение диабета 2 типа.",
                    sideEffects = "Легкие желудочно-кишечные расстройства в начале приема, тошнота, металлический вкус во рту.",
                    foodInteractions = "Категорически исключить алкоголь (риск лактоацидоза), ограничить быстрые углеводы.",
                    importantInfo = "Принимать во время или сразу после еды. Не разжевывать и не делить таблетки XR."
                )
                isStatin -> MedicationAiDetails(
                    activeIngredients = "Аторвастатин / Розувастатин (Atorvastatin / Rosuvastatin)",
                    uses = "Снижение уровня холестерина ЛПНП и триглицеридов, защита сосудов от атеросклероза.",
                    sideEffects = "Легкая мышечная боль, головная боль, временное повышение ферментов печени.",
                    foodInteractions = "Категорически исключить грейпфрут и грейпфрутовый сок (повышает токсичность для мышц).",
                    importantInfo = "Принимать вечером перед сном. При сильной мышечной боли обратитесь к врачу."
                )
                isAmlodipine -> MedicationAiDetails(
                    activeIngredients = "Амлодипина безилат (Amlodipine Besylate)",
                    uses = "Лечение высокого артериального давления (гипертензии), профилактика приступов стенокардии.",
                    sideEffects = "Небольшие отеки лодыжек и стоп, легкое головокружение при вставании, приливы к лицу.",
                    foodInteractions = "Избегать большого количества грейпфрутового сока и соленых продуктов.",
                    importantInfo = "Принимать ежедневно в одно время. Избегать резких движений при вставании."
                )
                isConcor -> MedicationAiDetails(
                    activeIngredients = "Бисопролол (Bisoprolol Fumarate)",
                    uses = "Нормализация пульса и давления, защита миокарда при гипертензии и ишемической болезни.",
                    sideEffects = "Замедление пульса, прохладные конечности, быстрая утомляемость.",
                    foodInteractions = "Ограничить кофеин и энергетические напитки, стимулирующие пульс.",
                    importantInfo = "Не отменять резко без консультации врача. Принимать утром запивая водой."
                )
                isAspirin -> MedicationAiDetails(
                    activeIngredients = "Ацетилсалициловая кислота (Acetylsalicylic Acid 75-100 мг)",
                    uses = "Профилактика тромбообразования, снижает риск инфаркта и инсульта.",
                    sideEffects = "Раздражение желудка, изжога, повышенная кровоточивость.",
                    foodInteractions = "Принимать строго после еды. Избегать алкоголя.",
                    importantInfo = "Проглатывать таблетки целиком запивая стаканом воды."
                )
                isOmeprazole -> MedicationAiDetails(
                    activeIngredients = "Эзомепразол / Омепразол (Esomeprazole / Omeprazole)",
                    uses = "Лечение изжоги, гиперацидного гастрита и рефлюкс-эзофагита (ГЭРБ).",
                    sideEffects = "Головная боль, диарея или запор, при долгом приеме - снижение B12 и магния.",
                    foodInteractions = "Избегать острой, жирной пищи, газировки и шоколада.",
                    importantInfo = "Принимать утром натощак за 30 минут до завтрака."
                )
                else -> MedicationAiDetails(
                    activeIngredients = medicationName,
                    uses = "Принимать по назначению врача согласно медицинским рекомендациям.",
                    sideEffects = "Возможны индивидуальные реакции гиперчувствительности.",
                    foodInteractions = "Запивать водой. Избегать алкоголя и обильной жирной пищи при приеме.",
                    importantInfo = "Соблюдать назначенную дозировку и график приема."
                )
            }
            AppLanguage.EN -> when {
                isMetformin -> MedicationAiDetails(
                    activeIngredients = "Metformin Hydrochloride",
                    uses = "Regulates blood glucose levels, improves insulin sensitivity, treats type 2 diabetes.",
                    sideEffects = "Mild stomach upset, nausea, metallic taste in mouth initially.",
                    foodInteractions = "Avoid alcohol completely (risk of lactic acidosis), minimize sugary beverages.",
                    importantInfo = "Take with or right after meals. Do not crush or chew extended-release tablets."
                )
                isStatin -> MedicationAiDetails(
                    activeIngredients = "Atorvastatin / Rosuvastatin Calcium",
                    uses = "Lowers LDL cholesterol and triglycerides, protects arterial lining and heart health.",
                    sideEffects = "Mild muscle soreness, headache, temporary liver enzyme elevation.",
                    foodInteractions = "Avoid grapefruit and grapefruit juice completely (increases muscle toxicity risk).",
                    importantInfo = "Preferably take in the evening or bedtime. Report severe muscle pain to doctor."
                )
                isAmlodipine -> MedicationAiDetails(
                    activeIngredients = "Amlodipine Besylate",
                    uses = "Treats high blood pressure (hypertension) and prevents angina chest pain.",
                    sideEffects = "Mild ankle swelling, slight dizziness when standing, facial flushing.",
                    foodInteractions = "Avoid excessive grapefruit juice and high-sodium foods.",
                    importantInfo = "Take at the same time daily. Avoid sudden standing from sitting position."
                )
                isConcor -> MedicationAiDetails(
                    activeIngredients = "Bisoprolol Fumarate",
                    uses = "Regulates heart rate and blood pressure, protects heart muscle.",
                    sideEffects = "Slower heart rate, cold extremities, general tiredness.",
                    foodInteractions = "Limit caffeine and energy drinks that elevate heart rate.",
                    importantInfo = "Do not stop abruptly without doctor supervision. Take in the morning with water."
                )
                isAspirin -> MedicationAiDetails(
                    activeIngredients = "Acetylsalicylic Acid (75-100 mg)",
                    uses = "Prevents blood clots, reduces risk of heart attack and stroke.",
                    sideEffects = "Stomach irritation, heartburn, increased bleeding tendency.",
                    foodInteractions = "Take strictly after meals. Avoid alcohol to protect gastric lining.",
                    importantInfo = "Swallow enteric-coated tablets whole with water."
                )
                isOmeprazole -> MedicationAiDetails(
                    activeIngredients = "Esomeprazole / Omeprazole",
                    uses = "Treats acid reflux (GERD), heartburn, and protects stomach lining.",
                    sideEffects = "Headache, mild digestive discomfort, B12/magnesium reduction on long term use.",
                    foodInteractions = "Avoid spicy, greasy foods, carbonated drinks, and chocolate.",
                    importantInfo = "Take in the morning on an empty stomach 30 minutes before breakfast."
                )
                else -> MedicationAiDetails(
                    activeIngredients = medicationName,
                    uses = "Take as prescribed according to healthcare provider instructions.",
                    sideEffects = "Individual hypersensitivity or mild digestive upset may occur.",
                    foodInteractions = "Take with water. Avoid alcohol and greasy foods.",
                    importantInfo = "Follow prescribed dosage and timing strictly."
                )
            }
            else -> when {
                isMetformin -> MedicationAiDetails(
                    activeIngredients = "Metformin Hydrochloride (ميتفورمين هيدروكلوريد)",
                    uses = "تنظيم مستوى سكر الدم، تحسين استجابة وحساسية خلايا الجسم للإنسولين، علاج السكري من النوع الثاني وتكيس المبايض.",
                    sideEffects = "اضطرابات معوية خفيفة في البداية، غثيان أو إسهال عابر، طعم معدني بالفم.",
                    foodInteractions = "تجنب المشروبات الكحولية تماماً (لتفادي خطر الحماض اللبني)، تجنب السكريات السريعة والعصائر المحلاة، وتجنب تفويت الوجبات لمنع هبوط السكر المفاجئ.",
                    importantInfo = "يؤخذ وسط وجبة الطعام أو بعدها مباشرة للحد من اضطراب المعدة. لا تقسم أو تسحق الأقراص ممتدة المفعول (XR)."
                )
                isStatin -> MedicationAiDetails(
                    activeIngredients = "Atorvastatin / Rosuvastatin Calcium (مركبات الستاتين الخافضة للكولسترول)",
                    uses = "خفض الكولسترول الضار (LDL) والدهون الثلاثية، رفع الكولسترول الجيد، حماية الشرايين والوقاية من الجلطات القلبية.",
                    sideEffects = "آلام أو تشنجات عضلية خفيفة، صداع، اضطراب هضمي، ارتفاع طفيف مؤقت في إنزيمات الكبد.",
                    foodInteractions = "تجنب فاكهة وعصير الجريب فروت (Grapefruit) تماماً لأنه يضاعف تركيز الدواء في الدم ويزيد سمية العضلات، تجنب الأطعمة الغنية بالدهون المهدرجة والمقالي.",
                    importantInfo = "يفضل تناوله مساءً أو قبل النوم لأن الكبد ينتج معظم الكولسترول ليلاً. أخبر طبيبك فوراً إذا شعرت بألم عضلي حاد غير معتاد."
                )
                isAmlodipine -> MedicationAiDetails(
                    activeIngredients = "Amlodipine Besylate (أملوديبين بيسيلات - محصرات قنوات الكالسيوم)",
                    uses = "علاج ارتفاع ضغط الدم، توسيع الشرايين، والوقاية من الذبحة الصدرية ونوبات ألم الصدر.",
                    sideEffects = "تورم طفيف في الكاحلين أو القدمين، دوار خفيف عند الوقوف، احمرار الوجه، إرهاق عابر.",
                    foodInteractions = "تجنب تناول كميات كبيرة من عصير الجريب فروت، تجنب الأغذية عالية الصوديوم والمخللات لأنها تعاكس مفعول خفض الضغط.",
                    importantInfo = "يؤخذ في نفس التوقيت يومياً مع أو بدون طعام. تجنب الوقوف المفاجئ من وضعية الجلوس لتجنب الدوار."
                )
                isConcor -> MedicationAiDetails(
                    activeIngredients = "Bisoprolol Fumarate (بيسوبرولول فومارات - حاصرات بيتا 1 الانتقائية)",
                    uses = "تنظيم نبضات وتسارع القلب، خفض ضغط الدم، حماية عضلة القلب بعد الأزمات وقصور القلب.",
                    sideEffects = "بطء في معدل نبضات القلب، برودة الأطراف، إجهاد عام، دوار خفيف في الصباح.",
                    foodInteractions = "تجنب الإفراط في المنبهات والكافيين ومشروبات الطاقة لأنها ترفع ضربات القلب وتعاكس مفعول الدواء، تجنب الأطعمة شديدة الملوحة.",
                    importantInfo = "لا توقف تناول الدواء فجأة دون إشراف الطبيب لتجنب الارتفاع الارتدادي في النبض والضغط. يؤخذ صباحاً مع كوب ماء."
                )
                else -> {
                    val cleanMedName = medicationName.ifBlank { "الدواء المحدد" }
                    MedicationAiDetails(
                        activeIngredients = "المادة الفعالة الدوائية والتركيبة الكيميائية المصنفة لمستحضر $cleanMedName",
                        uses = "علاج ومتابعة الحالة المرضية الموصوف لها مستحضر $cleanMedName وتحقيق التوازن الصحي وفق الخطة الطبية.",
                        sideEffects = "الأعراض الجانبية الشائعة: اضطراب هضمي طفيف، دوار خفيف أو صداع عابر. في حال ظهور طفح جلدي أو حساسية يجب مراجعة الطبيب فوراً.",
                        foodInteractions = "تجنب المشروبات الكحولية والغازية، توخ الحذر مع عصير الجريب فروت، واشرب كوباً كاملاً من الماء مع الجرعة.",
                        importantInfo = "احرص على أخذ الجرعة في موعدها المحدد دون تأخير، ولا توقف العلاج فجأة دون استشارة الطبيب المعالج أو الصيدلي."
                    )
                }
            }
        }
    }

    internal fun generateRuleBasedMedicalConsultation(
        labTests: List<LabTestEntity>,
        bpRecords: List<BloodPressureEntity>,
        sugarRecords: List<BloodSugarEntity>,
        latestWeight: WeightRecordEntity?,
        profile: UserProfileEntity?,
        medications: List<MedicationEntity>,
        language: AppLanguage,
        currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ): AiConsultationEntity {
        val highTests = labTests.filter {
            val s = it.status.lowercase()
            s.contains("زائد") || s.contains("مرتفع") || s.contains("high") || s.contains("повыш") || s == "h"
        }
        val lowTests = labTests.filter {
            val s = it.status.lowercase()
            s.contains("نقص") || s.contains("منخفض") || s.contains("low") || s.contains("пониж") || s == "l"
        }
        val normalCount = (labTests.size - highTests.size - lowTests.size).coerceAtLeast(0)

        val latestBp = bpRecords.firstOrNull()
        val latestSugar = sugarRecords.firstOrNull()
        val activeMeds = medications.filter { it.isActive }

        val hasLipidIssue = highTests.any {
            val n = it.testName.lowercase()
            n.contains("chol") || n.contains("ldl") || n.contains("trig") || n.contains("دهون") || n.contains("كولسترول")
        }
        val hasSugarIssue = highTests.any {
            val n = it.testName.lowercase()
            n.contains("gluc") || n.contains("hba1c") || n.contains("سكر") || n.contains("تراكمي")
        } || (latestSugar != null && latestSugar.value >= 130)
        val hasBpIssue = latestBp != null && (latestBp.systolic >= 135 || latestBp.diastolic >= 85)
        val hasVitaminOrIronDeficiency = lowTests.any {
            val n = it.testName.lowercase()
            n.contains("vit") || n.contains("b12") || n.contains("iron") || n.contains("ferritin") ||
                n.contains("hgb") || n.contains("hemoglobin") || n.contains("فيتامين") || n.contains("حديد") || n.contains("دم")
        }

        return when (language) {
            AppLanguage.EN -> {
                val summary = buildString {
                    if (profile != null && profile.name.isNotBlank()) {
                        append("Patient: ${profile.name} (${profile.age} yrs). ")
                    }
                    append("Analyzed ${labTests.size} lab tests ($normalCount within normal range")
                    if (highTests.isNotEmpty()) {
                        append(", ${highTests.size} elevated: ${highTests.take(5).joinToString { "${it.testName} (${it.value} ${it.unit})" }}")
                    }
                    if (lowTests.isNotEmpty()) {
                        append(", ${lowTests.size} low: ${lowTests.take(5).joinToString { "${it.testName} (${it.value} ${it.unit})" }}")
                    }
                    append("). ")
                    if (latestBp != null) {
                        append("Latest blood pressure: ${latestBp.systolic}/${latestBp.diastolic} mmHg (pulse ${latestBp.pulse} bpm, ${latestBp.category}). ")
                    }
                    if (latestSugar != null) {
                        append("Latest blood glucose (${latestSugar.type}): ${latestSugar.value} mg/dL (${latestSugar.status}). ")
                    }
                    if (latestWeight != null) {
                        append("Current weight: ${latestWeight.weightKg} kg (BMI ${latestWeight.bmi} - ${latestWeight.bmiCategory}). ")
                    }
                    if (activeMeds.isNotEmpty()) {
                        append("${activeMeds.size} active scheduled medications reviewed for food and timing interactions.")
                    }
                }

                val dietAdvice = buildString {
                    append("Follow a balanced Mediterranean-style nutrition plan with 3 main meals and 2 light healthy snacks. ")
                    if (hasSugarIssue) append("Prioritize low-glycemic complex carbohydrates (oats, quinoa, brown rice) to stabilize glucose and insulin levels. ")
                    if (hasLipidIssue) append("Increase soluble fiber and omega-3 intake while minimizing saturated and trans fats to reduce LDL and triglycerides. ")
                    if (hasBpIssue) append("Follow DASH diet guidelines by keeping daily sodium intake low and increasing potassium-rich vegetables. ")
                    if (hasVitaminOrIronDeficiency) append("Include iron-rich and vitamin-fortified whole foods alongside vitamin C sources to boost absorption.")
                }

                val foodsToEat = buildString {
                    appendLine("• Fatty fish (salmon, sardines, tuna) twice weekly for Omega-3 and Vitamin D")
                    appendLine("• Whole oats, flaxseeds, and chia seeds to support healthy cholesterol and blood sugar")
                    appendLine("• Dark leafy greens (spinach, arugula, broccoli) rich in minerals and antioxidants")
                    appendLine("• Probiotic plain yogurt, boiled eggs, and lean poultry")
                    append("• Raw unsalted nuts (walnuts, almonds) and extra-virgin olive oil")
                }

                val foodsToAvoid = buildString {
                    appendLine("• Refined sugars, sweetened juices, and carbonated beverages")
                    appendLine("• Hydrogenated oils, deep-fried foods, and processed meats")
                    appendLine("• High-sodium pickles, canned salty foods, and excess caffeine")
                    if (activeMeds.any { it.name.lowercase().let { n -> n.contains("statin") || n.contains("lipitor") || n.contains("crestor") || n.contains("amlodipin") || n.contains("norvasc") } }) {
                        append("• Grapefruit and grapefruit juice (interferes with cholesterol and blood pressure medications)")
                    } else {
                        append("• Grapefruit juice and alcohol alongside scheduled medications")
                    }
                }

                val lifestyleAdvice = buildString {
                    appendLine("• Brisk walking or moderate aerobic activity for 30 minutes, 5 days a week.")
                    appendLine("• Drink 2 to 2.5 liters of water distributed throughout the day.")
                    appendLine("• Maintain 7–8 hours of regular nighttime sleep to support metabolic and hormone balance.")
                    append("• Monitor blood pressure and blood glucose at consistent times and log readings in the app.")
                }

                val medicationAdvice = if (activeMeds.isEmpty()) {
                    "No active medications are currently logged. Add your prescriptions in the Daily Tracking tab to check food interactions and optimal timing."
                } else {
                    activeMeds.joinToString("\n\n") { med ->
                        val fallback = generateRuleBasedMedicationDetails(med.name, med.dosage, AppLanguage.EN)
                        val foodNote = med.foodInteractions.ifBlank { fallback.foodInteractions }
                        val infoNote = med.importantInfo.ifBlank { fallback.importantInfo }
                        "• ${med.name} (${med.dosage} - ${med.time}, ${med.instructions}): $infoNote Food interaction: $foodNote"
                    }
                }

                AiConsultationEntity(
                    title = "Comprehensive Clinical & Nutrition Analysis",
                    summary = summary,
                    dietAdvice = dietAdvice,
                    foodsToEat = foodsToEat,
                    foodsToAvoid = foodsToAvoid,
                    lifestyleAdvice = lifestyleAdvice,
                    medicationAdvice = medicationAdvice,
                    fullResponse = summary,
                    date = currentDate
                )
            }

            AppLanguage.RU -> {
                val summary = buildString {
                    if (profile != null && profile.name.isNotBlank()) {
                        append("Пациент: ${profile.name} (${profile.age} лет). ")
                    }
                    append("Проанализировано анализов: ${labTests.size} (в норме: $normalCount")
                    if (highTests.isNotEmpty()) {
                        append(", повышены (${highTests.size}): ${highTests.take(5).joinToString { "${it.testName} (${it.value} ${it.unit})" }}")
                    }
                    if (lowTests.isNotEmpty()) {
                        append(", понижены (${lowTests.size}): ${lowTests.take(5).joinToString { "${it.testName} (${it.value} ${it.unit})" }}")
                    }
                    append("). ")
                    if (latestBp != null) {
                        append("Последнее АД: ${latestBp.systolic}/${latestBp.diastolic} мм рт. ст. (пульс ${latestBp.pulse}). ")
                    }
                    if (latestSugar != null) {
                        append("Последний сахар крови (${latestSugar.type}): ${latestSugar.value} мг/дл. ")
                    }
                    if (latestWeight != null) {
                        append("Текущий вес: ${latestWeight.weightKg} кг (ИМТ ${latestWeight.bmi}). ")
                    }
                    if (activeMeds.isNotEmpty()) {
                        append("Учтено активных препаратов: ${activeMeds.size}.")
                    }
                }

                val medicationAdvice = if (activeMeds.isEmpty()) {
                    "Активные препараты не указаны. Добавьте принимаемые лекарства для проверки пищевых взаимодействий."
                } else {
                    activeMeds.joinToString("\n\n") { med ->
                        val fallback = generateRuleBasedMedicationDetails(med.name, med.dosage, AppLanguage.RU)
                        val foodNote = med.foodInteractions.ifBlank { fallback.foodInteractions }
                        val infoNote = med.importantInfo.ifBlank { fallback.importantInfo }
                        "• ${med.name} (${med.dosage} - ${med.time}, ${med.instructions}): $infoNote Взаимодействие с пищей: $foodNote"
                    }
                }

                AiConsultationEntity(
                    title = "Комплексный медицинский и диетологический отчет",
                    summary = summary,
                    dietAdvice = "Сбалансированное дробное питание (3 основных приема пищи и 2 перекуса) с акцентом на сложные углеводы, клетчатку и чистый белок.",
                    foodsToEat = "• Жирная морская рыба (лосось, сардины) 2 раза в неделю\n• Овсянка, семена льна и чиа для снижения ЛПНП\n• Листовая зелень (шпинат, брокколи)\n• Натуральный йогурт и отварные яйца\n• Несоленые орехи (грецкий орех, миндаль)",
                    foodsToAvoid = "• Простые сахара, сладкие напитки и газировка\n• Трансжиры, жареная пища и фастфуд\n• Соленья и избыток соли\n• Грейпфрутовый сок при приеме статинов и препаратов от давления",
                    lifestyleAdvice = "• Быстрая ходьба не менее 30 минут 5 раз в неделю.\n• Питьевой режим: 2–2,5 литра чистой воды в день.\n• Полноценный ночной сон 7–8 часов.\n• Регулярный контроль АД и глюкозы крови.",
                    medicationAdvice = medicationAdvice,
                    fullResponse = summary,
                    date = currentDate
                )
            }

            else -> {
                val summary = buildString {
                    if (profile != null && profile.name.isNotBlank()) {
                        append("المراجع: ${profile.name} (${profile.age} سنة")
                        if (profile.chronicDiseases.isNotBlank()) append(" - ${profile.chronicDiseases}")
                        append("). ")
                    }
                    if (labTests.isNotEmpty()) {
                        append("تم تحليل ${labTests.size} فحصاً مخبرياً ($normalCount ضمن النطاق الطبيعي")
                        if (highTests.isNotEmpty()) {
                            append("، وارتفاع في ${highTests.size} فحوصات: ${highTests.take(6).joinToString("، ") { "${it.testName} (${it.value} ${it.unit})" }}")
                        }
                        if (lowTests.isNotEmpty()) {
                            append("، وانخفاض في ${lowTests.size} فحوصات: ${lowTests.take(6).joinToString("، ") { "${it.testName} (${it.value} ${it.unit})" }}")
                        }
                        append("). ")
                    } else {
                        append("تحليل شامل للمؤشرات الحيوية والأدوية المسجلة في ملفك الصحي. ")
                    }
                    if (latestBp != null) {
                        append("آخر قراءة لضغط الدم: ${latestBp.systolic}/${latestBp.diastolic} mmHg (النبض ${latestBp.pulse} نبضة/دقيقة - ${latestBp.category}). ")
                    }
                    if (latestSugar != null) {
                        append("آخر قراءة لسكر الدم (${latestSugar.type}): ${latestSugar.value} mg/dL (${latestSugar.status}). ")
                    }
                    if (latestWeight != null) {
                        append("الوزن الحالي: ${latestWeight.weightKg} كجم (مؤشر الكتلة ${latestWeight.bmi} - ${latestWeight.bmiCategory}). ")
                    }
                    if (activeMeds.isNotEmpty()) {
                        append("تمت مراجعة ${activeMeds.size} أدوية مجدولة وفحص تداخلاتها الغذائية وتوقيتها العلاجي.")
                    }
                }

                val dietAdvice = buildString {
                    append("اتباع حمية غذائية متوازنة تعتمد على الكربوهيدرات المعقدة (الشوفان، الأرز البني، البرغل)، والبروتين الصافي، وتوزيع الوجبات على 3 وجبات رئيسية ووجبتين خفيفتين. ")
                    if (hasSugarIssue) {
                        append("نظراً لمؤشرات سكر الدم، يُنصح بتقليل النشويات البيضاء والبدء بطبق السلطة الخضراء والألياف قبل الوجبة لتبطيء امتصاص الجلوكوز. ")
                    }
                    if (hasLipidIssue) {
                        append("لتحسين صورة الدهون والكولسترول، يُوصى بالاعتماد على زيت الزيتون البكر والألياف الذائبة وتجنب الدهون المشبعة. ")
                    }
                    if (hasBpIssue) {
                        append("لدعم استقرار ضغط الدم، يُنصح بتقليل ملح الطعام المضاف والاعتماد على الأعشاب الطبيعية والليمون للتتبيل مع زيادة الأغذية الغنية بالبوتاسيوم والمغنيسيوم. ")
                    }
                    if (hasVitaminOrIronDeficiency) {
                        append("لتعويض النقص في الفيتامينات أو المعادن، يُنصح بدمج مصادر الحديد والفيتامينات الطبيعية مع مصادر فيتامين C (كالليمون والفلفل الرومي) لرفع كفاءة الامتصاص.")
                    }
                }

                val foodsToEat = buildString {
                    appendLine("• الأسماك الدهنية (السلمون، السردين، التونة) مرتين أسبوعياً لدعم الأوميغا 3 وفيتامين د")
                    appendLine("• الشوفان الكامل وبذور الكتان والشيا للمساعدة في ضبط السكر والكولسترول الضار LDL")
                    appendLine("• الخضروات الورقية الخضراء (السبانخ، الجرجير، البروكلي، البقدونس) الغنية بمضادات الأكسدة")
                    appendLine("• الزبادي الطبيعي قليل الدسم الغني بالبروبيوتيك والبيض المسلوق والبقوليات المعتدلة")
                    append("• حفنة يومية من المكسرات النيئة غير المملحة (الجوز واللوز) وزيت الزيتون البكر على البارد")
                }

                val foodsToAvoid = buildString {
                    appendLine("• السكريات البسيطة، الحلويات الشرقية، العصائر المحلاة، والمشروبات الغازية")
                    appendLine("• الزيوت المهدرجة، الأطعمة المقلية، الوجبات السريعة، واللحوم المصنعة")
                    appendLine("• المخللات، الأجبان شديدة الملوحة، والأغذية المعلبة عالية الصوديوم")
                    append("• فاكهة وعصير الجريب فروت (لتعارضه مع أدوية الكولسترول والضغط) والإفراط في الكافيين")
                }

                val lifestyleAdvice = buildString {
                    appendLine("• ممارسة المشي السريع أو النشاط البدني المعتدل لمدة 30 دقيقة يومياً (5 أيام في الأسبوع).")
                    appendLine("• شرب 8 إلى 10 أكواب من الماء موزعة بانتظام خلال اليوم لدعم وظائف الكلى والدورة الدموية.")
                    appendLine("• الحرص على النوم الليلي المنتظم لمدة 7–8 ساعات وتجنب السهر والتوتر العصبي.")
                    append("• قياس ضغط الدم وسكر الدم في أوقات ثابتة وتسجيلها في التطبيق لمتابعة المنحنى الصحي.")
                }

                val medicationAdvice = if (activeMeds.isEmpty()) {
                    "لا توجد أدوية نشطة مسجلة حالياً. يُمكنك إضافة أدويتك في قسم المتابعة اليومية لفحص تداخلاتها مع الطعام ومواعيدها المثلى."
                } else {
                    buildString {
                        activeMeds.forEachIndexed { idx, med ->
                            val fallback = generateRuleBasedMedicationDetails(med.name, med.dosage, AppLanguage.AR)
                            val foodNote = med.foodInteractions.ifBlank { fallback.foodInteractions }
                            val infoNote = med.importantInfo.ifBlank { fallback.importantInfo }
                            append("• ${med.name} (${med.dosage} - ${med.time}، ${med.instructions}): $infoNote\n  التداخل الغذائي: $foodNote")
                            if (idx < activeMeds.lastIndex) append("\n\n")
                        }
                        append("\n\nتنبيه طبي: التزم بمواعيد الأدوية الموصوفة ولا تقم بتعديل الجرعة أو إيقاف أي دواء دون مراجعة طبيبك المعالج.")
                    }
                }

                AiConsultationEntity(
                    title = "التقرير الطبي والغذائي الشامل",
                    summary = summary,
                    dietAdvice = dietAdvice,
                    foodsToEat = foodsToEat,
                    foodsToAvoid = foodsToAvoid,
                    lifestyleAdvice = lifestyleAdvice,
                    medicationAdvice = medicationAdvice,
                    fullResponse = summary,
                    date = currentDate
                )
            }
        }
    }

    internal fun generateRuleBasedChatResponse(
        userMessage: String,
        medicalContext: String,
        language: AppLanguage
    ): String {
        val q = userMessage.lowercase()
        return when (language) {
            AppLanguage.EN -> buildString {
                appendLine("Based on your medical profile and question:")
                when {
                    q.contains("sugar") || q.contains("glucose") || q.contains("diabet") -> {
                        appendLine("• To keep blood glucose stable, focus on high-fiber foods (oats, leafy vegetables, legumes) and lean proteins while avoiding refined sugars and white bread.")
                        appendLine("• Take diabetes medications (such as Metformin) with or immediately after meals to minimize stomach upset, and walk for 20–30 minutes after your main meal.")
                    }
                    q.contains("pressure") || q.contains("bp") || q.contains("salt") -> {
                        appendLine("• For healthy blood pressure, reduce daily salt intake, avoid pickles and canned foods, and increase potassium-rich foods like spinach, avocados, and yogurt.")
                        appendLine("• Take blood pressure medications at the same time every day and avoid sudden posture changes.")
                    }
                    q.contains("cholesterol") || q.contains("lipid") || q.contains("triglyceride") || q.contains("fat") -> {
                        appendLine("• To lower LDL cholesterol and triglycerides, eat fatty fish twice weekly, add oats and flaxseeds, and replace fried foods with olive oil.")
                        appendLine("• Statin medications work best when taken in the evening; avoid grapefruit juice completely.")
                    }
                    else -> {
                        appendLine("• Maintain balanced meals combining vegetables, lean protein, and complex carbohydrates, and stay hydrated with 2–2.5L of water daily.")
                        appendLine("• Take your scheduled medications consistently as prescribed and avoid grapefruit juice, excess salt, and refined sugars.")
                    }
                }
                if (medicalContext.isNotBlank()) {
                    append("\nNote: This guidance is tailored to the records logged in your health profile. Always consult your physician before changing any medication dose.")
                }
            }

            AppLanguage.RU -> buildString {
                appendLine("На основе вашего медицинского профиля и вопроса:")
                appendLine("• Придерживайтесь сбалансированного питания с овощами, рыбой, цельнозерновыми продуктами и достаточным потреблением воды (2–2,5 л в день).")
                appendLine("• Ограничьте простые сахара, соль и жареную пищу, принимайте назначенные препараты строго по расписанию и избегайте грейпфрутового сока.")
                append("Примечание: Рекомендация носит справочный характер и не заменяет очную консультацию врача.")
            }

            else -> buildString {
                appendLine("بناءً على بياناتك الصحية المسجلة وسؤالك:")
                when {
                    q.contains("سكر") || q.contains("جلوكوز") || q.contains("تراكمي") || q.contains("ميتفورمين") || q.contains("حلا") -> {
                        appendLine("• لضبط مستوى سكر الدم: ابدأ وجبتك بطبق السلطة الخضراء والألياف، واعتمد على النشويات المعقدة (الشوفان، الخبز الأسمر، البرغل) مع تقليل السكريات والعصائر والخبز الأبيض.")
                        appendLine("• يُنصح بالمشي لمدة 20–30 دقيقة بعد الوجبة الرئيسية، وتناول أدوية السكر في مواعيدها المحددة مع الوجبات لتجنب اضطراب المعدة أو هبوط السكر.")
                    }
                    q.contains("ضغط") || q.contains("ملح") || q.contains("نبض") || q.contains("صداع") -> {
                        appendLine("• للمحافظة على استقرار ضغط الدم: قلل الملح والمخللات والأطعمة المعلبة والمنبهات، وأكثر من الخضروات الورقية والزبادي والمشروبات الهادئة كالكركديه المعتدل.")
                        appendLine("• التزم بتناول دواء الضغط في نفس الموعد يومياً، وقس الضغط بعد الجلوس بهدوء لمدة 5 دقائق.")
                    }
                    q.contains("كولسترول") || q.contains("دهون") || q.contains("ثلاثية") || q.contains("ستاتين") -> {
                        appendLine("• لخفض الكولسترول الضار والدهون الثلاثية: تناول الأسماك المشوية مرتين أسبوعياً، وأضف الشوفان وبذور الكتان وزيت الزيتون البكر لنظامك الغذائي مع تجنب المقالي والدهون المهدرجة.")
                        appendLine("• يُفضل تناول أدوية الكولسترول مساءً مع تجنب عصير الجريب فروت تماماً.")
                    }
                    q.contains("دواء") || q.contains("أدوي") || q.contains("جرع") || q.contains("تعارض") -> {
                        appendLine("• احرص على أخذ كل دواء في موعده المحدد مع كوب كامل من الماء، وافصل بين مكملات الحديد/الكالسيوم وأدوية الغدة أو المضادات الحيوية بساعتين على الأقل.")
                        appendLine("• تجنب عصير الجريب فروت والمشروبات الغازية والمنبهات وقت تناول الأدوية.")
                    }
                    q.contains("أكل") || q.contains("غذاء") || q.contains("فطور") || q.contains("عشاء") || q.contains("غداء") || q.contains("نظام") -> {
                        appendLine("• الفطور المقترح: شوفان بالحليب قليل الدسم أو بيضة مسلوقة مع خضروات ورقية وخبز حبوب كاملة.")
                        appendLine("• الغداء المقترح: سمك مشوي أو صدر دجاج مع سلطة خضراء بزيت الزيتون وكمية معتدلة من الأرز البني أو البرغل.")
                        appendLine("• العشاء المقترح: زبادي طبيعي مع حفنة مكسرات نيئة غير مملحة أو شوربة خضار خفيفة قبل النوم بـ 3 ساعات.")
                    }
                    else -> {
                        appendLine("• يُنصح باتباع نظام غذائي متوازن غني بالخضروات الطازجة، البروتين الصافي (الأسماك والدواجن)، والحبوب الكاملة، مع شرب 8–10 أكواب ماء يومياً.")
                        appendLine("• التزم بمواعيد أدويتك المجدولة، ومارس المشي المعتدل لمدة 30 دقيقة يومياً، وتجنب الأطعمة عالية الملح والسكريات المكررة.")
                    }
                }
                append("\nملاحظة إرشادية: هذه التوصيات مبنية على سجل فحوصاتك وأدويتك في التطبيق، ولا تغني عن مراجعة طبيبك المختص عند الحاجة.")
            }
        }
    }

    fun chatWithAdvisorStream(
        userMessage: String,
        medicalContext: String,
        language: AppLanguage = AppLanguage.AR
    ): Flow<String> = flow {
        val effective = language.resolveEffectiveLanguage()
        val langInstruction = when (effective) {
            AppLanguage.EN -> "Answer the patient's question in English with a professional, clear, reassuring, and scientifically sound tone. Focus on diet, lifestyle, foods to eat/avoid, and safety advice."
            AppLanguage.RU -> "Ответьте на вопрос пациента на русском языке в профессиональном, четком, научно обоснованном и заботливом тоне. Сосредоточьтесь на диете, образе жизни и рекомендациях."
            else -> "أجب على سؤال المريض باللغة العربية بأسلوب علمي، دقيق، مفهوم ومطمئن. ركز على خيارات الغذاء، أسلوب الحياة، الأطعمة الموصى بها والممنوعة، والنصائح الوقائية."
        }
        val prompt = """
            $langInstruction
            Patient health data context:
            $medicalContext

            User question: $userMessage
        """.trimIndent()

        val accumulated = StringBuilder()
        try {
            withTimeout(REQUEST_TIMEOUT_MS) {
                chatModel.generateContentStream(prompt).collect { chunk ->
                    val text = chunk.text.orEmpty()
                    if (text.isNotEmpty()) {
                        accumulated.append(text)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                emit(generateRuleBasedChatResponse(userMessage, medicalContext, effective))
            }
        } catch (e: CancellationException) {
            if (e is TimeoutCancellationException) {
                Log.w(TAG, "Chat stream timed out; emitting local clinical response", e)
                if (accumulated.isBlank()) {
                    emit(generateRuleBasedChatResponse(userMessage, medicalContext, effective))
                }
            } else {
                throw e
            }
        } catch (e: Exception) {
            Log.w(TAG, "Chat stream unavailable; emitting local clinical response", e)
            if (accumulated.isBlank()) {
                emit(generateRuleBasedChatResponse(userMessage, medicalContext, effective))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun chatWithAdvisor(
        userMessage: String,
        medicalContext: String,
        language: AppLanguage = AppLanguage.AR
    ): Result<String> = withContext(Dispatchers.IO) {
        val effective = language.resolveEffectiveLanguage()
        val langInstruction = when (effective) {
            AppLanguage.EN -> "Answer the patient's question in English with a professional, clear, reassuring, and scientifically sound tone. Focus on diet, lifestyle, foods to eat/avoid, and safety advice."
            AppLanguage.RU -> "Ответьте на вопрос пациента на русском языке в профессиональном, четком, научно обоснованном и заботливом тоне. Сосредоточьтесь на диете, образе жизни и рекомендациях."
            else -> "أجب على سؤال المريض باللغة العربية بأسلوب علمي، دقيق، مفهوم ومطمئن. ركز على خيارات الغذاء، أسلوب الحياة، الأطعمة الموصى بها والممنوعة، والنصائح الوقائية."
        }
        val prompt = """
            $langInstruction
            Patient health data context:
            $medicalContext

            User question: $userMessage
        """.trimIndent()

        try {
            val response = withTimeout(REQUEST_TIMEOUT_MS) {
                chatModel.generateContent(prompt)
            }
            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.success(generateRuleBasedChatResponse(userMessage, medicalContext, effective))
            }
        } catch (e: TimeoutCancellationException) {
            Log.w(TAG, "Chat timed out; returning local clinical response", e)
            Result.success(generateRuleBasedChatResponse(userMessage, medicalContext, effective))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Chat failed; returning local clinical response", e)
            Result.success(generateRuleBasedChatResponse(userMessage, medicalContext, effective))
        }
    }

    private suspend fun executeGeminiMultimodal(
        promptText: String,
        fileBase64: String?,
        mimeType: String,
        pageImagesBase64: List<String> = emptyList()
    ): String {
        var totalInlineBytes = 0L
        val multimodalContent = content {
            text(promptText)
            if (pageImagesBase64.isNotEmpty()) {
                pageImagesBase64.forEach { imgB64 ->
                    val bytes = Base64.decode(imgB64, Base64.DEFAULT)
                    totalInlineBytes += bytes.size
                    if (totalInlineBytes > MAX_INLINE_MEDIA_BYTES) {
                        throw IllegalArgumentException("حجم الصور المرفقة يتجاوز الحد المسموح (20 ميجابايت).")
                    }
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        image(bitmap)
                    } else {
                        inlineData(bytes, "image/jpeg")
                    }
                }
            } else if (!fileBase64.isNullOrEmpty()) {
                val bytes = Base64.decode(fileBase64, Base64.DEFAULT)
                totalInlineBytes += bytes.size
                if (totalInlineBytes > MAX_INLINE_MEDIA_BYTES) {
                    throw IllegalArgumentException("حجم الملف المرفق يتجاوز الحد المسموح (20 ميجابايت).")
                }
                if (mimeType.startsWith("image/")) {
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        image(bitmap)
                    } else {
                        inlineData(bytes, mimeType)
                    }
                } else {
                    inlineData(bytes, mimeType)
                }
            }
        }

        val response = withTimeout(MULTIMODAL_TIMEOUT_MS) {
            jsonExtractionModel.generateContent(multimodalContent)
        }
        return response.text ?: throw IllegalStateException("Model returned empty response.")
    }

    private fun cleanJsonOutput(raw: String): String {
        val str = raw.trim()
        val arrayStart = str.indexOf('[')
        val arrayEnd = str.lastIndexOf(']')
        if (arrayStart != -1 && arrayEnd > arrayStart) {
            return str.substring(arrayStart, arrayEnd + 1).trim()
        }
        val objStart = str.indexOf('{')
        val objEnd = str.lastIndexOf('}')
        if (objStart != -1 && objEnd > objStart) {
            return str.substring(objStart, objEnd + 1).trim()
        }
        return str
    }

    private fun localizedTimeoutError(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> "Request timed out. Please try again."
        AppLanguage.RU -> "Превышено время ожидания. Попробуйте снова."
        else -> "انتهت مهلة الاستجابة. يُرجى المحاولة مجدداً."
    }

    private fun localizedEmptyResponseError(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> "Empty response received. Please try again."
        AppLanguage.RU -> "Получен пустой ответ. Попробуйте снова."
        else -> "لم يتم استلام استجابة. يُرجى المحاولة مجدداً."
    }

    private fun localizedGenericAiError(lang: AppLanguage): String = when (lang) {
        AppLanguage.EN -> "Unable to complete the analysis right now. Please check your internet connection and try again."
        AppLanguage.RU -> "Не удалось выполнить анализ сейчас. Проверьте подключение к интернету и повторите попытку."
        else -> "تعذر إتمام التحليل الطبي حالياً. تحقق من اتصال الإنترنت وحاول مجدداً."
    }
}
