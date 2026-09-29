package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InstituteDao {

    // USERS
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'STUDENT'")
    fun getAllStudents(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'TEACHER'")
    fun getAllTeachers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    // COURSES
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE isEnrolled = 1")
    fun getEnrolledCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: String): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("UPDATE courses SET isEnrolled = 1 WHERE id = :courseId")
    suspend fun enrollInCourse(courseId: String)

    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteCourse(courseId: String)

    // LIVE CLASSES
    @Query("SELECT * FROM live_classes ORDER BY CASE WHEN status = 'LIVE_NOW' THEN 0 WHEN status = 'UPCOMING' THEN 1 ELSE 2 END")
    fun getAllLiveClasses(): Flow<List<LiveClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveClass(liveClass: LiveClassEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveClasses(classes: List<LiveClassEntity>)

    @Query("UPDATE live_classes SET status = :status WHERE id = :classId")
    suspend fun updateLiveClassStatus(classId: String, status: String)

    @Query("UPDATE live_classes SET isReminderSet = :reminder WHERE id = :classId")
    suspend fun toggleReminder(classId: String, reminder: Boolean)

    @Query("DELETE FROM live_classes WHERE id = :classId")
    suspend fun deleteLiveClass(classId: String)

    // RECORDED LECTURES
    @Query("SELECT * FROM recorded_lectures ORDER BY id ASC")
    fun getAllRecordedLectures(): Flow<List<RecordedLectureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecordedLecture(lecture: RecordedLectureEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecordedLectures(lectures: List<RecordedLectureEntity>)

    @Query("UPDATE recorded_lectures SET watchProgressPercent = :progress, isCompleted = :completed WHERE id = :lectureId")
    suspend fun updateLectureProgress(lectureId: String, progress: Float, completed: Boolean)

    @Query("DELETE FROM recorded_lectures WHERE id = :lectureId")
    suspend fun deleteLecture(lectureId: String)

    // STUDY MATERIALS / NOTES
    @Query("SELECT * FROM study_materials ORDER BY uploadDate DESC")
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(material: StudyMaterialEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterials(materials: List<StudyMaterialEntity>)

    @Query("UPDATE study_materials SET isBookmarked = :bookmarked WHERE id = :materialId")
    suspend fun toggleBookmark(materialId: String, bookmarked: Boolean)

    @Query("UPDATE study_materials SET isDownloaded = :downloaded WHERE id = :materialId")
    suspend fun setDownloaded(materialId: String, downloaded: Boolean)

    @Query("DELETE FROM study_materials WHERE id = :materialId")
    suspend fun deleteStudyMaterial(materialId: String)

    // TESTS & EXAMS
    @Query("SELECT * FROM test_exams")
    fun getAllTests(): Flow<List<TestExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestExamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTests(tests: List<TestExamEntity>)

    @Query("UPDATE test_exams SET attempted = 1, score = :score WHERE id = :testId")
    suspend fun recordTestResult(testId: String, score: Int)

    @Query("DELETE FROM test_exams WHERE id = :testId")
    suspend fun deleteTest(testId: String)

    // QUESTIONS
    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY questionNumber ASC")
    suspend fun getQuestionsForTest(testId: String): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    // ASSIGNMENTS
    @Query("SELECT * FROM assignments ORDER BY dueDate ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<AssignmentEntity>)

    @Query("UPDATE assignments SET status = 'SUBMITTED', studentSubmissionText = :submissionText WHERE id = :assignmentId")
    suspend fun submitAssignment(assignmentId: String, submissionText: String)

    @Query("UPDATE assignments SET status = 'EVALUATED', marksAwarded = :marks, teacherFeedback = :feedback WHERE id = :assignmentId")
    suspend fun gradeAssignment(assignmentId: String, marks: Int, feedback: String)

    @Query("DELETE FROM assignments WHERE id = :assignmentId")
    suspend fun deleteAssignment(assignmentId: String)

    // DOUBTS
    @Query("SELECT * FROM doubts ORDER BY createdAt DESC")
    fun getAllDoubts(): Flow<List<DoubtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoubt(doubt: DoubtEntity)

    @Query("UPDATE doubts SET teacherReply = :reply, status = 'RESOLVED', repliedAt = :repliedAt WHERE id = :doubtId")
    suspend fun answerDoubt(doubtId: String, reply: String, repliedAt: String)

    // ATTENDANCE
    @Query("SELECT * FROM attendance_records ORDER BY date DESC")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceRecordEntity>)

    // NOTIFICATIONS
    @Query("SELECT * FROM notifications ORDER BY id DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: String)

    // PAYMENTS
    @Query("SELECT * FROM payments ORDER BY date DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    // REFERRALS
    @Query("SELECT * FROM referrals ORDER BY date DESC")
    fun getAllReferrals(): Flow<List<ReferralRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(referral: ReferralRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferrals(referrals: List<ReferralRecordEntity>)

    // GAMIFICATION
    @Query("UPDATE users SET points = points + :pointsAdded WHERE id = :userId")
    suspend fun addPointsToUser(userId: String, pointsAdded: Int)

    @Query("UPDATE users SET streakDays = streakDays + 1 WHERE id = :userId")
    suspend fun incrementStreak(userId: String)

    @Query("SELECT * FROM users WHERE role = 'STUDENT' ORDER BY points DESC")
    fun getLeaderboard(): Flow<List<UserEntity>>
}
