package com.example.data.local

import com.example.data.model.LabTestEntity
import com.example.data.model.UserProfileEntity

object DefaultData {
    val defaultUserProfile = UserProfileEntity(
        id = 1,
        name = "",
        age = 0,
        gender = "",
        heightCm = 0f,
        initialWeightKg = 0f,
        bloodType = "",
        chronicDiseases = "",
        notes = "",
        profileImageUri = ""
    )

    // Blank templates with no mock numbers, no fake values, and no status
    val blankTemplateLabTests = listOf(
        LabTestEntity(
            testName = "Complete Blood Count (CBC), EDTA Whole Blood",
            testCategory = "الدم العام",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "g/dL",
            referenceRange = "12.0 - 17.5",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Fasting Blood Sugar (FBS)",
            testCategory = "السكري والتمثيل الغذائي",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "70 - 99",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Blood Urea Nitrogen (BUN)",
            testCategory = "وظائف الكلى",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "7 - 20",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Creatinine, Serum",
            testCategory = "وظائف الكلى",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "0.6 - 1.2",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Cholesterol, total",
            testCategory = "الدهون والقلب",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "< 200",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Cholesterol - HDL",
            testCategory = "الدهون والقلب",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "> 40",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Cholesterol - LDL",
            testCategory = "الدهون والقلب",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "< 100",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Triglycerides",
            testCategory = "الدهون والقلب",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "< 150",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Electrolytes, serum",
            testCategory = "الأملاح والمعادن",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mEq/L",
            referenceRange = "135-145 / 3.5-5.0",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Thyroid Stimulating Hormone (TSH)",
            testCategory = "الهرمونات والغدد",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "µIU/mL",
            referenceRange = "0.4 - 4.0",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Glutamate Oxaloacetic Transaminase (GOT)",
            testCategory = "وظائف الكبد",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "U/L",
            referenceRange = "10 - 40",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Glutamate Pyruvate Transaminase (GPT)",
            testCategory = "وظائف الكبد",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "U/L",
            referenceRange = "10 - 45",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Erythrocyte Sedimentation Rate - ESR",
            testCategory = "مؤشرات الالتهاب",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mm/hr",
            referenceRange = "0 - 20",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Uric Acid",
            testCategory = "الأملاح والنقرس",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "3.5 - 7.2",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Vitamin B12",
            testCategory = "الفيتامينات",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "pg/mL",
            referenceRange = "200 - 900",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "25-Hydroxy Vitamin D",
            testCategory = "الفيتامينات",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "ng/mL",
            referenceRange = "30 - 100",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Calcium, Serum",
            testCategory = "المعادن والعظام",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "8.5 - 10.5",
            statusFlag = "Unknown",
            notes = ""
        ),
        LabTestEntity(
            testName = "Magnesium, serum",
            testCategory = "المعادن والعظام",
            testDate = "",
            labName = "",
            status = "",
            value = "",
            unit = "mg/dL",
            referenceRange = "1.7 - 2.2",
            statusFlag = "Unknown",
            notes = ""
        )
    )

