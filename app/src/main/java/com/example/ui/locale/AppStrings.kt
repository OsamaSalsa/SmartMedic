package com.example.ui.locale

class AppStrings(
    val common: CommonStrings,
    val nav: NavStrings,
    val home: HomeStrings,
    val labTests: LabTestsStrings,
    val daily: DailyStrings,
    val results: ResultsStrings,
    val aiAdvisor: AiAdvisorStrings,
    val profile: ProfileStrings
) {
    // Convenient getters for backward compatibility
    val appName get() = common.appName
    val save get() = common.save
    val cancel get() = common.cancel
    val close get() = common.close
    val delete get() = common.delete
    val edit get() = common.edit
    val add get() = common.add
    val confirm get() = common.confirm
    val all get() = common.all
    val today get() = common.today
    val yesterday get() = common.yesterday
    val date get() = common.date
    val time get() = common.time
    val notes get() = common.notes
    val loading get() = common.loading
    val search get() = common.search
    val status get() = common.status
    val normal get() = common.normal
    val high get() = common.high
    val low get() = common.low
    val critical get() = common.critical
    val active get() = common.active
    val inactive get() = common.inactive
    val yes get() = common.yes
    val no get() = common.no
    val success get() = common.success
    val error get() = common.error
    val refresh get() = common.refresh

    val navHome get() = nav.navHome
    val navDaily get() = nav.navDaily
    val navResults get() = nav.navResults
    val navAiAdvisor get() = nav.navAiAdvisor
    val navProfile get() = nav.navProfile

    val welcomeUser get() = home.welcomeUser
    val dailyHealthSummary get() = home.dailyHealthSummary
    val quickVitals get() = home.quickVitals
    val bloodSugar get() = home.bloodSugar
    val bloodPressure get() = home.bloodPressure
    val weight get() = home.weight
    val pulse get() = home.pulse
    val bmi get() = home.bmi
    val quickActions get() = home.quickActions
    val addLabTest get() = home.addLabTest
    val logVitals get() = home.logVitals
    val consultAi get() = home.consultAi
    val uploadPrescription get() = home.uploadPrescription
    val recentLabTests get() = home.recentLabTests
    val viewAll get() = home.viewAll
    val emergencyCall get() = home.emergencyCall
    val dailyHealthTips get() = home.dailyHealthTips
    val emergencyTitle get() = home.emergencyTitle
    val emergencySubtitle get() = home.emergencySubtitle
    val call123 get() = home.call123
    val currentReadingsSummary get() = home.currentReadingsSummary
    val fullHistory get() = home.fullHistory
    val noReadings get() = home.noReadings
    val kgUnit get() = home.kgUnit
    val todayMultipleReadings get() = home.todayMultipleReadings
    val noReadingsToday get() = home.noReadingsToday
    val tapCardsToLog get() = home.tapCardsToLog
    val viewPastReadings get() = home.viewPastReadings
    val consultAiComprehensive get() = home.consultAiComprehensive
    val scanPrescriptionTitle get() = home.scanPrescriptionTitle
    val scanPrescriptionSubtitle get() = home.scanPrescriptionSubtitle
    val uploadLabReportTitle get() = home.uploadLabReportTitle
    val uploadLabReportSubtitle get() = home.uploadLabReportSubtitle
    val aiBadge get() = home.aiBadge
    val logSugarTitle get() = home.logSugarTitle
    val logBpTitle get() = home.logBpTitle
    val logWeightTitle get() = home.logWeightTitle
    val fastingOption get() = home.fastingOption
    val postBreakfast get() = home.postBreakfast
    val beforeLunch get() = home.beforeLunch
    val afterLunch get() = home.afterLunch
    val beforeDinner get() = home.beforeDinner
    val afterDinner get() = home.afterDinner
    val beforeBed get() = home.beforeBed
    val randomOption get() = home.randomOption
    val morningOption get() = home.morningOption
    val noonOption get() = home.noonOption
    val eveningOption get() = home.eveningOption
    val nightOption get() = home.nightOption
    val systolicLabel get() = home.systolicLabel
    val diastolicLabel get() = home.diastolicLabel
    val pulseLabel get() = home.pulseLabel
    val measurementPeriod get() = home.measurementPeriod
    val weightKgLabel get() = home.weightKgLabel
    val heightCmLabel get() = home.heightCmLabel
    val bmiLabel get() = home.bmiLabel
    val sugarSavedSuccess get() = home.sugarSavedSuccess
    val bpSavedSuccess get() = home.bpSavedSuccess
    val weightSavedSuccess get() = home.weightSavedSuccess
    val pleaseEnterValidNumber get() = home.pleaseEnterValidNumber

    val labTestsTitle get() = labTests.labTestsTitle
    val uploadPdfOrImage get() = labTests.uploadPdfOrImage
    val addManualTest get() = labTests.addManualTest
    val testName get() = labTests.testName
    val category get() = labTests.category
    val resultValue get() = labTests.resultValue
    val unit get() = labTests.unit
    val referenceRange get() = labTests.referenceRange
    val labName get() = labTests.labName
    val filterAll get() = labTests.filterAll
    val filterAbnormal get() = labTests.filterAbnormal
    val filterNormal get() = labTests.filterNormal
    val aiExtractSummary get() = labTests.aiExtractSummary
    val extractTestsFromReport get() = labTests.extractTestsFromReport
    val selectLabTestName get() = labTests.selectLabTestName
    val standardRange get() = labTests.standardRange
    val labTestsCountFormat get() = labTests.labTestsCountFormat
    val searchPlaceholder get() = labTests.searchPlaceholder
    val filterAbnormalOnly get() = labTests.filterAbnormalOnly
    val filterNormalOnly get() = labTests.filterNormalOnly
    val aiLabConsultation get() = labTests.aiLabConsultation
    val aiLabConsultationDesc get() = labTests.aiLabConsultationDesc
    val noTestsFoundSearch get() = labTests.noTestsFoundSearch
    val tryAnotherKeyword get() = labTests.tryAnotherKeyword
    val noTestsYet get() = labTests.noTestsYet
    val tapPlusToAdd get() = labTests.tapPlusToAdd
    val addNewTestBtn get() = labTests.addNewTestBtn
    val testDateLabel get() = labTests.testDateLabel
    val labHospitalLabel get() = labTests.labHospitalLabel
    val resultLabel get() = labTests.resultLabel
    val refRangeLabel get() = labTests.refRangeLabel
    val testNameLabel get() = labTests.testNameLabel
    val categoryLabel get() = labTests.categoryLabel
    val testValueLabel get() = labTests.testValueLabel
    val testUnitLabel get() = labTests.testUnitLabel
    val editTestTitle get() = labTests.editTestTitle
    val previousHistoryForTest get() = labTests.previousHistoryForTest
    val levelSignificance get() = labTests.levelSignificance

    val dailyTrackingTitle get() = daily.dailyTrackingTitle
    val vitalsTab get() = daily.vitalsTab
    val medicationsTab get() = daily.medicationsTab
    val medicationReminders get() = daily.medicationReminders
    val addMedication get() = daily.addMedication
    val editMedication get() = daily.editMedication
    val medicationName get() = daily.medicationName
    val dosage get() = daily.dosage
    val frequency get() = daily.frequency
    val instructions get() = daily.instructions
    val medicationTime get() = daily.medicationTime
    val markTaken get() = daily.markTaken
    val takenToday get() = daily.takenToday
    val pendingToday get() = daily.pendingToday
    val activeIngredientsLabel get() = daily.activeIngredientsLabel
    val sideEffectsLabel get() = daily.sideEffectsLabel
    val usesLabel get() = daily.usesLabel
    val foodInteractionsLabel get() = daily.foodInteractionsLabel
    val importantInfoLabel get() = daily.importantInfoLabel
    val aiMedicationAnalysis get() = daily.aiMedicationAnalysis
    val hideAiDetails get() = daily.hideAiDetails
    val showAiDetails get() = daily.showAiDetails
    val fetchAiDetails get() = daily.fetchAiDetails
    val backgroundAlarmsInfo get() = daily.backgroundAlarmsInfo
    val logSugar get() = daily.logSugar
    val logPressure get() = daily.logPressure
    val logWeight get() = daily.logWeight
    val fasting get() = daily.fasting
    val postprandial get() = daily.postprandial
    val randomSugar get() = daily.randomSugar
    val systolic get() = daily.systolic
    val diastolic get() = daily.diastolic
    val dailyTrackingHeader get() = daily.dailyTrackingHeader
    val dailyTrackingSubtitle get() = daily.dailyTrackingSubtitle
    val notifActiveTitle get() = daily.notifActiveTitle
    val notifActiveSubtitle get() = daily.notifActiveSubtitle
    val enableNotifBtn get() = daily.enableNotifBtn
    val activeMedsHeader get() = daily.activeMedsHeader
    val inactiveMedsHeader get() = daily.inactiveMedsHeader
    val noMedsTitle get() = daily.noMedsTitle
    val noMedsSubtitle get() = daily.noMedsSubtitle
    val scanPrescriptionBtn get() = daily.scanPrescriptionBtn
    val addMedBtn get() = daily.addMedBtn
    val testAlarm get() = daily.testAlarm
    val takenTodayBadge get() = daily.takenTodayBadge
    val pendingTodayBadge get() = daily.pendingTodayBadge
    val doseTimeLabel get() = daily.doseTimeLabel
    val doseInstructionsLabel get() = daily.doseInstructionsLabel
    val frequencyLabel get() = daily.frequencyLabel
    val dailyFreq get() = daily.dailyFreq
    val twiceDailyFreq get() = daily.twiceDailyFreq
    val thriceDailyFreq get() = daily.thriceDailyFreq
    val weeklyFreq get() = daily.weeklyFreq
    val asNeededFreq get() = daily.asNeededFreq
    val beforeFood get() = daily.beforeFood
    val afterFood get() = daily.afterFood
    val withFood get() = daily.withFood
    val beforeSleep get() = daily.beforeSleep
    val onePill get() = daily.onePill
    val twoPills get() = daily.twoPills
    val syrupDose get() = daily.syrupDose
    val injectionDose get() = daily.injectionDose
    val dropsDose get() = daily.dropsDose
    val editMedicationTitle get() = daily.editMedicationTitle
    val bpScheduleTitle get() = daily.bpScheduleTitle
    val sugarScheduleTitle get() = daily.sugarScheduleTitle
    val weightScheduleTitle get() = daily.weightScheduleTitle
    val targetNormalSugar get() = daily.targetNormalSugar
    val targetNormalBp get() = daily.targetNormalBp
    val normalBmiRange get() = daily.normalBmiRange
    val deleteConfirmTitle get() = daily.deleteConfirmTitle
    val deleteConfirmMsg get() = daily.deleteConfirmMsg
    val medEnrichedSuccess get() = daily.medEnrichedSuccess
    val medUpdatedSuccess get() = daily.medUpdatedSuccess

    val resultsVitalsTitle get() = results.resultsVitalsTitle
    val resultsVitalsSubtitle get() = results.resultsVitalsSubtitle
    val chartsTab get() = results.chartsTab
    val historyTab get() = results.historyTab
    val summaryTab get() = results.summaryTab
    val bloodSugarTrend get() = results.bloodSugarTrend
    val bloodPressureTrend get() = results.bloodPressureTrend
    val weightTrend get() = results.weightTrend
    val last7Days get() = results.last7Days
    val last30Days get() = results.last30Days
    val allTime get() = results.allTime
    val avgSugar get() = results.avgSugar
    val avgBp get() = results.avgBp
    val latestWeightLabel get() = results.latestWeightLabel
    val normalRangeLabel get() = results.normalRangeLabel
    val noDataForChart get() = results.noDataForChart

    val aiAdvisorTitle get() = aiAdvisor.aiAdvisorTitle
    val fullAnalysisTab get() = aiAdvisor.fullAnalysisTab
    val chatTab get() = aiAdvisor.chatTab
    val askDoctorPlaceholder get() = aiAdvisor.askDoctorPlaceholder
    val send get() = aiAdvisor.send
    val generateNewAnalysis get() = aiAdvisor.generateNewAnalysis
    val analyzingMedicalData get() = aiAdvisor.analyzingMedicalData
    val medicalDisclaimer get() = aiAdvisor.medicalDisclaimer
    val activeMedicationsSection get() = aiAdvisor.activeMedicationsSection
    val clinicalRecommendations get() = aiAdvisor.clinicalRecommendations
    val laboratoryEvaluation get() = aiAdvisor.laboratoryEvaluation
    val cardiovascularStatus get() = aiAdvisor.cardiovascularStatus
    val nutritionAndDiet get() = aiAdvisor.nutritionAndDiet
    val aiAdvisorSubtitle get() = aiAdvisor.aiAdvisorSubtitle
    val quickPrompts get() = aiAdvisor.quickPrompts
    val promptDietForDiabetes get() = aiAdvisor.promptDietForDiabetes
    val promptDrugInteractions get() = aiAdvisor.promptDrugInteractions
    val promptLabInterpret get() = aiAdvisor.promptLabInterpret
    val promptLifestyleAdvice get() = aiAdvisor.promptLifestyleAdvice
    val askDoctorQuestion get() = aiAdvisor.askDoctorQuestion
    val disclaimerText get() = aiAdvisor.disclaimerText

    val healthProfileTitle get() = profile.healthProfileTitle
    val personalInfo get() = profile.personalInfo
    val fullName get() = profile.fullName
    val age get() = profile.age
    val gender get() = profile.gender
    val male get() = profile.male
    val female get() = profile.female
    val bloodType get() = profile.bloodType
    val heightCm get() = profile.heightCm
    val initialWeightKg get() = profile.initialWeightKg
    val chronicDiseases get() = profile.chronicDiseases
    val allergies get() = profile.allergies
    val pastSurgeries get() = profile.pastSurgeries
    val emergencyContact get() = profile.emergencyContact
    val personalInfoSection get() = profile.personalInfoSection
    val medicalHistorySection get() = profile.medicalHistorySection
    val emergencyContactSection get() = profile.emergencyContactSection
    val saveProfileBtn get() = profile.saveProfileBtn
    val profileSavedSuccess get() = profile.profileSavedSuccess
    val bloodTypeSelect get() = profile.bloodTypeSelect
    val profileAndSettings get() = profile.profileAndSettings
    val editBasicInfo get() = profile.editBasicInfo
    val changePhoto get() = profile.changePhoto
    val languageSetting get() = profile.languageSetting
    val languageSubtitle get() = profile.languageSubtitle
    val selectLanguage get() = profile.selectLanguage
    val autoDeviceLanguage get() = profile.autoDeviceLanguage
    val arabicLanguage get() = profile.arabicLanguage
    val englishLanguage get() = profile.englishLanguage
    val russianLanguage get() = profile.russianLanguage
    val darkMode get() = profile.darkMode
    val darkModeSubtitle get() = profile.darkModeSubtitle
    val googleDriveSync get() = profile.googleDriveSync
    val googleDriveTitle get() = profile.googleDriveTitle
    val driveSyncBadge get() = profile.driveSyncBadge
    val driveSyncSubtitle get() = profile.driveSyncSubtitle
    val dataReadyForSync get() = profile.dataReadyForSync
    val recordsCount get() = profile.recordsCount
    val exportToDrive get() = profile.exportToDrive
    val saveToFolder get() = profile.saveToFolder
    val syncHistoryTitle get() = profile.syncHistoryTitle
    val hideText get() = profile.hideText
    val showText get() = profile.showText
    val exportPdf get() = profile.exportPdf
    val clearRecords get() = profile.clearRecords
    val clearRecordsSubtitle get() = profile.clearRecordsSubtitle
    val logout get() = profile.logout
    val aboutApp get() = profile.aboutApp
    val appVersion get() = profile.appVersion
    val importFromGoogleDrive get() = profile.importFromGoogleDrive
    val syncStatusSuccess get() = profile.syncStatusSuccess
    val emergencyCallDoctor get() = profile.emergencyCallDoctor
    val emergencyCallAmbulance get() = profile.emergencyCallAmbulance

    val isArabic get() = common.save == "حفظ"
    val isRussian get() = common.save == "Сохранить"

    val todayOverview get() = if (isArabic) "نظرة عامة اليوم" else if (isRussian) "Обзор за сегодня" else "Today's Overview"
    val logNewTest get() = if (isArabic) "تسجيل فحص جديد" else if (isRussian) "Новый замер" else "Log New Reading"
    val scanOrUploadReport get() = if (isArabic) "تصوير فحص أو تحليل" else if (isRussian) "Сканировать анализ" else "Scan Lab Report"
    val addMedicationQuick get() = if (isArabic) "إضافة دواء" else if (isRussian) "Добавить лекарство" else "Add Medication"
    val latestSugarReading get() = if (isArabic) "آخر فحص السكر" else if (isRussian) "Последний замер сахара" else "Latest Blood Sugar"
    val latestWeightReading get() = if (isArabic) "آخر قياس للوزن" else if (isRussian) "Последний замер веса" else "Latest Weight"
    val latestBpReading get() = if (isArabic) "آخر قياس لضغط الدم" else if (isRussian) "Последний замер давления" else "Latest Blood Pressure"
    val recentHistoryTitle get() = if (isArabic) "السجل الأخير" else if (isRussian) "Последние записи" else "Recent History"
    val labResultsTitle get() = if (isArabic) "الفحوصات" else if (isRussian) "Анализы" else "Lab Results"
    val smartAnalysis get() = if (isArabic) "تحليل ذكي" else if (isRussian) "Умный анализ" else "Smart AI"
    val criticalDanger get() = if (isArabic) "خطر / عالي" else if (isRussian) "Опасно / Высокий" else "Critical / High"
    val emergencyQuickConsult get() = if (isArabic) "استشارة طارئة فورية" else if (isRussian) "Экстренная консультация" else "Emergency Consultation"
    val profilePhoto get() = if (isArabic) "الصورة الشخصية" else if (isRussian) "Фото профиля" else "Profile Photo"
    val welcome get() = if (isArabic) "مرحباً" else if (isRussian) "Привет" else "Welcome"
    val clearAllDataTitle get() = if (isArabic) "تأكيد حذف كافة البيانات وإعادة الضبط" else if (isRussian) "Подтверждение удаления данных" else "Confirm Reset & Delete All Data"
    val clearAllDataMsg get() = if (isArabic) "هل تريد مسح كافة البيانات والفحوصات والأدوية وقراءات السكر وضغط الدم وحذف المعلومات بالوضع الافتراضي للبدء بسجل نظيف وفارغ تماماً؟" else if (isRussian) "Вы уверены, что хотите удалить все медицинские данные?" else "Do you want to clear all health records, vitals, and medications to start with a clean slate?"
    val clearAllConfirmBtn get() = if (isArabic) "نعم، مسح وحذف الكل" else if (isRussian) "Да, удалить все" else "Yes, Delete All"
    val clearAllSuccess get() = if (isArabic) "تم مسح كافة البيانات وحذف المعلومات الافتراضية بنجاح" else if (isRussian) "Все данные успешно очищены" else "All medical data has been cleared successfully"

    val twoDaysAgo get() = if (isArabic) "قبل يومين" else if (isRussian) "2 дня назад" else "2 days ago"
    val currentTime get() = if (isArabic) "الوقت الحالي" else if (isRussian) "Текущее время" else "Current Time"
    val selectTime get() = if (isArabic) "تحديد وقت دقيق" else if (isRussian) "Выбрать время" else "Select Time"
    val addCustomTest get() = if (isArabic) "إضافة اسم تحليل" else if (isRussian) "Добавить анализ" else "Add Test Name"
    val testNamePlaceholder get() = if (isArabic) "اختر من القائمة أو اكتب اسم الفحص..." else if (isRussian) "Выберите из списка или введите название..." else "Select from list or type test name..."
    val clearAll get() = if (isArabic) "مسح" else if (isRussian) "Очистить" else "Clear"
    val popular get() = if (isArabic) "شائع:" else if (isRussian) "Популярные:" else "Popular:"
    val useEnteredName get() = if (isArabic) "استخدام الاسم المدخل" else if (isRussian) "Использовать введенное" else "Use Entered Name"
    val confirmSelectTest get() = if (isArabic) "تأكيد" else if (isRussian) "Подтвердить" else "Confirm"
    val closeList get() = if (isArabic) "إغلاق القائمة" else if (isRussian) "Закрыть список" else "Close List"
    val previous get() = if (isArabic) "السابق" else if (isRussian) "Назад" else "Previous"
    val next get() = if (isArabic) "التالي" else if (isRussian) "Далее" else "Next"
    val skip get() = if (isArabic) "تخطي" else if (isRussian) "Пропустить" else "Skip"
    val getStarted get() = if (isArabic) "ابدأ الآن" else if (isRussian) "Начать" else "Get Started"
    val medicalNotes get() = if (isArabic) "ملاحظات طبية" else if (isRussian) "Медицинские заметки" else "Medical Notes"
    val calendarPick get() = if (isArabic) "تقويم..." else if (isRussian) "Календарь..." else "Calendar..."
    val saveEdit get() = if (isArabic) "حفظ التعديل" else if (isRussian) "Сохранить изменения" else "Save Changes"
    val addPastReading get() = if (isArabic) "إضافة قراءة سابقة" else if (isRussian) "Добавить прошлый замер" else "Add Past Reading"
    val addTestRecordNow get() = if (isArabic) "إضافة تحليل جديد الآن" else if (isRussian) "Добавить новый анализ" else "Add New Test Now"
    val noMatchingTests get() = if (isArabic) "لم يتم العثور على تحليل مطابق للاسم المدخل" else if (isRussian) "Не найдено анализов по запросу" else "No matching test found"
    val myCustomTests get() = if (isArabic) "تحاليلك المخصصة" else if (isRussian) "Ваши анализы" else "Your Custom Tests"
    val commonMedicalTests get() = if (isArabic) "التحاليل الطبية الشائعة" else if (isRussian) "Популярные анализы" else "Common Medical Tests"
    val addCustomTestTitle get() = if (isArabic) "إضافة اسم تحليل جديد للقائمة" else if (isRussian) "Добавить новый анализ в список" else "Add New Test to List"
    val addCustomTestHint get() = if (isArabic) "سيتم حفظ هذا التحليل في قائمتك بحيث يمكنك اختياره بضغطة زر في أي وقت." else if (isRussian) "Этот анализ сохранится в вашем списке для быстрого выбора." else "This test will be saved to your list for quick selection anytime."
    val testNameInputLabel get() = if (isArabic) "اسم الفحص (بالعربية أو الإنجليزية) *" else if (isRussian) "Название анализа *" else "Test Name *"
    val testNameInputPlaceholder get() = if (isArabic) "مثال: فيتامين سي، Ferritin..." else if (isRussian) "Например: Ферритин, Витамин C..." else "e.g. Vitamin C, Ferritin..."
    val medicalCategory get() = if (isArabic) "الفئة الطبية:" else if (isRussian) "Медицинская категория:" else "Medical Category:"
    val defaultUnit get() = if (isArabic) "الوحدة الافتراضية" else if (isRussian) "Единица измерения" else "Default Unit"
    val normalRangeInput get() = if (isArabic) "المعدل الطبيعي" else if (isRussian) "Нормальный диапазон" else "Reference Range"
    val saveAndSelectTest get() = if (isArabic) "حفظ واختيار التحليل" else if (isRussian) "Сохранить и выбрать" else "Save & Select Test"

    val onbTitle1 get() = if (isArabic) "رعاية صحتك أولويتنا" else if (isRussian) "Ваше здоровье — наш приоритет" else "Your Health Is Our Priority"
    val onbDesc1 get() = if (isArabic) "تابع مؤشراتك الحيوية، الفحوصات المخبرية، وجدول أدويتك اليومي بسهولة وأمان عبر تطبيق $appName." else if (isRussian) "Отслеживайте показатели здоровья, анализы и прием лекарств в приложении $appName." else "Track your daily vitals, lab test results, and medication schedule seamlessly with $appName."
    val onbBadge1 get() = if (isArabic) "سجل طبي ذكي" else if (isRussian) "Умная медкарта" else "Smart Health Record"

    val onbTitle2 get() = if (isArabic) "قراءة التحاليل والوصفات الطبية" else if (isRussian) "Сканирование анализов и рецептов" else "Smart Lab & Rx Scanning"
    val onbDesc2 get() = if (isArabic) "ارفع تقارير المختبر بصيغة PDF أو صوّر الوصفة الطبية لاستخراج النتائج وتفعيل منبهات الجرعات تلقائياً." else if (isRussian) "Загружайте PDF-отчеты или фотографируйте рецепты для автоматического извлечения данных и напоминаний." else "Upload PDF lab reports or snap a photo of your prescription to extract results and set dose alarms automatically."
    val onbBadge2 get() = if (isArabic) "استخراج ذكي فوري" else if (isRussian) "Быстрое распознавание" else "Instant AI Extraction"

    val onbTitle3 get() = if (isArabic) "مستشارك الطبي بالذكاء الاصطناعي" else if (isRussian) "Медицинский AI-ассистент" else "AI Medical Advisor"
    val onbDesc3 get() = if (isArabic) "تحليل دقيق للتقارير الطبية وفحوصات الدم مع إرشادات يومية لنمط الحياة، التغذية، والجرعات الدوائية." else if (isRussian) "Точный анализ медицинских отчетов и крови с персональными рекомендациями." else "Accurate analysis of lab reports and vitals with daily diet, lifestyle, and medication reminders."
    val onbBadge3 get() = if (isArabic) "ذكاء اصطناعي 2026" else if (isRussian) "ИИ 2026" else "AI Health 2026"

    val authWelcomeTitle get() = if (isArabic) "مرحباً بك في $appName" else if (isRussian) "Добро пожаловать в $appName" else "Welcome to $appName"
    val authSubtitleLogin get() = if (isArabic) "سجّل الدخول إلى ملفك الصحي الموحد" else if (isRussian) "Войдите в свою медкарту" else "Sign in to your unified health record"
    val authSubtitleRegister get() = if (isArabic) "أنشئ حساباً جديداً للوصول لكافة الخدمات" else if (isRussian) "Создайте аккаунт для доступа ко всем услугам" else "Create a new account to access all health services"
    val loginTitle get() = if (isArabic) "تسجيل الدخول" else if (isRussian) "Вход" else "Sign In"
    val registerTitle get() = if (isArabic) "إنشاء حساب" else if (isRussian) "Регистрация" else "Create Account"
    val emailOrPhone get() = if (isArabic) "البريد الإلكتروني / رقم الهاتف" else if (isRussian) "Email или телефон" else "Email / Phone Number"
    val emailOrPhonePlaceholder get() = if (isArabic) "name@example.com أو +966" else if (isRussian) "name@example.com или +7" else "name@example.com or +1"
    val fullNamePlaceholder get() = if (isArabic) "مثال: أحمد محمد" else if (isRussian) "Например: Иван Иванов" else "e.g. John Doe"
    val passwordLabel get() = if (isArabic) "كلمة المرور" else if (isRussian) "Пароль" else "Password"
    val forgotPassword get() = if (isArabic) "نسيت كلمة المرور؟" else if (isRussian) "Забыли пароль?" else "Forgot password?"
    val resetPasswordSent get() = if (isArabic) "تم إرسال رابط استعادة كلمة المرور" else if (isRussian) "Ссылка для сброса пароля отправлена" else "Password reset link sent"
    val loginAndProceed get() = if (isArabic) "تسجيل الدخول والمتابعة" else if (isRussian) "Войти и продолжить" else "Sign In & Continue"
    val registerAndProceed get() = if (isArabic) "إنشاء الحساب والمتابعة" else if (isRussian) "Зарегистрироваться и продолжить" else "Create Account & Continue"
    val orDivider get() = if (isArabic) "أو" else if (isRussian) "или" else "or"
    val biometricLogin get() = if (isArabic) "الدخول بالبصمة الحيوية" else if (isRussian) "Вход по биометрии" else "Biometric Sign In"
    val biometricSuccess get() = if (isArabic) "تم التحقق بالبصمة بنجاح!" else if (isRussian) "Биометрия успешно подтверждена!" else "Biometric verification successful!"
    val phoneOtpLogin get() = if (isArabic) "الدخول برقم الهاتف و OTP" else if (isRussian) "Вход по SMS коду (OTP)" else "Sign In with Phone & OTP"
    val otpSentSuccess get() = if (isArabic) "تم إرسال رمز التحقق OTP إلى هاتفك" else if (isRussian) "Код OTP отправлен на ваш телефон" else "Verification code sent to your phone"
    val sampleUserName get() = if (isArabic) "أحمد محمد" else if (isRussian) "Алексей Смирнов" else "Alex Smith"

    val uploadReportHeader get() = if (isArabic) "رفع وتحليل تقارير الفحوصات" else if (isRussian) "Загрузка и анализ отчетов" else "Upload & Analyze Lab Reports"
    val uploadReportSubheader get() = if (isArabic) "بصيغة PDF أو صورة مخبرية بالذكاء الاصطناعي" else if (isRussian) "В формате PDF или фото с помощью ИИ" else "PDF format or lab report photo via AI"
    val pdfUploadTitle get() = if (isArabic) "رفع مستند PDF للتحاليل" else if (isRussian) "Загрузить PDF-документ анализов" else "Upload Lab PDF Document"
    val pdfUploadSubtitle get() = if (isArabic) "يدعم التقارير المخبرية الرسمية المتعددة الصفحات" else if (isRussian) "Поддержка многостраничных отчетов" else "Supports multi-page official medical lab reports"
    val imageUploadTitle get() = if (isArabic) "رفع صورة الفحص (JPG / PNG)" else if (isRussian) "Загрузить фото анализа (JPG / PNG)" else "Upload Lab Report Photo (JPG / PNG)"
    val imageUploadSubtitle get() = if (isArabic) "صورة ملتقطة بالكاميرا أو محفوظة في الاستوديو" else if (isRussian) "Фото с камеры или из галереи" else "Photo captured with camera or from gallery"
    val uploadReportChooseMethod get() = if (isArabic) "اختر طريقة رفع التقرير الطبي لاستخراج الفحوصات وتخزينها:" else if (isRussian) "Выберите способ загрузки мед. отчета:" else "Choose a method to upload your report to extract tests:"
    val reportNotesPlaceholder get() = if (isArabic) "مثال: مختبر البرج، صائم 10 ساعات..." else if (isRussian) "Например: лаборатория, натощак..." else "e.g. Central Lab, fasting 10 hours..."
    val reportNotesLabel get() = if (isArabic) "ملاحظات إضافية أو اسم المختبر (اختياري)" else if (isRussian) "Доп. примечания или лаборатория (необязательно)" else "Additional notes or lab name (optional)"
    val extractedTestsTitle get() = if (isArabic) "قائمة الفحوصات المستخرجة:" else if (isRussian) "Извлеченные анализы:" else "Extracted Lab Tests:"
    val finishAndSave get() = if (isArabic) "تم وإنهاء" else if (isRussian) "Готово" else "Done & Save"
    val previewReport get() = if (isArabic) "معاينة التقرير" else if (isRussian) "Предпросмотр" else "Preview Report"
    val pdfDocumentLabel get() = if (isArabic) "مستند PDF مخبري" else if (isRussian) "PDF документ" else "Lab PDF Document"
    val imageDocumentLabel get() = if (isArabic) "صورة فحص مخبري" else if (isRussian) "Фото анализа" else "Lab Report Photo"

    val scanPrescriptionHeader get() = if (isArabic) "مسح الوصفة الطبية بالذكاء الاصطناعي" else if (isRussian) "Сканирование рецепта с помощью ИИ" else "Scan Medical Prescription with AI"
    val scanPrescriptionSubheader get() = if (isArabic) "استخراج أسماء الأدوية والجرعات والتذكيرات آلياً" else if (isRussian) "Автоматическое извлечение названий, дозировок и напоминаний" else "Automatically extract medication names, dosages, and reminders"
    val choosePrescriptionMethod get() = if (isArabic) "1. اختر طريقة إضافة الوصفة الطبية:" else if (isRussian) "1. Выберите способ добавления рецепта:" else "1. Choose method to add prescription:"
    val cameraCaptureTitle get() = if (isArabic) "تصوير بالكاميرا" else if (isRussian) "Снимок камерой" else "Take Photo"
    val cameraCaptureSubtitle get() = if (isArabic) "التقاط الروشتة فوراً" else if (isRussian) "Сфотографировать рецепт" else "Capture prescription immediately"
    val galleryImageTitle get() = if (isArabic) "صورة من المعرض" else if (isRussian) "Из галереи" else "From Gallery"
    val galleryImageSubtitle get() = if (isArabic) "اختيار صورة محفوظة" else if (isRussian) "Выбрать сохраненное фото" else "Choose saved photo"
    val pdfPrescriptionTitle get() = if (isArabic) "مستند PDF" else if (isRussian) "PDF документ" else "PDF Document"
    val pdfPrescriptionSubtitle get() = if (isArabic) "تقرير أو وصفة رقمية" else if (isRussian) "Электронный рецепт или отчет" else "Digital report or e-prescription"
    val prescriptionNotesPlaceholder get() = if (isArabic) "ملاحظات إضافية (اختياري - مثل اسم دواء أو تعليمات الطبيب)" else if (isRussian) "Дополнительные примечания (необязательно)" else "Additional notes (optional - e.g. doctor's instructions)"
    val analyzePrescriptionBtn get() = if (isArabic) "تحليل الوصفة واستخراج الأدوية (AI)" else if (isRussian) "Анализировать рецепт и извлечь лекарства (ИИ)" else "Analyze Prescription & Extract Meds (AI)"
    val analyzingPrescriptionText get() = if (isArabic) "جاري قراءة خط الطبيب واستخراج الأدوية..." else if (isRussian) "Распознавание почерка врача и извлечение лекарств..." else "Reading doctor's handwriting & extracting medications..."
    val extractedMedicationsTitle get() = if (isArabic) "الأدوية المستخرجة" else if (isRussian) "Извлеченные лекарства" else "Extracted Medications"
    val addAnotherMedication get() = if (isArabic) "إضافة دواء آخر" else if (isRussian) "Добавить еще лекарство" else "Add Another Medication"
    val saveMedicationsAndReminders get() = if (isArabic) "حفظ الأدوية وتفعيل التذكيرات" else if (isRussian) "Сохранить лекарства и включить напоминания" else "Save Medications & Set Reminders"
    val dosageLabel get() = if (isArabic) "الجرعة" else if (isRussian) "Дозировка" else "Dosage"
    val medNamePlaceholder get() = if (isArabic) "اسم الدواء (مثل: Rosuvastatin, Amlodipine)" else if (isRussian) "Название лекарства (например: Rosuvastatin)" else "Medication name (e.g., Rosuvastatin, Amlodipine)"
    val medDosagePlaceholder get() = if (isArabic) "الجرعة (مثل: 20 mg، قرص واحد، 5 مل)" else if (isRussian) "Дозировка (например: 20 мг, 1 таблетка)" else "Dosage (e.g. 20 mg, 1 tablet, 5 ml)"
    val usageInstructionsLabel get() = if (isArabic) "تعليمات الاستخدام:" else if (isRussian) "Инструкции по применению:" else "Usage instructions:"
    val dosageFrequencyLabel get() = if (isArabic) "تكرار الجرعة:" else if (isRussian) "Частота приема:" else "Frequency:"
    val dailyReminderTimeLabel get() = if (isArabic) "توقيت التنبيه اليومي:" else if (isRussian) "Время ежедневного напоминания:" else "Daily reminder time:"
    val hourLabel get() = if (isArabic) "الساعة (0-23)" else if (isRussian) "Час (0-23)" else "Hour (0-23)"
    val minuteLabel get() = if (isArabic) "الدقيقة (0-59)" else if (isRussian) "Минута (0-59)" else "Minute (0-59)"
    val addMedToPrescriptionTitle get() = if (isArabic) "إضافة دواء إلى الوصفة" else if (isRussian) "Добавить лекарство в рецепт" else "Add Medication to Prescription"
    val editMedInPrescriptionTitle get() = if (isArabic) "تعديل بيانات الدواء" else if (isRussian) "Редактировать данные лекарства" else "Edit Medication Details"
    val pharmaAnalysisHeader get() = if (isArabic) "التحليل الصيدلاني للدواء (التركيبة والأعراض الجانبية)" else if (isRussian) "Фармацевтический анализ (состав и побочные эффекты)" else "Pharmaceutical Analysis (Ingredients & Side Effects)"
    val aiDataAndInteractionsLabel get() = if (isArabic) "بيانات الذكاء الاصطناعي والتداخلات الغذائية:" else if (isRussian) "Данные ИИ и пищевые взаимодействия:" else "AI Data & Food Interactions:"
    val saveChanges get() = if (isArabic) "حفظ التعديل" else if (isRussian) "Сохранить изменения" else "Save Changes"
    val prescriptionDocument get() = if (isArabic) "مستند الوصفة الطبية" else if (isRussian) "Документ рецепта" else "Prescription document"
    val pdfReadyForAnalysis get() = if (isArabic) "ملف PDF جاهز للتحليل" else if (isRussian) "PDF документ готов к анализу" else "PDF ready for AI analysis"
    val photoReadyForAnalysis get() = if (isArabic) "صورة واضحة جاهزة للتحليل بالذكاء الاصطناعي" else if (isRussian) "Четкое фото готово к ИИ-анализу" else "Clear photo ready for AI analysis"
    val deletePhoto get() = if (isArabic) "حذف الصورة" else if (isRussian) "Удалить фото" else "Delete photo"
    val previewPrescription get() = if (isArabic) "معاينة الوصفة" else if (isRussian) "Предпросмотр рецепта" else "Preview prescription"
    val autoExtractedAiNotes get() = if (isArabic) "مستخرج آلياً بواسطة الذكاء الاصطناعي من الوصفة الطبية" else if (isRussian) "Автоматически извлечено ИИ из рецепта" else "Auto-extracted by AI from prescription"

    // Google Sign-In Strings
    val googleSignInTitle get() = if (isArabic) "تسجيل الدخول بحساب Google" else if (isRussian) "Вход через Google" else "Sign in with Google"
    val googleSignInSubtitle get() = if (isArabic) "سجّل الدخول بحسابك في Google لحفظ بياناتك الصحية وتفعيل المستشار الطبي الذكي بأمان وخصوصية تامة." else if (isRussian) "Войдите через аккаунт Google, чтобы безопасно пользоваться умным медицинским помощником и сохранять данные." else "Sign in with your Google account to securely access the Smart Medical Advisor and protect your health records."
    val googleAccountPrivacyBadge get() = if (isArabic) "دخول سريع وآمن عبر حساب Google" else if (isRussian) "Быстрый и безопасный вход через Google" else "Fast & Secure Sign-In with Google"
    val signingInProgress get() = if (isArabic) "جاري التحقق من حساب Google..." else if (isRussian) "Выполняется вход через Google..." else "Signing in with Google..."
    val signedInAccountLabel get() = if (isArabic) "الحساب المتصل" else if (isRussian) "Подключенный аккаунт" else "Signed-in Account"
    val signOutSubtitle get() = if (isArabic) "تسجيل الخروج من حساب Google الحالي" else if (isRussian) "Выйти из текущего аккаунта Google" else "Sign out of your current Google account"
    val signOutConfirmTitle get() = if (isArabic) "تسجيل الخروج" else if (isRussian) "Выход из аккаунта" else "Sign Out"
    val signOutConfirmMsg get() = if (isArabic) "هل تريد تسجيل الخروج من حساب Google الخاص بك؟" else if (isRussian) "Вы уверены, что хотите выйти из аккаунта Google?" else "Are you sure you want to sign out of your Google account?"
    val aiAnalysisErrorTitle get() = if (isArabic) "تعذر إتمام التحليل الذكي" else if (isRussian) "Ошибка анализа ИИ" else "AI Analysis Failed"
    val retryBtn get() = if (isArabic) "إعادة المحاولة" else if (isRussian) "Повторить" else "Retry"
    val dismissBtn get() = if (isArabic) "إغلاق" else if (isRussian) "Закрыть" else "Dismiss"
}

