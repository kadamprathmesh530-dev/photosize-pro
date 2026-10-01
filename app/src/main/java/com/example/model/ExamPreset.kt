package com.example.model

enum class PresetCategory {
    PHOTO,
    SIGNATURE,
    DOCUMENT,
    CUSTOM
}

data class ExamPreset(
    val id: String,
    val nameKey: String,
    val category: PresetCategory,
    val minKb: Int,
    val maxKb: Int,
    val defaultTargetKb: Int,
    val targetWidthPx: Int?,
    val targetHeightPx: Int?,
    val displayRule: String,
    val iconName: String = "badge"
)

object Presets {
    val allPresets = listOf(
        ExamPreset(
            id = "ssc_photo",
            nameKey = "preset_ssc_photo",
            category = PresetCategory.PHOTO,
            minKb = 20,
            maxKb = 50,
            defaultTargetKb = 45,
            targetWidthPx = 100,
            targetHeightPx = 120,
            displayRule = "20–50 KB • 100x120 px"
        ),
        ExamPreset(
            id = "ssc_sign",
            nameKey = "preset_ssc_sign",
            category = PresetCategory.SIGNATURE,
            minKb = 10,
            maxKb = 20,
            defaultTargetKb = 18,
            targetWidthPx = 140,
            targetHeightPx = 60,
            displayRule = "10–20 KB • 140x60 px"
        ),
        ExamPreset(
            id = "rrb_photo",
            nameKey = "preset_rrb_photo",
            category = PresetCategory.PHOTO,
            minKb = 30,
            maxKb = 70,
            defaultTargetKb = 50,
            targetWidthPx = 350,
            targetHeightPx = 450,
            displayRule = "30–70 KB • 35x45 mm"
        ),
        ExamPreset(
            id = "rrb_sign",
            nameKey = "preset_rrb_sign",
            category = PresetCategory.SIGNATURE,
            minKb = 10,
            maxKb = 30,
            defaultTargetKb = 20,
            targetWidthPx = 140,
            targetHeightPx = 60,
            displayRule = "10–30 KB • 140x60 px"
        ),
        ExamPreset(
            id = "upsc_photo",
            nameKey = "preset_upsc_photo",
            category = PresetCategory.PHOTO,
            minKb = 20,
            maxKb = 300,
            defaultTargetKb = 100,
            targetWidthPx = 350,
            targetHeightPx = 350,
            displayRule = "20–300 KB • Min 350x350 px"
        ),
        ExamPreset(
            id = "bank_photo",
            nameKey = "preset_bank_photo",
            category = PresetCategory.PHOTO,
            minKb = 20,
            maxKb = 50,
            defaultTargetKb = 45,
            targetWidthPx = 200,
            targetHeightPx = 230,
            displayRule = "20–50 KB • 200x230 px"
        ),
        ExamPreset(
            id = "bank_sign",
            nameKey = "preset_bank_sign",
            category = PresetCategory.SIGNATURE,
            minKb = 10,
            maxKb = 20,
            defaultTargetKb = 18,
            targetWidthPx = 140,
            targetHeightPx = 60,
            displayRule = "10–20 KB • 140x60 px"
        ),
        ExamPreset(
            id = "passport",
            nameKey = "preset_passport",
            category = PresetCategory.PHOTO,
            minKb = 20,
            maxKb = 100,
            defaultTargetKb = 80,
            targetWidthPx = 413,
            targetHeightPx = 531,
            displayRule = "Under 100 KB • 35x45 mm"
        ),
        ExamPreset(
            id = "aadhaar_pan",
            nameKey = "preset_aadhaar_pan",
            category = PresetCategory.DOCUMENT,
            minKb = 50,
            maxKb = 200,
            defaultTargetKb = 180,
            targetWidthPx = null,
            targetHeightPx = null,
            displayRule = "Under 200 KB • Document"
        ),
        ExamPreset(
            id = "scholarship",
            nameKey = "preset_scholarship",
            category = PresetCategory.DOCUMENT,
            minKb = 30,
            maxKb = 100,
            defaultTargetKb = 90,
            targetWidthPx = null,
            targetHeightPx = null,
            displayRule = "Under 100 KB • High Clarity"
        ),
        ExamPreset(
            id = "custom",
            nameKey = "preset_custom",
            category = PresetCategory.CUSTOM,
            minKb = 5,
            maxKb = 5000,
            defaultTargetKb = 50,
            targetWidthPx = null,
            targetHeightPx = null,
            displayRule = "Custom KB & Dimensions"
        )
    )
}
