package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.OutputFormat
import com.example.ui.components.AppHeader
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PrimaryGradient
import com.example.util.AdMobConfig
import com.example.util.FileHelper
import com.example.util.I18n
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@Composable
fun ConvertFormatScreen(
    onBack: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var targetFormat by remember { mutableStateOf(OutputFormat.JPEG) }
    var qualitySlider by remember { mutableFloatStateOf(90f) }

    var isConverting by remember { mutableStateOf(false) }
    var convertedFile by remember { mutableStateOf<File?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            convertedFile = null
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                title = I18n.t("tool_convert"),
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
            item {
                if (selectedUri == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                            .testTag("convert_pick_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSuccess.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Transform, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = I18n.t("btn_choose_photo"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "JPG, PNG, WEBP में कन्वर्ट करने के लिए फोटो चुनें",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = selectedUri,
                                    contentDescription = "Source Image",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Text(
                                text = "फोटो चुनी गई है",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Change")
                            }
                        }
                    }
                }
            }

            // Target Format Options
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = I18n.t("output_format"),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutputFormat.values().forEach { fmt ->
                                val isSelected = targetFormat == fmt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { targetFormat = fmt },
                                    label = { Text(fmt.name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IndigoPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        if (targetFormat != OutputFormat.PNG) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "क्वालिटी (Quality): ${qualitySlider.toInt()}%", style = MaterialTheme.typography.bodyMedium)
                            }
                            Slider(
                                value = qualitySlider,
                                onValueChange = { qualitySlider = it },
                                valueRange = 30f..100f,
                                colors = SliderDefaults.colors(thumbColor = IndigoPrimary, activeTrackColor = IndigoPrimary)
                            )
                        }
                    }
                }
            }

            // Convert Button
            item {
                if (isConverting) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = IndigoPrimary)
                    }
                } else {
                    Button(
                        onClick = {
                            if (selectedUri == null) {
                                Toast.makeText(context, I18n.t("btn_choose_photo"), Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isConverting = true
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    val stream = context.contentResolver.openInputStream(selectedUri!!)
                                    val bm = BitmapFactory.decodeStream(stream)
                                    val file = File(context.cacheDir, "converted_${System.currentTimeMillis()}.${targetFormat.extension}")
                                    val compressFmt = when (targetFormat) {
                                        OutputFormat.JPEG -> Bitmap.CompressFormat.JPEG
                                        OutputFormat.PNG -> Bitmap.CompressFormat.PNG
                                        OutputFormat.WEBP -> {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                                Bitmap.CompressFormat.WEBP_LOSSY
                                            } else {
                                                @Suppress("DEPRECATION")
                                                Bitmap.CompressFormat.WEBP
                                            }
                                        }
                                    }
                                    FileOutputStream(file).use { fos ->
                                        bm.compress(compressFmt, qualitySlider.roundToInt(), fos)
                                    }
                                    withContext(Dispatchers.Main) {
                                        convertedFile = file
                                        Toast.makeText(context, "कन्वर्ट हो गया!", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    withContext(Dispatchers.Main) { isConverting = false }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("convert_format_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Text(
                            text = "${targetFormat.name} में बदलें",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }
            }

            // Result
            if (convertedFile != null) {
                item {
                    val file = convertedFile!!
                    val sizeKb = file.length() / 1024L

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSuccess.copy(alpha = 0.08f)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = PrimaryGradient)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "कन्वर्टेड फाइल तैयार है (${sizeKb} KB)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val uri = FileHelper.saveToGallery(context, file, file.name, targetFormat.mimeType)
                                        if (uri != null) {
                                            Toast.makeText(context, I18n.t("saved_to_gallery"), Toast.LENGTH_SHORT).show()
                                            AdMobConfig.onUserSaveOrShareAction()
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                                ) {
                                    Text(I18n.t("btn_save_gallery"), fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                OutlinedButton(
                                    onClick = {
                                        FileHelper.shareFile(context, file, targetFormat.mimeType)
                                        AdMobConfig.onUserSaveOrShareAction()
                                    },
                                    modifier = Modifier.weight(1f).height(46.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(I18n.t("btn_share"), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
