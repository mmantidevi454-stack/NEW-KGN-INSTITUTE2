package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        LiveClassEntity::class,
        RecordedLectureEntity::class,
        StudyMaterialEntity::class,
        TestExamEntity::class,
        QuestionEntity::class,
        AssignmentEntity::class,
        DoubtEntity::class,
        AttendanceRecordEntity::class,
        NotificationEntity::class,
        PaymentEntity::class,
        ReferralRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun instituteDao(): InstituteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "new_kgn_institute.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
