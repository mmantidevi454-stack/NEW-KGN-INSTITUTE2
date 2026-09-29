package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.InstituteRepository
import com.example.ui.navigation.ChatMessage
import com.example.ui.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InstituteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InstituteRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = InstituteRepository(db.instituteDao())
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            // Set initial active student user by default
            val student = repository.authenticateUser("student@kgn.edu", "pass123")
            _currentUser.value = student
        }
    }

    // Navigation Stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.StudentDashboard))
    val currentScreen: StateFlow<Screen> = _screenStack.map { it.lastOrNull() ?: Screen.StudentDashboard }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.StudentDashboard)

    fun navigateTo(screen: Screen) {
        val currentList = _screenStack.value.toMutableList()
        currentList.add(screen)
        _screenStack.value = currentList
    }

    fun navigateBack(): Boolean {
        val currentList = _screenStack.value.toMutableList()
        if (currentList.size > 1) {
            currentList.removeAt(currentList.lastIndex)
            _screenStack.value = currentList
            return true
        }
        return false
    }

    fun setRootScreen(screen: Screen) {
        _screenStack.value = listOf(screen)
    }

    // Auth & User State
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // App Preferences
    val isDarkMode = MutableStateFlow(false)
    val isHindi = MutableStateFlow(false) // Language switch (English / Hindi)

    // Data Flows from Repository
    val allCourses: StateFlow<List<CourseEntity>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val enrolledCourses: StateFlow<List<CourseEntity>> = repository.enrolledCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveClasses: StateFlow<List<LiveClassEntity>> = repository.allLiveClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recordedLectures: StateFlow<List<RecordedLectureEntity>> = repository.allRecordedLectures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyMaterials: StateFlow<List<StudyMaterialEntity>> = repository.allStudyMaterials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTests: StateFlow<List<TestExamEntity>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssignments: StateFlow<List<AssignmentEntity>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDoubts: StateFlow<List<DoubtEntity>> = repository.allDoubts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendance: StateFlow<List<AttendanceRecordEntity>> = repository.allAttendance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = repository.allPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<UserEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeachers: StateFlow<List<UserEntity>> = repository.allTeachers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<UserEntity>> = repository.leaderboard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReferrals: StateFlow<List<ReferralRecordEntity>> = repository.allReferrals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search Query
    val searchQuery = MutableStateFlow("")

    // Auth Operations
    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _authError.value = null
            val user = repository.authenticateUser(email, pass)
            if (user != null) {
                _currentUser.value = user
                when (user.role) {
                    "TEACHER" -> setRootScreen(Screen.TeacherDashboard)
                    "ADMIN" -> setRootScreen(Screen.AdminDashboard)
                    else -> setRootScreen(Screen.StudentDashboard)
                }
                onResult(true)
            } else {
                _authError.value = "Invalid email or password. Please try again."
                onResult(false)
            }
        }
    }

    fun quickSwitchRole(role: String) {
        viewModelScope.launch {
            val email = when (role) {
                "TEACHER" -> "teacher@kgn.edu"
                "ADMIN" -> "admin@kgn.edu"
                else -> "student@kgn.edu"
            }
            val user = repository.authenticateUser(email, "pass123")
            if (user != null) {
                _currentUser.value = user
                when (role) {
                    "TEACHER" -> setRootScreen(Screen.TeacherDashboard)
                    "ADMIN" -> setRootScreen(Screen.AdminDashboard)
                    else -> setRootScreen(Screen.StudentDashboard)
                }
            }
        }
    }

    fun register(name: String, email: String, mobile: String, pass: String, courseBatch: String, referral: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val newUser = UserEntity(
                id = "u_std_" + System.currentTimeMillis(),
                role = "STUDENT",
                name = name,
                email = email,
                mobile = mobile,
                passwordHash = pass,
                courseOrBatch = courseBatch,
                referralCode = referral,
                avatar = "std_new"
            )
            repository.registerUser(newUser)
            _currentUser.value = newUser
            setRootScreen(Screen.StudentDashboard)
            onResult(true)
        }
    }

    fun logout() {
        _currentUser.value = null
        setRootScreen(Screen.Login)
    }

    fun updateProfile(name: String, mobile: String, course: String) {
        val user = _currentUser.value ?: return
        val updated = user.copy(name = name, mobile = mobile, courseOrBatch = course)
        _currentUser.value = updated
        viewModelScope.launch {
            repository.updateUser(updated)
        }
    }

    // Live Classes Interactive Session
    private val _liveChatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(senderName = "Prof. Salman Qureshi", text = "Welcome students to Rotational Dynamics Session! Feel free to ask queries in the chat.", time = "06:01 PM", isTeacher = true),
            ChatMessage(senderName = "Fatima Shaikh", text = "Good evening sir! Audio and video are crystal clear.", time = "06:02 PM"),
            ChatMessage(senderName = "Rohan Sharma", text = "Sir, will we solve the previous year JEE Advanced problem on rolling motion today?", time = "06:03 PM", isDoubt = true),
            ChatMessage(senderName = "Prof. Salman Qureshi", text = "Yes Rohan, after standard derivations we have 4 high-yield JEE problems lined up.", time = "06:04 PM", isTeacher = true)
        )
    )
    val liveChatMessages: StateFlow<List<ChatMessage>> = _liveChatMessages.asStateFlow()

    val isHandRaised = MutableStateFlow(false)
    val isMicMuted = MutableStateFlow(true)
    val isCameraOn = MutableStateFlow(false)

    fun sendLiveMessage(text: String, isDoubt: Boolean = false) {
        if (text.isBlank()) return
        val sender = _currentUser.value?.name ?: "Student"
        val isTeacher = _currentUser.value?.role == "TEACHER"
        val msg = ChatMessage(
            senderName = sender,
            text = text.trim(),
            time = "Just now",
            isTeacher = isTeacher,
            isDoubt = isDoubt
        )
        _liveChatMessages.value = _liveChatMessages.value + msg
    }

    fun toggleRaiseHand() {
        isHandRaised.value = !isHandRaised.value
        if (isHandRaised.value) {
            sendLiveMessage("✋ Raised hand to ask a question", isDoubt = true)
        }
    }

    fun toggleLiveReminder(liveClass: LiveClassEntity) {
        viewModelScope.launch {
            repository.toggleReminder(liveClass.id, !liveClass.isReminderSet)
        }
    }

    // Video Lectures
    fun updateLectureProgress(lectureId: String, progress: Float) {
        viewModelScope.launch {
            val completed = progress >= 0.95f
            repository.updateLectureProgress(lectureId, progress, completed)
            if (completed) {
                // Award points for completing lecture
                val user = _currentUser.value
                if (user != null) {
                    repository.awardPoints(user.id, 50)
                    _currentUser.value = user.copy(points = user.points + 50)
                }
            }
        }
    }

    fun claimDailyStreak() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.incrementStreak(user.id)
            repository.awardPoints(user.id, 25)
            _currentUser.value = user.copy(streakDays = user.streakDays + 1, points = user.points + 25)
        }
    }

    // Notes
    fun toggleBookmarkNote(material: StudyMaterialEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(material.id, !material.isBookmarked)
        }
    }

    fun toggleDownloadNote(material: StudyMaterialEntity) {
        viewModelScope.launch {
            repository.setDownloaded(material.id, !material.isDownloaded)
        }
    }

    // Test Taking Engine
    private val _currentQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val currentQuestions: StateFlow<List<QuestionEntity>> = _currentQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // qIndex -> option 0..3
    val selectedAnswers: StateFlow<Map<Int, Int>> = _selectedAnswers.asStateFlow()

    private val _markedForReview = MutableStateFlow<Set<Int>>(emptySet())
    val markedForReview: StateFlow<Set<Int>> = _markedForReview.asStateFlow()

    private val _testTimerSeconds = MutableStateFlow(0)
    val testTimerSeconds: StateFlow<Int> = _testTimerSeconds.asStateFlow()

    private var timerJob: Job? = null

    fun startTest(test: TestExamEntity) {
        viewModelScope.launch {
            val qList = repository.getQuestionsForTest(test.id)
            _currentQuestions.value = qList
            _currentQuestionIndex.value = 0
            _selectedAnswers.value = emptyMap()
            _markedForReview.value = emptySet()
            _testTimerSeconds.value = test.durationMinutes * 60

            timerJob?.cancel()
            timerJob = launch {
                while (_testTimerSeconds.value > 0) {
                    delay(1000)
                    _testTimerSeconds.value = _testTimerSeconds.value - 1
                }
                submitExam(test)
            }
            navigateTo(Screen.ExamAttempt(test))
        }
    }

    fun selectOption(optionIndex: Int) {
        val qIdx = _currentQuestionIndex.value
        val map = _selectedAnswers.value.toMutableMap()
        map[qIdx] = optionIndex
        _selectedAnswers.value = map
    }

    fun clearOption() {
        val qIdx = _currentQuestionIndex.value
        val map = _selectedAnswers.value.toMutableMap()
        map.remove(qIdx)
        _selectedAnswers.value = map
    }

    fun toggleMarkForReview() {
        val qIdx = _currentQuestionIndex.value
        val set = _markedForReview.value.toMutableSet()
        if (set.contains(qIdx)) set.remove(qIdx) else set.add(qIdx)
        _markedForReview.value = set
    }

    fun jumpToQuestion(index: Int) {
        if (index in _currentQuestions.value.indices) {
            _currentQuestionIndex.value = index
        }
    }

    fun submitExam(test: TestExamEntity) {
        timerJob?.cancel()
        viewModelScope.launch {
            val questions = _currentQuestions.value
            val answers = _selectedAnswers.value
            var correctCount = 0
            var wrongCount = 0

            questions.forEachIndexed { idx, q ->
                val chosen = answers[idx]
                if (chosen != null) {
                    if (chosen == q.correctOption) {
                        correctCount++
                    } else {
                        wrongCount++
                    }
                }
            }

            // Calculation: 4 marks for correct, -1 for wrong
            val rawScore = (correctCount * 4) - (wrongCount * 1)
            val finalScore = if (rawScore < 0) 0 else rawScore
            val maxScore = questions.size * 4

            repository.recordTestResult(test.id, finalScore)
            val user = _currentUser.value
            if (user != null) {
                val awarded = 100 + (finalScore * 5)
                repository.awardPoints(user.id, awarded)
                _currentUser.value = user.copy(points = user.points + awarded)
            }
            navigateTo(Screen.ExamResult(test, finalScore, maxScore, correctCount, wrongCount))
        }
    }

    // Assignments
    fun submitAssignmentSolution(assignmentId: String, text: String) {
        viewModelScope.launch {
            repository.submitAssignment(assignmentId, text)
        }
    }

    fun gradeAssignment(assignmentId: String, marks: Int, feedback: String) {
        viewModelScope.launch {
            repository.gradeAssignment(assignmentId, marks, feedback)
        }
    }

    // Doubts
    fun askDoubt(subject: String, topic: String, text: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val doubt = DoubtEntity(
                id = "dbt_" + System.currentTimeMillis(),
                studentName = user.name,
                studentId = user.id,
                subject = subject,
                topic = topic,
                questionText = text,
                status = "UNDER_REVIEW",
                createdAt = "Just now"
            )
            repository.askDoubt(doubt)
        }
    }

    fun answerDoubt(doubtId: String, reply: String) {
        viewModelScope.launch {
            repository.answerDoubt(doubtId, reply, "Just now")
        }
    }

    // Course Purchase
    fun purchaseCourse(courseId: String, appliedDiscount: Double = 0.0, usedReferralCode: String = "") {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val course = allCourses.value.find { it.id == courseId } ?: return@launch
            val finalPrice = (course.price - appliedDiscount).coerceAtLeast(0.0)
            repository.enrollInCourse(courseId, user, finalPrice)
            if (usedReferralCode.isNotBlank()) {
                val referralRecord = ReferralRecordEntity(
                    id = "ref_" + System.currentTimeMillis(),
                    referrerId = "ref_" + usedReferralCode.lowercase(),
                    referrerName = "Institute Ambassador",
                    referralCode = usedReferralCode.uppercase(),
                    referredUserName = user.name,
                    referredUserEmail = user.email,
                    bonusAmount = 500.0,
                    discountGiven = if (appliedDiscount > 0) appliedDiscount else 500.0,
                    date = "Today"
                )
                repository.recordReferral(referralRecord)
            }
        }
    }

    // Notifications
    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    // Teacher & Admin Management
    fun createLiveClass(title: String, subject: String, courseId: String, date: String, time: String) {
        val teacherName = _currentUser.value?.name ?: "Faculty KGN"
        viewModelScope.launch {
            val live = LiveClassEntity(
                id = "live_" + System.currentTimeMillis(),
                title = title,
                subject = subject,
                courseId = courseId,
                teacherName = teacherName,
                date = date,
                time = time,
                status = "UPCOMING",
                roomKey = "KGN-" + (1000..9999).random()
            )
            repository.createLiveClass(live)
        }
    }

    fun uploadStudyMaterial(title: String, subject: String, chapter: String, fileType: String, content: String) {
        viewModelScope.launch {
            val mat = StudyMaterialEntity(
                id = "mat_" + System.currentTimeMillis(),
                courseId = "c_jee",
                subject = subject,
                chapterName = chapter,
                title = title,
                fileType = fileType,
                fileSize = "3.2 MB",
                uploadDate = "Today",
                contentPreview = content
            )
            repository.addStudyMaterial(mat)
        }
    }

    fun createAnnouncement(title: String, message: String) {
        viewModelScope.launch {
            val notif = NotificationEntity(
                id = "notif_" + System.currentTimeMillis(),
                title = "📢 " + title,
                message = message,
                type = "ANNOUNCEMENT",
                timestamp = "Just now",
                isRead = false
            )
            repository.addNotification(notif)
        }
    }

    fun createCourse(title: String, category: String, desc: String, price: Double, instructor: String) {
        viewModelScope.launch {
            val course = CourseEntity(
                id = "c_" + System.currentTimeMillis(),
                title = title,
                category = category,
                description = desc,
                instructorName = instructor,
                price = price,
                originalPrice = price * 2.5,
                duration = "10 Months",
                validityMonths = 12,
                totalLectures = 100,
                totalNotes = 40,
                totalTests = 20
            )
            repository.createCourse(course)
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            repository.deleteCourse(courseId)
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            repository.deleteUser(userId)
        }
    }
}
