package com.example.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssignmentEntity
import com.example.data.model.DoubtEntity
import com.example.ui.components.LogoHeader
import com.example.ui.theme.KgnGold
import com.example.ui.theme.KgnNavyPrimary
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun TeacherDashboardScreen(
    viewModel: InstituteViewModel,
    onRoleSwitchClick: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val liveClasses by viewModel.liveClasses.collectAsState()
    val doubts by viewModel.allDoubts.collectAsState()
    val assignments by viewModel.allAssignments.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Overview & Live, 1: Doubts, 2: Grade Assignments
    var showScheduleLiveDialog by remember { mutableStateOf(false) }
    var showUploadNotesDialog by remember { mutableStateOf(false) }
    var activeDoubtToReply by remember { mutableStateOf<DoubtEntity?>(null) }
    var activeAssignmentToGrade by remember { mutableStateOf<AssignmentEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Teacher Branding Header
        LogoHeader(
            showRoleBadge = "TEACHER",
            onRoleClick = onRoleSwitchClick
        )

        // Faculty Profile Summary Banner
        Surface(
            color = Color(0xFF065F46),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Faculty Portal: ${user?.name ?: "Prof. Salman"}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = user?.courseOrBatch ?: "Physics & Mathematics HOD",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(
                        onClick = { showScheduleLiveDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("teacher_schedule_live_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Schedule Live", fontSize = 11.sp, color = Color(0xFF065F46), fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = { showUploadNotesDialog = true },
                        modifier = Modifier.testTag("teacher_upload_notes_btn")
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = "Upload Notes", tint = Color.White)
                    }
                }
            }
        }

        // Tabs
        TabRow(selectedTabIndex = activeTab) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Live Classes (${liveClasses.size})") })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Doubts (${doubts.size})") })
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("Assignments (${assignments.size})") })
        }

        // Content
        if (activeTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Managed Live Classes & Upcoming Batches:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }
                items(liveClasses, key = { it.id }) { live ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Surface(
                                    color = if (live.status == "LIVE_NOW") Color(0xFFDC2626) else MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = live.status,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (live.status == "LIVE_NOW") Color.White else MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text("${live.date} • ${live.time}", style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(live.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text("Subject: ${live.subject} • Faculty: ${live.teacherName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        } else if (activeTab == 1) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Student Queries & Doubts Queue:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }
                items(doubts, key = { it.id }) { doubt ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${doubt.subject} • ${doubt.topic}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                Text(doubt.status, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (doubt.status == "RESOLVED") Color(0xFF059669) else KgnGold))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Student (${doubt.studentName}): ${doubt.questionText}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                            if (doubt.teacherReply != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Your Answer: ${doubt.teacherReply}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF065F46))
                            } else {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { activeDoubtToReply = doubt },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("reply_doubt_btn_" + doubt.id)
                                ) {
                                    Icon(Icons.Default.Reply, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Reply & Resolve Doubt")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Student Submissions & Evaluation Queue:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }
                items(assignments, key = { it.id }) { asn ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(asn.subject, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                Text("Status: ${asn.status}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(asn.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            if (asn.studentSubmissionText != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Submission: ${asn.studentSubmissionText}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (asn.status == "EVALUATED") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Graded: ${asn.marksAwarded}/${asn.maxMarks} • Feedback: ${asn.teacherFeedback}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF059669), fontWeight = FontWeight.Bold))
                            } else {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { activeAssignmentToGrade = asn },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("grade_asn_btn_" + asn.id)
                                ) {
                                    Icon(Icons.Default.RateReview, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Grade & Award Marks")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Schedule Live Class Dialog
    if (showScheduleLiveDialog) {
        var liveTitle by remember { mutableStateOf("") }
        var liveSubject by remember { mutableStateOf("Physics") }
        var liveDate by remember { mutableStateOf("Tomorrow") }
        var liveTime by remember { mutableStateOf("05:00 PM - 06:30 PM") }

        AlertDialog(
            onDismissRequest = { showScheduleLiveDialog = false },
            title = { Text("Schedule New Live Session") },
            text = {
                Column {
                    OutlinedTextField(
                        value = liveTitle,
                        onValueChange = { liveTitle = it },
                        label = { Text("Session Title") },
                        placeholder = { Text("e.g. Modern Physics: Photoelectric Effect") },
                        modifier = Modifier.fillMaxWidth().testTag("live_title_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = liveSubject,
                        onValueChange = { liveSubject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = liveTime,
                        onValueChange = { liveTime = it },
                        label = { Text("Time Slot") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (liveTitle.isNotBlank()) {
                            viewModel.createLiveClass(liveTitle, liveSubject, "c_jee", liveDate, liveTime)
                            showScheduleLiveDialog = false
                        }
                    }
                ) {
                    Text("Schedule Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScheduleLiveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Upload Notes Dialog
    if (showUploadNotesDialog) {
        var noteTitle by remember { mutableStateOf("") }
        var noteSubject by remember { mutableStateOf("Physics") }
        var noteChapter by remember { mutableStateOf("Modern Physics") }
        var noteContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showUploadNotesDialog = false },
            title = { Text("Upload Study Material / PDF") },
            text = {
                Column {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Document Title") },
                        placeholder = { Text("e.g. Wave Optics Formula Cheat Sheet") },
                        modifier = Modifier.fillMaxWidth().testTag("note_title_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteSubject,
                        onValueChange = { noteSubject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteChapter,
                        onValueChange = { noteChapter = it },
                        label = { Text("Chapter") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Content Notes / Highlights") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank()) {
                            viewModel.uploadStudyMaterial(noteTitle, noteSubject, noteChapter, "PDF", noteContent.ifBlank { "Comprehensive faculty notes & solved illustrations." })
                            showUploadNotesDialog = false
                        }
                    }
                ) {
                    Text("Upload PDF Material")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadNotesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reply Doubt Dialog
    if (activeDoubtToReply != null) {
        val targetDoubt = activeDoubtToReply!!
        var replyText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { activeDoubtToReply = null },
            title = { Text("Answer Student Doubt") },
            text = {
                Column {
                    Text("Student: ${targetDoubt.studentName}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Question: ${targetDoubt.questionText}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text("Faculty Answer & Derivation Steps") },
                        placeholder = { Text("Type step-by-step conceptual answer...") },
                        modifier = Modifier.fillMaxWidth().testTag("doubt_reply_input"),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            viewModel.answerDoubt(targetDoubt.id, replyText)
                            activeDoubtToReply = null
                        }
                    }
                ) {
                    Text("Send Answer")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeDoubtToReply = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Grade Assignment Dialog
    if (activeAssignmentToGrade != null) {
        val targetAsn = activeAssignmentToGrade!!
        var marksText by remember { mutableStateOf("28") }
        var feedbackText by remember { mutableStateOf("Good attempt. Keep working on numerical precision.") }

        AlertDialog(
            onDismissRequest = { activeAssignmentToGrade = null },
            title = { Text("Grade Student Submission") },
            text = {
                Column {
                    Text(targetAsn.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("Max Marks: ${targetAsn.maxMarks}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = marksText,
                        onValueChange = { marksText = it },
                        label = { Text("Marks Awarded (out of ${targetAsn.maxMarks})") },
                        modifier = Modifier.fillMaxWidth().testTag("grade_marks_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        label = { Text("Faculty Feedback") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val marks = marksText.toIntOrNull() ?: targetAsn.maxMarks
                        viewModel.gradeAssignment(targetAsn.id, marks, feedbackText)
                        activeAssignmentToGrade = null
                    }
                ) {
                    Text("Submit Grade")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeAssignmentToGrade = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