// Sub-groups split to avoid > 30 parameter constructor register limits in ART/DEX

class CommonStrings(
    val general: CommonGeneralStrings,
    val statusGroup: CommonStatusStrings
) {
    val appName get() = general.appName
    val save get() = general.save
    val cancel get() = general.cancel
    val close get() = general.close
    val delete get() = general.delete
    val edit get() = general.edit
    val add get() = general.add
    val confirm get() = general.confirm
    val all get() = general.all
    val today get() = general.today
    val yesterday get() = general.yesterday
    val date get() = general.date
    val time get() = general.time
    val notes get() = general.notes
    val loading get() = general.loading
    val search get() = general.search

    val status get() = statusGroup.status
    val normal get() = statusGroup.normal
    val high get() = statusGroup.high
    val low get() = statusGroup.low
    val critical get() = statusGroup.critical
    val active get() = statusGroup.active
    val inactive get() = statusGroup.inactive
    val yes get() = statusGroup.yes
    val no get() = statusGroup.no
    val success get() = statusGroup.success
    val error get() = statusGroup.error
    val refresh get() = statusGroup.refresh
}

data class CommonGeneralStrings(
    val appName: String, val save: String, val cancel: String, val close: String, val delete: String,
    val edit: String, val add: String, val confirm: String, val all: String, val today: String,
    val yesterday: String, val date: String, val time: String, val notes: String, val loading: String,
    val search: String
)

