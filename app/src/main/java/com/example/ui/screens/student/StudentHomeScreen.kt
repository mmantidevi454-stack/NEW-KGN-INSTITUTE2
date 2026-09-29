package com.example.ui.screens.student

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CourseEntity
import com.example.data.model.LiveClassEntity
import com.example.data.model.RecordedLectureEntity
import com.example.ui.components.CourseCard
import com.example.ui.components.LogoHeader
import com.example.ui.navigation.Screen
import com.example.ui.theme.KgnGold
import com.example.ui.theme.KgnNavyDark
import com.example.ui.theme.KgnNavyPrimary
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun StudentHomeScreen(
    viewModel: InstituteViewModel,
    onNavigate: (Screen) -> Unit,
    onRoleSwitchClick: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val liveClasses by viewModel.liveClasses.collectAsState()
    val enrolledCourses by viewModel.enrolledCourses.collectAsState()
    val allCourses by viewModel.allCourses.collectAsState()
    val recordedLectures by viewModel.recordedLectures.collectAsState()
    val isHindi by viewModel.isHindi.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val nextLiveClass = liveClasses.firstOrNull { it.status == "LIVE_NOW" } ?: liveClasses.firstOrNull { it.status == "UPCOMING" }
    val continueLecture = recordedLectures.firstOrNull { it.watchProgressPercent in 0.01f..0.94f } ?: recordedLectures.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Institute Branding Header
        item {
            LogoHeader(
                showRoleBadge = user?.role ?: "STUDENT",
                onRoleClick = onRoleSwitchClick
            )
        }

        // 2. Student Welcome & Streak / Points Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(18.dp),
                color = KgnNavyPrimary
            ) {
                Box {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(KgnGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) "नमस्ते, ${user?.name ?: "छात्र"}" else "Welcome, ${user?.name ?: "Student"}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = user?.courseOrBatch ?: "Target 2026 Batch",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                                    )
                                }
                            }

                            // Quick Profile button
                            IconButton(
                                onClick = { onNavigate(Screen.Profile) },
                                modifier = Modifier.testTag("home_profile_btn")
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = "Profile", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gamification Row: Streak + XP Points + Claim button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Streak Card
                            Surface(
                                color = Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🔥", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "${user?.streakDays ?: 14} Days",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = if (isHindi) "अध्ययन स्ट्रीक" else "Study Streak",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        )
                                    }
                                }
                            }

                            // Points Card
                            Surface(
                                color = Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).clickable { onNavigate(Screen.Leaderboard) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚡", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "${user?.points ?: 1450} XP",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                color = KgnGold
                                            )
                                        )
                                        Text(
                                            text = if (isHindi) "लीडरबोर्ड" else "Rank #2",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )
                                        )
                                    }
                                }
                            }

                            // Claim Daily Streak
                            Button(
                                onClick = { viewModel.claimDailyStreak() },
                                colors = ButtonDefaults.buttonColors(containerColor = KgnGold),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("claim_streak_btn")
                            ) {
                                Text(
                                    text = "+25 XP",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Search Bar
        item {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(if (isHindi) "कोर्स, विषय, लेक्चर या नोट्स खोजें..." else "Search courses, lectures, notes, tests...")
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
                    singleLine = true
                )
            }
        }

        // 4. Live Class Spotlight (Prominent)
        if (nextLiveClass != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "🔴 लाइव कक्षाएं" else "🔴 Live Sessions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = { onNavigate(Screen.LiveClasses) }) {
                            Text(if (isHindi) "सभी देखें" else "View All")
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (nextLiveClass.status == "LIVE_NOW") Color(0xFF1E293B) else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(Screen.LiveRoom(nextLiveClass)) }
                            .testTag("home_live_class_card")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (nextLiveClass.status == "LIVE_NOW") Color(0xFFDC2626) else MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (nextLiveClass.status == "LIVE_NOW") "● LIVE NOW" else "UPCOMING",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (nextLiveClass.status == "LIVE_NOW") Color.White else MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = nextLiveClass.time,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (nextLiveClass.status == "LIVE_NOW") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = nextLiveClass.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (nextLiveClass.status == "LIVE_NOW") Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = KgnGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${nextLiveClass.teacherName} • ${nextLiveClass.subject}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (nextLiveClass.status == "LIVE_NOW") Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { onNavigate(Screen.LiveRoom(nextLiveClass)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("join_live_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (nextLiveClass.status == "LIVE_NOW") Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (nextLiveClass.status == "LIVE_NOW") "JOIN LIVE CLASSROOM" else "SET REMINDER & DETAILS",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Quick Access Grid (10 Core Institute Features)
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isHindi) "त्वरित सुविधाएं" else "Quick Learning Hub",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                val hubItems = listOf(
                    Triple("Live Classes", Icons.Default.Videocam, Screen.LiveClasses),
                    Triple("Recorded", Icons.Default.PlayCircle, Screen.RecordedLectures),
                    Triple("Notes & PDFs", Icons.Default.MenuBook, Screen.StudyNotes),
                    Triple("Tests & Exams", Icons.Default.Assignment, Screen.ExamsList),
                    Triple("Assignments", Icons.Default.Task, Screen.Assignments),
                    Triple("Ask Doubts", Icons.Default.LiveHelp, Screen.Doubts),
                    Triple("Leaderboard", Icons.Default.EmojiEvents, Screen.Leaderboard),
                    Triple("Attendance", Icons.Default.DateRange, Screen.Attendance),
                    Triple("Refer & Earn", Icons.Default.CardGiftcard, Screen.ReferralProgram),
                    Triple("All Courses", Icons.Default.ShoppingBag, Screen.CourseCatalog)
                )

                // 2 rows of 5 icons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        hubItems.take(5).forEach { (title, icon, screen) ->
                            QuickHubButton(
                                title = title,
                                icon = icon,
                                onClick = { onNavigate(screen) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        hubItems.drop(5).forEach { (title, icon, screen) ->
                            QuickHubButton(
                                title = title,
                                icon = icon,
                                onClick = { onNavigate(screen) }
                            )
                        }
                    }
                }
            }
        }

        // 6. Continue Learning (Recorded lecture resume)
        if (continueLecture != null) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = if (isHindi) "पढ़ाई जारी रखें" else "Continue Learning",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        onClick = { onNavigate(Screen.VideoPlayer(continueLecture)) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("continue_learning_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(KgnNavyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = continueLecture.subject.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = KgnGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = continueLecture.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { continueLecture.watchProgressPercent },
                                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${(continueLecture.watchProgressPercent * 100).toInt()}% watched • Resume",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. My Enrolled Courses
        item {
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "मेरे नामांकित पाठ्यक्रम" else "My Enrolled Courses",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { onNavigate(Screen.CourseCatalog) }) {
                        Text(if (isHindi) "कैटलॉग" else "Explore More")
                    }
                }

                if (enrolledCourses.isEmpty()) {
                    Text(
                        text = "You haven't enrolled in any batch yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(enrolledCourses, key = { it.id }) { course ->
                            Box(modifier = Modifier.width(280.dp)) {
                                CourseCard(
                                    course = course,
                                    onClick = { onNavigate(Screen.CourseDetail(course)) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 8. Refer & Earn Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable { onNavigate(Screen.ReferralProgram) },
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFEF3C7)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🎁", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "दोस्तों को रेफर करें और कमाएं!" else "Refer Friends & Earn ₹500",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F)
                            )
                        )
                        Text(
                            text = "Your friends get ₹500 discount & you earn ₹500 wallet cash for every enrollment.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF92400E)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF78350F)
                    )
                }
            }
        }

        // 9. Institute Contact & Support Helpline
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "KGN Academic Helpline",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Mon-Sat • 9:00 AM - 8:00 PM (+91 98765 00000)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Call/Chat", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickHubButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
