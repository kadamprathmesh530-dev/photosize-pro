package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppHeader
import com.example.ui.components.BannerAdCard
import com.example.ui.theme.*
import com.example.util.I18n

data class ToolHubItem(
    val titleKey: String,
    val descKey: String,
    val infoTag: String,
    val icon: ImageVector,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
fun ToolsScreen(
    onNavigateToCompress: () -> Unit,
    onNavigateToSignature: () -> Unit,
    onNavigateToCrop: () -> Unit,
    onNavigateToPdf: () -> Unit,
    onNavigateToConvert: () -> Unit,
    onNavigateToBatch: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val tools = listOf(
        ToolHubItem(
            titleKey = "tool_compress",
            descKey = "tool_compress_desc",
            infoTag = "SSC, UPSC, Bank",
            icon = Icons.Default.Compress,
            color = IndigoPrimary,
            onClick = onNavigateToCompress
        ),
        ToolHubItem(
            titleKey = "tool_signature",
            descKey = "tool_signature_desc",
            infoTag = "140x60 px • 10-20 KB",
            icon = Icons.Default.Draw,
            color = VioletAccent,
            onClick = onNavigateToSignature
        ),
        ToolHubItem(
            titleKey = "tool_crop",
            descKey = "tool_crop_desc",
            infoTag = "35x45 mm • 1:1 • 3:4",
            icon = Icons.Default.Crop,
            color = Color(0xFF0284C7),
            onClick = onNavigateToCrop
        ),
        ToolHubItem(
            titleKey = "tool_pdf",
            descKey = "tool_pdf_desc",
            infoTag = "Multi-page • A4",
            icon = Icons.Default.PictureAsPdf,
            color = RoseError,
            onClick = onNavigateToPdf
        ),
        ToolHubItem(
            titleKey = "tool_convert",
            descKey = "tool_convert_desc",
            infoTag = "JPG • PNG • WEBP",
            icon = Icons.Default.Transform,
            color = EmeraldSuccess,
            onClick = onNavigateToConvert
        ),
        ToolHubItem(
            titleKey = "tool_batch",
            descKey = "tool_batch_desc",
            infoTag = "Up to 10 Images",
            icon = Icons.Default.BurstMode,
            color = Color(0xFFD97706),
            onClick = onNavigateToBatch
        )
    )

    Scaffold(
        topBar = {
            AppHeader(
                title = I18n.t("nav_tools"),
                showBack = false,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(tools.size) { index ->
                val tool = tools[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = tool.onClick),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(tool.color.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = null,
                                tint = tool.color,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = I18n.t(tool.titleKey),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(tool.color.copy(alpha = 0.12f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tool.infoTag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = tool.color,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = I18n.t(tool.descKey),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            item {
                BannerAdCard()
            }
        }
    }
}
