package com.example.data.repository

import com.example.data.local.DefaultData
import com.example.data.local.MedicalDao
import com.example.data.model.AiConsultationEntity
import com.example.data.model.BloodPressureEntity
import com.example.data.model.BloodSugarEntity
import com.example.data.model.CustomTestNameEntity
import com.example.data.model.DriveSyncFileEntity
import com.example.data.model.LabTestEntity
import com.example.data.model.MedicationEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightRecordEntity
import kotlinx.coroutines.flow.Flow

class MedicalRepository(private val dao: MedicalDao) {

    val allLabTests: Flow<List<LabTestEntity>> = dao.getAllLabTests()
    val allBloodPressure: Flow<List<BloodPressureEntity>> = dao.getAllBloodPressure()
    val allBloodSugar: Flow<List<BloodSugarEntity>> = dao.getAllBloodSugar()
    val allWeight: Flow<List<WeightRecordEntity>> = dao.getAllWeight()
    val allConsultations: Flow<List<AiConsultationEntity>> = dao.getAllConsultations()
    val allMedications: Flow<List<MedicationEntity>> = dao.getAllMedications()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allDriveFiles: Flow<List<DriveSyncFileEntity>> = dao.getAllDriveFiles()

    suspend fun initializeDefaultDataIfNeeded() {
        // Automatically clean any residual placeholder 'غير مسحوب' so nothing is written when there is no data
        dao.cleanNotDrawnStatus()
        dao.cleanNotDrawnLabName()
        dao.cleanNotDrawnValue()
        dao.deleteEmptyTests()
        dao.deleteShifaLabTests()

        // Clean any default mock profile so the app starts fresh without automatic names
        val currentProfile = dao.getUserProfileOnce()
        if (currentProfile != null && (currentProfile.name.contains("أسامة") || currentProfile.name.contains("أحمد") || currentProfile.name == "أسامة جمال يوسف سلسع")) {
            dao.saveUserProfile(DefaultData.defaultUserProfile)
        }
    }

    suspend fun deleteShifaTests() = dao.deleteShifaLabTests()

    suspend fun insertLabTest(test: LabTestEntity) = dao.insertLabTest(test)
    suspend fun updateLabTest(test: LabTestEntity) = dao.updateLabTest(test)
    suspend fun deleteLabTest(test: LabTestEntity) = dao.deleteLabTest(test)
    suspend fun insertAllLabTests(tests: List<LabTestEntity>): List<Long> = dao.insertAllLabTests(tests)
    suspend fun getAllLabTestsList(): List<LabTestEntity> = dao.getAllLabTestsList()

    suspend fun insertBloodPressure(bp: BloodPressureEntity) = dao.insertBloodPressure(bp)
    suspend fun insertAllBloodPressure(list: List<BloodPressureEntity>) = dao.insertAllBloodPressure(list)
    suspend fun deleteBloodPressure(bp: BloodPressureEntity) = dao.deleteBloodPressure(bp)
    suspend fun getAllBloodPressureList(): List<BloodPressureEntity> = dao.getAllBloodPressureList()

    suspend fun insertBloodSugar(sugar: BloodSugarEntity) = dao.insertBloodSugar(sugar)
    suspend fun insertAllBloodSugar(list: List<BloodSugarEntity>) = dao.insertAllBloodSugar(list)
    suspend fun deleteBloodSugar(sugar: BloodSugarEntity) = dao.deleteBloodSugar(sugar)
    suspend fun getAllBloodSugarList(): List<BloodSugarEntity> = dao.getAllBloodSugarList()

    suspend fun insertWeight(weight: WeightRecordEntity) = dao.insertWeight(weight)
    suspend fun insertAllWeight(list: List<WeightRecordEntity>) = dao.insertAllWeight(list)
    suspend fun deleteWeight(weight: WeightRecordEntity) = dao.deleteWeight(weight)
    suspend fun getAllWeightList(): List<WeightRecordEntity> = dao.getAllWeightList()

    suspend fun insertConsultation(c: AiConsultationEntity) = dao.insertConsultation(c)
    suspend fun insertAllConsultations(list: List<AiConsultationEntity>) = dao.insertAllConsultations(list)
    suspend fun deleteConsultation(c: AiConsultationEntity) = dao.deleteConsultation(c)
    suspend fun getAllConsultationsList(): List<AiConsultationEntity> = dao.getAllConsultationsList()

    suspend fun insertMedication(med: MedicationEntity): Long = dao.insertMedication(med)
    suspend fun insertAllMedications(list: List<MedicationEntity>) = dao.insertAllMedications(list)
    suspend fun updateMedication(med: MedicationEntity) = dao.updateMedication(med)
    suspend fun deleteMedication(med: MedicationEntity) = dao.deleteMedication(med)
    suspend fun clearMedications() = dao.clearMedications()
    suspend fun getAllMedicationsList(): List<MedicationEntity> = dao.getAllMedicationsList()

    suspend fun saveUserProfile(profile: UserProfileEntity) = dao.saveUserProfile(profile)
    suspend fun getUserProfileOnce(): UserProfileEntity? = dao.getUserProfileOnce()

    suspend fun clearAllData(includeTests: Boolean = true) {
        dao.clearBloodPressure()
        dao.clearBloodSugar()
        dao.clearWeight()
        dao.clearConsultations()
        dao.clearMedications()
        dao.saveUserProfile(DefaultData.defaultUserProfile)
        if (includeTests) {
            dao.clearLabTests()
        }
    }

    suspend fun loadBlankLabTestTemplates() {
        dao.clearLabTests()
        dao.insertAllLabTests(DefaultData.blankTemplateLabTests)
    }

    suspend fun deleteEmptyTests() {
        dao.deleteEmptyTests()
    }

    suspend fun insertDriveFile(file: DriveSyncFileEntity): Long = dao.insertDriveFile(file)
    suspend fun deleteDriveFile(file: DriveSyncFileEntity) = dao.deleteDriveFile(file)
    suspend fun clearDriveFiles() = dao.clearDriveFiles()

    // --- Custom Test Names ---
    val allCustomTestNames: Flow<List<CustomTestNameEntity>> = dao.getAllCustomTestNames()
    val distinctSavedTestNames: Flow<List<String>> = dao.getDistinctSavedTestNames()

    suspend fun insertCustomTestName(test: CustomTestNameEntity) = dao.insertCustomTestName(test)
    suspend fun deleteCustomTestName(name: String) = dao.deleteCustomTestName(name)
}