    // Complete real lab report tests from Al-Shifa Specialized Medical Complex (مجمع الشفاء الطبي التخصصي - SSMC)
    val shifaLabTestsExample = listOf(
        // === HEMATOLOGY (صورة الدم الكاملة CBC) ===
        LabTestEntity(
            testName = "White blood cells (WBCs)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "5.90",
            unit = "10^3 /uL",
            referenceRange = "4 - 10",
            statusFlag = "Normal",
            notes = "ضمن المعدل الطبيعي"
        ),
        LabTestEntity(
            testName = "Granulocytes",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "2.84",
            unit = "10^3 /uL",
            referenceRange = "2 - 7",
            statusFlag = "Normal",
            notes = "ضمن المعدل الطبيعي"
        ),
        LabTestEntity(
            testName = "Lymphocytes",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "2.47",
            unit = "10^3 /uL",
            referenceRange = "0.8 - 4",
            statusFlag = "Normal",
            notes = "ضمن المعدل الطبيعي"
        ),
        LabTestEntity(
            testName = "Monocyte",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "0.35",
            unit = "10^3 /uL",
            referenceRange = "0.12 - 1.20",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Eosinophils",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "0.17",
            unit = "10^3 /uL",
            referenceRange = "0.02 - 0.52",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Basophils",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "0.07",
            unit = "10^3 /uL",
            referenceRange = "0.0 - 0.1",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Granulocytes%",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "نقص",
            value = "48.1",
            unit = "%",
            referenceRange = "50 - 70",
            statusFlag = "Low",
            notes = "أقل قليلاً من الحد الأدنى (48.1% مقابل 50%)"
        ),
        LabTestEntity(
            testName = "Lymphocytes%",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "زائد",
            value = "41.8",
            unit = "%",
            referenceRange = "20 - 40",
            statusFlag = "High",
            notes = "أعلى قليلاً من الحد الأقصى (41.8% مقابل 40%)"
        ),
        LabTestEntity(
            testName = "Monocyte %",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "6.0",
            unit = "%",
            referenceRange = "3 - 12",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Eosinophils %",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "2.9",
            unit = "%",
            referenceRange = "0.5 - 5.0",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Basophils %",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "زائد",
            value = "1.2",
            unit = "%",
            referenceRange = "0.0 - 1.0",
            statusFlag = "High",
            notes = "ارتفاع طفيف (1.2% مقابل 1.0%)"
        ),
        LabTestEntity(
            testName = "Abnormal Lymphocytes (ALY)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "0.00",
            unit = "10^3 /uL",
            referenceRange = "0.0 - 0.2",
            statusFlag = "Normal",
            notes = "طبيعي - لا توجد خلايا غير طبيعية"
        ),
        LabTestEntity(
            testName = "Large Immature Cells (LIC)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "0.05",
            unit = "10^3 /uL",
            referenceRange = "0.0 - 0.2",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Abnormal Lymphocytes (ALY) %",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "0.1",
            unit = "%",
            referenceRange = "0.0 - 2.0",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Large Immature Cells (LIC) %",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "0.8",
            unit = "%",
            referenceRange = "0.0 - 2.5",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Red blood cells (RBC)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "زائد",
            value = "5.53",
            unit = "10^6 /uL",
            referenceRange = "3.9 - 5.5",
            statusFlag = "High",
            notes = "ارتفاع طفيف جداً فوق الحد الأقصى (5.53 مقابل 5.5)"
        ),
        LabTestEntity(
            testName = "Haemoglobin (HGB)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "16.1",
            unit = "g/dL",
            referenceRange = "14 - 18",
            statusFlag = "Normal",
            notes = "ممتاز وطبيعي للذكور (16.1 g/dL)"
        ),
        LabTestEntity(
            testName = "Hematocrit (HCT)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "47.2",
            unit = "%",
            referenceRange = "40 - 55",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Mean cell volume (MCV)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "85.4",
            unit = "fl",
            referenceRange = "80 - 100",
            statusFlag = "Normal",
            notes = "حجم كريات الدم الحمراء طبيعي"
        ),
        LabTestEntity(
            testName = "Mean Cell Haemoglobin (MCH)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "29.0",
            unit = "pg",
            referenceRange = "27 - 34",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Mean Cell haemoglobin concentration (MCHC)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "34.0",
            unit = "g/dL",
            referenceRange = "32 - 36",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "RDW%",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "12.9",
            unit = "%",
            referenceRange = "11 - 16",
            statusFlag = "Normal",
            notes = "توزيع حجم الكريات طبيعي ومتجانس"
        ),
        LabTestEntity(
            testName = "RDW -SD",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "44.6",
            unit = "fl",
            referenceRange = "35.0 - 56.0",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Platelets Count",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "246",
            unit = "10^3 /uL",
            referenceRange = "100 - 400",
            statusFlag = "Normal",
            notes = "تعداد الصفائح الدموية ممتاز وطبيعي جداً"
        ),
        LabTestEntity(
            testName = "Mean Platelet Volume (MPV)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "9.5",
            unit = "fl",
            referenceRange = "6.5 - 12.0",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "Platelet Distrubtion Width (PDW)",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "9.6",
            unit = "fl",
            referenceRange = "9 - 17",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "PCT",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "0.234",
            unit = "%",
            referenceRange = "0.108 - 0.282",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "P-LCR",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "24.8",
            unit = "%",
            referenceRange = "11.0 - 45.0",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),
        LabTestEntity(
            testName = "P-LCC",
            testCategory = "صورة الدم CBC",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "61",
            unit = "10^3 /uL",
            referenceRange = "30 - 90",
            statusFlag = "Normal",
            notes = "طبيعي"
        ),

        // === HEMATOLOGY - ESR ===
        LabTestEntity(
            testName = "ESR (سرعة الترسيب)",
            testCategory = "أمراض الدم (Hematology)",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "6",
            unit = "mm/h",
            referenceRange = "< 15 للذكور",
            statusFlag = "Normal",
            notes = "معدل سرعة الترسيب طبيعي ومثالي جداً (لا توجد علامات التهاب جهازي)"
        ),

        // === CLINICAL CHEMISTRY - Uric Acid ===
        LabTestEntity(
            testName = "Uric Acid, serum (حمض اليوريك)",
            testCategory = "الكيمياء السريرية (Clinical Chemistry)",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "زائد",
            value = "7.2",
            unit = "mg/dL",
            referenceRange = "3.4 - 7.0 mg/dL للذكور",
            statusFlag = "High",
            notes = "مرتفع قليلاً (7.2 مقابل الحد الأقصى 7.0) - يوصى بتقليل اللحوم الحمراء وشرب الماء بكثرة"
        ),

        // === VITAMINES - Vitamin D3 & Vitamin B12 ===
        LabTestEntity(
            testName = "25-OH-Vitamin D3 (فيتامين د3)",
            testCategory = "الفيتامينات (Vitamines)",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "نقص",
            value = "24.05",
            unit = "ng/mL",
            referenceRange = "30 - 100 ng/mL (غير كافٍ: 10 - 29)",
            statusFlag = "Low",
            notes = "غير كافٍ (Insufficient: 24.05 ng/mL) - يتطلب مكمل غذائي والتعرض للشمس لرفعه فوق 30"
        ),
        LabTestEntity(
            testName = "Vitamin B12 (فيتامين ب12)",
            testCategory = "الفيتامينات (Vitamines)",
            testDate = "10/5/2026",
            labName = "مجمع الشفاء الطبي التخصصي (SSMC)",
            status = "طبيعي",
            value = "253",
            unit = "pg/mL",
            referenceRange = "200 - 835 pg/mL للبالغين",
            statusFlag = "Normal",
            notes = "طبيعي ولكن في النطاق الأدنى (253 pg/mL) - مفيد دعمه بالخميرة والبيض والمكملات"
        )
    )
}
