package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.AppDatabase
import com.example.data.CompressionHistoryEntity
import com.example.data.HistoryRepository
import com.example.ui.components.AppHeader
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError
import com.example.util.FileHelper
import com.example.util.I18n
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repo = remember { HistoryRepository(AppDatabase.getDatabase(context).historyDao()) }
    val historyList by repo.allHistory.collectAsStateWithLifecycle(initialValue = emptyList())

    var showClearConfirm by remember { mutableStateOf(false) }
    var previewEntity by remember { mutableStateOf<CompressionHistoryEntity?>(null) }

    Scaffold(
        topBar = {
            AppHeader(
                title = I18n.t("history_title"),
                showBack = false,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (historyList.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = I18n.t("history_empty"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = I18n.t("history_empty_sub"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${historyList.size} Saved Items",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    TextButton(onClick = { showClearConfirm = true }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = RoseError, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(I18n.t("btn_clear_history"), color = RoseError, fontSize = 12.sp)
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyList, key = { it.id }) { item ->
                        HistoryCardItem(
                            item = item,
                            onPreview = { previewEntity = item },
                            onShare = {
                                val file = File(item.filePath)
                                if (file.exists()) {
                                    FileHelper.shareFile(context, file, "image/jpeg")
                                } else {
                                    Toast.makeText(context, "File no longer in cache", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onSave = {
                                val file = File(item.filePath)
                                if (file.exists()) {
                                    FileHelper.saveToGallery(context, file, item.fileName, "image/jpeg")
                                    Toast.makeText(context, I18n.t("saved_to_gallery"), Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    repo.deleteById(item.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation dialog before clearing
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(I18n.t("btn_clear_history")) },
            text = { Text(I18n.t("confirm_clear_history")) },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch { repo.clearAll() }
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                ) {
                    Text(I18n.t("btn_confirm"))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearConfirm = false }) {
                    Text(I18n.t("btn_cancel"))
                }
            }
        )
    }

    // Preview Dialog
    if (previewEntity != null) {
        val entity = previewEntity!!
        val file = File(entity.filePath)

        Dialog(onDismissRequest = { previewEntity = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = entity.fileName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (file.exists()) {
                            AsyncImage(
                                model = file,
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("Image not found in cache", color = Color.White)
                        }
                    }

                    Text(
                        text = "Size: ${entity.compressedSizeBytes / 1024} KB • ${entity.width}x${entity.height} px",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = IndigoPrimary
                    )

                    Button(
                        onClick = { previewEntity = null },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCardItem(
    item: CompressionHistoryEntity,
    onPreview: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    val file = File(item.filePath)
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp))
    val compressedKb = item.compressedSizeBytes / 1024L
    val originalKb = item.originalSizeBytes / 1024L

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPreview)
            .testTag("history_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = item.fileName,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.BrokenImage,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.fileName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                    maxLines = 1
                )
                Text(
                    text = "${originalKb}KB  ➜  ${compressedKb}KB (${item.format})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "$dateStr • ${item.width}x${item.height}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onSave, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.SaveAlt, contentDescription = "Save", tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RoseError, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
