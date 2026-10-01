package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CompressionResult
import com.example.model.OutputFormat
import com.example.ui.components.AppHeader
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PrimaryGradient
import com.example.util.AdMobConfig
import com.example.util.FileHelper
import com.example.util.I18n
import com.example.util.ImageCompressor
import kotlinx.coroutines.launch
import java.io.File

data class BatchItem(
    val uri: Uri,
    val result: CompressionResult? = null,
    val isProcessing: Boolean = false,
    val isDone: Boolean = false
)

@Composable
fun BatchCompressScreen(
    onBack: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var batchItems by remember { mutableStateOf<List<BatchItem>>(emptyList()) }
    var targetKb by remember { mutableIntStateOf(50) }
    var isBatchProcessing by remember { mutableStateOf(false) }

    val multiPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            batchItems = uris.map { BatchItem(uri = it) }
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                title = I18n.t("batch_title"),
                showBack = true,
                onBackClick = onBack,
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pick Images
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            multiPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                        .testTag("batch_pick_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BurstMode, contentDescription = null, tint = IndigoPrimary)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${batchItems.size} फोटो चुनी गई हैं",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = I18n.t("batch_select_hint"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = IndigoPrimary)
                    }
                }
            }

            // Target KB Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "सभी फोटो का टारगेट साइज: $targetKb KB", fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(20, 50, 100, 200).forEach { kb ->
                                val isSelected = targetKb == kb
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { targetKb = kb }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "$kb KB",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Items List
            itemsIndexed(batchItems) { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            AsyncImage(
                                model = item.uri,
                                contentDescription = "Item",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Photo #${index + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (item.result != null) {
                                Text(
                                    text = "Compressed: ${item.result.formattedCompressedSize} (-${item.result.percentSaved}%)",
                                    color = EmeraldSuccess,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else if (item.isProcessing) {
                                Text(text = "Compressing...", color = IndigoPrimary, fontSize = 12.sp)
                            } else {
                                Text(text = "Ready to compress", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        if (item.isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = IndigoPrimary)
                        } else if (item.isDone) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                        }
                    }
                }
            }

            // Action Buttons
            if (batchItems.isNotEmpty()) {
                item {
                    val allDone = batchItems.all { it.isDone }
                    if (!allDone) {
                        Button(
                            onClick = {
                                isBatchProcessing = true
                                coroutineScope.launch {
                                    val updated = batchItems.toMutableList()
                                    for (i in updated.indices) {
                                        updated[i] = updated[i].copy(isProcessing = true)
                                        batchItems = updated.toList()

                                        try {
                                            val meta = FileHelper.getImageMetadata(context, updated[i].uri)
                                            if (meta != null) {
                                                val res = ImageCompressor.compressImage(
                                                    context = context,
                                                    metadata = meta,
                                                    targetKb = targetKb,
                                                    outputFormat = OutputFormat.JPEG
                                                )
                                                updated[i] = updated[i].copy(
                                                    result = res,
                                                    isProcessing = false,
                                                    isDone = true
                                                )
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            updated[i] = updated[i].copy(isProcessing = false)
                                        }
                                        batchItems = updated.toList()
                                    }
                                    isBatchProcessing = false
                                    Toast.makeText(context, "सभी फोटो कंप्रेस हो गईं!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isBatchProcessing,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Text(
                                text = I18n.t("btn_compress_all"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val files = batchItems.mapNotNull { it.result?.outputFilePath?.let { p -> File(p) } }
                                    for (f in files) {
                                        FileHelper.saveToGallery(context, f, f.name, "image/jpeg")
                                    }
                                    Toast.makeText(context, "सभी फोटो गैलरी में सेव हो गईं!", Toast.LENGTH_SHORT).show()
                                    AdMobConfig.onUserSaveOrShareAction()
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                            ) {
                                Icon(Icons.Default.SaveAlt, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(I18n.t("btn_save_all"), fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            OutlinedButton(
                                onClick = {
                                    val files = batchItems.mapNotNull { it.result?.outputFilePath?.let { p -> File(p) } }
                                    FileHelper.shareMultipleFiles(context, files, "image/jpeg")
                                    AdMobConfig.onUserSaveOrShareAction()
                                },
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(I18n.t("btn_share"), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
