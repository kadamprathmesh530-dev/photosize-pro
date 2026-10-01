package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppHeader
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PrimaryGradient
import com.example.util.I18n
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

data class AspectRatioOption(val label: String, val ratioW: Float?, val ratioH: Float?)

@Composable
fun CropScreen(
    onBack: () -> Unit,
    onCroppedDone: (Uri) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val ratios = listOf(
        AspectRatioOption("Free", null, null),
        AspectRatioOption("1:1", 1f, 1f),
        AspectRatioOption("3:4", 3f, 4f),
        AspectRatioOption("4:3", 4f, 3f),
        AspectRatioOption("35:45 (Passport)", 35f, 45f),
        AspectRatioOption("16:9", 16f, 9f)
    )
    var selectedRatio by remember { mutableStateOf(ratios[0]) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    currentBitmap = BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun rotateBitmap(degrees: Float) {
        val bm = currentBitmap ?: return
        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        currentBitmap = rotated
    }

    fun flipHorizontal() {
        val bm = currentBitmap ?: return
        val matrix = Matrix().apply { postScale(-1f, 1f) }
        val flipped = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        currentBitmap = flipped
    }

    fun cropToSelectedRatio() {
        val bm = currentBitmap ?: return
        val rw = selectedRatio.ratioW
        val rh = selectedRatio.ratioH

        if (rw != null && rh != null) {
            val targetRatio = rw / rh
            val originalRatio = bm.width.toFloat() / bm.height.toFloat()

            val cropW: Int
            val cropH: Int
            if (originalRatio > targetRatio) {
                cropH = bm.height
                cropW = (cropH * targetRatio).roundToInt().coerceAtMost(bm.width)
            } else {
                cropW = bm.width
                cropH = (cropW / targetRatio).roundToInt().coerceAtMost(bm.height)
            }

            val startX = (bm.width - cropW) / 2
            val startY = (bm.height - cropH) / 2

            val cropped = Bitmap.createBitmap(bm, startX, startY, cropW, cropH)
            currentBitmap = cropped
        }

        // Save to cache file and return
        coroutineScope.launch(Dispatchers.IO) {
            val file = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { fos ->
                currentBitmap!!.compress(Bitmap.CompressFormat.JPEG, 95, fos)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, I18n.t("btn_apply_crop"), Toast.LENGTH_SHORT).show()
                onCroppedDone(Uri.fromFile(file))
            }
        }
    }

    Scaffold(
        topBar = {
            AppHeader(
                title = I18n.t("crop_title"),
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentBitmap == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clickable {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                        .testTag("crop_select_photo_card"),
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
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Crop, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = I18n.t("btn_choose_photo"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "गैलरी से फोटो चुनें जिसे क्रॉप या रोटेट करना है",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Interactive Preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        androidx.compose.foundation.Image(
                            bitmap = currentBitmap!!.asImageBitmap(),
                            contentDescription = "Image to Crop",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Ratio Selector Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ratios) { ratio ->
                        val isSelected = selectedRatio.label == ratio.label
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRatio = ratio },
                            label = { Text(ratio.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IndigoPrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Rotation & Flip Controls
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { rotateBitmap(-90f) }) {
                            Icon(Icons.AutoMirrored.Filled.RotateLeft, contentDescription = "Rotate Left", tint = IndigoPrimary)
                        }
                        IconButton(onClick = { rotateBitmap(90f) }) {
                            Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = "Rotate Right", tint = IndigoPrimary)
                        }
                        IconButton(onClick = { flipHorizontal() }) {
                            Icon(Icons.Default.Flip, contentDescription = "Flip Horizontal", tint = IndigoPrimary)
                        }
                    }
                }

                // Apply Crop Button
                Button(
                    onClick = { cropToSelectedRatio() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("apply_crop_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Text(
                            text = I18n.t("btn_apply_crop"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                }
            }
        }
    }
}
