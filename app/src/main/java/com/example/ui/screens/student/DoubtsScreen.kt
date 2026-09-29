package com.example.ui.screens.student

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
import com.example.data.model.DoubtEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.KgnGold
import com.example.ui.theme.KgnNavyPrimary
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun DoubtsScreen(
    viewModel: InstituteViewModel
) {
    val doubts by viewModel.allDoubts.collectAsState()
    var showAskDoubtDialog by remember { mutableStateOf(false) }
    var doubtSubject by remember { mutableStateOf("Physics") }
    var doubtTopic by remember { mutableStateOf("") }
    var doubtQuestion by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAskDoubtDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("ask_doubt_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ask A Doubt")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveHelp,
                        contentDescription = null,
                        tint = KgnGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "24/7 Academic Doubt Solving",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "KGN expert faculties review and resolve questions within hours",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (doubts.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.HelpCenter,
                    title = "No Doubts Asked Yet",
                    description = "Have a question regarding homework or lectures? Tap 'Ask A Doubt' below.",
                    actionButtonText = "Ask First Question",
                    onActionClick = { showAskDoubtDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(doubts, key = { it.id }) { doubt ->
                        DoubtCard(doubt = doubt)
                    }
                }
            }
        }
    }

    if (showAskDoubtDialog) {
        val subjects = listOf("Physics", "Chemistry", "Mathematics", "Biology")
        AlertDialog(
            onDismissRequest = { showAskDoubtDialog = false },
            title = { Text("Ask Your Faculty A Doubt") },
            text = {
                Column {
                    Text("Select Subject:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        subjects.forEach { s ->
                            FilterChip(
                                selected = doubtSubject == s,
                                onClick = { doubtSubject = s },
                                label = { Text(s, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = doubtTopic,
                        onValueChange = { doubtTopic = it },
                        label = { Text("Topic / Chapter Name") },
                        placeholder = { Text("e.g. Rotational Kinematics") },
                        modifier = Modifier.fillMaxWidth().testTag("doubt_topic_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = doubtQuestion,
                        onValueChange = { doubtQuestion = it },
                        label = { Text("Describe Your Question *") },
                        placeholder = { Text("Specify problem number or concept you are stuck on...") },
                        modifier = Modifier.fillMaxWidth().testTag("doubt_question_input"),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (doubtQuestion.isNotBlank()) {
                            viewModel.askDoubt(doubtSubject, doubtTopic.ifBlank { "General Query" }, doubtQuestion)
                            doubtTopic = ""
                            doubtQuestion = ""
                            showAskDoubtDialog = false
                        }
                    }
                ) {
                    Text("Submit Doubt")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAskDoubtDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DoubtCard(doubt: DoubtEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("doubt_card_" + doubt.id)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${doubt.subject} • ${doubt.topic}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = when (doubt.status) {
                        "RESOLVED" -> Color(0xFFD1FAE5)
                        "ANSWERED" -> Color(0xFFDBEAFE)
                        else -> Color(0xFFFEF3C7)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (doubt.status) {
                            "RESOLVED" -> "RESOLVED"
                            "ANSWERED" -> "FACULTY REPLIED"
                            else -> "UNDER REVIEW"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (doubt.status) {
                                "RESOLVED" -> Color(0xFF065F46)
                                "ANSWERED" -> Color(0xFF1E3A8A)
                                else -> Color(0xFF78350F)
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Q: ${doubt.questionText}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Asked by ${doubt.studentName} • ${doubt.createdAt}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (doubt.teacherReply != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = KgnNavyPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Faculty Explanation & Solution:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = KgnNavyPrimary)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = doubt.teacherReply,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (doubt.repliedAt != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Replied: ${doubt.repliedAt}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
