package com.example.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.StudyMaterialEntity
import com.example.ui.components.DocumentViewer
import com.example.ui.components.EmptyStateView
import com.example.ui.navigation.Screen
import com.example.ui.theme.KgnGold
import com.example.ui.viewmodel.InstituteViewModel

@Composable
fun NotesScreen(
    viewModel: InstituteViewModel,
    onNavigate: (Screen) -> Unit
) {
    val materials by viewModel.studyMaterials.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(materials, selectedFilter, searchQuery) {
        materials.filter { mat ->
            val matchFilter = when (selectedFilter) {
                "BOOKMARKED" -> mat.isBookmarked
                "DOWNLOADED" -> mat.isDownloaded
                "Physics" -> mat.subject.equals("Physics", ignoreCase = true)
                "Chemistry" -> mat.subject.equals("Chemistry", ignoreCase = true)
                "Mathematics" -> mat.subject.equals("Mathematics", ignoreCase = true)
                "Biology" -> mat.subject.equals("Biology", ignoreCase = true)
                else -> true
            }
            val matchSearch = searchQuery.isBlank() ||
                    mat.title.contains(searchQuery, ignoreCase = true) ||
                    mat.chapterName.contains(searchQuery, ignoreCase = true) ||
                    mat.subject.contains(searchQuery, ignoreCase = true)
            matchFilter && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search
        Box(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search formula sheets, notes, DPPs...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("notes_search_input"),
                singleLine = true
            )
        }

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chips = listOf("ALL" to "All Notes", "BOOKMARKED" to "★ Bookmarked", "DOWNLOADED" to "⬇ Downloaded", "Physics" to "Physics", "Chemistry" to "Chemistry", "Mathematics" to "Mathematics", "Biology" to "Biology")
            items(chips) { (key, label) ->
                FilterChip(
                    selected = selectedFilter == key,
                    onClick = { selectedFilter = key },
                    label = { Text(label) },
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredList.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.MenuBook,
                title = "No Study Material Found",
                description = "Try changing your filter or keyword search."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { material ->
                    StudyMaterialCard(
                        material = material,
                        onClick = { onNavigate(Screen.NoteViewer(material)) },
                        onBookmarkToggle = { viewModel.toggleBookmarkNote(material) },
                        onDownloadToggle = { viewModel.toggleDownloadNote(material) }
                    )
                }
            }
        }
    }
}

@Composable
fun StudyMaterialCard(
    material: StudyMaterialEntity,
    onClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onDownloadToggle: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("note_card_" + material.id)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${material.subject} • ${material.chapterName}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(32.dp).testTag("bookmark_btn_" + material.id)
                    ) {
                        Icon(
                            imageVector = if (material.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (material.isBookmarked) KgnGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDownloadToggle,
                        modifier = Modifier.size(32.dp).testTag("download_btn_" + material.id)
                    ) {
                        Icon(
                            imageVector = if (material.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                            contentDescription = "Download",
                            tint = if (material.isDownloaded) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = material.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = material.contentPreview,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${material.fileType} • ${material.fileSize} • ${material.uploadDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Tap to read PDF →",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteViewerScreen(
    material: StudyMaterialEntity,
    viewModel: InstituteViewModel,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(material.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        DocumentViewer(
            material = material,
            onBookmarkToggle = { viewModel.toggleBookmarkNote(material) },
            onDownloadToggle = { viewModel.toggleDownloadNote(material) }
        )
    }
}
