package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExamPreset
import com.example.model.Presets
import com.example.ui.components.AppHeader
import com.example.ui.theme.*
import com.example.util.AdMobConfig
import com.example.util.FileHelper
import com.example.util.I18n
import com.example.util.SignatureCleaner
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class DrawStroke(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureToolScreen(
    onBack: () -> Unit,
    onSendToCompressor: (Uri, ExamPreset) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Clean photo, 1: Draw

    // Tab 1: Clean Photo
    var rawPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var rawBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cleanedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var thresholdSlider by remember { mutableFloatStateOf(180f) }
    var selectedInkColor by remember { mutableStateOf(SignatureCleaner.InkColor.BLACK) }
    var autoTrim by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            rawPhotoUri = uri
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bm = android.graphics.BitmapFactory.decodeStream(stream)
                    rawBitmap = bm
                    coroutineScope.launch {
                        isProcessing = true
                        cleanedBitmap = SignatureCleaner.cleanSignature(
                            inputBitmap = bm,
                            threshold = thresholdSlider.toInt(),
                            inkColor = selectedInkColor,
                            autoTrim = autoTrim
                        )
                        isProcessing = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Tab 2: Draw with Finger
    var strokes by remember { mutableStateOf<List<DrawStroke>>(emptyList()) }
    var currentStrokePoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var drawColor by remember { mutableStateOf(Color(0xFF111827)) }
    var drawStrokeWidth by remember { mutableFloatStateOf(6f) }

    Scaffold(
        topBar = {
            AppHeader(
                title = I18n.t("sig_tool_title"),
                showBack = true,
                onBackClick = onBack,
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
            // Tabs Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = I18n.t("sig_tab_clean"),
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = I18n.t("sig_tab_draw"),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.Gesture, contentDescription = null) }
                )
            }

            if (selectedTab == 0) {
                // Upload & Clean Photo
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        if (cleanedBitmap != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .testTag("cleaned_signature_preview"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = CardDefaults.outlinedCardBorder().copy(brush = PrimaryGradient)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.foundation.Image(
                                        bitmap = cleanedBitmap!!.asImageBitmap(),
                                        contentDescription = "Cleaned Signature",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        } else {
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
                                    .testTag("upload_signature_card"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(VioletAccent.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = VioletAccent,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "कागज पर किए गए साइन की फोटो चुनें",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "हम बैकग्राउंड सफेद और स्याही को साफ कर देंगे",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (rawBitmap != null) {
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = I18n.t("sig_threshold"),
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${thresholdSlider.toInt()}",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = IndigoPrimary
                                            )
                                        )
                                    }

                                    Slider(
                                        value = thresholdSlider,
                                        onValueChange = { newVal ->
                                            thresholdSlider = newVal
                                            coroutineScope.launch {
                                                cleanedBitmap = SignatureCleaner.cleanSignature(
                                                    inputBitmap = rawBitmap!!,
                                                    threshold = newVal.toInt(),
                                                    inkColor = selectedInkColor,
                                                    autoTrim = autoTrim
                                                )
                                            }
                                        },
                                        valueRange = 80f..240f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = VioletAccent,
                                            activeTrackColor = VioletAccent
                                        )
                                    )

                                    // Ink color toggle
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = I18n.t("sig_color"),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            FilterChip(
                                                selected = selectedInkColor == SignatureCleaner.InkColor.BLACK,
                                                onClick = {
                                                    selectedInkColor = SignatureCleaner.InkColor.BLACK
                                                    coroutineScope.launch {
                                                        cleanedBitmap = SignatureCleaner.cleanSignature(
                                                            rawBitmap!!,
                                                            thresholdSlider.toInt(),
                                                            SignatureCleaner.InkColor.BLACK,
                                                            autoTrim
                                                        )
                                                    }
                                                },
                                                label = { Text("Black (काला)") }
                                            )
                                            FilterChip(
                                                selected = selectedInkColor == SignatureCleaner.InkColor.BLUE,
                                                onClick = {
                                                    selectedInkColor = SignatureCleaner.InkColor.BLUE
                                                    coroutineScope.launch {
                                                        cleanedBitmap = SignatureCleaner.cleanSignature(
                                                            rawBitmap!!,
                                                            thresholdSlider.toInt(),
                                                            SignatureCleaner.InkColor.BLUE,
                                                            autoTrim
                                                        )
                                                    }
                                                },
                                                label = { Text("Blue (नीला)") }
                                            )
                                        }
                                    }

                                    // Auto Trim Checkbox
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = I18n.t("sig_autotrim"),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Checkbox(
                                            checked = autoTrim,
                                            onCheckedChange = { checked ->
                                                autoTrim = checked
                                                coroutineScope.launch {
                                                    cleanedBitmap = SignatureCleaner.cleanSignature(
                                                        rawBitmap!!,
                                                        thresholdSlider.toInt(),
                                                        selectedInkColor,
                                                        checked
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = {
                                    if (cleanedBitmap != null) {
                                        // Save cleaned signature to temp file and open in compressor with SSC sign preset
                                        val tempFile = File(context.cacheDir, "cleaned_sig_${System.currentTimeMillis()}.jpg")
                                        FileOutputStream(tempFile).use { fos ->
                                            cleanedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 95, fos)
                                        }
                                        val sscPreset = Presets.allPresets.firstOrNull { it.id == "ssc_sign" }
                                            ?: Presets.allPresets.first()
                                        onSendToCompressor(Uri.fromFile(tempFile), sscPreset)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("export_signature_btn"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Compress, contentDescription = null, tint = Color.White)
                                    Text(
                                        text = "${I18n.t("btn_export_signature")} (140x60 px, 10–20 KB)",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Draw with Finger Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Finger Drawing Canvas
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .testTag("drawing_canvas_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = PrimaryGradient)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            currentStrokePoints = listOf(offset)
                                        },
                                        onDrag = { change, _ ->
                                            change.consume()
                                            currentStrokePoints = currentStrokePoints + change.position
                                        },
                                        onDragEnd = {
                                            if (currentStrokePoints.isNotEmpty()) {
                                                strokes = strokes + DrawStroke(
                                                    points = currentStrokePoints,
                                                    color = drawColor,
                                                    strokeWidth = drawStrokeWidth
                                                )
                                                currentStrokePoints = emptyList()
                                            }
                                        }
                                    )
                                }
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw completed strokes
                                for (stroke in strokes) {
                                    val pts = stroke.points
                                    for (i in 0 until pts.size - 1) {
                                        drawLine(
                                            color = stroke.color,
                                            start = pts[i],
                                            end = pts[i + 1],
                                            strokeWidth = stroke.strokeWidth,
                                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                                        )
                                    }
                                }
                                // Draw active stroke
                                for (i in 0 until currentStrokePoints.size - 1) {
                                    drawLine(
                                        color = drawColor,
                                        start = currentStrokePoints[i],
                                        end = currentStrokePoints[i + 1],
                                        strokeWidth = drawStrokeWidth,
                                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                                    )
                                }
                            }

                            if (strokes.isEmpty() && currentStrokePoints.isEmpty()) {
                                Text(
                                    text = "उंगली से यहां साइन करें (Sign here with finger)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray.copy(alpha = 0.6f),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }

                    // Drawing Controls (Color, Thickness, Undo, Clear)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pen colors
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF111827))
                                        .border(
                                            if (drawColor == Color(0xFF111827)) 3.dp else 0.dp,
                                            IndigoPrimary,
                                            CircleShape
                                        )
                                        .clickable { drawColor = Color(0xFF111827) }
                                )
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1D4ED8))
                                        .border(
                                            if (drawColor == Color(0xFF1D4ED8)) 3.dp else 0.dp,
                                            IndigoPrimary,
                                            CircleShape
                                        )
                                        .clickable { drawColor = Color(0xFF1D4ED8) }
                                )
                            }

                            // Undo & Clear
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        if (strokes.isNotEmpty()) {
                                            strokes = strokes.dropLast(1)
                                        }
                                    },
                                    enabled = strokes.isNotEmpty(),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Undo, contentDescription = "Undo", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(I18n.t("btn_undo"), fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { strokes = emptyList() },
                                    enabled = strokes.isNotEmpty(),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(I18n.t("btn_clear"), fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Export Drawn Signature Button
                    Button(
                        onClick = {
                            if (strokes.isEmpty()) {
                                Toast.makeText(context, "कृपया पहले साइन करें", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Render strokes onto bitmap
                            val bitmapW = 600
                            val bitmapH = 280
                            val bitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bitmap)
                            canvas.drawColor(AndroidColor.WHITE)

                            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                                style = Paint.Style.STROKE
                                strokeCap = Paint.Cap.ROUND
                                strokeJoin = Paint.Join.ROUND
                            }

                            for (stroke in strokes) {
                                paint.color = if (stroke.color == Color(0xFF1D4ED8)) AndroidColor.rgb(29, 78, 216)
                                else AndroidColor.rgb(17, 24, 39)
                                paint.strokeWidth = stroke.strokeWidth * 1.5f

                                val pts = stroke.points
                                for (i in 0 until pts.size - 1) {
                                    // Scale to bitmap dimensions
                                    canvas.drawLine(pts[i].x, pts[i].y, pts[i + 1].x, pts[i + 1].y, paint)
                                }
                            }

                            val outFile = File(context.cacheDir, "drawn_signature_${System.currentTimeMillis()}.jpg")
                            FileOutputStream(outFile).use { fos ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos)
                            }

                            val sscPreset = Presets.allPresets.firstOrNull { it.id == "ssc_sign" }
                                ?: Presets.allPresets.first()
                            onSendToCompressor(Uri.fromFile(outFile), sscPreset)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("export_drawn_signature_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Text(
                                text = I18n.t("btn_export_signature"),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