data class CommonStatusStrings(
    val status: String, val normal: String, val high: String, val low: String, val critical: String,
    val active: String, val inactive: String, val yes: String, val no: String, val success: String,
    val error: String, val refresh: String
)

data class NavStrings(
    val navHome: String, val navDaily: String, val navResults: String, val navAiAdvisor: String,
    val navProfile: String
)

class HomeStrings(
    val p1: HomePart1Strings,
    val p2: HomePart2Strings,
    val p3: HomePart3Strings
) {
    val welcomeUser get() = p1.welcomeUser
    val dailyHealthSummary get() = p1.dailyHealthSummary
    val quickVitals get() = p1.quickVitals
    val bloodSugar get() = p1.bloodSugar
    val bloodPressure get() = p1.bloodPressure
    val weight get() = p1.weight
    val pulse get() = p1.pulse
    val bmi get() = p1.bmi
    val quickActions get() = p1.quickActions
    val addLabTest get() = p1.addLabTest
    val logVitals get() = p1.logVitals
    val consultAi get() = p1.consultAi
    val uploadPrescription get() = p1.uploadPrescription
    val recentLabTests get() = p1.recentLabTests
    val viewAll get() = p1.viewAll
    val emergencyCall get() = p1.emergencyCall
    val dailyHealthTips get() = p1.dailyHealthTips
    val emergencyTitle get() = p1.emergencyTitle
    val emergencySubtitle get() = p1.emergencySubtitle
    val call123 get() = p1.call123

    val currentReadingsSummary get() = p2.currentReadingsSummary
    val fullHistory get() = p2.fullHistory
    val noReadings get() = p2.noReadings
    val kgUnit get() = p2.kgUnit
    val todayMultipleReadings get() = p2.todayMultipleReadings
    val noReadingsToday get() = p2.noReadingsToday
    val tapCardsToLog get() = p2.tapCardsToLog
    val viewPastReadings get() = p2.viewPastReadings
    val consultAiComprehensive get() = p2.consultAiComprehensive
    val scanPrescriptionTitle get() = p2.scanPrescriptionTitle
    val scanPrescriptionSubtitle get() = p2.scanPrescriptionSubtitle
    val uploadLabReportTitle get() = p2.uploadLabReportTitle
    val uploadLabReportSubtitle get() = p2.uploadLabReportSubtitle
    val aiBadge get() = p2.aiBadge
    val logSugarTitle get() = p2.logSugarTitle
    val logBpTitle get() = p2.logBpTitle
    val logWeightTitle get() = p2.logWeightTitle
    val fastingOption get() = p2.fastingOption
    val postBreakfast get() = p2.postBreakfast
    val beforeLunch get() = p2.beforeLunch

    val afterLunch get() = p3.afterLunch
    val beforeDinner get() = p3.beforeDinner
    val afterDinner get() = p3.afterDinner
    val beforeBed get() = p3.beforeBed
    val randomOption get() = p3.randomOption
    val morningOption get() = p3.morningOption
    val noonOption get() = p3.noonOption
    val eveningOption get() = p3.eveningOption
    val nightOption get() = p3.nightOption
    val systolicLabel get() = p3.systolicLabel
    val diastolicLabel get() = p3.diastolicLabel
    val pulseLabel get() = p3.pulseLabel
    val measurementPeriod get() = p3.measurementPeriod
    val weightKgLabel get() = p3.weightKgLabel
    val heightCmLabel get() = p3.heightCmLabel
    val bmiLabel get() = p3.bmiLabel
    val sugarSavedSuccess get() = p3.sugarSavedSuccess
    val bpSavedSuccess get() = p3.bpSavedSuccess
    val weightSavedSuccess get() = p3.weightSavedSuccess
    val pleaseEnterValidNumber get() = p3.pleaseEnterValidNumber
}

