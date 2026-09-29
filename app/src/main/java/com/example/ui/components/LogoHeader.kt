package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.KgnGold
import com.example.ui.theme.KgnNavyPrimary

@Composable
fun LogoHeader(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showRoleBadge: String? = null,
    onRoleClick: (() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (compact) 8.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Institute Logo Asset
                Box(
                    modifier = Modifier
                        .size(if (compact) 40.dp else 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(KgnNavyPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kgn_logo),
                        contentDescription = "NEW KGN INSTITUTE Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "NEW KGN",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "INSTITUTE",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = KgnGold
                            )
                        )
                    }
                    Text(
                        text = "Knowledge • Growth • Nurture",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            if (showRoleBadge != null) {
                Surface(
                    color = when (showRoleBadge) {
                        "ADMIN" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                        "TEACHER" -> Color(0xFF10B981).copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .testTag("role_badge")
                        .clickable(enabled = onRoleClick != null) { onRoleClick?.invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (showRoleBadge) {
                                "ADMIN" -> Icons.Default.AdminPanelSettings
                                "TEACHER" -> Icons.Default.School
                                else -> Icons.Default.Person
                            },
                            contentDescription = null,
                            tint = when (showRoleBadge) {
                                "ADMIN" -> Color(0xFFDC2626)
                                "TEACHER" -> Color(0xFF059669)
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = showRoleBadge,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (showRoleBadge) {
                                    "ADMIN" -> Color(0xFFDC2626)
                                    "TEACHER" -> Color(0xFF059669)
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}
