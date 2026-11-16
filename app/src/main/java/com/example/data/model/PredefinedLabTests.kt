package com.example.data.model

data class PredefinedLabTest(
    val key: String,
    val nameAr: String,
    val nameEn: String,
    val category: String,
    val defaultUnit: String,
    val defaultRefRange: String,
    val commonKeywords: List<String> = emptyList()
) {
    val fullDisplayName: String
        get() = "$nameAr ($nameEn)"

    fun displayName(isArabic: Boolean): String = if (isArabic) "$nameAr ($nameEn)" else nameEn
    fun localizedName(isArabic: Boolean): String = if (isArabic) nameAr else nameEn

    fun matches(query: String): Boolean {
        if (query.isBlank()) return true
        val q = query.trim().lowercase()
        return nameAr.lowercase().contains(q) ||
                nameEn.lowercase().contains(q) ||
                category.lowercase().contains(q) ||
                commonKeywords.any { it.lowercase().contains(q) }
    }
}

object PredefinedLabTestsCatalog {
    val categories = listOf(
        "الكل",
        "الدم (CBC)",
        "السكري",
        "الدهون والكولسترول",
        "وظائف الكلى والأملاح",
        "وظائف الكبد",
        "الفيتامينات والمعادن",
        "الهرمونات والغدد",
        "القلب والأوعية"
    )