data class HomePart1Strings(
    val welcomeUser: String, val dailyHealthSummary: String, val quickVitals: String,
    val bloodSugar: String, val bloodPressure: String, val weight: String, val pulse: String,
    val bmi: String, val quickActions: String, val addLabTest: String, val logVitals: String,
    val consultAi: String, val uploadPrescription: String, val recentLabTests: String,
    val viewAll: String, val emergencyCall: String, val dailyHealthTips: String,
    val emergencyTitle: String, val emergencySubtitle: String, val call123: String
)

data class HomePart2Strings(
    val currentReadingsSummary: String, val fullHistory: String, val noReadings: String,
    val kgUnit: String, val todayMultipleReadings: String, val noReadingsToday: String,
    val tapCardsToLog: String, val viewPastReadings: String, val consultAiComprehensive: String,
    val scanPrescriptionTitle: String, val scanPrescriptionSubtitle: String,
    val uploadLabReportTitle: String, val uploadLabReportSubtitle: String, val aiBadge: String,
    val logSugarTitle: String, val logBpTitle: String, val logWeightTitle: String,
    val fastingOption: String, val postBreakfast: String, val beforeLunch: String
)

data class HomePart3Strings(
    val afterLunch: String, val beforeDinner: String, val afterDinner: String,
    val beforeBed: String, val randomOption: String, val morningOption: String,
    val noonOption: String, val eveningOption: String, val nightOption: String,
    val systolicLabel: String, val diastolicLabel: String, val pulseLabel: String,
    val measurementPeriod: String, val weightKgLabel: String, val heightCmLabel: String,
    val bmiLabel: String, val sugarSavedSuccess: String, val bpSavedSuccess: String,
    val weightSavedSuccess: String, val pleaseEnterValidNumber: String
)

class LabTestsStrings(
    val p1: LabTestsPart1Strings,
    val p2: LabTestsPart2Strings
) {
    val labTestsTitle get() = p1.labTestsTitle
    val uploadPdfOrImage get() = p1.uploadPdfOrImage
    val addManualTest get() = p1.addManualTest
    val testName get() = p1.testName
    val category get() = p1.category
    val resultValue get() = p1.resultValue
    val unit get() = p1.unit
    val referenceRange get() = p1.referenceRange
    val labName get() = p1.labName
    val filterAll get() = p1.filterAll
    val filterAbnormal get() = p1.filterAbnormal
    val filterNormal get() = p1.filterNormal
    val aiExtractSummary get() = p1.aiExtractSummary
    val extractTestsFromReport get() = p1.extractTestsFromReport
    val selectLabTestName get() = p1.selectLabTestName
    val standardRange get() = p1.standardRange
    val labTestsCountFormat get() = p1.labTestsCountFormat
    val searchPlaceholder get() = p1.searchPlaceholder
    val filterAbnormalOnly get() = p1.filterAbnormalOnly
    val filterNormalOnly get() = p1.filterNormalOnly

    val aiLabConsultation get() = p2.aiLabConsultation
    val aiLabConsultationDesc get() = p2.aiLabConsultationDesc
    val noTestsFoundSearch get() = p2.noTestsFoundSearch
    val tryAnotherKeyword get() = p2.tryAnotherKeyword
    val noTestsYet get() = p2.noTestsYet
    val tapPlusToAdd get() = p2.tapPlusToAdd
    val addNewTestBtn get() = p2.addNewTestBtn
    val testDateLabel get() = p2.testDateLabel
    val labHospitalLabel get() = p2.labHospitalLabel
    val resultLabel get() = p2.resultLabel
    val refRangeLabel get() = p2.refRangeLabel
    val testNameLabel get() = p2.testNameLabel
    val categoryLabel get() = p2.categoryLabel
    val testValueLabel get() = p2.testValueLabel
    val testUnitLabel get() = p2.testUnitLabel
    val editTestTitle get() = p2.editTestTitle
    val previousHistoryForTest get() = p2.previousHistoryForTest
    val levelSignificance get() = p2.levelSignificance
}

data class LabTestsPart1Strings(
    val labTestsTitle: String, val uploadPdfOrImage: String, val addManualTest: String,
    val testName: String, val category: String, val resultValue: String, val unit: String,
    val referenceRange: String, val labName: String, val filterAll: String,
    val filterAbnormal: String, val filterNormal: String, val aiExtractSummary: String,
    val extractTestsFromReport: String, val selectLabTestName: String, val standardRange: String,
    val labTestsCountFormat: String, val searchPlaceholder: String, val filterAbnormalOnly: String,
    val filterNormalOnly: String
)

data class LabTestsPart2Strings(
    val aiLabConsultation: String, val aiLabConsultationDesc: String, val noTestsFoundSearch: String,
    val tryAnotherKeyword: String, val noTestsYet: String, val tapPlusToAdd: String,
    val addNewTestBtn: String, val testDateLabel: String, val labHospitalLabel: String,
    val resultLabel: String, val refRangeLabel: String, val testNameLabel: String,
    val categoryLabel: String, val testValueLabel: String, val testUnitLabel: String,
    val editTestTitle: String, val previousHistoryForTest: String, val levelSignificance: String
)

class DailyStrings(
    val p1: DailyPart1Strings,
    val p2: DailyPart2Strings,
    val p3: DailyPart3Strings,
    val p4: DailyPart4Strings
) {
    val dailyTrackingTitle get() = p1.dailyTrackingTitle
    val vitalsTab get() = p1.vitalsTab
    val medicationsTab get() = p1.medicationsTab
    val medicationReminders get() = p1.medicationReminders
    val addMedication get() = p1.addMedication
    val editMedication get() = p1.editMedication
    val medicationName get() = p1.medicationName
    val dosage get() = p1.dosage
    val frequency get() = p1.frequency
    val instructions get() = p1.instructions
    val medicationTime get() = p1.medicationTime
    val markTaken get() = p1.markTaken
    val takenToday get() = p1.takenToday
    val pendingToday get() = p1.pendingToday
    val activeIngredientsLabel get() = p1.activeIngredientsLabel
    val sideEffectsLabel get() = p1.sideEffectsLabel
    val usesLabel get() = p1.usesLabel
    val foodInteractionsLabel get() = p1.foodInteractionsLabel
    val importantInfoLabel get() = p1.importantInfoLabel

    val aiMedicationAnalysis get() = p2.aiMedicationAnalysis
    val hideAiDetails get() = p2.hideAiDetails
    val showAiDetails get() = p2.showAiDetails
    val fetchAiDetails get() = p2.fetchAiDetails
    val backgroundAlarmsInfo get() = p2.backgroundAlarmsInfo
    val logSugar get() = p2.logSugar
    val logPressure get() = p2.logPressure
    val logWeight get() = p2.logWeight
    val fasting get() = p2.fasting
    val postprandial get() = p2.postprandial
    val randomSugar get() = p2.randomSugar
    val systolic get() = p2.systolic
    val diastolic get() = p2.diastolic
    val dailyTrackingHeader get() = p2.dailyTrackingHeader
    val dailyTrackingSubtitle get() = p2.dailyTrackingSubtitle
    val notifActiveTitle get() = p2.notifActiveTitle
    val notifActiveSubtitle get() = p2.notifActiveSubtitle
    val enableNotifBtn get() = p2.enableNotifBtn
    val activeMedsHeader get() = p2.activeMedsHeader

    val inactiveMedsHeader get() = p3.inactiveMedsHeader
    val noMedsTitle get() = p3.noMedsTitle
    val noMedsSubtitle get() = p3.noMedsSubtitle
    val scanPrescriptionBtn get() = p3.scanPrescriptionBtn
    val addMedBtn get() = p3.addMedBtn
    val testAlarm get() = p3.testAlarm
    val takenTodayBadge get() = p3.takenTodayBadge
    val pendingTodayBadge get() = p3.pendingTodayBadge
    val doseTimeLabel get() = p3.doseTimeLabel
    val doseInstructionsLabel get() = p3.doseInstructionsLabel
    val frequencyLabel get() = p3.frequencyLabel
    val dailyFreq get() = p3.dailyFreq
    val twiceDailyFreq get() = p3.twiceDailyFreq
    val thriceDailyFreq get() = p3.thriceDailyFreq
    val weeklyFreq get() = p3.weeklyFreq
    val asNeededFreq get() = p3.asNeededFreq
    val beforeFood get() = p3.beforeFood
    val afterFood get() = p3.afterFood
    val withFood get() = p3.withFood

    val beforeSleep get() = p4.beforeSleep
    val onePill get() = p4.onePill
    val twoPills get() = p4.twoPills
    val syrupDose get() = p4.syrupDose
    val injectionDose get() = p4.injectionDose
    val dropsDose get() = p4.dropsDose
    val editMedicationTitle get() = p4.editMedicationTitle
    val bpScheduleTitle get() = p4.bpScheduleTitle
    val sugarScheduleTitle get() = p4.sugarScheduleTitle
    val weightScheduleTitle get() = p4.weightScheduleTitle
    val targetNormalSugar get() = p4.targetNormalSugar
    val targetNormalBp get() = p4.targetNormalBp
    val normalBmiRange get() = p4.normalBmiRange
    val deleteConfirmTitle get() = p4.deleteConfirmTitle
    val deleteConfirmMsg get() = p4.deleteConfirmMsg
    val medEnrichedSuccess get() = p4.medEnrichedSuccess
    val medUpdatedSuccess get() = p4.medUpdatedSuccess
}

data class DailyPart1Strings(
    val dailyTrackingTitle: String, val vitalsTab: String, val medicationsTab: String,
    val medicationReminders: String, val addMedication: String, val editMedication: String,
    val medicationName: String, val dosage: String, val frequency: String, val instructions: String,
    val medicationTime: String, val markTaken: String, val takenToday: String, val pendingToday: String,
    val activeIngredientsLabel: String, val sideEffectsLabel: String, val usesLabel: String,
    val foodInteractionsLabel: String, val importantInfoLabel: String
)

data class DailyPart2Strings(
    val aiMedicationAnalysis: String, val hideAiDetails: String, val showAiDetails: String,
    val fetchAiDetails: String, val backgroundAlarmsInfo: String, val logSugar: String,
    val logPressure: String, val logWeight: String, val fasting: String, val postprandial: String,
    val randomSugar: String, val systolic: String, val diastolic: String,
    val dailyTrackingHeader: String, val dailyTrackingSubtitle: String, val notifActiveTitle: String,
    val notifActiveSubtitle: String, val enableNotifBtn: String, val activeMedsHeader: String
)

data class DailyPart3Strings(
    val inactiveMedsHeader: String, val noMedsTitle: String, val noMedsSubtitle: String,
    val scanPrescriptionBtn: String, val addMedBtn: String, val testAlarm: String,
    val takenTodayBadge: String, val pendingTodayBadge: String, val doseTimeLabel: String,
    val doseInstructionsLabel: String, val frequencyLabel: String, val dailyFreq: String,
    val twiceDailyFreq: String, val thriceDailyFreq: String, val weeklyFreq: String,
    val asNeededFreq: String, val beforeFood: String, val afterFood: String, val withFood: String
)

data class DailyPart4Strings(
    val beforeSleep: String, val onePill: String, val twoPills: String, val syrupDose: String,
    val injectionDose: String, val dropsDose: String, val editMedicationTitle: String,
    val bpScheduleTitle: String, val sugarScheduleTitle: String, val weightScheduleTitle: String,
    val targetNormalSugar: String, val targetNormalBp: String, val normalBmiRange: String,
    val deleteConfirmTitle: String, val deleteConfirmMsg: String, val medEnrichedSuccess: String,
    val medUpdatedSuccess: String
)

data class ResultsStrings(
    val resultsVitalsTitle: String, val resultsVitalsSubtitle: String, val chartsTab: String,
    val historyTab: String, val summaryTab: String, val bloodSugarTrend: String,
    val bloodPressureTrend: String, val weightTrend: String, val last7Days: String,
    val last30Days: String, val allTime: String, val avgSugar: String, val avgBp: String,
    val latestWeightLabel: String, val normalRangeLabel: String, val noDataForChart: String
)

data class AiAdvisorStrings(
    val aiAdvisorTitle: String, val fullAnalysisTab: String, val chatTab: String,
    val askDoctorPlaceholder: String, val send: String, val generateNewAnalysis: String,
    val analyzingMedicalData: String, val medicalDisclaimer: String,
    val activeMedicationsSection: String, val clinicalRecommendations: String,
    val laboratoryEvaluation: String, val cardiovascularStatus: String,
    val nutritionAndDiet: String, val aiAdvisorSubtitle: String, val quickPrompts: String,
    val promptDietForDiabetes: String, val promptDrugInteractions: String,
    val promptLabInterpret: String, val promptLifestyleAdvice: String,
    val askDoctorQuestion: String, val disclaimerText: String
)

class ProfileStrings(
    val p1: ProfilePart1Strings,
    val p2: ProfilePart2Strings
) {
    val healthProfileTitle get() = p1.healthProfileTitle
    val personalInfo get() = p1.personalInfo
    val fullName get() = p1.fullName
    val age get() = p1.age
    val gender get() = p1.gender
    val male get() = p1.male
    val female get() = p1.female
    val bloodType get() = p1.bloodType
    val heightCm get() = p1.heightCm
    val initialWeightKg get() = p1.initialWeightKg
    val chronicDiseases get() = p1.chronicDiseases
    val allergies get() = p1.allergies
    val pastSurgeries get() = p1.pastSurgeries
    val emergencyContact get() = p1.emergencyContact
    val personalInfoSection get() = p1.personalInfoSection
    val medicalHistorySection get() = p1.medicalHistorySection
    val emergencyContactSection get() = p1.emergencyContactSection
    val saveProfileBtn get() = p1.saveProfileBtn
    val profileSavedSuccess get() = p1.profileSavedSuccess
    val bloodTypeSelect get() = p1.bloodTypeSelect

    val profileAndSettings get() = p2.profileAndSettings
    val editBasicInfo get() = p2.editBasicInfo
    val changePhoto get() = p2.changePhoto
    val languageSetting get() = p2.languageSetting
    val languageSubtitle get() = p2.languageSubtitle
    val selectLanguage get() = p2.selectLanguage
    val autoDeviceLanguage get() = p2.autoDeviceLanguage
    val arabicLanguage get() = p2.arabicLanguage
    val englishLanguage get() = p2.englishLanguage
    val russianLanguage get() = p2.russianLanguage
    val darkMode get() = p2.darkMode
    val darkModeSubtitle get() = p2.darkModeSubtitle
    val googleDriveSync get() = p2.googleDriveSync
    val googleDriveTitle get() = p2.googleDriveTitle
    val driveSyncBadge get() = p2.driveSyncBadge
    val driveSyncSubtitle get() = p2.driveSyncSubtitle
    val dataReadyForSync get() = p2.dataReadyForSync
    val recordsCount get() = p2.recordsCount
    val exportToDrive get() = p2.exportToDrive
    val saveToFolder get() = p2.saveToFolder
    val syncHistoryTitle get() = p2.syncHistoryTitle
    val hideText get() = p2.hideText
    val showText get() = p2.showText
    val exportPdf get() = p2.exportPdf
    val clearRecords get() = p2.clearRecords
    val clearRecordsSubtitle get() = p2.clearRecordsSubtitle
    val logout get() = p2.logout
    val aboutApp get() = p2.aboutApp
    val appVersion get() = p2.appVersion
    val importFromGoogleDrive get() = p2.importFromGoogleDrive
    val syncStatusSuccess get() = p2.syncStatusSuccess
    val emergencyCallDoctor get() = p2.emergencyCallDoctor
    val emergencyCallAmbulance get() = p2.emergencyCallAmbulance
}

