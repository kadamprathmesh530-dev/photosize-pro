package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExamPreset
import com.example.model.Presets
import com.example.ui.components.AppHeader
import com.example.ui.components.BannerAdCard
import com.example.ui.components.PresetCard
import com.example.ui.components.TrustBadgesRow
import com.example.ui.theme.*
import com.example.util.I18n

data class QuickToolItem(
    val titleKey: String,
    val descKey: String,
    val icon: ImageVector,
    val iconColor: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCompress: (Uri?, ExamPreset?) -> Unit,
    onNavigateToSignature: () -> Unit,
    onNavigateToCrop: () -> Unit,
    onNavigateToPdf: () -> Unit,
    onNavigateToConvert: () -> Unit,
    onNavigateToBatch: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var showSourceSheet by remember { mutableStateOf(false) }
    var selectedPresetForUpload by remember { mutableStateOf<ExamPreset?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onNavigateToCompress(uri, selectedPresetForUpload)
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
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
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("home_hero_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PrimaryGradient)
                            .padding(22.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "100% OFFLINE & ACCURATE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Text(
                                text = I18n.t("hero_title"),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 21.sp,
                                    lineHeight = 28.sp
                                )
                            )

                            Text(
                                text = I18n.t("hero_subtitle"),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White.copy(alpha = 0.88f),
                                    fontSize = 13.sp
                                )
                            )

                            Button(
                                onClick = {
                                    selectedPresetForUpload = null
                                    showSourceSheet = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("hero_choose_photo_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = IndigoPrimary
                                )
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = I18n.t("btn_choose_photo"),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = IndigoPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Trust Badges
            item {
                TrustBadgesRow()
            }

            // Popular Exam Presets
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = I18n.t("popular_presets"),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = I18n.t("popular_presets_sub"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(Presets.allPresets) { preset ->
                            PresetCard(
                                preset = preset,
                                onClick = {
                                    selectedPresetForUpload = preset
                                    showSourceSheet = true
                                }
                            )
                        }
                    }
                }
            }

            // Quick Tools Section
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = I18n.t("quick_tools"),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    val tools = listOf(
                        QuickToolItem(
                            titleKey = "tool_compress",
                            descKey = "tool_compress_desc",
                            icon = Icons.Default.Compress,
                            iconColor = IndigoPrimary,
                            onClick = {
                                selectedPresetForUpload = null
                                showSourceSheet = true
                            }
                        ),
                        QuickToolItem(
                            titleKey = "tool_signature",
                            descKey = "tool_signature_desc",
                            icon = Icons.Default.Draw,
                            iconColor = VioletAccent,
                            onClick = onNavigateToSignature
                        ),
                        QuickToolItem(
                            titleKey = "tool_crop",
                            descKey = "tool_crop_desc",
                            icon = Icons.Default.Crop,
                            iconColor = Color(0xFF0284C7),
                            onClick = onNavigateToCrop
                        ),
                        QuickToolItem(
                            titleKey = "tool_pdf",
                            descKey = "tool_pdf_desc",
                            icon = Icons.Default.PictureAsPdf,
                            iconColor = RoseError,
                            onClick = onNavigateToPdf
                        ),
                        QuickToolItem(
                            titleKey = "tool_convert",
                            descKey = "tool_convert_desc",
                            icon = Icons.Default.Transform,
                            iconColor = EmeraldSuccess,
                            onClick = onNavigateToConvert
                        ),
                        QuickToolItem(
                            titleKey = "tool_batch",
                            descKey = "tool_batch_desc",
                            icon = Icons.Default.BurstMode,
                            iconColor = Color(0xFFD97706),
                            onClick = onNavigateToBatch
                        )
                    )

                    // 2-Column Grid
                    for (i in tools.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                QuickToolCard(tool = tools[i])
                            }
                            if (i + 1 < tools.size) {
                                Box(modifier = Modifier.weight(1f)) {
                                    QuickToolCard(tool = tools[i + 1])
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // How It Works
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = I18n.t("how_it_works"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        HowItWorksStep(number = "1", title = I18n.t("step_1_title"), desc = I18n.t("step_1_desc"))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        HowItWorksStep(number = "2", title = I18n.t("step_2_title"), desc = I18n.t("step_2_desc"))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        HowItWorksStep(number = "3", title = I18n.t("step_3_title"), desc = I18n.t("step_3_desc"))
                    }
                }
            }

            // AdMob Banner Placeholder
            item {
                BannerAdCard()
            }
        }
    }

    // Modal Bottom Sheet for Image Picking
    if (showSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSourceSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (selectedPresetForUpload != null) {
                        "${I18n.t(selectedPresetForUpload!!.nameKey)} — ${I18n.t("btn_choose_photo")}"
                    } else {
                        I18n.t("btn_choose_photo")
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Pick from Gallery
                FilledTonalButton(
                    onClick = {
                        showSourceSheet = false
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null)
                        Text(
                            text = I18n.t("btn_gallery"),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                // If no file selected yet, navigate directly to editor to pick
                OutlinedButton(
                    onClick = {
                        showSourceSheet = false
                        onNavigateToCompress(null, selectedPresetForUpload)
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = I18n.t("editor_title"),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickToolCard(tool: QuickToolItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clickable(onClick = tool.onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(tool.iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.titleKey,
                    tint = tool.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = I18n.t(tool.titleKey),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = I18n.t(tool.descKey),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun HowItWorksStep(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(IndigoPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
