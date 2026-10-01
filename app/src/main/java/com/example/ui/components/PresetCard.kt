package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExamPreset
import com.example.model.PresetCategory
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.VioletAccent
import com.example.util.I18n

@Composable
fun PresetCard(
    preset: ExamPreset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (preset.category) {
        PresetCategory.PHOTO -> Icons.Default.AccountBox
        PresetCategory.SIGNATURE -> Icons.Default.Draw
        PresetCategory.DOCUMENT -> Icons.Default.Description
        PresetCategory.CUSTOM -> Icons.Default.Tune
    }

    val categoryColor = when (preset.category) {
        PresetCategory.PHOTO -> IndigoPrimary
        PresetCategory.SIGNATURE -> VioletAccent
        PresetCategory.DOCUMENT -> EmeraldSuccess
        PresetCategory.CUSTOM -> Color(0xFFF59E0B)
    }

    Card(
        modifier = modifier
            .width(170.dp)
            .height(145.dp)
            .clickable(onClick = onClick)
            .testTag("preset_card_${preset.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = preset.nameKey,
                        tint = categoryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(categoryColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${preset.defaultTargetKb} KB",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = I18n.t(preset.nameKey),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = preset.displayRule,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}