data class ProfilePart1Strings(
    val healthProfileTitle: String, val personalInfo: String, val fullName: String,
    val age: String, val gender: String, val male: String, val female: String, val bloodType: String,
    val heightCm: String, val initialWeightKg: String, val chronicDiseases: String,
    val allergies: String, val pastSurgeries: String, val emergencyContact: String,
    val personalInfoSection: String, val medicalHistorySection: String,
    val emergencyContactSection: String, val saveProfileBtn: String, val profileSavedSuccess: String,
    val bloodTypeSelect: String
)

data class ProfilePart2Strings(
    val profileAndSettings: String, val editBasicInfo: String, val changePhoto: String,
    val languageSetting: String, val languageSubtitle: String, val selectLanguage: String,
    val autoDeviceLanguage: String, val arabicLanguage: String, val englishLanguage: String,
    val russianLanguage: String, val darkMode: String, val darkModeSubtitle: String,
    val googleDriveSync: String, val exportPdf: String, val clearRecords: String,
    val clearRecordsSubtitle: String, val logout: String, val aboutApp: String,
    val appVersion: String, val importFromGoogleDrive: String, val syncStatusSuccess: String,
    val emergencyCallDoctor: String, val emergencyCallAmbulance: String,
    val googleDriveTitle: String = "Google Drive Sync",
    val driveSyncBadge: String = "Secure Cloud",
    val driveSyncSubtitle: String = "Auto backup & instant sync across devices",
    val dataReadyForSync: String = "Records ready for backup",
    val recordsCount: String = "records",
    val exportToDrive: String = "Export to Drive",
    val saveToFolder: String = "Save as file...",
    val syncHistoryTitle: String = "Sync History & Backups",
    val hideText: String = "Hide",
    val showText: String = "Show"
)

// ----------------- App Language Constants Definitions -----------------

val AppStringsAr = AppStrings(
    common = CommonStrings(
        general = CommonGeneralStrings(
            appName = "طبيبي الذكي", save = "حفظ", cancel = "إلغاء", close = "إغلاق", delete = "حذف",
            edit = "تعديل", add = "إضافة", confirm = "تأكيد", all = "الكل", today = "اليوم",
            yesterday = "أمس", date = "التاريخ", time = "الوقت", notes = "ملاحظات", loading = "جاري التحميل...",
            search = "بحث..."
        ),
        statusGroup = CommonStatusStrings(
            status = "الحالة", normal = "طبيعي", high = "مرتفع", low = "منخفض", critical = "حرج",
            active = "نشط", inactive = "متوقف", yes = "نعم", no = "لا", success = "تم بنجاح",
            error = "حدث خطأ", refresh = "تحديث"
        )
    ),
    nav = NavStrings(
        navHome = "الرئيسية", navDaily = "المتابعة اليومية", navResults = "الفحوصات",
        navAiAdvisor = "المستشار الذكي", navProfile = "الملف الشخصي"
    ),
    home = HomeStrings(
        p1 = HomePart1Strings(
            welcomeUser = "مرحباً بك", dailyHealthSummary = "موجز مؤشراتك الصحية اليومية",
            quickVitals = "العلامات الحيوية السريعة", bloodSugar = "سكر الدم", bloodPressure = "ضغط الدم",
            weight = "الوزن", pulse = "النبض", bmi = "مؤشر الكتلة", quickActions = "إجراءات سريعة",
            addLabTest = "إضافة فحص", logVitals = "تسجيل قراءة", consultAi = "استشارة طبية",
            uploadPrescription = "مسح الروشتة", recentLabTests = "أحدث الفحوصات الطبية", viewAll = "عرض الكل",
            emergencyCall = "اتصال طوارئ (123)", dailyHealthTips = "نصائح وإرشادات طبية",
            emergencyTitle = "طلب مساعدة طارئة", emergencySubtitle = "اتصال فوري بالإسعاف وأرقام الطوارئ الطبية الوطنية المعتمدة",
            call123 = "اتصال بالإسعاف (123)"
        ),
        p2 = HomePart2Strings(
            currentReadingsSummary = "ملخص القراءات الحالية", fullHistory = "السجل الكامل", noReadings = "لا قراءات",
            kgUnit = "كجم", todayMultipleReadings = "قراءات اليوم المتعددة", noReadingsToday = "لم تسجل قراءات اليوم بعد",
            tapCardsToLog = "اضغط على البطاقات في الأعلى لتسجيل سكر الدم أو الضغط أو الوزن اليومي ولأي وقت سابق",
            viewPastReadings = "عرض السجل والفحوصات السابقة", consultAiComprehensive = "استشارة طبية وتحليل شامل بالذكاء الاصطناعي",
            scanPrescriptionTitle = "مسح الوصفة الطبية (الروشتة)", scanPrescriptionSubtitle = "تصوير الوصفة لقراءة الأدوية والجرعات وتفعيل منبهات المواعيد",
            uploadLabReportTitle = "رفع تحاليل (PDF أو صورة)", uploadLabReportSubtitle = "استخراج قيم وأسماء الفحوصات وتخزينها تلقائياً في السجل",
            aiBadge = "ذكاء اصطناعي", logSugarTitle = "تسجيل فحص سكر الدم", logBpTitle = "تسجيل قياس ضغط الدم",
            logWeightTitle = "تسجيل قياس الوزن", fastingOption = "صائم", postBreakfast = "بعد الإفطار",
            beforeLunch = "قبل الغداء"
        ),
        p3 = HomePart3Strings(
            afterLunch = "بعد الغداء", beforeDinner = "قبل العشاء", afterDinner = "بعد العشاء",
            beforeBed = "قبل النوم", randomOption = "عشوائي", morningOption = "صباحاً", noonOption = "ظهراً",
            eveningOption = "مساءً", nightOption = "ليلاً", systolicLabel = "الانقباضي (العالي)",
            diastolicLabel = "الانبساطي (المنخفض)", pulseLabel = "نبض القلب (bpm)", measurementPeriod = "فترة القياس",
            weightKgLabel = "الوزن الكلي (كجم)", heightCmLabel = "الطول (سم)", bmiLabel = "مؤشر كتلة الجسم (BMI)",
            sugarSavedSuccess = "تم حفظ قراءة سكر الدم بنجاح!", bpSavedSuccess = "تم حفظ قياس ضغط الدم بنجاح!",
            weightSavedSuccess = "تم تحديث قياس الوزن بنجاح!", pleaseEnterValidNumber = "يرجى إدخال أرقام صحيحة"
        )
    ),
    labTests = LabTestsStrings(
        p1 = LabTestsPart1Strings(
            labTestsTitle = "الفحوصات والتحاليل الطبية", uploadPdfOrImage = "رفع تقرير تحاليل (PDF/صورة)",
            addManualTest = "إضافة فحص يدوي", testName = "اسم الفحص", category = "التصنيف الطبي",
            resultValue = "النتيجة", unit = "الوحدة", referenceRange = "المعدل الطبيعي",
            labName = "اسم المختبر", filterAll = "الكل", filterAbnormal = "غير طبيعي (زائد/نقص)",
            filterNormal = "طبيعي", aiExtractSummary = "تحليل وقراءة التقارير بالذكاء الاصطناعي",
            extractTestsFromReport = "استخراج نتائج الفحوصات تلقائياً من الملف المرفق",
            selectLabTestName = "اختر اسم الفحص الطبي", standardRange = "المعدل القياسي المعتمد",
            labTestsCountFormat = "إجمالي الفحوصات المسجلة: %d فحص", searchPlaceholder = "بحث عن فحص (CBC, سكر, كولسترول...)",
            filterAbnormalOnly = "الفحوصات غير الطبيعية فقط", filterNormalOnly = "الفحوصات السليمة فقط"
        ),
        p2 = LabTestsPart2Strings(
            aiLabConsultation = "تحليل المستشار الطبي الذكي للتحاليل",
            aiLabConsultationDesc = "تحليل شامل لنتائج الفحوصات المرفقة وتحديد المؤشرات التي تحتاج متابعة مع الطبيب",
            noTestsFoundSearch = "لم يتم العثور على فحوصات تطابق كلمة البحث",
            tryAnotherKeyword = "جرّب البحث باسم آخر مثل (سكر، كبد، كلى، CBC)",
            noTestsYet = "لا توجد فحوصات مخبرية مسجلة حتى الآن",
            tapPlusToAdd = "يمكنك إضافة فحوصاتك اليدوية أو رفع تقرير تحاليل بصيغة PDF أو صورة لقراءتها تلقائياً",
            addNewTestBtn = "إضافة فحص طبي جديد", testDateLabel = "تاريخ إجراء الفحص",
            labHospitalLabel = "المختبر / المستشفى", resultLabel = "النتيجة الحالية",
            refRangeLabel = "النطاق الطبيعي المرجعي", testNameLabel = "اسم الفحص الطبي",
            categoryLabel = "التصنيف (سكر، كبد، كلى...)", testValueLabel = "قراءة النتيجة",
            testUnitLabel = "وحدة القياس (mg/dL...)", editTestTitle = "تعديل تفاصيل الفحص الطبي",
            previousHistoryForTest = "السجل السابق لهذا الفحص", levelSignificance = "الأهمية الطبية والتأثير"
        )
    ),
    daily = DailyStrings(
        p1 = DailyPart1Strings(
            dailyTrackingTitle = "المتابعة اليومية والعلامات الحيوية", vitalsTab = "جدول العلامات الحيوية",
            medicationsTab = "جدول وتذكيرات الأدوية", medicationReminders = "جدول مواعيد الأدوية والتنبيهات",
            addMedication = "إضافة دواء جديد", editMedication = "تعديل بيانات الدواء",
            medicationName = "اسم الدواء", dosage = "الجرعة (مثال: حبة واحدة، 500 ملجم)",
            frequency = "التكرار اليومي", instructions = "تعليمات الأخذ (قبل الأكل، بعد الأكل...)",
            medicationTime = "وقت التنبيه", markTaken = "تم أخذ الدواء", takenToday = "تم أخذ الجرعة اليوم",
            pendingToday = "جرعة معلقة لليوم", activeIngredientsLabel = "المادة الفعالة والتصنيف",
            sideEffectsLabel = "الآثار الجانبية الشائعة", usesLabel = "دواعي الاستعمال الرئيسية",
            foodInteractionsLabel = "التفاعلات مع الطعام والأدوية", importantInfoLabel = "تحذيرات هامة"
        ),

        p2 = DailyPart2Strings(
            aiMedicationAnalysis = "تحليل الدواء والتفاعلات بالذكاء الاصطناعي", hideAiDetails = "إخفاء التفاصيل الطبية",
            showAiDetails = "عرض الشرح الطبي الشامل", fetchAiDetails = "جلب الشرح والآثار الجانبية من الذكاء الاصطناعي",
            backgroundAlarmsInfo = "التنبيهات تعمل في الخلفية بدقة عالية", logSugar = "تسجيل قياس السكر",
            logPressure = "تسجيل قياس الضغط", logWeight = "تسجيل قياس الوزن", fasting = "صائم",
            postprandial = "بعد الأكل", randomSugar = "عشوائي", systolic = "انقباضي", diastolic = "انبساطي",
            dailyTrackingHeader = "المتابعة اليومية وتذكير الأدوية", dailyTrackingSubtitle = "سجل القياسات اليومية ومواعيد الجرعات للتنبيه المباشر",
            notifActiveTitle = "منبهات الأدوية تعمل بالتوقيت الدقيق", notifActiveSubtitle = "تنبيهات صوتية واهتزاز فور موعد أخذ الدواء حتى لو كان التطبيق مغلقاً",
            enableNotifBtn = "تفعيل الإشعارات", activeMedsHeader = "الأدوية الحالية والجرعات اليومية"
        ),
        p3 = DailyPart3Strings(
            inactiveMedsHeader = "الأدوية الموقوفة / السابقة", noMedsTitle = "لا توجد أدوية مجدولة حالياً",
            noMedsSubtitle = "أضف أدويتك اليومية أو قم بمسح الروشتة الطبية لتلقي التنبيهات بمواعيد الجرعات",
            scanPrescriptionBtn = "مسح الروشتة بالذكاء الاصطناعي", addMedBtn = "إضافة دواء يدوي",
            testAlarm = "تجربة التنبيه", takenTodayBadge = "تم أخذ الجرعة اليوم",
            pendingTodayBadge = "جرعة معلقة لم تؤخذ بعد", doseTimeLabel = "موعد الجرعة القادمة",
            doseInstructionsLabel = "التعليمات", frequencyLabel = "التكرار", dailyFreq = "يومياً",
            twiceDailyFreq = "مرتين يومياً", thriceDailyFreq = "3 مرات يومياً", weeklyFreq = "أسبوعياً",
            asNeededFreq = "عند اللزوم", beforeFood = "قبل الأكل", afterFood = "بعد الأكل",
            withFood = "مع الطعام"
        ),
        p4 = DailyPart4Strings(
            beforeSleep = "قبل النوم", onePill = "حبة واحدة (1 Tablet)", twoPills = "حبتين (2 Tablets)",
            syrupDose = "ملعقة شراب (5 ml)", injectionDose = "حقنة / قلم (Injection)",
            dropsDose = "قطرات (Drops)", editMedicationTitle = "تعديل بيانات الدواء والتنبيه",
            bpScheduleTitle = "جدول وتاريخ قياسات ضغط الدم", sugarScheduleTitle = "جدول وتاريخ قياسات سكر الدم",
            weightScheduleTitle = "جدول وتاريخ قياسات الوزن والكتلة", targetNormalSugar = "المعدل الطبيعي للصائم (70 - 99 mg/dL)",
            targetNormalBp = "المعدل الطبيعي المثالي (120/80 mmHg)", normalBmiRange = "مؤشر الكتلة الطبيعي (18.5 - 24.9)",
            deleteConfirmTitle = "تأكيد الحذف", deleteConfirmMsg = "هل أنت تأكد من رغبتك في حذف هذا العنصر؟",
            medEnrichedSuccess = "تم جلب الشرح الطبي والدواني بنجاح!", medUpdatedSuccess = "تم تحديث الدواء بنجاح!"
        )
    ),
    results = ResultsStrings(
        resultsVitalsTitle = "نتائج الفحوصات والمؤشرات", resultsVitalsSubtitle = "مستوى التحاليل (طبيعي / زائد / نقص) وسجل الفحوصات القديمة",
        chartsTab = "المخطط البياني", historyTab = "السجل القديم", summaryTab = "الفحوصات والمستوى",
        bloodSugarTrend = "مسار وتغير سكر الدم", bloodPressureTrend = "مسار قياسات ضغط الدم",
        weightTrend = "مسار وتغير الوزن", last7Days = "آخر 7 أيام", last30Days = "آخر 30 يوم",
        allTime = "كافة الفترات", avgSugar = "متوسط السكر", avgBp = "متوسط الضغط",
        latestWeightLabel = "أحدث قياس وزن", normalRangeLabel = "النطاق الطبيعي المستهدف",
        noDataForChart = "لا توجد قراءات كافية لعرض الرسم البياني"
    ),
    aiAdvisor = AiAdvisorStrings(
        aiAdvisorTitle = "المستشار الطبي الذكي", fullAnalysisTab = "التقرير الطبي الشامل",
        chatTab = "محادثة الطبيب الذكي", askDoctorPlaceholder = "اكتب سؤالك الطبي هنا...",
        send = "إرسال", generateNewAnalysis = "توليد تحليل طبي شامل جديد",
        analyzingMedicalData = "جاري تحليل الفحوصات المؤشرات الصحية...",
        medicalDisclaimer = "تنبيه: التقرير استرشادي ولا يغني عن زيارة الطبيب المختص.",
        activeMedicationsSection = "الأدوية الحالية والتفاعلات:", clinicalRecommendations = "التوصيات الطبية والإكلينيكية:",
        laboratoryEvaluation = "تقييم نتائج الفحوصات المخبرية:", cardiovascularStatus = "حالة ضغط الدم والقلب:",
        nutritionAndDiet = "النظام الغذائي وتعديل نمط الحياة:", aiAdvisorSubtitle = "تحليل شامل بالذكاء الاصطناعي لفحوصاتك وأدويتك وسكر وضغط الدم",
        quickPrompts = "أسئلة شائعة مقترحة:", promptDietForDiabetes = "ما هو أفضل نظام غذائي مناسب لنتائج تحاليلي؟",
        promptDrugInteractions = "هل هناك تعارض أو تفاعل بين الأدوات التي أتناولها؟",
        promptLabInterpret = "اشرح لي نتائج تحاليلي الأخيرة بالتفصيل ومسارها؟",
        promptLifestyleAdvice = "نصائح لتحسين ضغط الدم والنشاط البدني", askDoctorQuestion = "اسأل المستشار الطبي الذكي...",
        disclaimerText = "إخلاء مسؤولية طبية: هذا المستشار الذكي يهدف للمساعدة في فهم المؤشرات الصحية ولا يغني عن استشارة الطبيب أو الطوارئ."
    ),
    profile = ProfileStrings(
        p1 = ProfilePart1Strings(
            healthProfileTitle = "الملف الطبي والتاريخ المرضي", personalInfo = "المعلومات الشخصية",
            fullName = "الاسم الكامل", age = "العمر (بالسنوات)", gender = "النوع", male = "ذكر",
            female = "أنثى", bloodType = "فصيلة الدم", heightCm = "الطول (سم)",
            initialWeightKg = "الوزن الأولي (كجم)", chronicDiseases = "الأمراض المزمنة (ضغط، سكر...)",
            allergies = "الحساسية من الأدوية أو الأطعمة", pastSurgeries = "العمليات الجراحية السابقة",
            emergencyContact = "رقم هاتف الطوارئ", personalInfoSection = "المعلومات الشخصية والبيانات الرئيسية",
            medicalHistorySection = "التاريخ المرضي والأمراض المزمنة", emergencyContactSection = "جهة اتصال الطوارئ",
            saveProfileBtn = "حفظ الملف الطبي", profileSavedSuccess = "تم حفظ الملف الطبي بنجاح",
            bloodTypeSelect = "اختر فصيلة الدم"
        ),
        p2 = ProfilePart2Strings(
            profileAndSettings = "الملف الشخصي والإعدادات", editBasicInfo = "تعديل الملف الشخصي",
            changePhoto = "تغيير الصورة", languageSetting = "اللغة",
            languageSubtitle = "اختر لغة التطبيق أو التحديد التلقائي لغة الجهاز",
            selectLanguage = "تغيير لغة التطبيق", autoDeviceLanguage = "تلقائي (لغة النظام)",
            arabicLanguage = "العربية", englishLanguage = "الإنجليزية",
            russianLanguage = "الروسية", darkMode = "الوضع الداكن",
            darkModeSubtitle = "تفعيل المظهر الليلي المريح للعينين", googleDriveSync = "مزامنة واستعادة البيانات من Google Drive",
            exportPdf = "تصدير تقرير طبي شامل بصيغة PDF", clearRecords = "مسح كافة البيانات والقراءات",
            clearRecordsSubtitle = "حذف سجلات السكر والضغط والأدوية السابقة", logout = "تسجيل الخروج",
            aboutApp = "عن التطبيق", appVersion = "الإصدار 2.5 (2026)", importFromGoogleDrive = "استيراد ملف من Google Drive",
            syncStatusSuccess = "تمت المزامنة بنجاح", emergencyCallDoctor = "اتصال بالدكتور المعالج",
            emergencyCallAmbulance = "اتصال بالإسعاف (123)",
            googleDriveTitle = "مزامنة Google Drive",
            driveSyncBadge = "سحابي آمن",
            driveSyncSubtitle = "حفظ تلقائي وتزامن فوري مع كافة الأجهزة",
            dataReadyForSync = "إجمالي السجلات القابلة للنسخ",
            recordsCount = "سجل",
            exportToDrive = "تصدير إلى Drive",
            saveToFolder = "حفظ كملف...",
            syncHistoryTitle = "سجل المزامنة والنسخ السابقة",
            hideText = "إخفاء",
            showText = "عرض"
        )
    )
)

