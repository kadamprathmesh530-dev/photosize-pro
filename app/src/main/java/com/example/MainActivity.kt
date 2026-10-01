package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.example.ui.MainScreen
import com.example.ui.theme.PhotoSizeProTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkThemeOverride by remember { mutableStateOf<Boolean?>(null) }
            val isDark = darkThemeOverride ?: systemDark

            PhotoSizeProTheme(darkTheme = isDark) {
                MainScreen(
                    isDarkTheme = isDark,
                    onToggleTheme = {
                        darkThemeOverride = !isDark
                    }
                )
            }
        }
    }
}
