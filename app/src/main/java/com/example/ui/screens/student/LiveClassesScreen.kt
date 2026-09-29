package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.LiveClassEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.navigation.Screen
import com.example.ui.theme.KgnGold
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun LiveClassesScreen(
    viewModel: InstituteViewModel,
    onNavigate: (Screen) -> Unit
) {
    val liveClasses by viewModel.liveClasses.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredList = remember(liveClasses, selectedFilter) {
        when (selectedFilter) {
            "LIVE_NOW" -> liveClasses.filter { it.status == "LIVE_NOW" }
            "UPCOMING" -> liveClasses.filter { it.status == "UPCOMING" }
            "COMPLETED" -> liveClasses.filter { it.status == "COMPLETED" }
            else -> liveClasses
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Filters
        ScrollableTabRow(
            selectedTabIndex = when (selectedFilter) {
                "LIVE_NOW" -> 1
                "UPCOMING" -> 2
                "COMPLETED" -> 3
                else -> 0
            },
            edgePadding = 16.dp
        ) {
            val tabs = listOf("ALL" to "All Classes", "LIVE_NOW" to "🔴 Live Now", "UPCOMING" to "Upcoming", "COMPLETED" to "Past / Recordings")
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
                icon = Icons.Default.VideocamOff,
                title = "No Live Classes Found",
                description = "There are no scheduled live sessions under this filter right now."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredList, key = { it.id }) { liveClass ->
                    LiveClassCard(
                        liveClass = liveClass,
                        onJoinClick = { onNavigate(Screen.LiveRoom(liveClass)) },
                        onToggleReminder = { viewModel.toggleLiveReminder(liveClass) }
                    )
                }
            }
        }
    }
}

@Composable
fun LiveClassCard(
    liveClass: LiveClassEntity,
    onJoinClick: () -> Unit,
    onToggleReminder: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_card_" + liveClass.id)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (liveClass.status) {
                        "LIVE_NOW" -> Color(0xFFDC2626)
                        "UPCOMING" -> Color(0xFF2563EB)
                        else -> Color(0xFF6B7280)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (liveClass.status) {
                            "LIVE_NOW" -> "● LIVE NOW"
                            "UPCOMING" -> "UPCOMING"
                            else -> "COMPLETED"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = liveClass.subject,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = liveClass.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = KgnGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Instructor: ${liveClass.teacherName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${liveClass.date} • ${liveClass.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (liveClass.status == "UPCOMING") {
                    TextButton(onClick = onToggleReminder) {
                        Icon(
                            imageVector = if (liveClass.isReminderSet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = if (liveClass.isReminderSet) KgnGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (liveClass.isReminderSet) "Reminder Set" else "Remind Me")
                    }
                } else if (liveClass.status == "LIVE_NOW") {
                    Text(
                        text = "${liveClass.participantCount} students attending",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                    )
                } else {
                    Text(
                        text = "Recording available in Lectures",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Button(
                    onClick = onJoinClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (liveClass.status == "LIVE_NOW") Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (liveClass.status == "COMPLETED") Icons.Default.PlayCircle else Icons.Default.Videocam,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (liveClass.status) {
                            "LIVE_NOW" -> "Join Live"
                            "UPCOMING" -> "Class Room"
                            else -> "Watch Recap"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
