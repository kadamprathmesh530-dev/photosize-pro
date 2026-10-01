package com.example.ui.screens

import android.graphics.Color as AndroidColor
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AppDatabase
import com.example.data.CompressionHistoryEntity
import com.example.data.HistoryRepository
import com.example.model.*
import com.example.ui.components.AppHeader
import com.example.ui.components.BannerAdCard
import com.example.ui.components.BeforeAfterSlider
import com.example.ui.components.InterstitialAdDialog
import com.example.ui.theme.*
import com.example.util.AdMobConfig
import com.example.util.FileHelper
import com.example.util.I18n
import com.example.util.ImageCompressor
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.roundToInt

enum class DimensionUnit(val label: String, val pxPerUnit: Float) {
    PX("px", 1.0f),
    MM("mm", 11.81f), // approx 300 dpi
    CM("cm", 118.11f),
    INCH("inch", 300.0f)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompressEditorScreen(
    initialUri: Uri?,
    initialPreset: ExamPreset?,
    onBack: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val historyRepo = remember {
        HistoryRepository(AppDatabase.getDatabase(context).historyDao())
    }

    var selectedUri by remember { mutableStateOf(initialUri) }
    var metadata by remember { mutableStateOf<ImageMetadata?>(null) }

    // Read metadata when URI changes
    LaunchedEffect(selectedUri) {
        if (selectedUri != null) {
            metadata = FileHelper.getImageMetadata(context, selectedUri!!)
        }
    }

    var selectedPreset by remember { mutableStateOf(initialPreset ?: Presets.allPresets.first()) }
    var targetKbText by remember { mutableStateOf(selectedPreset.defaultTargetKb.toString()) }
    var targetKbSlider by remember { mutableFloatStateOf(selectedPreset.defaultTargetKb.toFloat()) }

    var widthText by remember { mutableStateOf(selectedPreset.targetWidthPx?.toString() ?: "") }
    var heightText by remember { mutableStateOf(selectedPreset.targetHeightPx?.toString() ?: "") }
    var lockAspectRatio by remember { mutableStateOf(true) }
    var dimensionUnit by remember { mutableStateOf(DimensionUnit.PX) }

    var outputFormat by remember { mutableStateOf(OutputFormat.JPEG) }
    var isWhiteBackground by remember { mutableStateOf(true) }

    // Processing State
    var isCompressing by remember { mutableStateOf(false) }
    var compressProgress by remember { mutableFloatStateOf(0f) }
    var compressStatusText by remember { mutableStateOf("") }
    var compressionResult by remember { mutableStateOf<CompressionResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedUri = uri
            compressionResult = null
            errorMessage = null
        }
    }