    val list: List<PredefinedLabTest> = listOf(
        // --- السكري ---
        PredefinedLabTest(
            key = "fbs",
            nameAr = "سكر الدم الصائم",
            nameEn = "Fasting Blood Sugar (FBS)",
            category = "السكري",
            defaultUnit = "mg/dL",
            defaultRefRange = "70 - 100",
            commonKeywords = listOf("سكر", "صائم", "fbs", "glucose", "fbg")
        ),
        PredefinedLabTest(
            key = "hba1c",
            nameAr = "السكر التراكمي",
            nameEn = "HbA1c",
            category = "السكري",
            defaultUnit = "%",
            defaultRefRange = "4.0 - 5.6",
            commonKeywords = listOf("تراكمي", "a1c", "hba1c", "glycated")
        ),
        PredefinedLabTest(
            key = "rbs",
            nameAr = "سكر الدم العشوائي",
            nameEn = "Random Blood Sugar (RBS)",
            category = "السكري",
            defaultUnit = "mg/dL",
            defaultRefRange = "70 - 140",
            commonKeywords = listOf("عشوائي", "rbs", "rbg")
        ),
        PredefinedLabTest(
            key = "ppbg",
            nameAr = "سكر بعد الأكل بساعتين",
            nameEn = "Postprandial Blood Sugar (PPBG)",
            category = "السكري",
            defaultUnit = "mg/dL",
            defaultRefRange = "< 140",
            commonKeywords = listOf("بعد الاكل", "فاطر", "ppbg")
        ),

        // --- الدهون والكولسترول ---
        PredefinedLabTest(
            key = "cholesterol",
            nameAr = "الكولسترول الكلي",
            nameEn = "Total Cholesterol",
            category = "الدهون والكولسترول",
            defaultUnit = "mg/dL",
            defaultRefRange = "< 200",
            commonKeywords = listOf("كولسترول", "كوليسترول", "cholesterol")
        ),
        PredefinedLabTest(
            key = "triglycerides",
            nameAr = "الدهون الثلاثية",
            nameEn = "Triglycerides",
            category = "الدهون والكولسترول",
            defaultUnit = "mg/dL",
            defaultRefRange = "< 150",
            commonKeywords = listOf("ثلاثية", "دهون", "triglycerides", "tg")
        ),
        PredefinedLabTest(
            key = "hdl",
            nameAr = "الكولسترول النافع (HDL)",
            nameEn = "HDL - cholesterol",
            category = "الدهون والكولسترول",
            defaultUnit = "mg/dL",
            defaultRefRange = "40 - 65",
            commonKeywords = listOf("نافع", "جيد", "hdl")
        ),
        PredefinedLabTest(
            key = "ldl",
            nameAr = "الكولسترول الضار (LDL)",
            nameEn = "LDL - cholesterol",
            category = "الدهون والكولسترول",
            defaultUnit = "mg/dL",
            defaultRefRange = "< 100",
            commonKeywords = listOf("ضار", "سيء", "ldl")
        ),

        // --- صورة الدم الكاملة (CBC) ---
        PredefinedLabTest(
            key = "hgb",
            nameAr = "الهيموجلوبين (قوة الدم)",
            nameEn = "Haemoglobin (HGB)",
            category = "الدم (CBC)",
            defaultUnit = "g/dL",
            defaultRefRange = "13.0 - 17.0",
            commonKeywords = listOf("هيموجلوبين", "دم", "hgb", "hb", "hemoglobin")
        ),
        PredefinedLabTest(
            key = "wbcs",
            nameAr = "كريات الدم البيضاء",
            nameEn = "White blood cells (WBCs)",
            category = "الدم (CBC)",
            defaultUnit = "10^3 /uL",
            defaultRefRange = "4.0 - 10.0",
            commonKeywords = listOf("بيضاء", "مناعة", "wbcs", "wbc")
        ),
        PredefinedLabTest(
            key = "platelets",
            nameAr = "الصفائح الدموية",
            nameEn = "Platelets Count",
            category = "الدم (CBC)",
            defaultUnit = "10^3 /uL",
            defaultRefRange = "150 - 450",
            commonKeywords = listOf("صفائح", "تجلط", "platelets", "plt")
        ),
        PredefinedLabTest(
            key = "rbc",
            nameAr = "كريات الدم الحمراء",
            nameEn = "Red blood cells (RBC)",
            category = "الدم (CBC)",
            defaultUnit = "10^6 /uL",
            defaultRefRange = "4.0 - 5.5",
            commonKeywords = listOf("حمراء", "rbc")
        ),
        PredefinedLabTest(
            key = "hct",
            nameAr = "حجم الخلايا المكدسة (الهيماتوكريت)",
            nameEn = "Hematocrit (HCT)",
            category = "الدم (CBC)",
            defaultUnit = "%",
            defaultRefRange = "38 - 50",
            commonKeywords = listOf("هيماتوكريت", "hct")
        ),
        PredefinedLabTest(
            key = "mcv",
            nameAr = "الحجم الكروي الوسطي (MCV)",
            nameEn = "Mean cell volume (MCV)",
            category = "الدم (CBC)",
            defaultUnit = "fl",
            defaultRefRange = "80 - 100",
            commonKeywords = listOf("mcv")
        ),
        PredefinedLabTest(
            key = "mch",
            nameAr = "هيموجلوبين الكرية الوسطي (MCH)",
            nameEn = "Mean Cell Haemoglobin (MCH)",
            category = "الدم (CBC)",
            defaultUnit = "pg",
            defaultRefRange = "27 - 34",
            commonKeywords = listOf("mch")
        ),
        PredefinedLabTest(
            key = "mchc",
            nameAr = "تركيز هيموجلوبين الكرية (MCHC)",
            nameEn = "Mean Cell Haemoglobin Concentration (MCHC)",
            category = "الدم (CBC)",
            defaultUnit = "g/dL",
            defaultRefRange = "32 - 36",
            commonKeywords = listOf("mchc")
        ),
        PredefinedLabTest(
            key = "rdw",
            nameAr = "توزيع كريات الدم الحمراء (RDW)",
            nameEn = "RDW%",
            category = "الدم (CBC)",
            defaultUnit = "%",
            defaultRefRange = "11.0 - 16.0",
            commonKeywords = listOf("rdw")
        ),
        PredefinedLabTest(
            key = "esr",
            nameAr = "سرعة ترسيب الدم (ESR)",
            nameEn = "ESR",
            category = "الدم (CBC)",
            defaultUnit = "mm/h",
            defaultRefRange = "0 - 15",
            commonKeywords = listOf("ترسيب", "التهاب", "esr")
        ),
        PredefinedLabTest(
            key = "lymphocytes",
            nameAr = "الخلايا اللمفاوية (Lymphocytes)",
            nameEn = "Lymphocytes %",
            category = "الدم (CBC)",
            defaultUnit = "%",
            defaultRefRange = "20 - 40",
            commonKeywords = listOf("لمفاوية", "lymph")
        ),
        PredefinedLabTest(
            key = "granulocytes",
            nameAr = "الخلايا المحببة (Granulocytes)",
            nameEn = "Granulocytes %",
            category = "الدم (CBC)",
            defaultUnit = "%",
            defaultRefRange = "50 - 70",
            commonKeywords = listOf("محببة", "granulo")
        ),

        // --- وظائف الكلى والأملاح ---
        PredefinedLabTest(
            key = "creatinine",
            nameAr = "الكرياتينين (وظائف كلى)",
            nameEn = "Creatinine, serum",
            category = "وظائف الكلى والأملاح",
            defaultUnit = "mg/dL",
            defaultRefRange = "0.7 - 1.3",
            commonKeywords = listOf("كرياتينين", "كلى", "creatinine")
        ),
        PredefinedLabTest(
            key = "uric_acid",
            nameAr = "حمض اليوريك (النقرس)",
            nameEn = "Uric Acid, serum",
            category = "وظائف الكلى والأملاح",
            defaultUnit = "mg/dL",
            defaultRefRange = "3.5 - 7.2",
            commonKeywords = listOf("يوريك", "نقرس", "uric", "gout")
        ),
        PredefinedLabTest(
            key = "bun",
            nameAr = "نيتروجين يوريا الدم (BUN)",
            nameEn = "BUN (Blood Urea Nitrogen)",
            category = "وظائف الكلى والأملاح",
            defaultUnit = "mg/dL",
            defaultRefRange = "7 - 20",
            commonKeywords = listOf("يوريا", "bun", "urea")
        ),
        PredefinedLabTest(
            key = "sodium",
            nameAr = "أملاح الصوديوم",
            nameEn = "Sodium (Na)",
            category = "وظائف الكلى والأملاح",
            defaultUnit = "mmol/L",
            defaultRefRange = "135 - 145",
            commonKeywords = listOf("صوديوم", "املاح", "sodium", "na")
        ),
        PredefinedLabTest(
            key = "potassium",
            nameAr = "أملاح البوتاسيوم",
            nameEn = "Potassium (K)",
            category = "وظائف الكلى والأملاح",
            defaultUnit = "mmol/L",
            defaultRefRange = "3.5 - 5.1",
            commonKeywords = listOf("بوتاسيوم", "potassium", "k")
        ),
        PredefinedLabTest(
            key = "chloride",
            nameAr = "أملاح الكلوريد",
            nameEn = "Chloride (Cl)",
            category = "وظائف الكلى والأملاح",
            defaultUnit = "mmol/L",
            defaultRefRange = "98 - 107",
            commonKeywords = listOf("كلوريد", "chloride", "cl")
        ),
        PredefinedLabTest(
            key = "magnesium",
            nameAr = "المغنيسيوم (Mg)",
            nameEn = "Magnesium (Mg)",
            category = "وظائف الكلى والأملاح",
            defaultUnit = "mg/dL",
            defaultRefRange = "1.7 - 2.5",
            commonKeywords = listOf("مغنيسيوم", "mg", "magnesium")
        ),
        PredefinedLabTest(
            key = "calcium",
            nameAr = "الكالسيوم",
            nameEn = "Calcium, serum",
            category = "وظائف الكلى والأملاح",
            defaultUnit = "mg/dL",
            defaultRefRange = "8.5 - 10.5",
            commonKeywords = listOf("كالسيوم", "عظام", "calcium", "ca")
        ),

        // --- وظائف الكبد ---
        PredefinedLabTest(
            key = "sgpt",
            nameAr = "إنزيم الكبد (SGPT / ALT)",
            nameEn = "SGPT (ALT)",
            category = "وظائف الكبد",
            defaultUnit = "U/L",
            defaultRefRange = "< 41",
            commonKeywords = listOf("كبد", "alt", "sgpt")
        ),
        PredefinedLabTest(
            key = "sgot",
            nameAr = "إنزيم الكبد (SGOT / AST)",
            nameEn = "SGOT (AST)",
            category = "وظائف الكبد",
            defaultUnit = "U/L",
            defaultRefRange = "< 37",
            commonKeywords = listOf("كبد", "ast", "sgot")
        ),
        PredefinedLabTest(
            key = "bilirubin_total",
            nameAr = "الصفراء الكلي (Bilirubin)",
            nameEn = "Bilirubin Total",
            category = "وظائف الكبد",
            defaultUnit = "mg/dL",
            defaultRefRange = "0.2 - 1.2",
            commonKeywords = listOf("صفراء", "بيليروبين", "bilirubin", "يرقان")
        ),
        PredefinedLabTest(
            key = "albumin",
            nameAr = "الألبومين",
            nameEn = "Albumin",
            category = "وظائف الكبد",
            defaultUnit = "g/dL",
            defaultRefRange = "3.5 - 5.0",
            commonKeywords = listOf("البومين", "albumin")
        ),
        PredefinedLabTest(
            key = "alp",
            nameAr = "الفوسفاتاز القلوي (ALP)",
            nameEn = "Alkaline Phosphatase (ALP)",
            category = "وظائف الكبد",
            defaultUnit = "U/L",
            defaultRefRange = "40 - 130",
            commonKeywords = listOf("alp", "alkaline")
        ),

        // --- الفيتامينات والمعادن ---
        PredefinedLabTest(
            key = "vit_d3",
            nameAr = "فيتامين د3 (25-OH-Vitamin D3)",
            nameEn = "25-OH-Vitamin D3",
            category = "الفيتامينات والمعادن",
            defaultUnit = "ng/mL",
            defaultRefRange = "30 - 100",
            commonKeywords = listOf("فيتامين د", "vitamin d", "d3")
        ),
        PredefinedLabTest(
            key = "vit_b12",
            nameAr = "فيتامين ب12",
            nameEn = "Vitamin B12",
            category = "الفيتامينات والمعادن",
            defaultUnit = "pg/mL",
            defaultRefRange = "200 - 900",
            commonKeywords = listOf("اعصاب", "b12", "vitamin b12")
        ),
        PredefinedLabTest(
            key = "ferritin",
            nameAr = "مخزون الحديد (Ferritin)",
            nameEn = "Ferritin",
            category = "الفيتامينات والمعادن",
            defaultUnit = "ng/mL",
            defaultRefRange = "30 - 400",
            commonKeywords = listOf("مخزون الحديد", "ferritin", "انيميا")
        ),
        PredefinedLabTest(
            key = "iron",
            nameAr = "الحديد في الدم (Serum Iron)",
            nameEn = "Serum Iron",
            category = "الفيتامينات والمعادن",
            defaultUnit = "mcg/dL",
            defaultRefRange = "60 - 170",
            commonKeywords = listOf("حديد", "iron")
        ),

        // --- الهرمونات والغدد ---
        PredefinedLabTest(
            key = "tsh",
            nameAr = "هرمون الغدة الدرقية (TSH)",
            nameEn = "TSH (Thyroid Stimulating Hormone)",
            category = "الهرمونات والغدد",
            defaultUnit = "uIU/mL",
            defaultRefRange = "0.4 - 4.5",
            commonKeywords = listOf("غدة", "درقية", "tsh", "thyroid")
        ),
        PredefinedLabTest(
            key = "ft4",
            nameAr = "الثيروكسين الحر (Free T4)",
            nameEn = "Free T4",
            category = "الهرمونات والغدد",
            defaultUnit = "ng/dL",
            defaultRefRange = "0.8 - 1.8",
            commonKeywords = listOf("ft4", "t4")
        ),

        // --- القلب والأوعية (من تقرير الإيكو) ---
        PredefinedLabTest(
            key = "ef",
            nameAr = "كفاءة عضلة القلب (Ejection Fraction)",
            nameEn = "Ejection Fraction (E.F.)",
            category = "القلب والأوعية",
            defaultUnit = "%",
            defaultRefRange = "55 - 75",
            commonKeywords = listOf("ايكو", "قلب", "ef", "ejection")
        ),
        PredefinedLabTest(
            key = "crp",
            nameAr = "بروتين سي التفاعلي (CRP)",
            nameEn = "C-Reactive Protein (CRP)",
            category = "القلب والأوعية",
            defaultUnit = "mg/L",
            defaultRefRange = "< 5.0",
            commonKeywords = listOf("crp", "التهاب")
        )
    )
}
