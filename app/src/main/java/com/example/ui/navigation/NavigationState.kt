package com.example.ui.navigation

import com.example.data.model.*

sealed class Screen {
    // Auth
    object Login : Screen()
    object SignUp : Screen()
    object ForgotPassword : Screen()

    // Student
    object StudentDashboard : Screen()
    object LiveClasses : Screen()
    data class LiveRoom(val liveClass: LiveClassEntity) : Screen()
    object RecordedLectures : Screen()
    data class VideoPlayer(val lecture: RecordedLectureEntity) : Screen()
    object StudyNotes : Screen()
    data class NoteViewer(val material: StudyMaterialEntity) : Screen()
    object ExamsList : Screen()
    data class ExamAttempt(val exam: TestExamEntity) : Screen()
    data class ExamResult(val exam: TestExamEntity, val score: Int, val maxScore: Int, val correct: Int, val wrong: Int) : Screen()
    object Assignments : Screen()
    object Doubts : Screen()
    object Attendance : Screen()
    object CourseCatalog : Screen()
    data class CourseDetail(val course: CourseEntity) : Screen()
    object Notifications : Screen()
    object Profile : Screen()
    object Leaderboard : Screen()
    object ReferralProgram : Screen()

    // Teacher
    object TeacherDashboard : Screen()
    object TeacherDoubts : Screen()
    object TeacherAssignments : Screen()
    object TeacherScheduleLive : Screen()

    // Admin
    object AdminDashboard : Screen()
    object AdminManageStudents : Screen()
    object AdminManageTeachers : Screen()
    object AdminManageCourses : Screen()
    object AdminAnalytics : Screen()
    object AdminAnnouncements : Screen()
    object AdminReferrals : Screen()
}

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString() + "_" + (100..999).random(),
    val senderName: String,
    val text: String,
    val time: String,
    val isTeacher: Boolean = false,
    val isDoubt: Boolean = false
)
