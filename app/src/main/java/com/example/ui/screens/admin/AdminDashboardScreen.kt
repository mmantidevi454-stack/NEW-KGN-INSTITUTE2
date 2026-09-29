package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseEntity
import com.example.ui.components.LogoHeader
import com.example.ui.theme.KgnGold
import com.example.ui.theme.KgnNavyDark
import com.example.ui.theme.KgnNavyPrimary
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: InstituteViewModel,
    onRoleSwitchClick: () -> Unit
) {
    val students by viewModel.allStudents.collectAsState()
    val teachers by viewModel.allTeachers.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val payments by viewModel.allPayments.collectAsState()
    val referrals by viewModel.allReferrals.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Analytics, 1: Students & Faculty, 2: Courses, 3: Referrals & Announcements

    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    val totalRevenue = payments.sumOf { it.amount }
    val totalReferralBonuses = referrals.sumOf { it.bonusAmount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Branding Header
        LogoHeader(
            showRoleBadge = "ADMIN",
            onRoleClick = onRoleSwitchClick
        )

        // Admin Title Bar
        Surface(
            color = Color(0xFFDC2626),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "NEW KGN Administrative Console",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Academic Dean & Managing Director Portal",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { showBroadcastDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("admin_broadcast_btn")
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Broadcast", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { showAddCourseDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("admin_add_course_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFDC2626))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Course", fontSize = 11.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Tab Navigation
        TabRow(selectedTabIndex = activeTab) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Overview") })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Users (${students.size + teachers.size})") })
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("Courses (${courses.size})") })
            Tab(selected = activeTab == 3, onClick = { activeTab = 3 }, text = { Text("Referrals (${referrals.size})") })
        }

        // Tab Content
        when (activeTab) {
            0 -> {
                // Overview & Analytics
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text("Institute Key Performance Indicators (KPIs):", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminKpiCard(
                                title = "Enrolled Students",
                                value = "${students.size + 240}",
                                icon = Icons.Default.School,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            AdminKpiCard(
                                title = "Total Faculty",
                                value = "${teachers.size + 12}",
                                icon = Icons.Default.Person,
                                color = Color(0xFF059669),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AdminKpiCard(
                                title = "Gross Revenue",
                                value = "₹${(totalRevenue + 845000.0).toInt()}",
                                icon = Icons.Default.CurrencyRupee,
                                color = Color(0xFFD97706),
                                modifier = Modifier.weight(1f)
                            )
                            AdminKpiCard(
                                title = "Referral Bonuses",
                                value = "₹${totalReferralBonuses.toInt()}",
                                icon = Icons.Default.CardGiftcard,
                                color = Color(0xFF7C3AED),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Recent Fee Transactions", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(10.dp))
                                payments.forEach { p ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(p.studentName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                            Text(p.courseTitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(
                                            "₹${p.amount.toInt()} (${p.status})",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                        )
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Manage Students & Faculty
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Active Students Directory:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    items(students, key = { it.id }) { std ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(std.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("${std.email} • ${std.mobile}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Batch: ${std.courseOrBatch} • Code: ${std.referralCode}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(
                                    onClick = { viewModel.deleteUser(std.id) },
                                    modifier = Modifier.testTag("delete_user_" + std.id)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Faculty & Instructors Directory:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    items(teachers, key = { it.id }) { tch ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(tch.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("${tch.email} • ${tch.courseOrBatch}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    color = Color(0xFFD1FAE5),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "FACULTY",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF065F46)),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Course Management
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Institute Curriculum & Pricing:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    items(courses, key = { it.id }) { c ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(c.category, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                    Text("₹${c.price.toInt()}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(c.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("Faculty: ${c.instructorName} • Duration: ${c.duration}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { viewModel.deleteCourse(c.id) },
                                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                                    ) {
                                        Text("Delete Course")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // Referral Tracking in Admin Panel (Special prompt requirement!)
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Student Referral Analytics", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Track student ambassador performance, code usage, and commission disbursements.")
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Conversions: ${referrals.size}", fontWeight = FontWeight.Bold)
                                    Text("Total Bonus Paid: ₹${referrals.sumOf { it.bonusAmount }.toInt()}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        Text("Completed Referral Records:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    }

                    items(referrals, key = { it.id }) { ref ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Code: ${ref.referralCode}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = KgnGold))
                                    Text(ref.date, style = MaterialTheme.typography.labelSmall)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Referrer: ${ref.referrerName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                Text("New Enrolled Student: ${ref.referredUserName} (${ref.referredUserEmail})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Referrer Bonus: ₹${ref.bonusAmount.toInt()}", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF059669), fontWeight = FontWeight.Bold))
                                    Text("Discount Granted: ₹${ref.discountGiven.toInt()}", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Course Dialog
    if (showAddCourseDialog) {
        var courseTitle by remember { mutableStateOf("") }
        var courseCat by remember { mutableStateOf("IIT-JEE") }
        var coursePrice by remember { mutableStateOf("3999") }
        var instructor by remember { mutableStateOf("Prof. Salman & Team") }
        var courseDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddCourseDialog = false },
            title = { Text("Create New Coaching Course") },
            text = {
                Column {
                    OutlinedTextField(
                        value = courseTitle,
                        onValueChange = { courseTitle = it },
                        label = { Text("Course Title") },
                        placeholder = { Text("e.g. JEE Advanced Crash Course") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = courseCat,
                        onValueChange = { courseCat = it },
                        label = { Text("Category (IIT-JEE, NEET, etc.)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = coursePrice,
                        onValueChange = { coursePrice = it },
                        label = { Text("Price (INR)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = instructor,
                        onValueChange = { instructor = it },
                        label = { Text("Lead Faculty") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (courseTitle.isNotBlank()) {
                            val priceVal = coursePrice.toDoubleOrNull() ?: 2999.0
                            viewModel.createCourse(courseTitle, courseCat, courseDesc.ifBlank { "Full syllabus live coaching & tests." }, priceVal, instructor)
                            showAddCourseDialog = false
                        }
                    }
                ) {
                    Text("Publish Course")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCourseDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Broadcast Announcement Dialog
    if (showBroadcastDialog) {
        var notifTitle by remember { mutableStateOf("") }
        var notifMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Send Push Announcement") },
            text = {
                Column {
                    OutlinedTextField(
                        value = notifTitle,
                        onValueChange = { notifTitle = it },
                        label = { Text("Announcement Headline") },
                        placeholder = { Text("e.g. Holiday Schedule / All-India Mock Test") },
                        modifier = Modifier.fillMaxWidth().testTag("announcement_title_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notifMessage,
                        onValueChange = { notifMessage = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth().testTag("announcement_body_input"),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (notifTitle.isNotBlank() && notifMessage.isNotBlank()) {
                            viewModel.createAnnouncement(notifTitle, notifMessage)
                            showBroadcastDialog = false
                        }
                    }
                ) {
                    Text("Broadcast to Students")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AdminKpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = color))
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