    fun applyPreset(preset: ExamPreset) {
        selectedPreset = preset
        targetKbText = preset.defaultTargetKb.toString()
        targetKbSlider = preset.defaultTargetKb.toFloat()
        if (preset.targetWidthPx != null) {
            widthText = preset.targetWidthPx.toString()
            heightText = preset.targetHeightPx?.toString() ?: ""
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                title = I18n.t("editor_title"),
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
            // Pick Image or Preview Card
            item {
                if (selectedUri == null || metadata == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                            .testTag("editor_pick_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = PrimaryGradient)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(IndigoPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = I18n.t("btn_choose_photo"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "JPG, PNG, WEBP (Supports up to 25 MB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // Image Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = selectedUri,
                                    contentDescription = "Original Thumbnail",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = metadata!!.fileName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "${I18n.t("original_image")}: ${metadata!!.formattedOriginalSize}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = IndigoPrimary
                                )
                                Text(
                                    text = "${metadata!!.width} x ${metadata!!.height} px",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        androidx.activity.result.PickVisualMediaRequest(
                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                        )
                                    )
                                },
                                modifier = Modifier.testTag("change_photo_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Change Photo",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Presets Selector Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = I18n.t("popular_presets"),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(Presets.allPresets) { preset ->
                            val isSelected = selectedPreset.id == preset.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { applyPreset(preset) },
                                label = {
                                    Text(
                                        text = "${I18n.t(preset.nameKey)} (${preset.defaultTargetKb}KB)",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IndigoPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // Target KB Configuration Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = I18n.t("target_size_kb"),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Quick value badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IndigoPrimary.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$targetKbText KB",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary
                                    )
                                )
                            }
                        }

                        // Target KB Slider
                        Slider(
                            value = targetKbSlider,
                            onValueChange = { value ->
                                targetKbSlider = value
                                targetKbText = value.roundToInt().toString()
                            },
                            valueRange = 5f..1000f,
                            colors = SliderDefaults.colors(
                                thumbColor = IndigoPrimary,
                                activeTrackColor = IndigoPrimary
                            )
                        )

                        // Quick Chips: 10, 20, 50, 100, 200, 500
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(10, 20, 50, 100, 200, 500).forEach { kb ->
                                val isSelected = targetKbText == kb.toString()
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable {
                                            targetKbText = kb.toString()
                                            targetKbSlider = kb.toFloat()
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "$kb KB",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }

                        // Manual KB Input
                        OutlinedTextField(
                            value = targetKbText,
                            onValueChange = {
                                targetKbText = it.filter { char -> char.isDigit() }
                                val parsed = targetKbText.toFloatOrNull()
                                if (parsed != null && parsed in 5f..5000f) {
                                    targetKbSlider = parsed.coerceIn(5f, 1000f)
                                }
                            },
                            label = { Text(I18n.t("enter_target_kb")) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("target_kb_input"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        // Warning for very small targets
                        val kbVal = targetKbText.toIntOrNull() ?: 50
                        if (kbVal < 10) {
                            Text(
                                text = "⚠️ Notice: Target under 10 KB may downscale dimensions to fit exam portal rules.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = AmberWarning
                            )
                        }
                    }
                }
            }

            // Dimensions & Aspect Ratio Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = I18n.t("dimensions"),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Unit switch
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(2.dp)
                            ) {
                                DimensionUnit.values().forEach { unit ->
                                    val isSelected = dimensionUnit == unit
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) IndigoPrimary else Color.Transparent)
                                            .clickable { dimensionUnit = unit }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = unit.label,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = widthText,
                                onValueChange = { newW ->
                                    widthText = newW.filter { it.isDigit() }
                                    if (lockAspectRatio && metadata != null && metadata!!.width > 0) {
                                        val w = widthText.toIntOrNull()
                                        if (w != null) {
                                            val aspect = metadata!!.height.toFloat() / metadata!!.width.toFloat()
                                            heightText = (w * aspect).roundToInt().toString()
                                        }
                                    }
                                },
                                label = { Text("${I18n.t("width")} (${dimensionUnit.label})") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            IconButton(
                                onClick = { lockAspectRatio = !lockAspectRatio },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (lockAspectRatio) IndigoPrimary.copy(alpha = 0.15f) else Color.Transparent)
                            ) {
                                Icon(
                                    imageVector = if (lockAspectRatio) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = "Lock Aspect",
                                    tint = if (lockAspectRatio) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { newH ->
                                    heightText = newH.filter { it.isDigit() }
                                    if (lockAspectRatio && metadata != null && metadata!!.height > 0) {
                                        val h = heightText.toIntOrNull()
                                        if (h != null) {
                                            val aspect = metadata!!.width.toFloat() / metadata!!.height.toFloat()
                                            widthText = (h * aspect).roundToInt().toString()
                                        }
                                    }
                                },
                                label = { Text("${I18n.t("height")} (${dimensionUnit.label})") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Output Format & Background Color
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = I18n.t("output_format"),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutputFormat.values().forEach { fmt ->
                                val isSelected = outputFormat == fmt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { outputFormat = fmt },
                                    label = {
                                        Text(
                                            text = fmt.name,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = IndigoPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Background Color option
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = I18n.t("bg_color"),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isWhiteBackground) I18n.t("bg_white") else I18n.t("bg_transparent"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                checked = isWhiteBackground,
                                onCheckedChange = { isWhiteBackground = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = IndigoPrimary)
                            )
                        }
                    }
                }
            }

            // Compress Action Button or Animated Progress
            item {
                if (isCompressing) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { compressProgress },
                                modifier = Modifier.size(54.dp),
                                color = IndigoPrimary,
                                strokeWidth = 5.dp
                            )
                            Text(
                                text = "${(compressProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = IndigoPrimary
                            )
                            Text(
                                text = compressStatusText.ifEmpty { I18n.t("compressing") },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            if (metadata == null) {
                                Toast.makeText(context, I18n.t("btn_choose_photo"), Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val targetKb = targetKbText.toIntOrNull() ?: 50
                            val targetW = widthText.toIntOrNull()
                            val targetH = heightText.toIntOrNull()

                            isCompressing = true
                            compressProgress = 0.1f
                            compressStatusText = "Starting..."
                            errorMessage = null

                            coroutineScope.launch {
                                try {
                                    val bg = if (isWhiteBackground) AndroidColor.WHITE else AndroidColor.TRANSPARENT
                                    val result = ImageCompressor.compressImage(
                                        context = context,
                                        metadata = metadata!!,
                                        targetKb = targetKb,
                                        targetWidth = targetW,
                                        targetHeight = targetH,
                                        outputFormat = outputFormat,
                                        backgroundColor = bg,
                                        onProgress = { p, status ->
                                            compressProgress = p
                                            compressStatusText = status
                                        }
                                    )
                                    compressionResult = result

                                    // Save to Room DB history
                                    historyRepo.insert(
                                        CompressionHistoryEntity(
                                            fileName = metadata!!.fileName,
                                            originalSizeBytes = metadata!!.originalSizeBytes,
                                            compressedSizeBytes = result.compressedSizeBytes,
                                            targetKb = targetKb,
                                            width = result.resultWidth,
                                            height = result.resultHeight,
                                            format = outputFormat.extension.uppercase(),
                                            filePath = result.outputFilePath,
                                            presetId = selectedPreset.id
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    errorMessage = e.localizedMessage ?: "Compression failed"
                                } finally {
                                    isCompressing = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("compress_now_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Compress, contentDescription = null, tint = Color.White)
                            Text(
                                text = I18n.t("btn_compress_now"),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                // Error Message if any
                if (errorMessage != null) {
                    Text(
                        text = "Error: $errorMessage",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoseError,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Results Section
            if (compressionResult != null && selectedUri != null) {
                item {
                    val result = compressionResult!!

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("result_card"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (result.isWithinTarget) EmeraldSuccess.copy(alpha = 0.08f)
                            else AmberWarning.copy(alpha = 0.08f)
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = if (result.isWithinTarget) Brush.horizontalGradient(listOf(EmeraldSuccess, IndigoPrimary))
                            else Brush.horizontalGradient(listOf(AmberWarning, RoseError))
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (result.isWithinTarget) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (result.isWithinTarget) EmeraldSuccess else AmberWarning,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = if (result.isWithinTarget) I18n.t("result_success") else I18n.t("result_warning_target"),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldSuccess)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "-${result.percentSaved}%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            // Comparison Stats Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = I18n.t("original_image"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = result.originalMetadata.formattedOriginalSize,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${result.originalMetadata.width}x${result.originalMetadata.height}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = IndigoPrimary,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "NEW SIZE",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = IndigoPrimary
                                    )
                                    Text(
                                        text = result.formattedCompressedSize,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (result.isWithinTarget) EmeraldSuccess else AmberWarning
                                        )
                                    )
                                    Text(
                                        text = "${result.resultWidth}x${result.resultHeight} • ${result.outputFormat.name}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Before vs After Interactive Split Slider
                            Text(
                                text = I18n.t("before_after"),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            BeforeAfterSlider(
                                originalUri = selectedUri!!,
                                compressedUri = result.outputUri
                            )

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val uri = FileHelper.saveToGallery(
                                            context = context,
                                            sourceFile = File(result.outputFilePath),
                                            displayName = File(result.outputFilePath).name,
                                            mimeType = result.outputFormat.mimeType
                                        )
                                        if (uri != null) {
                                            Toast.makeText(context, I18n.t("saved_to_gallery"), Toast.LENGTH_SHORT).show()
                                            AdMobConfig.onUserSaveOrShareAction()
                                        } else {
                                            Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("save_to_gallery_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.SaveAlt, contentDescription = null, tint = Color.White)
                                        Text(
                                            text = I18n.t("btn_save_gallery"),
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }

                                OutlinedButton(
                                    onClick = {
                                        FileHelper.shareFile(
                                            context = context,
                                            file = File(result.outputFilePath),
                                            mimeType = result.outputFormat.mimeType
                                        )
                                        AdMobConfig.onUserSaveOrShareAction()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("share_result_btn"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                                        Text(
                                            text = I18n.t("btn_share"),
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Banner ad placeholder below result
                item {
                    BannerAdCard(adUnitId = AdMobConfig.AD_UNIT_BANNER_RESULT)
                }
            }
        }
    }

    // Interstitial Ad Dialog Trigger
    if (AdMobConfig.showInterstitialDialog) {
        InterstitialAdDialog(
            onDismiss = { AdMobConfig.dismissInterstitial() }
        )
    }
}
