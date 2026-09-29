package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.model.LiveClassEntity
import com.example.ui.components.LiveStreamPlayer
import com.example.ui.viewmodel.InstituteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveClassRoomScreen(
    liveClass: LiveClassEntity,
    viewModel: InstituteViewModel,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.liveChatMessages.collectAsState()
    val isHandRaised by viewModel.isHandRaised.collectAsState()
    val isMicMuted by viewModel.isMicMuted.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(liveClass.subject, style = MaterialTheme.typography.titleMedium)
                    Text("Faculty: ${liveClass.teacherName}", style = MaterialTheme.typography.labelSmall)
                }
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Info, contentDescription = "Info")
                }
            }
        )

        LiveStreamPlayer(
            liveClass = liveClass,
            messages = messages,
            onSendMessage = { text, isDoubt ->
                viewModel.sendLiveMessage(text, isDoubt)
            },
            isHandRaised = isHandRaised,
            onToggleRaiseHand = { viewModel.toggleRaiseHand() },
            isMicMuted = isMicMuted,
            onToggleMic = { viewModel.isMicMuted.value = !viewModel.isMicMuted.value }
        )
    }
}
