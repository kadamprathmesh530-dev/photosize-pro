package com.example.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * AdMob Configuration & Official Google Test Ad Unit IDs
 * Replace the test IDs with your real AdMob unit IDs before releasing to Google Play.
 */
object AdMobConfig {
    // Official Google Test App ID for Android
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"

    // AD_SLOT_BANNER_HOME: Replace with your real banner ad unit ID
    const val AD_UNIT_BANNER_HOME = "ca-app-pub-3940256099942544/6300978111"

    // AD_SLOT_BANNER_RESULT: Replace with your real banner ad unit ID
    const val AD_UNIT_BANNER_RESULT = "ca-app-pub-3940256099942544/6300978111"

    // AD_SLOT_INTERSTITIAL_SAVE_SHARE: Replace with your real interstitial ad unit ID
    const val AD_UNIT_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"

    private var actionCounter by mutableIntStateOf(0)
    var showInterstitialDialog by mutableStateOf(false)

    /**
     * Triggered on user save or share.
     * Shows an interstitial placeholder every 3 actions (as requested: "never more than once every 3 actions").
     */
    fun onUserSaveOrShareAction(onAdDismissed: () -> Unit = {}) {
        actionCounter++
        if (actionCounter % 3 == 0) {
            showInterstitialDialog = true
        }
    }

    fun dismissInterstitial() {
        showInterstitialDialog = false
    }
}