val AppStringsEn = AppStrings(
    common = CommonStrings(
        general = CommonGeneralStrings(
            appName = "Smart Medic", save = "Save", cancel = "Cancel", close = "Close", delete = "Delete",
            edit = "Edit", add = "Add", confirm = "Confirm", all = "All", today = "Today",
            yesterday = "Yesterday", date = "Date", time = "Time", notes = "Notes", loading = "Loading...",
            search = "Search..."
        ),
        statusGroup = CommonStatusStrings(
            status = "Status", normal = "Normal", high = "High", low = "Low", critical = "Critical",
            active = "Active", inactive = "Inactive", yes = "Yes", no = "No", success = "Success",
            error = "Error", refresh = "Refresh"
        )
    ),
    nav = NavStrings(
        navHome = "Home", navDaily = "Daily Tracking", navResults = "Lab Results",
        navAiAdvisor = "AI Advisor", navProfile = "Profile"
    ),
    home = HomeStrings(
        p1 = HomePart1Strings(
            welcomeUser = "Welcome back", dailyHealthSummary = "Daily Health Vitals Summary",
            quickVitals = "Quick Vitals", bloodSugar = "Blood Sugar", bloodPressure = "Blood Pressure",
            weight = "Weight", pulse = "Pulse", bmi = "BMI", quickActions = "Quick Actions",
            addLabTest = "Add Lab Test", logVitals = "Log Vitals", consultAi = "Medical AI",
            uploadPrescription = "Scan Rx", recentLabTests = "Recent Lab Tests", viewAll = "View All",
            emergencyCall = "Emergency Call (123)", dailyHealthTips = "Daily Medical Advice",
            emergencyTitle = "Emergency Medical Help", emergencySubtitle = "Direct speed call to emergency ambulance and medical services",
            call123 = "Call Ambulance (123)"
        ),
        p2 = HomePart2Strings(
            currentReadingsSummary = "Current Readings Summary", fullHistory = "Full History", noReadings = "No Readings",
            kgUnit = "kg", todayMultipleReadings = "Today's Multiple Readings", noReadingsToday = "No readings logged today",
            tapCardsToLog = "Tap top cards to log daily blood sugar, pressure, weight or previous readings",
            viewPastReadings = "View full history and lab records", consultAiComprehensive = "Consult AI for comprehensive medical analysis",
            scanPrescriptionTitle = "Scan Prescription (Rx)", scanPrescriptionSubtitle = "Scan Rx to extract medications, dosage, and setup automatic alarms",
            uploadLabReportTitle = "Upload Lab Report (PDF/Image)", uploadLabReportSubtitle = "Extract lab test names and values automatically into medical record",
            aiBadge = "AI Smart", logSugarTitle = "Log Blood Sugar Reading", logBpTitle = "Log Blood Pressure Reading",
            logWeightTitle = "Log Weight Measurement", fastingOption = "Fasting", postBreakfast = "Post-Breakfast",
            beforeLunch = "Before Lunch"
        ),
        p3 = HomePart3Strings(
            afterLunch = "After Lunch", beforeDinner = "Before Dinner", afterDinner = "After Dinner",
            beforeBed = "Before Bed", randomOption = "Random", morningOption = "Morning", noonOption = "Noon",
            eveningOption = "Evening", nightOption = "Night", systolicLabel = "Systolic (High)",
            diastolicLabel = "Diastolic (Low)", pulseLabel = "Pulse (bpm)", measurementPeriod = "Measurement Period",
            weightKgLabel = "Total Weight (kg)", heightCmLabel = "Height (cm)", bmiLabel = "Body Mass Index (BMI)",
            sugarSavedSuccess = "Blood sugar reading saved successfully!", bpSavedSuccess = "Blood pressure measurement saved!",
            weightSavedSuccess = "Weight measurement updated!", pleaseEnterValidNumber = "Please enter valid numeric values"
        )
    ),
    labTests = LabTestsStrings(
        p1 = LabTestsPart1Strings(
            labTestsTitle = "Lab Tests & Medical Reports", uploadPdfOrImage = "Upload Lab Report (PDF/Image)",
            addManualTest = "Add Manual Test", testName = "Test Name", category = "Category",
            resultValue = "Result Value", unit = "Unit", referenceRange = "Reference Range",
            labName = "Lab / Hospital Name", filterAll = "All", filterAbnormal = "Abnormal (High/Low)",
            filterNormal = "Normal", aiExtractSummary = "AI Automatic Test Extraction",
            extractTestsFromReport = "Extract lab test values automatically from uploaded file",
            selectLabTestName = "Select Lab Test Name", standardRange = "Standard Reference Range",
            labTestsCountFormat = "Total Recorded Tests: %d", searchPlaceholder = "Search tests (CBC, Glucose, Lipid...)",
            filterAbnormalOnly = "Abnormal Tests Only", filterNormalOnly = "Normal Tests Only"
        ),
        p2 = LabTestsPart2Strings(
            aiLabConsultation = "AI Medical Consultation for Lab Reports",
            aiLabConsultationDesc = "Comprehensive AI analysis of your lab reports highlighting key values that need doctor review",
            noTestsFoundSearch = "No lab tests found matching search keyword",
            tryAnotherKeyword = "Try searching with keywords like (Glucose, CBC, Kidney, Liver)",
            noTestsYet = "No lab tests recorded yet",
            tapPlusToAdd = "Add manual test entries or upload a PDF / image report for instant AI extraction",
            addNewTestBtn = "Add New Lab Test", testDateLabel = "Test Date",
            labHospitalLabel = "Lab / Hospital", resultLabel = "Current Result",
            refRangeLabel = "Reference Range", testNameLabel = "Lab Test Name",
            categoryLabel = "Category (Sugar, Liver, Kidney...)", testValueLabel = "Test Value",
            testUnitLabel = "Measurement Unit (mg/dL...)", editTestTitle = "Edit Lab Test Details",
            previousHistoryForTest = "Previous History for this Test", levelSignificance = "Medical Significance & Guidance"
        )
    ),
    daily = DailyStrings(
        p1 = DailyPart1Strings(
            dailyTrackingTitle = "Daily Tracking & Vitals", vitalsTab = "Vitals Schedule",
            medicationsTab = "Medications & Alarms", medicationReminders = "Medication Schedule & Reminders",
            addMedication = "Add New Medication", editMedication = "Edit Medication Details",
            medicationName = "Medication Name", dosage = "Dosage (e.g. 1 Tablet, 500mg)",
            frequency = "Daily Frequency", instructions = "Instructions (Before / After meal)",
            medicationTime = "Alarm Time", markTaken = "Mark as Taken", takenToday = "Dose Taken Today",
            pendingToday = "Pending Dose Today", activeIngredientsLabel = "Active Ingredient & Category",
            sideEffectsLabel = "Common Side Effects", usesLabel = "Primary Uses & Indications",
            foodInteractionsLabel = "Food & Drug Interactions", importantInfoLabel = "Important Precautions"
        ),
        p2 = DailyPart2Strings(
            aiMedicationAnalysis = "AI Medication & Interaction Analysis", hideAiDetails = "Hide Medical Details",
            showAiDetails = "Show Full Medical Summary", fetchAiDetails = "Fetch AI Medication Insights",
            backgroundAlarmsInfo = "High-precision background alarms enabled", logSugar = "Log Blood Sugar",
            logPressure = "Log Blood Pressure", logWeight = "Log Weight", fasting = "Fasting",
            postprandial = "Postprandial", randomSugar = "Random", systolic = "Systolic", diastolic = "Diastolic",
            dailyTrackingHeader = "Daily Tracking & Medication Alarms", dailyTrackingSubtitle = "Record daily vitals and manage precise medication reminders",
            notifActiveTitle = "Exact Timing Alarms Active", notifActiveSubtitle = "Sound and vibration alarms trigger precisely even if app is closed",
            enableNotifBtn = "Enable Notifications", activeMedsHeader = "Active Medications & Schedules"
        ),
        p3 = DailyPart3Strings(
            inactiveMedsHeader = "Stopped / Previous Medications", noMedsTitle = "No Scheduled Medications Yet",
            noMedsSubtitle = "Add daily medications manually or scan a prescription to schedule alarms",
            scanPrescriptionBtn = "Scan Prescription with AI", addMedBtn = "Add Medication",
            testAlarm = "Test Alarm Sound", takenTodayBadge = "Dose Taken Today",
            pendingTodayBadge = "Pending Dose Today", doseTimeLabel = "Next Dose Time",
            doseInstructionsLabel = "Instructions", frequencyLabel = "Frequency", dailyFreq = "Daily",
            twiceDailyFreq = "Twice Daily", thriceDailyFreq = "3 Times Daily", weeklyFreq = "Weekly",
            asNeededFreq = "As Needed", beforeFood = "Before Food", afterFood = "After Food",
            withFood = "With Food"
        ),
        p4 = DailyPart4Strings(
            beforeSleep = "Before Sleep", onePill = "1 Tablet", twoPills = "2 Tablets",
            syrupDose = "Syrup (5 ml)", injectionDose = "Injection / Pen",
            dropsDose = "Drops", editMedicationTitle = "Edit Medication & Reminder",
            bpScheduleTitle = "Blood Pressure History & Schedule", sugarScheduleTitle = "Blood Sugar History & Schedule",
            weightScheduleTitle = "Weight & BMI Tracking History", targetNormalSugar = "Normal Fasting Target (70 - 99 mg/dL)",
            targetNormalBp = "Ideal Normal BP (120/80 mmHg)", normalBmiRange = "Normal BMI Range (18.5 - 24.9)",
            deleteConfirmTitle = "Confirm Delete", deleteConfirmMsg = "Are you sure you want to delete this item?",
            medEnrichedSuccess = "Medication AI insights retrieved successfully!", medUpdatedSuccess = "Medication updated successfully!"
        )
    ),
    results = ResultsStrings(
        resultsVitalsTitle = "Vitals & Lab Trends", resultsVitalsSubtitle = "Lab indicators (Normal / High / Low) and full history",
        chartsTab = "Visual Charts", historyTab = "Full History", summaryTab = "Vitals & Results",
        bloodSugarTrend = "Blood Sugar Trend", bloodPressureTrend = "Blood Pressure Trend",
        weightTrend = "Weight & BMI Trend", last7Days = "Last 7 Days", last30Days = "Last 30 Days",
        allTime = "All Time", avgSugar = "Avg Sugar", avgBp = "Avg Pressure",
        latestWeightLabel = "Latest Weight", normalRangeLabel = "Target Normal Range",
        noDataForChart = "Insufficient data points to render chart"
    ),
    aiAdvisor = AiAdvisorStrings(
        aiAdvisorTitle = "Smart AI Medical Advisor", fullAnalysisTab = "Comprehensive Medical Report",
        chatTab = "Chat with AI Doctor", askDoctorPlaceholder = "Ask your medical question here...",
        send = "Send", generateNewAnalysis = "Generate New Medical Report",
        analyzingMedicalData = "Analyzing medical lab reports and vitals...",
        medicalDisclaimer = "Disclaimer: Report is for informational guidance and does not replace doctor visit.",
        activeMedicationsSection = "Active Medications & Interactions:", clinicalRecommendations = "Clinical Recommendations & Actions:",
        laboratoryEvaluation = "Lab Test Results Evaluation:", cardiovascularStatus = "Blood Pressure & Cardiac Status:",
        nutritionAndDiet = "Dietary Plan & Lifestyle Guidance:", aiAdvisorSubtitle = "Comprehensive AI synthesis of lab reports, medications, sugar and blood pressure",
        quickPrompts = "Suggested Questions:", promptDietForDiabetes = "What is the best diet based on my lab results?",
        promptDrugInteractions = "Are there any interactions between my current medications?",
        promptLabInterpret = "Explain my recent lab results in detail and their trends?",
        promptLifestyleAdvice = "Lifestyle tips for blood pressure management and physical activity", askDoctorQuestion = "Ask the AI Medical Consultant...",
        disclaimerText = "Medical Disclaimer: AI advisor provides health guidance and is not a substitute for professional medical care or emergency services."
    ),
    profile = ProfileStrings(
        p1 = ProfilePart1Strings(
            healthProfileTitle = "Medical Profile & History", personalInfo = "Personal Information",
            fullName = "Full Name", age = "Age (years)", gender = "Gender", male = "Male",
            female = "Female", bloodType = "Blood Type", heightCm = "Height (cm)",
            initialWeightKg = "Baseline Weight (kg)", chronicDiseases = "Chronic Diseases (Hypertension, Diabetes...)",
            allergies = "Medication / Food Allergies", pastSurgeries = "Surgeries / Medical Operations",
            emergencyContact = "Emergency Contact Phone", personalInfoSection = "Personal & Identification Info",
            medicalHistorySection = "Medical History & Chronic Conditions", emergencyContactSection = "Emergency Contact Info",
            saveProfileBtn = "Save Medical Profile", profileSavedSuccess = "Medical profile saved successfully",
            bloodTypeSelect = "Select Blood Type"
        ),
        p2 = ProfilePart2Strings(
            profileAndSettings = "Profile & Settings", editBasicInfo = "Edit Basic Information",
            changePhoto = "Change Photo", languageSetting = "Language",
            languageSubtitle = "Choose application language or follow system default",
            selectLanguage = "Select App Language", autoDeviceLanguage = "Automatic (System Default)",
            arabicLanguage = "Arabic", englishLanguage = "English",
            russianLanguage = "Russian", darkMode = "Dark Mode",
            darkModeSubtitle = "Enable eye-friendly dark color theme", googleDriveSync = "Google Drive Sync & Backup",
            exportPdf = "Export Medical Report to PDF", clearRecords = "Clear All Records & Vitals",
            clearRecordsSubtitle = "Delete past blood sugar, pressure, and weight logs", logout = "Log Out",
            aboutApp = "About App", appVersion = "Version 2.5 (2026)", importFromGoogleDrive = "Import File from Google Drive",
            syncStatusSuccess = "Sync completed successfully", emergencyCallDoctor = "Call Primary Doctor",
            emergencyCallAmbulance = "Call Ambulance (123)",
            googleDriveTitle = "Google Drive Sync",
            driveSyncBadge = "Secure Cloud",
            driveSyncSubtitle = "Auto backup & instant sync across devices",
            dataReadyForSync = "Total records ready for backup",
            recordsCount = "records",
            exportToDrive = "Export to Drive",
            saveToFolder = "Save as file...",
            syncHistoryTitle = "Sync History & Backups",
            hideText = "Hide",
            showText = "Show"
        )
    )
)

