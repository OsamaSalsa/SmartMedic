package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.DefaultData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertTrue(appName.isNotBlank())
  }

  @Test
  fun `launch MainActivity does not crash`() {
    org.robolectric.Robolectric.buildActivity(MainActivity::class.java).use { controller ->
      controller.setup()
    }
  }

  @Test
  fun `default lab tests populated properly`() {
    val tests = DefaultData.blankTemplateLabTests
    assertTrue("Should contain blank template tests", tests.isNotEmpty())
    assertTrue("Should include CBC", tests.any { it.testName.contains("CBC") })
    assertTrue("Should include FBS", tests.any { it.testName.contains("FBS") })
    assertTrue("Should include Cholesterol", tests.any { it.testName.contains("Cholesterol") })
    assertTrue("Should include Vitamin D", tests.any { it.testName.contains("Vitamin D") })
  }

  @Test
  fun `default user profile is empty initially`() {
    val profile = DefaultData.defaultUserProfile
    assertEquals(1L, profile.id)
    assertEquals("", profile.name)
    assertEquals(0, profile.age)
    assertEquals("", profile.bloodType)
  }

  @Test
  fun `shifa lab tests example contains all 33 tests`() {
    val tests = DefaultData.shifaLabTestsExample
    assertEquals(33, tests.size)
    
    // Check WBCs
    val wbcs = tests.find { it.testName.contains("WBCs") }
    assertTrue(wbcs != null && wbcs.value == "5.90")

    // Check Uric Acid
    val uric = tests.find { it.testName.contains("Uric Acid") }
    assertTrue(uric != null && uric.value == "7.2" && uric.statusFlag == "High")

    // Check Vitamin D3
    val vitD = tests.find { it.testName.contains("Vitamin D3") }
    assertTrue(vitD != null && vitD.value == "24.05" && vitD.statusFlag == "Low")

    // Check Vitamin B12
    val vitB12 = tests.find { it.testName.contains("Vitamin B12") }
    assertTrue(vitB12 != null && vitB12.value == "253")

    // Check ESR
    val esr = tests.find { it.testName.contains("ESR") }
    assertTrue(esr != null && esr.value == "6")

    // Check HGB
    val hgb = tests.find { it.testName.contains("HGB") }
    assertTrue(hgb != null && hgb.value == "16.1")
  }

  @Test
  fun `google drive backup summary calculation`() {
    val summary = com.example.util.BackupSummary(
      appName = "SmartHealth",
      backupDate = "2026-10-07 10:00",
      timestamp = 1728286000000L,
      patientName = "أسامة",
      labTestsCount = 10,
      bpCount = 5,
      sugarCount = 8,
      weightCount = 3,
      medicationsCount = 2,
      consultationsCount = 1,
      fileSizeFormatted = "12.5 KB"
    )
    assertEquals(29, summary.totalRecordsCount)
    assertEquals("أسامة", summary.patientName)
  }

  @Test
  fun `predefined lab tests catalog contains essential medical tests`() {
    val catalog = com.example.data.model.PredefinedLabTestsCatalog.list
    assertTrue(catalog.isNotEmpty())
    
    // Check Fasting Blood Sugar
    val fbs = catalog.find { it.key == "fbs" }
    assertTrue(fbs != null && fbs.nameAr.contains("سكر") && fbs.defaultUnit == "mg/dL")

    // Check Cholesterol
    val chol = catalog.find { it.key == "cholesterol" }
    assertTrue(chol != null && chol.defaultRefRange == "< 200")

    // Check Creatinine
    val creat = catalog.find { it.key == "creatinine" }
    assertTrue(creat != null && creat.defaultUnit == "mg/dL")

    // Check search matching
    assertTrue(fbs!!.matches("سكر"))
    assertTrue(fbs.matches("FBS"))
    assertTrue(fbs.matches("glucose"))
  }

  @Test
  fun `rule based medical consultation and chat generate accurate patient report`() {
    val service = com.example.data.remote.GeminiService()
    val consultation = service.generateRuleBasedMedicalConsultation(
      labTests = DefaultData.shifaLabTestsExample,
      bpRecords = listOf(
        com.example.data.model.BloodPressureEntity(
          date = "2026-11-16",
          time = "08:00",
          period = "صباحاً",
          systolic = 128,
          diastolic = 82,
          pulse = 74,
          category = "طبيعي"
        )
      ),
      sugarRecords = listOf(
        com.example.data.model.BloodSugarEntity(
          date = "2026-11-16",
          time = "08:00",
          type = "صائم",
          value = 105f,
          status = "طبيعي"
        )
      ),
      latestWeight = com.example.data.model.WeightRecordEntity(
        date = "2026-11-16",
        weightKg = 78.0f,
        bmi = 25.4f,
        bmiCategory = "طبيعي"
      ),
      profile = DefaultData.defaultUserProfile.copy(name = "أسامة", age = 38),
      medications = listOf(
        com.example.data.model.MedicationEntity(
          name = "Metformin",
          dosage = "500mg",
          time = "08:00",
          instructions = "بعد الفطور",
          frequency = "يومياً",
          isActive = true
        )
      ),
      language = com.example.ui.locale.AppLanguage.AR,
      currentDate = "2026-11-16"
    )
    assertTrue(consultation.title.isNotBlank())
    assertTrue(consultation.summary.contains("33"))
    assertTrue(consultation.dietAdvice.isNotBlank())
    assertTrue(consultation.foodsToEat.isNotBlank())
    assertTrue(consultation.foodsToAvoid.isNotBlank())
    assertTrue(consultation.medicationAdvice.contains("Metformin"))

    val chatReply = service.generateRuleBasedChatResponse(
      userMessage = "ما هو أفضل نظام غذائي لضبط السكر؟",
      medicalContext = consultation.summary,
      language = com.example.ui.locale.AppLanguage.AR
    )
    assertTrue(chatReply.contains("سكر"))
  }
}

