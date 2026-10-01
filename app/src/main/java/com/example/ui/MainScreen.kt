package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExamPreset
import com.example.ui.screens.*
import com.example.ui.theme.IndigoPrimary
import com.example.util.I18n

enum class MainTab(
    val titleKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("nav_home", Icons.Filled.Home, Icons.Outlined.Home),
    TOOLS("nav_tools", Icons.Filled.GridView, Icons.Outlined.GridView),
    HISTORY("nav_history", Icons.Filled.History, Icons.Outlined.History),
    SETTINGS("nav_settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

sealed class AppScreen {
    object MainHost : AppScreen()
    object Onboarding : AppScreen()
    data class CompressEditor(val uri: Uri?, val preset: ExamPreset?) : AppScreen()
    object SignatureTool : AppScreen()
    object CropTool : AppScreen()
    object ImageToPdf : AppScreen()
    object ConvertFormat : AppScreen()
    object BatchCompress : AppScreen()
}

@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("photosize_pro_prefs", Context.MODE_PRIVATE) }
    val hasSeenOnboarding = remember { prefs.getBoolean("has_seen_onboarding", false) }

    var currentScreen by remember {
        mutableStateOf<AppScreen>(
            if (!hasSeenOnboarding) AppScreen.Onboarding else AppScreen.MainHost
        )
    }

    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    // Intercept back button when inside secondary sub-screens
    BackHandler(enabled = currentScreen != AppScreen.MainHost && currentScreen != AppScreen.Onboarding) {
        currentScreen = AppScreen.MainHost
    }

    when (val screen = currentScreen) {
        is AppScreen.Onboarding -> {
            OnboardingScreen(
                onFinish = {
                    prefs.edit().putBoolean("has_seen_onboarding", true).apply()
                    currentScreen = AppScreen.MainHost
                }
            )
        }

        is AppScreen.CompressEditor -> {
            CompressEditorScreen(
                initialUri = screen.uri,
                initialPreset = screen.preset,
                onBack = { currentScreen = AppScreen.MainHost },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        is AppScreen.SignatureTool -> {
            SignatureToolScreen(
                onBack = { currentScreen = AppScreen.MainHost },
                onSendToCompressor = { uri, preset ->
                    currentScreen = AppScreen.CompressEditor(uri, preset)
                },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        is AppScreen.CropTool -> {
            CropScreen(
                onBack = { currentScreen = AppScreen.MainHost },
                onCroppedDone = { croppedUri ->
                    currentScreen = AppScreen.CompressEditor(croppedUri, null)
                },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        is AppScreen.ImageToPdf -> {
            ImageToPdfScreen(
                onBack = { currentScreen = AppScreen.MainHost },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        is AppScreen.ConvertFormat -> {
            ConvertFormatScreen(
                onBack = { currentScreen = AppScreen.MainHost },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        is AppScreen.BatchCompress -> {
            BatchCompressScreen(
                onBack = { currentScreen = AppScreen.MainHost },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        is AppScreen.MainHost -> {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        MainTab.values().forEach { tab ->
                            val isSelected = selectedTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { selectedTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = I18n.t(tab.titleKey)
                                    )
                                },
                                label = {
                                    Text(
                                        text = I18n.t(tab.titleKey),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = IndigoPrimary,
                                    selectedTextColor = IndigoPrimary,
                                    indicatorColor = IndigoPrimary.copy(alpha = 0.12f)
                                )
                            )
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.background
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (selectedTab) {
                        MainTab.HOME -> {
                            HomeScreen(
                                onNavigateToCompress = { uri, preset ->
                                    currentScreen = AppScreen.CompressEditor(uri, preset)
                                },
                                onNavigateToSignature = { currentScreen = AppScreen.SignatureTool },
                                onNavigateToCrop = { currentScreen = AppScreen.CropTool },
                                onNavigateToPdf = { currentScreen = AppScreen.ImageToPdf },
                                onNavigateToConvert = { currentScreen = AppScreen.ConvertFormat },
                                onNavigateToBatch = { currentScreen = AppScreen.BatchCompress },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = onToggleTheme
                            )
                        }

                        MainTab.TOOLS -> {
                            // Tools Grid Screen
                            ToolsScreen(
                                onNavigateToCompress = { currentScreen = AppScreen.CompressEditor(null, null) },
                                onNavigateToSignature = { currentScreen = AppScreen.SignatureTool },
                                onNavigateToCrop = { currentScreen = AppScreen.CropTool },
                                onNavigateToPdf = { currentScreen = AppScreen.ImageToPdf },
                                onNavigateToConvert = { currentScreen = AppScreen.ConvertFormat },
                                onNavigateToBatch = { currentScreen = AppScreen.BatchCompress },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = onToggleTheme
                            )
                        }

                        MainTab.HISTORY -> {
                            HistoryScreen(
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = onToggleTheme
                            )
                        }

                        MainTab.SETTINGS -> {
                            SettingsScreen(
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = onToggleTheme
                            )
                        }
                    }
                }
            }
        }
    }
}