val AppStringsRu = AppStrings(
    common = CommonStrings(
        general = CommonGeneralStrings(
            appName = "Смарт Медик", save = "Сохранить", cancel = "Отмена", close = "Закрыть", delete = "Удалить",
            edit = "Изменить", add = "Добавить", confirm = "Подтвердить", all = "Все", today = "Сегодня",
            yesterday = "Вчера", date = "Дата", time = "Время", notes = "Заметки", loading = "Загрузка...",
            search = "Поиск..."
        ),
        statusGroup = CommonStatusStrings(
            status = "Статус", normal = "Норма", high = "Высокий", low = "Низкий", critical = "Критический",
            active = "Активен", inactive = "Неактивен", yes = "Да", no = "Нет", success = "Успешно",
            error = "Ошибка", refresh = "Обновить"
        )
    ),
    nav = NavStrings(
        navHome = "Главная", navDaily = "Дневник", navResults = "Анализы",
        navAiAdvisor = "ИИ-Консультант", navProfile = "Профиль"
    ),
    home = HomeStrings(
        p1 = HomePart1Strings(
            welcomeUser = "Добро пожаловать", dailyHealthSummary = "Сводка показателей здоровья",
            quickVitals = "Основные показатели", bloodSugar = "Сахар крови", bloodPressure = "Давление",
            weight = "Вес", pulse = "Пульс", bmi = "ИМТ", quickActions = "Быстрые действия",
            addLabTest = "Добавить анализ", logVitals = "Записать замер", consultAi = "ИИ-Консультация",
            uploadPrescription = "Сканировать рецепт", recentLabTests = "Последние анализы", viewAll = "Все",
            emergencyCall = "Вызов скорой (123)", dailyHealthTips = "Медицинские рекомендации",
            emergencyTitle = "Экстренная помощь", emergencySubtitle = "Быстрый вызов скорой помощи и экстренных служб",
            call123 = "Вызов скорой (123)"
        ),
        p2 = HomePart2Strings(
            currentReadingsSummary = "Сводка текущих замеров", fullHistory = "Полный журнал", noReadings = "Нет данных",
            kgUnit = "кг", todayMultipleReadings = "Замеры за сегодня", noReadingsToday = "Сегодня замеры еще не заносились",
            tapCardsToLog = "Нажмите на карточку выше, чтобы внести сахар, давление или вес",
            viewPastReadings = "Открыть историю замеров и анализов", consultAiComprehensive = "Комплексный ИИ-анализ всех анализов и показателей",
            scanPrescriptionTitle = "Сканировать рецепт врача", scanPrescriptionSubtitle = "Распознавание лекарств, дозировок и настройка автонапоминаний",
            uploadLabReportTitle = "Загрузить бланки анализов (PDF/Фото)", uploadLabReportSubtitle = "Автоматический распознавание названий и значений в карточку",
            aiBadge = "ИИ Смарт", logSugarTitle = "Внести замер сахара крови", logBpTitle = "Внести замер давления",
            logWeightTitle = "Внести замер веса", fastingOption = "Натощак", postBreakfast = "После завтрака",
            beforeLunch = "Перед обедом"
        ),
        p3 = HomePart3Strings(
            afterLunch = "После обеда", beforeDinner = "Перед ужином", afterDinner = "После ужина",
            beforeBed = "Перед сном", randomOption = "Случайный", morningOption = "Утром", noonOption = "Днем",
            eveningOption = "Вечером", nightOption = "Ночью", systolicLabel = "Систолическое (Верхнее)",
            diastolicLabel = "Диастолическое (Нижнее)", pulseLabel = "Пульс (уд/мин)", measurementPeriod = "Период замера",
            weightKgLabel = "Общий вес (кг)", heightCmLabel = "Рост (см)", bmiLabel = "Индекс массы тела (ИМТ)",
            sugarSavedSuccess = "Показатель сахара успешно сохранен!", bpSavedSuccess = "Показатель давления успешно сохранен!",
            weightSavedSuccess = "Замер веса успешно обновлен!", pleaseEnterValidNumber = "Введите корректные числовые значения"
        )
    ),
    labTests = LabTestsStrings(
        p1 = LabTestsPart1Strings(
            labTestsTitle = "Медицинские анализы и отчеты", uploadPdfOrImage = "Загрузить бланк (PDF/Фото)",
            addManualTest = "Добавить анализ вручную", testName = "Название анализа", category = "Категория",
            resultValue = "Результат", unit = "Единица измерения", referenceRange = "Нормальные значения",
            labName = "Лаборатория / Клиника", filterAll = "Все", filterAbnormal = "Отклонения (Повышен/Понижен)",
            filterNormal = "В норме", aiExtractSummary = "Автоматическое распознавание ИИ",
            extractTestsFromReport = "Извлечь показатели анализов из загруженного файла",
            selectLabTestName = "Выберите название анализа", standardRange = "Стандартная референсная норма",
            labTestsCountFormat = "Всего анализов в базе: %d", searchPlaceholder = "Поиск анализов (CBC, Сахар, Холестерин...)",
            filterAbnormalOnly = "Только отклонения от нормы", filterNormalOnly = "Только показатели в норме"
        ),
        p2 = LabTestsPart2Strings(
            aiLabConsultation = "ИИ-Консультация по результатам анализов",
            aiLabConsultationDesc = "Детальный разбор показателей с выделением результатов, требующих внимания врача",
            noTestsFoundSearch = "По вашему запросу анализов не найдено",
            tryAnotherKeyword = "Попробуйте поискать по другим названиям (Сахар, Гемоглобин, Печень)",
            noTestsYet = "Анализы еще не добавлялись",
            tapPlusToAdd = "Внесите данные вручную или загрузите файл PDF / фото для распознавания ИИ",
            addNewTestBtn = "Добавить новый анализ", testDateLabel = "Дата сдачи анализа",
            labHospitalLabel = "Лаборатория / Клиника", resultLabel = "Текущий результат",
            refRangeLabel = "Референсный интервал", testNameLabel = "Название анализа",
            categoryLabel = "Категория (Сахар, Печень, Почки...)", testValueLabel = "Значение анализа",
            testUnitLabel = "Ед. измерения (ммоль/л...)", editTestTitle = "Редактирование данных анализа",
            previousHistoryForTest = "Динамика и предыдущие замеры", levelSignificance = "Медицинская значимость"
        )
    ),
    daily = DailyStrings(
        p1 = DailyPart1Strings(
            dailyTrackingTitle = "Дневник здоровья и приём лекарств", vitalsTab = "Замеры показателей",
            medicationsTab = "Лекарства и будильники", medicationReminders = "График и напоминания о приёме",
            addMedication = "Добавить лекарство", editMedication = "Редактировать препарат",
            medicationName = "Название препарата", dosage = "Дозировка (например: 1 табл., 500 мг)",
            frequency = "Регулярность", instructions = "Инструкция (до/после еды)",
            medicationTime = "Время напоминания", markTaken = "Отметить приём", takenToday = "Принято сегодня",
            pendingToday = "Ожидает приёма", activeIngredientsLabel = "Действующее вещество",
            sideEffectsLabel = "Возможные побочные эффекты", usesLabel = "Показания к применению",
            foodInteractionsLabel = "Взаимодействие с пищей", importantInfoLabel = "Особые указания"
        ),
        p2 = DailyPart2Strings(
            aiMedicationAnalysis = "ИИ-анализ препарата и совпадений", hideAiDetails = "Скрыть подробности",
            showAiDetails = "Показать описание ИИ", fetchAiDetails = "Запросить информацию ИИ",
            backgroundAlarmsInfo = "Точные будильники работают в фоновом режиме", logSugar = "Записать сахар",
            logPressure = "Записать давление", logWeight = "Записать вес", fasting = "Натощак",
            postprandial = "После еды", randomSugar = "Случайный", systolic = "Систолическое", diastolic = "Диастолическое",
            dailyTrackingHeader = "Дневник показателей и будильники", dailyTrackingSubtitle = "Учет ежедневных замеров и настройка напоминаний о лекарствах",
            notifActiveTitle = "Будильники лекарств активны", notifActiveSubtitle = "Звуковые сигналы сработают вовремя, даже если приложение закрыто",
            enableNotifBtn = "Включить уведомления", activeMedsHeader = "Текущие лекарства и график"
        ),
        p3 = DailyPart3Strings(
            inactiveMedsHeader = "Завершенные / Отмененные препараты", noMedsTitle = "Нет запланированных лекарств",
            noMedsSubtitle = "Добавьте препараты вручную или отсканируйте рецепт для настройки будильников",
            scanPrescriptionBtn = "Сканировать рецепт ИИ", addMedBtn = "Добавить препарат",
            testAlarm = "Проверить звук", takenTodayBadge = "Принято сегодня",
            pendingTodayBadge = "Ожидает приёма", doseTimeLabel = "Время следующего приёма",
            doseInstructionsLabel = "Инструкции", frequencyLabel = "Регулярность", dailyFreq = "Ежедневно",
            twiceDailyFreq = "2 раза в день", thriceDailyFreq = "3 раза в день", weeklyFreq = "Раз в неделю",
            asNeededFreq = "По необходимости", beforeFood = "До еды", afterFood = "После еды",
            withFood = "Во время еды"
        ),
        p4 = DailyPart4Strings(
            beforeSleep = "Перед сном", onePill = "1 таблетка", twoPills = "2 таблетки",
            syrupDose = "Ложка сиропа (5 мл)", injectionDose = "Инъекция / Укол",
            dropsDose = "Капли", editMedicationTitle = "Редактировать препарат и время",
            bpScheduleTitle = "Журнал измерений давления", sugarScheduleTitle = "Журнал измерений сахара",
            weightScheduleTitle = "Динамика изменения веса и ИМТ", targetNormalSugar = "Целевая норма натощак (70 - 99 мг/дл)",
            targetNormalBp = "Целевое нормальное давление (120/80 мм рт. ст.)", normalBmiRange = "Нормальный ИМТ (18.5 - 24.9)",
            deleteConfirmTitle = "Подтверждение удаления", deleteConfirmMsg = "Вы уверены, что хотите удалить эту запись?",
            medEnrichedSuccess = "Информация о препарате успешно получена!", medUpdatedSuccess = "Данные о препарате обновлены!"
        )
    ),
    results = ResultsStrings(
        resultsVitalsTitle = "Результаты замеров и анализов", resultsVitalsSubtitle = "Оценка показателей (Норма / Повышен / Понижен) и история",
        chartsTab = "Графики", historyTab = "Полный журнал", summaryTab = "Анализы и нормы",
        bloodSugarTrend = "Динамика сахара крови", bloodPressureTrend = "Динамика давления",
        weightTrend = "Динамика веса и ИМТ", last7Days = "За 7 дней", last30Days = "За 30 дней",
        allTime = "За всё время", avgSugar = "Средний сахар", avgBp = "Среднее давление",
        latestWeightLabel = "Последний вес", normalRangeLabel = "Целевой нормальный диапазон",
        noDataForChart = "Недостаточно данных для построения графика"
    ),
    aiAdvisor = AiAdvisorStrings(
        aiAdvisorTitle = "Смарт ИИ-Консультант", fullAnalysisTab = "Полный медицинский отчет",
        chatTab = "Чат с ИИ-Врачом", askDoctorPlaceholder = "Задайте вопрос врачу здесь...",
        send = "Отправить", generateNewAnalysis = "Сформировать новый отчет",
        analyzingMedicalData = "Идет анализ анализов и показателей здоровья...",
        medicalDisclaimer = "Внимание: Отчет носит информационный характер и не заменяет визит к врачу.",
        activeMedicationsSection = "Текущие лекарства и их взаимодействие:", clinicalRecommendations = "Клинические и медицинские рекомендации:",
        laboratoryEvaluation = "Оценка результатов лабораторных анализов:", cardiovascularStatus = "Состояние сердечно-сосудистой системы и давление:",
        nutritionAndDiet = "План питания и коррекция образа жизни:", aiAdvisorSubtitle = "Комплексный ИИ-анализ ваших анализов, лекарств, сахара и давления",
        quickPrompts = "Частые вопросы:", promptDietForDiabetes = "Какая диета лучше всего подходит по моيم анализам?",
        promptDrugInteractions = "Есть ли несовместимость между моими лекарствами?", promptLabInterpret = "Объясните подробно результаты моих последних анализов?",
        promptLifestyleAdvice = "Советы по улучшению давления и физической активности", askDoctorQuestion = "Задайте вопрос медицинскому ИИ-консультанту...",
        disclaimerText = "Медицинское предупреждение: Данный ИИ-консультант помогает понять показатели здоровья и не заменяет консультацию врача или обращение в скорую помощь."
    ),
    profile = ProfileStrings(
        p1 = ProfilePart1Strings(
            healthProfileTitle = "Медицинский профиль и история болезни", personalInfo = "Личная информация",
            fullName = "Полное имя", age = "Возраст (лет)", gender = "Пол", male = "Мужской",
            female = "Женский", bloodType = "Группа крови", heightCm = "Рост (см)",
            initialWeightKg = "Базовый вес (кг)", chronicDiseases = "Хронические заболевания (гипертония, диабет...)",
            allergies = "Аллергии на лекарства и пищу", pastSurgeries = "Перенесенные операции",
            emergencyContact = "Экстренный контакт", personalInfoSection = "Личная информация и основные данные",
            medicalHistorySection = "Анамнез и хронические заболевания", emergencyContactSection = "Экстренный контакт",
            saveProfileBtn = "Сохранить медицинский профиль", profileSavedSuccess = "Медицинский профиль успешно сохранен",
            bloodTypeSelect = "Выберите группу крови"
        ),
        p2 = ProfilePart2Strings(
            profileAndSettings = "Профиль и настройки", editBasicInfo = "Редактировать профиль",
            changePhoto = "Изменить фото", languageSetting = "Язык",
            languageSubtitle = "Выберите язык приложения или автоопределение системы",
            selectLanguage = "Выбор языка приложения", autoDeviceLanguage = "Автоматически (Язык системы)",
            arabicLanguage = "Арабский", englishLanguage = "Английский",
            russianLanguage = "Русский", darkMode = "Темная тема",
            darkModeSubtitle = "Приятная для глаз темная цветовая гамма", googleDriveSync = "Синхронизация и бэкап с Google Диском",
            exportPdf = "Экспорт медицинского отчета в PDF", clearRecords = "Сбросить и очистить все записи",
            clearRecordsSubtitle = "Удалить прошлые записи сахара, давления и веса", logout = "Выйти из системы",
            aboutApp = "О приложении", appVersion = "Версия 2.5 (2026)", importFromGoogleDrive = "Импорт файла с Google Диска",
            syncStatusSuccess = "Синхронизация успешно завершена", emergencyCallDoctor = "Позвонить лечащему врачу",
            emergencyCallAmbulance = "Вызов скорой (123)",
            googleDriveTitle = "Синхронизация Google Диск",
            driveSyncBadge = "Защищено",
            driveSyncSubtitle = "Автобэкап и синхронизация между устройствами",
            dataReadyForSync = "Всего записей для бэкапа",
            recordsCount = "записей",
            exportToDrive = "Экспорт на Диск",
            saveToFolder = "Сохранить как файл...",
            syncHistoryTitle = "История синхронизации",
            hideText = "Скрыть",
            showText = "Показать"
        )
    )
)

