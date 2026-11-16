package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.AiConsultationEntity
import com.example.data.model.BloodPressureEntity
import com.example.data.model.BloodSugarEntity
import com.example.data.model.LabTestEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightRecordEntity
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.getAppStrings
import com.example.ui.locale.localizePeriod
import com.example.ui.locale.localizeStatus
import com.example.ui.locale.localizeSugarType
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportExporter {

    private fun resolveLanguage(context: Context, language: AppLanguage?): AppLanguage {
        if (language != null) return language.resolveEffectiveLanguage()
        val prefs = context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)
        val code = prefs.getString("selected_language_code", AppLanguage.AUTO.code) ?: AppLanguage.AUTO.code
        val saved = AppLanguage.entries.firstOrNull { it.code == code } ?: AppLanguage.AUTO
        return saved.resolveEffectiveLanguage()
    }

    fun generateAndShareMedicalPdf(
        context: Context,
        labTests: List<LabTestEntity>,
        bpList: List<BloodPressureEntity>,
        sugarList: List<BloodSugarEntity>,
        weightList: List<WeightRecordEntity>,
        consultation: AiConsultationEntity?,
        profile: UserProfileEntity? = null,
        targetDrive: Boolean = false,
        language: AppLanguage? = null
    ): File? {
        val effectiveLang = resolveLanguage(context, language)
        val strings = getAppStrings(effectiveLang)
        val isRtl = effectiveLang == AppLanguage.AR

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 standard size in points
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
        }

        // Header Background
        paint.color = Color.parseColor("#0D47A1")
        canvas.drawRect(0f, 0f, 595f, 95f, paint)

        // Header Title
        paint.color = Color.WHITE
        paint.textSize = 19f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER

        val reportHeaderTitle = when (effectiveLang) {
            AppLanguage.AR -> "التقرير الطبي الشامل - طبيبي الذكي"
            AppLanguage.RU -> "Полный медицинский отчет - Умный Доктор"
            else -> "Comprehensive Medical Report - Smart Doctor"
        }
        canvas.drawText(reportHeaderTitle, 595f / 2f, 38f, paint)

        paint.textSize = 10.5f
        paint.typeface = Typeface.DEFAULT
        val currentDateStr = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault()).format(Date())

        val unspecifiedLabel = when (effectiveLang) {
            AppLanguage.AR -> "غير محدد"
            AppLanguage.RU -> "Не указано"
            else -> "Not specified"
        }
        val issueDateLabel = when (effectiveLang) {
            AppLanguage.AR -> "تاريخ الإصدار: $currentDateStr"
            AppLanguage.RU -> "Дата выпуска: $currentDateStr"
            else -> "Issue Date: $currentDateStr"
        }
        val patientInfo = if (profile != null) {
            when (effectiveLang) {
                AppLanguage.AR -> "المريض: ${profile.name.ifEmpty { unspecifiedLabel }} | العمر: ${profile.age} سنة | الجنس: ${profile.gender} | الطول: ${profile.heightCm} سم | الوزن: ${profile.initialWeightKg} كجم"
                AppLanguage.RU -> "Пациент: ${profile.name.ifEmpty { unspecifiedLabel }} | Возраст: ${profile.age} | Пол: ${profile.gender} | Рост: ${profile.heightCm} см | Вес: ${profile.initialWeightKg} кг"
                else -> "Patient: ${profile.name.ifEmpty { unspecifiedLabel }} | Age: ${profile.age} | Gender: ${profile.gender} | Height: ${profile.heightCm} cm | Weight: ${profile.initialWeightKg} kg"
            }
        } else {
            issueDateLabel
        }
        canvas.drawText(patientInfo, 595f / 2f, 64f, paint)
        canvas.drawText(issueDateLabel, 595f / 2f, 82f, paint)

        var yPos = 120f
        val textAnchorX = if (isRtl) 555f else 40f
        val sectionTitleX = if (isRtl) 570f else 25f
        paint.textAlign = if (isRtl) Paint.Align.RIGHT else Paint.Align.LEFT

        // Section 1: AI Health Summary & Advice
        if (consultation != null) {
            paint.color = Color.parseColor("#E3F2FD")
            canvas.drawRoundRect(25f, yPos - 15f, 570f, yPos + 115f, 12f, 12f, paint)

            paint.color = Color.parseColor("#0D47A1")
            paint.textSize = 13.5f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val aiSectionHeader = when (effectiveLang) {
                AppLanguage.AR -> "مستشار الذكاء الاصطناعي والتوصيات الصحية"
                AppLanguage.RU -> "ИИ-консультант и медицинские рекомендации"
                else -> "AI Health Advisor & Clinical Recommendations"
            }
            canvas.drawText(aiSectionHeader, textAnchorX, yPos + 8f, paint)

            paint.color = Color.DKGRAY
            paint.textSize = 9.5f
            paint.typeface = Typeface.DEFAULT

            val evalPrefix = when (effectiveLang) {
                AppLanguage.AR -> "التقييم"
                AppLanguage.RU -> "Оценка"
                else -> "Assessment"
            }
            val dietPrefix = when (effectiveLang) {
                AppLanguage.AR -> "النظام الغذائي"
                AppLanguage.RU -> "Диета"
                else -> "Diet Plan"
            }
            val lifePrefix = when (effectiveLang) {
                AppLanguage.AR -> "أسلوب الحياة"
                AppLanguage.RU -> "Образ жизни"
                else -> "Lifestyle"
            }
            val medPrefix = when (effectiveLang) {
                AppLanguage.AR -> "المتابعة الطبية"
                AppLanguage.RU -> "Мед. наблюдение"
                else -> "Medical Follow-up"
            }

            val summaryShort = if (consultation.summary.length > 110) consultation.summary.take(110) + "..." else consultation.summary
            canvas.drawText("$evalPrefix: $summaryShort", textAnchorX, yPos + 30f, paint)

            val dietShort = if (consultation.dietAdvice.length > 110) consultation.dietAdvice.take(110) + "..." else consultation.dietAdvice
            canvas.drawText("$dietPrefix: $dietShort", textAnchorX, yPos + 50f, paint)

            val lifeShort = if (consultation.lifestyleAdvice.length > 110) consultation.lifestyleAdvice.take(110) + "..." else consultation.lifestyleAdvice
            canvas.drawText("$lifePrefix: $lifeShort", textAnchorX, yPos + 70f, paint)

            val medShort = if (consultation.medicationAdvice.length > 110) consultation.medicationAdvice.take(110) + "..." else consultation.medicationAdvice
            canvas.drawText("$medPrefix: $medShort", textAnchorX, yPos + 90f, paint)

            yPos += 140f
        }

        // Section 2: Core Lab Tests
        paint.color = Color.parseColor("#1565C0")
        paint.textSize = 13.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val labSectionTitle = when (effectiveLang) {
            AppLanguage.AR -> "الفحوصات الطبية والمخبرية الأساسية"
            AppLanguage.RU -> "Основные лабораторные анализы"
            else -> "Core Laboratory & Medical Tests"
        }
        canvas.drawText(labSectionTitle, sectionTitleX, yPos, paint)

        // Table Header
        yPos += 18f
        paint.color = Color.parseColor("#EEEEEE")
        canvas.drawRect(25f, yPos - 12f, 570f, yPos + 10f, paint)

        paint.color = Color.BLACK
        paint.textSize = 9.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val colNameX = if (isRtl) 560f else 35f
        val colValueX = if (isRtl) 340f else 235f
        val colRangeX = if (isRtl) 240f else 340f
        val colStatusX = if (isRtl) 125f else 440f
        val colDateX = if (isRtl) 55f else 505f

        val colTestLabel = when (effectiveLang) {
            AppLanguage.AR -> "الفحص المخبري"
            AppLanguage.RU -> "Анализ"
            else -> "Lab Test"
        }
        val colResultLabel = when (effectiveLang) {
            AppLanguage.AR -> "النتيجة"
            AppLanguage.RU -> "Результат"
            else -> "Result"
        }
        val colRefLabel = when (effectiveLang) {
            AppLanguage.AR -> "المعدل الطبيعي"
            AppLanguage.RU -> "Норма"
            else -> "Ref. Range"
        }
        val colStatusLabel = when (effectiveLang) {
            AppLanguage.AR -> "الحالة"
            AppLanguage.RU -> "Статус"
            else -> "Status"
        }
        val colDateLabel = when (effectiveLang) {
            AppLanguage.AR -> "التاريخ"
            AppLanguage.RU -> "Дата"
            else -> "Date"
        }

        canvas.drawText(colTestLabel, colNameX, yPos, paint)
        canvas.drawText(colResultLabel, colValueX, yPos, paint)
        canvas.drawText(colRefLabel, colRangeX, yPos, paint)
        canvas.drawText(colStatusLabel, colStatusX, yPos, paint)
        canvas.drawText(colDateLabel, colDateX, yPos, paint)

        yPos += 16f
        paint.typeface = Typeface.DEFAULT
        labTests.take(12).forEach { test ->
            paint.color = when (test.statusFlag) {
                "High" -> Color.parseColor("#D32F2F")
                "Low" -> Color.parseColor("#E65100")
                else -> Color.parseColor("#333333")
            }

            val nameShort = if (test.testName.length > 30) test.testName.take(28) + ".." else test.testName
            canvas.drawText(nameShort, colNameX, yPos, paint)
            val valueText = if (test.value.isNotBlank() && test.value != "غير مسحوب" && test.value != "-") "${test.value} ${test.unit}" else ""
            canvas.drawText(valueText, colValueX, yPos, paint)
            canvas.drawText(test.referenceRange, colRangeX, yPos, paint)
            val statusClean = if (test.status.isNotBlank() && test.status != "غير مسحوب" && test.status != "-") strings.localizeStatus(test.status) else ""
            canvas.drawText(statusClean, colStatusX, yPos, paint)
            val dateClean = if (test.testDate.isNotBlank() && test.testDate != "-") test.testDate else ""
            canvas.drawText(dateClean, colDateX, yPos, paint)
            yPos += 15f
        }

        // Section 3: Daily Vitals (BP, Sugar, Weight)
        yPos += 15f
        paint.color = Color.parseColor("#1565C0")
        paint.textSize = 13.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val vitalsTitle = when (effectiveLang) {
            AppLanguage.AR -> "سجل المتابعة اليومية (ضغط الدم - السكر - الوزن)"
            AppLanguage.RU -> "Ежедневный мониторинг (Давление - Сахар - Вес)"
            else -> "Daily Vitals Log (Blood Pressure - Sugar - Weight)"
        }
        canvas.drawText(vitalsTitle, sectionTitleX, yPos, paint)

        yPos += 20f
        paint.color = Color.parseColor("#333333")
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT

        val vitalsTextX = if (isRtl) 560f else 35f

        // BP Summary
        val latestBp = bpList.firstOrNull()
        if (latestBp != null) {
            val localizedCat = strings.localizeStatus(latestBp.category)
            val localizedPer = strings.localizePeriod(latestBp.period)
            val bpLine = when (effectiveLang) {
                AppLanguage.AR -> "آخر قراءة لضغط الدم: ${latestBp.systolic}/${latestBp.diastolic} mmHg (نبض: ${latestBp.pulse}) - $localizedCat [${latestBp.date} $localizedPer]"
                AppLanguage.RU -> "Последнее АД: ${latestBp.systolic}/${latestBp.diastolic} мм рт. ст. (пульс: ${latestBp.pulse}) - $localizedCat [${latestBp.date} $localizedPer]"
                else -> "Latest Blood Pressure: ${latestBp.systolic}/${latestBp.diastolic} mmHg (Pulse: ${latestBp.pulse}) - $localizedCat [${latestBp.date} $localizedPer]"
            }
            canvas.drawText(bpLine, vitalsTextX, yPos, paint)
            yPos += 15f
        }

        // Sugar Summary
        val latestSugar = sugarList.firstOrNull()
        if (latestSugar != null) {
            val localizedType = strings.localizeSugarType(latestSugar.type)
            val localizedStat = strings.localizeStatus(latestSugar.status)
            val sugarLine = when (effectiveLang) {
                AppLanguage.AR -> "آخر قراءة لسكر الدم: ${latestSugar.value} mg/dL ($localizedType) - $localizedStat [${latestSugar.date}]"
                AppLanguage.RU -> "Последний сахар крови: ${latestSugar.value} мг/дл ($localizedType) - $localizedStat [${latestSugar.date}]"
                else -> "Latest Blood Sugar: ${latestSugar.value} mg/dL ($localizedType) - $localizedStat [${latestSugar.date}]"
            }
            canvas.drawText(sugarLine, vitalsTextX, yPos, paint)
            yPos += 15f
        }

        // Weight Summary
        val latestW = weightList.firstOrNull()
        if (latestW != null) {
            val localizedBmiCat = strings.localizeStatus(latestW.bmiCategory)
            val weightLine = when (effectiveLang) {
                AppLanguage.AR -> "الوزن الحالي: ${latestW.weightKg} كجم (طول ${latestW.heightCm} سم) | مؤشر كتلة الجسم BMI: ${latestW.bmi} ($localizedBmiCat)"
                AppLanguage.RU -> "Текущий вес: ${latestW.weightKg} кг (рост ${latestW.heightCm} см) | ИМТ (BMI): ${latestW.bmi} ($localizedBmiCat)"
                else -> "Current Weight: ${latestW.weightKg} kg (Height ${latestW.heightCm} cm) | BMI: ${latestW.bmi} ($localizedBmiCat)"
            }
            canvas.drawText(weightLine, vitalsTextX, yPos, paint)
            yPos += 15f
        }

        // Footer Disclaimer
        yPos = 810f
        paint.color = Color.GRAY
        paint.textSize = 9f
        paint.textAlign = Paint.Align.CENTER
        val disclaimerText = when (effectiveLang) {
            AppLanguage.AR -> "تنبيه طبي: هذا التقرير تم إعداده آلياً للمساعدة في متابعة الصحة ولا يغني عن الاستشارة الطبية المتخصصة."
            AppLanguage.RU -> "Примечание: Этот отчет создан автоматически для мониторинга здоровья и не заменяет консультацию врача."
            else -> "Medical Disclaimer: This report is auto-generated for health tracking and does not replace professional medical advice."
        }
        canvas.drawText(disclaimerText, 595f / 2f, yPos, paint)

        document.finishPage(page)

        // Write to cache file
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()
        val file = File(reportsDir, "SmartHealth_Report_${System.currentTimeMillis()}.pdf")

        try {
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            sharePdfFile(context, file, targetDrive, effectiveLang)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            document.close()
            val errMsg = when (effectiveLang) {
                AppLanguage.AR -> "فشل إنشاء ملف PDF: ${e.message}"
                AppLanguage.RU -> "Ошибка создания PDF: ${e.message}"
                else -> "Failed to generate PDF: ${e.message}"
            }
            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun sharePdfFile(
        context: Context,
        file: File,
        targetDrive: Boolean,
        effectiveLang: AppLanguage
    ) {
        val uri: Uri = try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Uri.fromFile(file)
        }

        val subject = when (effectiveLang) {
            AppLanguage.AR -> "التقرير الطبي الشامل - طبيبي الذكي"
            AppLanguage.RU -> "Полный медицинский отчет - Умный Доктор"
            else -> "Comprehensive Medical Report - Smart Doctor"
        }
        val bodyText = when (effectiveLang) {
            AppLanguage.AR -> "مرفق التقرير الطبي الشامل لنتائج الفحوصات ومتابعة السكر وضغط الدم واستشارات أسلوب الحياة والتغذية."
            AppLanguage.RU -> "Во вложении полный медицинский отчет с результатами анализов, мониторингом давления, сахара и рекомендациями."
            else -> "Attached is the comprehensive medical report including lab results, blood pressure, blood sugar, and health recommendations."
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, bodyText)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val title = when (effectiveLang) {
            AppLanguage.AR -> if (targetDrive) "حفظ التقرير في Google Drive / المشاركة" else "مشاركة وتحميل التقرير PDF"
            AppLanguage.RU -> if (targetDrive) "Сохранить отчет в Google Drive / Поделиться" else "Поделиться и скачать PDF-отчет"
            else -> if (targetDrive) "Save Report to Google Drive / Share" else "Share & Download PDF Report"
        }
        context.startActivity(Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
