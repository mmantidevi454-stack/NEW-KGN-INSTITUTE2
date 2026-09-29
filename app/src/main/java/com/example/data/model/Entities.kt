package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val role: String, // STUDENT, TEACHER, ADMIN
    val name: String,
    val email: String,
    val mobile: String,
    val passwordHash: String,
    val courseOrBatch: String,
    val avatar: String = "avatar_student",
    val referralCode: String = "",
    val referralCount: Int = 3,
    val walletBonus: Double = 1500.0,
    val points: Int = 1450,
    val badges: String = "Top Scorer,Consistent Learner,Streak Master",
    val attendancePercentage: Float = 92.5f,
    val streakDays: Int = 14,
    val studyMinutes: Int = 420,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "referrals")
data class ReferralRecordEntity(
    @PrimaryKey val id: String,
    val referrerId: String,
    val referrerName: String,
    val referralCode: String,
    val referredUserName: String,
    val referredUserEmail: String,
    val bonusAmount: Double,
    val discountGiven: Double,
    val date: String,
    val status: String = "COMPLETED"
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // IIT-JEE, NEET-UG, Class 10th, Class 12th, Foundation
    val description: String,
    val instructorName: String,
    val price: Double,
    val originalPrice: Double,
    val duration: String,
    val validityMonths: Int,
    val totalLectures: Int,
    val totalNotes: Int,
    val totalTests: Int,
    val isPopular: Boolean = false,
    val isEnrolled: Boolean = false
)

@Entity(tableName = "live_classes")
data class LiveClassEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subject: String,
    val courseId: String,
    val teacherName: String,
    val date: String,
    val time: String,
    val status: String, // LIVE_NOW, UPCOMING, COMPLETED
    val streamUrl: String = "",
    val roomKey: String = "",
    val participantCount: Int = 0,
    val isReminderSet: Boolean = false
)

@Entity(tableName = "recorded_lectures")
data class RecordedLectureEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val subject: String,
    val chapterName: String,
    val title: String,
    val durationMinutes: Int,
    val teacherName: String,
    val videoUrl: String = "",
    val watchProgressPercent: Float = 0f,
    val isCompleted: Boolean = false,
    val viewsCount: Int = 120
)

@Entity(tableName = "study_materials")
data class StudyMaterialEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val subject: String,
    val chapterName: String,
    val title: String,
    val fileType: String, // PDF, DOCX, SLIDES
    val fileSize: String,
    val downloadUrl: String = "",
    val isBookmarked: Boolean = false,
    val isDownloaded: Boolean = false,
    val uploadDate: String = "Today",
    val contentPreview: String = ""
)

@Entity(tableName = "test_exams")
data class TestExamEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val subject: String,
    val title: String,
    val totalQuestions: Int,
    val durationMinutes: Int,
    val totalMarks: Int,
    val negativeMarking: Float = 1.0f,
    val isPublished: Boolean = true,
    val attempted: Boolean = false,
    val score: Int = 0,
    val maxScore: Int = 100
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val questionNumber: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: Int, // 0..3
    val explanation: String
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val subject: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val maxMarks: Int,
    val status: String, // PENDING, SUBMITTED, EVALUATED
    val studentSubmissionText: String? = null,
    val marksAwarded: Int? = null,
    val teacherFeedback: String? = null
)

@Entity(tableName = "doubts")
data class DoubtEntity(
    @PrimaryKey val id: String,
    val studentName: String,
    val studentId: String,
    val subject: String,
    val topic: String,
    val questionText: String,
    val status: String, // UNDER_REVIEW, ANSWERED, RESOLVED
    val teacherReply: String? = null,
    val repliedAt: String? = null,
    val createdAt: String
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val studentName: String,
    val date: String,
    val subject: String,
    val status: String // PRESENT, ABSENT, LATE
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // LIVE, EXAM, ANNOUNCEMENT, ASSIGNMENT, FEE
    val timestamp: String,
    val isRead: Boolean = false
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val studentName: String,
    val courseTitle: String,
    val amount: Double,
    val transactionId: String,
    val date: String,
    val status: String, // SUCCESS, PENDING, FAILED
    val invoiceNumber: String
)