fun getAppStrings(language: AppLanguage): AppStrings {
    val effective = language.resolveEffectiveLanguage()
    return when (effective) {
        AppLanguage.AR -> AppStringsAr
        AppLanguage.RU -> AppStringsRu
        AppLanguage.EN, AppLanguage.AUTO -> AppStringsEn
    }
}

// ----------------- Dynamic Localization Helpers -----------------

fun AppStrings.localizeStatus(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val lower = raw.trim().lowercase()
    return when {
        lower in listOf("normal", "طبيعي", "طبيعية", "جيد", "سليم", "норма", "в норме") -> normal
        lower in listOf("high", "زائد", "مرتفع", "عالي", "ارتفاع", "повышен", "высокий") -> high
        lower in listOf("low", "نقص", "منخفض", "انخفاض", "هبوط", "понижен", "низкий") -> low
        lower in listOf("critical", "حرج", "خطير", "критический") -> critical
        lower in listOf("active", "نشط", "активен") -> active
        lower in listOf("inactive", "متوقف", "موقوف", "неактивен") -> inactive
        else -> raw
    }
}

fun AppStrings.localizeSugarType(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val lower = raw.trim().lowercase()
    return when {
        lower.contains("صائم") || lower.contains("fasting") || lower.contains("натощак") -> fastingOption
        lower.contains("إفطار") || lower.contains("breakfast") || lower.contains("завтрак") -> postBreakfast
        lower.contains("قبل الغداء") || lower.contains("before lunch") || lower.contains("перед обедом") -> beforeLunch
        lower.contains("بعد الغداء") || lower.contains("after lunch") || lower.contains("после обеда") -> afterLunch
        lower.contains("قبل العشاء") || lower.contains("before dinner") || lower.contains("перед ужином") -> beforeDinner
        lower.contains("بعد العشاء") || lower.contains("after dinner") || lower.contains("после ужина") -> afterDinner
        lower.contains("نوم") || lower.contains("bed") || lower.contains("сном") -> beforeBed
        lower.contains("عشوائي") || lower.contains("random") || lower.contains("случайн") -> randomOption
        lower.contains("أكل") || lower.contains("postprandial") || lower.contains("еды") -> postprandial
        else -> raw
    }
}

fun AppStrings.localizePeriod(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val lower = raw.trim().lowercase()
    return when {
        lower.contains("صباح") || lower.contains("morning") || lower.contains("утр") -> morningOption
        lower.contains("ظهر") || lower.contains("noon") || lower.contains("днем") -> noonOption
        lower.contains("مساء") || lower.contains("evening") || lower.contains("вечер") -> eveningOption
        lower.contains("ليل") || lower.contains("night") || lower.contains("ноч") -> nightOption
        else -> raw
    }
}

fun AppStrings.localizeFrequency(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val lower = raw.trim().lowercase()
    return when {
        lower.contains("يومياً") || lower.contains("daily") || lower.contains("ежедневно") -> dailyFreq
        lower.contains("مرتين") || lower.contains("twice") || lower.contains("дважды") -> twiceDailyFreq
        lower.contains("3") || lower.contains("ثلاث") || lower.contains("3 раза") -> thriceDailyFreq
        lower.contains("أسبوع") || lower.contains("weekly") || lower.contains("еженедельно") -> weeklyFreq
        lower.contains("لزوم") || lower.contains("needed") || lower.contains("необходимости") -> asNeededFreq
        else -> raw
    }
}

fun AppStrings.localizeInstruction(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val lower = raw.trim().lowercase()
    return when {
        lower.contains("قبل الأكل") || lower.contains("before meal") || lower.contains("до еды") -> beforeFood
        lower.contains("بعد الأكل") || lower.contains("after meal") || lower.contains("после еды") -> afterFood
        lower.contains("مع الطعام") || lower.contains("with food") || lower.contains("во время еды") -> withFood
        lower.contains("قبل النوم") || lower.contains("before bed") || lower.contains("перед сном") -> beforeSleep
        else -> raw
    }
}

fun AppStrings.localizeCategory(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val lower = raw.trim().lowercase()
    return when {
        lower == "الكل" || lower == "all" || lower == "все" -> if (isArabic) "الكل" else if (isRussian) "Все" else "All"
        lower.contains("دم") || lower.contains("cbc") || lower.contains("кров") -> if (isArabic) "الدم (CBC)" else if (isRussian) "Кровь (CBC)" else "Blood (CBC)"
        lower.contains("سكر") || lower.contains("glucose") || lower.contains("сахар") || lower.contains("diabet") -> if (isArabic) "السكري" else if (isRussian) "Диабет и сахар" else "Diabetes & Sugar"
        lower.contains("دهون") || lower.contains("كولسترول") || lower.contains("lipid") || lower.contains("холестерин") -> if (isArabic) "الدهون والكولسترول" else if (isRussian) "Липидный профиль" else "Lipids & Cholesterol"
        lower.contains("كلى") || lower.contains("renal") || lower.contains("kidney") || lower.contains("почк") -> if (isArabic) "وظائف الكلى والأملاح" else if (isRussian) "Функция почек" else "Kidney & Electrolytes"
        lower.contains("كبد") || lower.contains("liver") || lower.contains("печен") -> if (isArabic) "وظائف الكبد" else if (isRussian) "Функция печени" else "Liver Function"
        lower.contains("فيتامين") || lower.contains("vitamin") || lower.contains("витамин") -> if (isArabic) "الفيتامينات والمعادن" else if (isRussian) "Витамины и минералы" else "Vitamins & Minerals"
        lower.contains("هرمون") || lower.contains("hormone") || lower.contains("гормон") -> if (isArabic) "الهرمونات والغدد" else if (isRussian) "Гормоны и железы" else "Hormones & Endocrine"
        lower.contains("قلب") || lower.contains("cardio") || lower.contains("сердц") -> if (isArabic) "القلب والأوعية" else if (isRussian) "Сердечно-сосудистые" else "Cardiovascular"
        lower.contains("عام") || lower.contains("general") || lower.contains("общ") -> if (isArabic) "عام" else if (isRussian) "Общие" else "General"
        else -> raw
    }
}

fun AppStrings.localizeTestName(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val trimmed = raw.trim()
    val match = com.example.data.model.PredefinedLabTestsCatalog.list.firstOrNull {
        it.key.equals(trimmed, ignoreCase = true) ||
        it.nameEn.equals(trimmed, ignoreCase = true) ||
        it.nameAr.equals(trimmed, ignoreCase = true) ||
        trimmed.contains(it.nameEn, ignoreCase = true) ||
        trimmed.contains(it.nameAr, ignoreCase = true)
    }
    if (match != null) {
        return if (isArabic) match.nameAr else match.nameEn
    }
    val regex = Regex("""^([^\(]+)\s*\((.+)\)$""")
    val regexMatch = regex.find(trimmed)
    if (regexMatch != null) {
        val part1 = regexMatch.groupValues[1].trim()
        val part2 = regexMatch.groupValues[2].trim()
        val isPart1Arabic = part1.any { it in '\u0600'..'\u06FF' }
        val isPart2Arabic = part2.any { it in '\u0600'..'\u06FF' }
        if (isArabic) {
            return if (isPart1Arabic) part1 else if (isPart2Arabic) part2 else trimmed
        } else {
            return if (!isPart1Arabic) part1 else if (!isPart2Arabic) part2 else trimmed
        }
    }
    return trimmed
}

fun AppStrings.localizeDosage(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    var s = raw.trim()
    s = s.replace("أقراص", if (isArabic) "أقراص" else if (isRussian) "таб." else "tabs")
         .replace("قرص واحد", if (isArabic) "قرص واحد" else if (isRussian) "1 таб." else "1 tab")
         .replace("قرص", if (isArabic) "قرص" else if (isRussian) "таб." else "tab")
         .replace("كبسولة واحدة", if (isArabic) "كبسولة واحدة" else if (isRussian) "1 капс." else "1 cap")
         .replace("كبسولة", if (isArabic) "كبسولة" else if (isRussian) "капс." else "cap")
         .replace("الجرعة:", if (isArabic) "الجرعة:" else if (isRussian) "Дозировка:" else "Dosage:")
    return s
}

fun AppStrings.localizeTime(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    var s = raw.trim()
    s = s.replace(" ص", if (isArabic) " ص" else if (isRussian) " AM" else " AM")
         .replace(" م", if (isArabic) " م" else if (isRussian) " PM" else " PM")
    return s
}
