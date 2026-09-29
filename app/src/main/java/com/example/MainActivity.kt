package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.KgnBottomBar
import com.example.ui.components.KgnTopAppBar
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.navigation.Screen
import com.example.ui.screens.auth.ForgotPasswordScreen
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.SignUpScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.student.*
import com.example.ui.screens.teacher.TeacherDashboardScreen
import com.example.ui.theme.NewKgnInstituteTheme
import com.example.ui.viewmodel.InstituteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: InstituteViewModel = viewModel()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val isHindi by viewModel.isHindi.collectAsState()
            val notifications by viewModel.allNotifications.collectAsState()

            var showRoleDialog by remember { mutableStateOf(false) }
            val unreadCount = remember(notifications) { notifications.count { !it.isRead } }

            val isAuthScreen = currentScreen is Screen.Login || currentScreen is Screen.SignUp || currentScreen is Screen.ForgotPassword
            val isTeacherScreen = currentScreen is Screen.TeacherDashboard
            val isAdminScreen = currentScreen is Screen.AdminDashboard
            val isLiveRoom = currentScreen is Screen.LiveRoom
            val isExamAttempt = currentScreen is Screen.ExamAttempt

            // Handle back navigation safely
            val canGoBack = !isAuthScreen && currentScreen !is Screen.StudentDashboard && !isTeacherScreen && !isAdminScreen

            BackHandler(enabled = canGoBack) {
                viewModel.navigateBack()
            }

            NewKgnInstituteTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        if (!isAuthScreen && !isLiveRoom && !isExamAttempt) {
                            val title = when (currentScreen) {
                                is Screen.StudentDashboard -> "NEW KGN INSTITUTE"
                                is Screen.LiveClasses -> if (isHindi) "लाइव कक्षाएं" else "Live Classes"
                                is Screen.RecordedLectures -> if (isHindi) "रिकॉर्डेड लेक्चर्स" else "Recorded Classes"
                                is Screen.StudyNotes -> if (isHindi) "स्टडी नोट्स" else "Study Material"
                                is Screen.ExamsList -> if (isHindi) "ऑनलाइन टेस्ट" else "Test & Exams"
                                is Screen.Assignments -> if (isHindi) "असाइनमेंट्स" else "Assignments"
                                is Screen.Doubts -> if (isHindi) "शंका समाधान" else "Doubt Solving"
                                is Screen.Attendance -> if (isHindi) "उपस्थिति" else "Attendance Tracker"
                                is Screen.CourseCatalog -> if (isHindi) "पाठ्यक्रम" else "All Courses"
                                is Screen.Leaderboard -> if (isHindi) "लीडरबोर्ड" else "All-India Leaderboard"
                                is Screen.ReferralProgram -> if (isHindi) "रेफर और कमाएं" else "Refer & Earn"
                                is Screen.Notifications -> "Notifications"
                                is Screen.Profile -> "Profile & Settings"
                                is Screen.TeacherDashboard -> "Teacher Portal"
                                is Screen.AdminDashboard -> "Admin Console"
                                else -> "NEW KGN INSTITUTE"
                            }

                            KgnTopAppBar(
                                title = title,
                                canNavigateBack = canGoBack,
                                onNavigateBack = { viewModel.navigateBack() },
                                unreadNotificationsCount = unreadCount,
                                onNotificationClick = { viewModel.navigateTo(Screen.Notifications) },
                                isDarkMode = isDarkMode,
                                onToggleDarkMode = { viewModel.isDarkMode.value = !isDarkMode },
                                isHindi = isHindi,
                                onToggleLanguage = { viewModel.isHindi.value = !isHindi },
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                        }
                    },
                    bottomBar = {
                        val showBottomBar = !isAuthScreen && !isTeacherScreen && !isAdminScreen && !isLiveRoom && !isExamAttempt
                        if (showBottomBar) {
                            KgnBottomBar(
                                currentScreen = currentScreen,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        when (val screen = currentScreen) {
                            // Auth
                            is Screen.Login -> LoginScreen(
                                viewModel = viewModel,
                                onNavigateToSignUp = { viewModel.navigateTo(Screen.SignUp) },
                                onNavigateToForgotPassword = { viewModel.navigateTo(Screen.ForgotPassword) }
                            )
                            is Screen.SignUp -> SignUpScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                            is Screen.ForgotPassword -> ForgotPasswordScreen(
                                onNavigateBack = { viewModel.navigateBack() }
                            )

                            // Student Core
                            is Screen.StudentDashboard -> StudentHomeScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) },
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.LiveClasses -> LiveClassesScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            is Screen.LiveRoom -> LiveClassRoomScreen(
                                liveClass = screen.liveClass,
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                            is Screen.RecordedLectures -> RecordedLecturesScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            is Screen.VideoPlayer -> VideoPlayerScreen(
                                lecture = screen.lecture,
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                            is Screen.StudyNotes -> NotesScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            is Screen.NoteViewer -> NoteViewerScreen(
                                material = screen.material,
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                            is Screen.ExamsList -> ExamsScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            is Screen.ExamAttempt -> ExamAttemptScreen(
                                exam = screen.exam,
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                            is Screen.ExamResult -> ExamResultScreen(
                                exam = screen.exam,
                                score = screen.score,
                                maxScore = screen.maxScore,
                                correct = screen.correct,
                                wrong = screen.wrong,
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateTo(Screen.ExamsList) },
                                onViewLeaderboard = { viewModel.navigateTo(Screen.Leaderboard) }
                            )
                            is Screen.Assignments -> AssignmentsScreen(
                                viewModel = viewModel
                            )
                            is Screen.Doubts -> DoubtsScreen(
                                viewModel = viewModel
                            )
                            is Screen.Attendance -> AttendanceScreen(
                                viewModel = viewModel
                            )
                            is Screen.CourseCatalog -> CoursesCatalogScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            is Screen.CourseDetail -> CoursesCatalogScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            is Screen.Leaderboard -> LeaderboardScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                            is Screen.ReferralProgram -> ReferralScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                            is Screen.Notifications -> NotificationsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.navigateBack() }
                            )
                            is Screen.Profile -> StudentProfileScreen(
                                viewModel = viewModel,
                                onNavigate = { viewModel.navigateTo(it) },
                                onNavigateBack = { viewModel.navigateBack() }
                            )

                            // Teacher
                            is Screen.TeacherDashboard -> TeacherDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.TeacherDoubts -> TeacherDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.TeacherAssignments -> TeacherDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.TeacherScheduleLive -> TeacherDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )

                            // Admin
                            is Screen.AdminDashboard -> AdminDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.AdminManageStudents -> AdminDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.AdminManageTeachers -> AdminDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.AdminManageCourses -> AdminDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.AdminAnalytics -> AdminDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.AdminAnnouncements -> AdminDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                            is Screen.AdminReferrals -> AdminDashboardScreen(
                                viewModel = viewModel,
                                onRoleSwitchClick = { showRoleDialog = true }
                            )
                        }
                    }
                }

                if (showRoleDialog) {
                    RoleSwitcherDialog(
                        currentRole = currentUser?.role ?: "STUDENT",
                        onDismiss = { showRoleDialog = false },
                        onSelectRole = { newRole ->
                            viewModel.quickSwitchRole(newRole)
                        }
                    )
                }
            }
        }
    }
}
