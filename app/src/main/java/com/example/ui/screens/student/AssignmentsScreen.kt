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
import com.example.data.model.AssignmentEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.KgnGold
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun AssignmentsScreen(
    viewModel: InstituteViewModel
) {
    val assignments by viewModel.allAssignments.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var activeSubmissionAssignment by remember { mutableStateOf<AssignmentEntity?>(null) }
    var submissionText by remember { mutableStateOf("") }

    val filteredList = remember(assignments, selectedFilter) {
        when (selectedFilter) {
            "PENDING" -> assignments.filter { it.status == "PENDING" }
            "SUBMITTED" -> assignments.filter { it.status == "SUBMITTED" }
            "EVALUATED" -> assignments.filter { it.status == "EVALUATED" }
            else -> assignments
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = when (selectedFilter) {
                "PENDING" -> 1
                "SUBMITTED" -> 2
                "EVALUATED" -> 3
                else -> 0
            }
        ) {
            val tabs = listOf("ALL" to "All", "PENDING" to "Pending", "SUBMITTED" to "Submitted", "EVALUATED" to "Graded")
            tabs.forEachIndexed { _, (key, label) ->
                Tab(
                    selected = selectedFilter == key,
                    onClick = { selectedFilter = key },
                    text = { Text(label, fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (filteredList.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.TaskAlt,
                title = "No Assignments Found",
                description = "All caught up! There are no assignments matching this filter."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredList, key = { it.id }) { assignment ->
                    AssignmentCard(
                        assignment = assignment,
                        onSubmitClick = {
                            activeSubmissionAssignment = assignment
                            submissionText = ""
                        }
                    )
                }
            }
        }
    }

    if (activeSubmissionAssignment != null) {
        val target = activeSubmissionAssignment!!
        AlertDialog(
            onDismissRequest = { activeSubmissionAssignment = null },
            title = { Text("Submit Solution") },
            text = {
                Column {
                    Text(target.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Enter your solution steps or attach file name below:", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = submissionText,
                        onValueChange = { submissionText = it },
                        placeholder = { Text("e.g. Completed step 1 to 15 in homework notebook. Attached PDF: Rotational_DPP_Solution.pdf") },
                        modifier = Modifier.fillMaxWidth().testTag("assignment_submit_input"),
                        minLines = 3,
                        maxLines = 5
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PDF/Image file attached: solution_scan.pdf", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val textToSubmit = if (submissionText.isNotBlank()) submissionText else "Solution file solution_scan.pdf uploaded successfully."
                        viewModel.submitAssignmentSolution(target.id, textToSubmit)
                        activeSubmissionAssignment = null
                    }
                ) {
                    Text("Confirm Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeSubmissionAssignment = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AssignmentCard(
    assignment: AssignmentEntity,
    onSubmitClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("assignment_card_" + assignment.id)
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
                        text = assignment.subject,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = when (assignment.status) {
                        "PENDING" -> Color(0xFFFEF3C7)
                        "SUBMITTED" -> Color(0xFFDBEAFE)
                        else -> Color(0xFFD1FAE5)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (assignment.status) {
                            "PENDING" -> "PENDING"
                            "SUBMITTED" -> "SUBMITTED"
                            else -> "GRADED: ${assignment.marksAwarded}/${assignment.maxMarks}"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (assignment.status) {
                                "PENDING" -> Color(0xFF78350F)
                                "SUBMITTED" -> Color(0xFF1E3A8A)
                                else -> Color(0xFF065F46)
                            }
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = assignment.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Due: ${assignment.dueDate}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                }

                Text(
                    text = "Max Marks: ${assignment.maxMarks}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (assignment.status == "EVALUATED" && assignment.teacherFeedback != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFD1FAE5).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Teacher Feedback:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                        )
                        Text(
                            text = assignment.teacherFeedback,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF065F46)
                        )
                    }
                }
            }

            if (assignment.status == "PENDING") {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onSubmitClick,
                    modifier = Modifier.fillMaxWidth().testTag("submit_asn_btn_" + assignment.id),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Submit Solution")
                }
            }
        }
    }
}
