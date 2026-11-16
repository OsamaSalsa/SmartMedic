package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface MedicalDao {
    // --- Lab Tests ---
    @Query("SELECT * FROM lab_tests ORDER BY id ASC")
    fun getAllLabTests(): Flow<List<LabTestEntity>>

    @Query("SELECT * FROM lab_tests WHERE id = :id")
    suspend fun getLabTestById(id: Long): LabTestEntity?

    @Query("SELECT COUNT(*) FROM lab_tests")
    suspend fun getLabTestsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabTest(labTest: LabTestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLabTests(tests: List<LabTestEntity>): List<Long>

    @Query("SELECT * FROM lab_tests ORDER BY id ASC")
    suspend fun getAllLabTestsList(): List<LabTestEntity>

    @Update
    suspend fun updateLabTest(labTest: LabTestEntity)

    @Delete
    suspend fun deleteLabTest(labTest: LabTestEntity)

    // --- Blood Pressure ---
    @Query("SELECT * FROM blood_pressure_records ORDER BY timestamp DESC")
    fun getAllBloodPressure(): Flow<List<BloodPressureEntity>>

    @Query("SELECT * FROM blood_pressure_records ORDER BY timestamp DESC")
    suspend fun getAllBloodPressureList(): List<BloodPressureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBloodPressure(record: BloodPressureEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBloodPressure(records: List<BloodPressureEntity>)

    @Delete
    suspend fun deleteBloodPressure(record: BloodPressureEntity)

    // --- Blood Sugar ---
    @Query("SELECT * FROM blood_sugar_records ORDER BY timestamp DESC")
    fun getAllBloodSugar(): Flow<List<BloodSugarEntity>>

    @Query("SELECT * FROM blood_sugar_records ORDER BY timestamp DESC")
    suspend fun getAllBloodSugarList(): List<BloodSugarEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBloodSugar(record: BloodSugarEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBloodSugar(records: List<BloodSugarEntity>)

    @Delete
    suspend fun deleteBloodSugar(record: BloodSugarEntity)

    // --- Weight ---
    @Query("SELECT * FROM weight_records ORDER BY timestamp DESC")
    fun getAllWeight(): Flow<List<WeightRecordEntity>>

    @Query("SELECT * FROM weight_records ORDER BY timestamp DESC")
    suspend fun getAllWeightList(): List<WeightRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(record: WeightRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWeight(records: List<WeightRecordEntity>)

    @Delete
    suspend fun deleteWeight(record: WeightRecordEntity)

    // --- AI Consultations ---
    @Query("SELECT * FROM ai_consultations ORDER BY timestamp DESC")
    fun getAllConsultations(): Flow<List<AiConsultationEntity>>

    @Query("SELECT * FROM ai_consultations ORDER BY timestamp DESC")
    suspend fun getAllConsultationsList(): List<AiConsultationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultation(record: AiConsultationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllConsultations(records: List<AiConsultationEntity>)

    @Delete
    suspend fun deleteConsultation(record: AiConsultationEntity)

    // --- Medications ---
    @Query("SELECT * FROM medications ORDER BY hour ASC, minute ASC")
    fun getAllMedications(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications ORDER BY hour ASC, minute ASC")
    suspend fun getAllMedicationsList(): List<MedicationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMedications(records: List<MedicationEntity>)

    @Update
    suspend fun updateMedication(medication: MedicationEntity)

    @Delete
    suspend fun deleteMedication(medication: MedicationEntity)

    @Query("DELETE FROM medications")
    suspend fun clearMedications()

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    // --- Data Reset (Clear Demo / Wipe all tables) ---
    @Query("DELETE FROM lab_tests")
    suspend fun clearLabTests()

    @Query("DELETE FROM blood_pressure_records")
    suspend fun clearBloodPressure()

    @Query("DELETE FROM blood_sugar_records")
    suspend fun clearBloodSugar()

    @Query("DELETE FROM weight_records")
    suspend fun clearWeight()

    @Query("DELETE FROM ai_consultations")
    suspend fun clearConsultations()

    // --- Cleanup queries for 'غير مسحوب' and empty tests ---
    @Query("UPDATE lab_tests SET status = '' WHERE status = 'غير مسحوب'")
    suspend fun cleanNotDrawnStatus()

    @Query("UPDATE lab_tests SET labName = '' WHERE labName = 'غير مسحوب'")
    suspend fun cleanNotDrawnLabName()

    @Query("UPDATE lab_tests SET value = '' WHERE value = 'غير مسحوب'")
    suspend fun cleanNotDrawnValue()

    @Query("DELETE FROM lab_tests WHERE (value = '' OR value IS NULL) AND (testDate = '' OR testDate IS NULL) AND (status = '' OR status IS NULL OR status = 'غير مسحوب')")
    suspend fun deleteEmptyTests()

    @Query("DELETE FROM lab_tests WHERE labName LIKE '%الشفاء%' OR labName LIKE '%SSMC%'")
    suspend fun deleteShifaLabTests()

    // --- Google Drive Synced Files ---
    @Query("SELECT * FROM drive_synced_files ORDER BY timestamp DESC")
    fun getAllDriveFiles(): Flow<List<DriveSyncFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriveFile(file: DriveSyncFileEntity): Long

    @Delete
    suspend fun deleteDriveFile(file: DriveSyncFileEntity)

    @Query("DELETE FROM drive_synced_files")
    suspend fun clearDriveFiles()

    // --- Custom Test Names Catalog ---
    @Query("SELECT * FROM custom_test_names ORDER BY name ASC")
    fun getAllCustomTestNames(): Flow<List<CustomTestNameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomTestName(item: CustomTestNameEntity)

    @Query("DELETE FROM custom_test_names WHERE name = :name")
    suspend fun deleteCustomTestName(name: String)

    @Query("SELECT DISTINCT testName FROM lab_tests WHERE testName != '' ORDER BY testName ASC")
    fun getDistinctSavedTestNames(): Flow<List<String>>
}
