package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AiConsultationEntity
import com.example.data.model.BloodPressureEntity
import com.example.data.model.BloodSugarEntity
import com.example.data.model.CustomTestNameEntity
import com.example.data.model.DriveSyncFileEntity
import com.example.data.model.LabTestEntity
import com.example.data.model.MedicationEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WeightRecordEntity

@Database(
    entities = [
        LabTestEntity::class,
        BloodPressureEntity::class,
        BloodSugarEntity::class,
        WeightRecordEntity::class,
        AiConsultationEntity::class,
        UserProfileEntity::class,
        MedicationEntity::class,
        DriveSyncFileEntity::class,
        CustomTestNameEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class MedicalDatabase : RoomDatabase() {
    abstract fun medicalDao(): MedicalDao

    companion object {
        @Volatile
        private var INSTANCE: MedicalDatabase? = null

        fun getDatabase(context: Context): MedicalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedicalDatabase::class.java,
                    "medical_ai_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
