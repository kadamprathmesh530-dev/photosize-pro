package com.example.ui.components

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ui.theme.*
import com.example.util.AdMobConfig
import com.example.util.AppLanguage
import com.example.util.I18n
import kotlin.math.roundToInt

@Composable
fun AppHeader(
    title: String = I18n.t("app_name"),
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    showLangToggle: Boolean = true,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (showBack) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compress,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!showBack) {
                        Text(
                            text = "Photo & Signature Compressor",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showLangToggle) {
                    OutlinedButton(
                        onClick = {
                            I18n.currentLanguage = if (I18n.currentLanguage == AppLanguage.HINDI) {
                                AppLanguage.ENGLISH
                            } else {
                                AppLanguage.HINDI
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("lang_toggle_btn"),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            text = if (I18n.currentLanguage == AppLanguage.HINDI) "English" else "हिंदी",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier.size(36.dp).testTag("theme_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * AD_SLOT_BANNER_HOME / AD_SLOT_BANNER_RESULT
 * Official Test Ad Unit ID placeholder container
 */
@Composable
fun BannerAdCard(
    adUnitId: String = AdMobConfig.AD_UNIT_BANNER_HOME,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("admob_banner_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = PrimaryGradient)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(IndigoPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            fontSize = 10.sp
                        )
                    )
                }
                Column {
                    Text(
                        text = "Google AdMob (Test Ad Slot)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ID: ...${adUnitId.takeLast(10)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Ad Info",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun InterstitialAdDialog(
    onDismiss: () -> Unit
) {
    var secondsLeft by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        while (secondsLeft > 0) {
            kotlinx.coroutines.delay(1000)
            secondsLeft--
        }
    }

    Dialog(onDismissRequest = {
        if (secondsLeft <= 0) onDismiss()
    }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Ad",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "Google AdMob Test Interstitial",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Unit ID: ${AdMobConfig.AD_UNIT_INTERSTITIAL}\nThis interstitial triggers every 3 successful actions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onDismiss,
                    enabled = secondsLeft <= 0,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                ) {
                    Text(
                        text = if (secondsLeft > 0) "Close in ${secondsLeft}s..." else "Close Ad",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Trust badges row: 100% Private, No Server Upload, Free, Offline
 */
@Composable
fun TrustBadgesRow() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmeraldSuccess.copy(alpha = 0.08f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(EmeraldSuccess, IndigoPrimary)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrustBadgeItem(icon = Icons.Default.Lock, text = I18n.t("trust_private"))
            TrustBadgeItem(icon = Icons.Default.CloudOff, text = I18n.t("trust_no_upload"))
            TrustBadgeItem(icon = Icons.Default.CheckCircle, text = I18n.t("trust_free"))
            TrustBadgeItem(icon = Icons.Default.WifiOff, text = I18n.t("trust_offline"))
        }
    }
}

@Composable
private fun TrustBadgeItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = EmeraldSuccess,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Before / After Interactive Split Slider
 * User drags the vertical line left and right to inspect the compression result vs original!
 */
@Composable
fun BeforeAfterSlider(
    originalUri: Uri,
    compressedUri: Uri,
    modifier: Modifier = Modifier
) {
    var splitFraction by remember { mutableFloatStateOf(0.5f) }
    var containerWidthPx by remember { mutableFloatStateOf(1f) }

    val animatedSplit by animateFloatAsState(targetValue = splitFraction, label = "split")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .testTag("before_after_card"),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    containerWidthPx = coordinates.size.width.toFloat()
                }
        ) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            val splitPx = width * animatedSplit

            // Compressed Image on full background
            AsyncImage(
                model = compressedUri,
                contentDescription = "Compressed After",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            // Original Image clipped to left side
            androidx.compose.foundation.Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background visual clipping handled via layout overlay
            }

            // Box containing original image clipped to split width
            val density = LocalDensity.current
            val splitDp = with(density) { splitPx.toDp() }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(splitDp)
                    .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
            ) {
                AsyncImage(
                    model = originalUri,
                    contentDescription = "Original Before",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(with(density) { width.toDp() })
                )
            }

            // Labels: "BEFORE" on left, "AFTER" on right
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "BEFORE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(IndigoPrimary.copy(alpha = 0.85f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "AFTER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            // Draggable Divider Line & Handle
            Box(
                modifier = Modifier
                    .offset { IntOffset(x = splitPx.roundToInt() - 16, y = (height / 2 - 24).roundToInt()) }
                    .size(32.dp, 48.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            if (width > 0) {
                                val newFraction = (splitFraction + dragAmount.x / width).coerceIn(0.05f, 0.95f)
                                splitFraction = newFraction
                            }
                        }
                    }
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(2.dp, IndigoPrimary, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = "Drag to compare",
                    tint = IndigoPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
