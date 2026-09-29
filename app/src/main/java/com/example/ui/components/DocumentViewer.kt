package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudyMaterialEntity
import com.example.ui.theme.KgnGold
import com.example.ui.theme.KgnNavyPrimary

@Composable
fun DocumentViewer(
    material: StudyMaterialEntity,
    onBookmarkToggle: () -> Unit,
    onDownloadToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomLevel by remember { mutableStateOf(100) }
    var currentPage by remember { mutableStateOf(1) }
    val totalPages = 14

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Top Document Toolbar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        text = "Page $currentPage of $totalPages",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (zoomLevel > 75) zoomLevel -= 15 },
                        modifier = Modifier.testTag("zoom_out_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                    }
                    Text(
                        text = "$zoomLevel%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                    )
                    IconButton(
                        onClick = { if (zoomLevel < 150) zoomLevel += 15 },
                        modifier = Modifier.testTag("zoom_in_btn")
                    ) {
                        Icon(imageVector = Icons.Default.ZoomIn, contentDescription = "Zoom In")
                    }
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.testTag("bookmark_doc_btn")
                    ) {
                        Icon(
                            imageVector = if (material.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (material.isBookmarked) KgnGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDownloadToggle,
                        modifier = Modifier.testTag("download_doc_btn")
                    ) {
                        Icon(
                            imageVector = if (material.isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                            contentDescription = "Download",
                            tint = if (material.isDownloaded) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // PDF Page Sheet Viewer
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Header on PDF page
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NEW KGN INSTITUTE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = KgnNavyPrimary
                            )
                        )
                        Text(
                            text = material.subject.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = KgnGold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Chapter: ${material.chapterName}",
                        style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )

                    Text(
                        text = material.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = material.contentPreview,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Formatted formulas box
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Key Equations & Quick Recap:",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1. Moment of Inertia (Continuous): I = ∫ r² dm\n" +
                                        "2. Parallel Axis Theorem: I = I_cm + M · d²\n" +
                                        "3. Perpendicular Axis Theorem: I_z = I_x + I_y (planar bodies)\n" +
                                        "4. Pure Rolling condition: v_cm = R · ω  and  a_cm = R · α\n" +
                                        "5. Total Kinetic Energy: KE = ½ M v_cm² + ½ I_cm ω²",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Practice Example 01:\nA solid cylinder of mass M and radius R rolls down an inclined plane of inclination θ without slipping. Calculate its acceleration along the incline.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Solution:\nFor rolling without slipping: a = g·sin(θ) / (1 + I_cm/(M·R²)).\nSince I_cm for solid cylinder is ½ M R², denominator = 1 + 0.5 = 1.5 = 3/2.\nTherefore, a = (2/3) g sin(θ).",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Confidential • KGN Institute Study Material",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                        )
                        Text(
                            text = "- 1 -",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Bottom Page Navigator
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { if (currentPage > 1) currentPage-- },
                    enabled = currentPage > 1,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Previous Page")
                }

                Text(
                    text = "$currentPage / $totalPages",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                Button(
                    onClick = { if (currentPage < totalPages) currentPage++ },
                    enabled = currentPage < totalPages,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Next Page")
                }
            }
        }
    }
}
