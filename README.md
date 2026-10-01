# PhotoSize Pro: Photo, Signature & Document Size Compressor for Indian Forms

PhotoSize Pro is a native mobile application designed specifically for Indian government, exam, bank, and college forms (SSC, UPSC, Railway/RRB, IBPS/SBI, Passport, PAN/Aadhaar). Everything runs 100% on-device and works offline without login, backend, or cloud upload.

---

## Key Features

1. **Exact KB Compressor & Dimension Scaler**
   - Binary search on image quality with real-time byte measurement.
   - Iterative dimension downscaling if quality alone cannot achieve the target KB.
   - Never exceeds the target KB.
   - Presets for SSC (Photo & Signature), RRB, UPSC, IBPS/SBI, Passport, Aadhaar/PAN.
   - Custom KB (5–5000 KB) with aspect ratio lock and unit switch (px, mm, cm, inch).

2. **Before / After Comparison Slider**
   - Interactive split slider allowing users to slide and compare original vs compressed photo side-by-side.

3. **Signature Tool**
   - **Upload & Clean:** Grayscale, contrast boost, paper white threshold slider, and auto-trim empty margins.
   - **Draw with Finger:** Smooth finger canvas with black/blue pen, thickness control, undo, and clear.
   - One-tap export to official signature dimensions (e.g., 140x60 px, 10–20 KB).

4. **Interactive Crop & Rotate**
   - Ratios: Free, 1:1, 3:4, 4:3, 35x45 mm (Passport), 16:9.
   - Rotate 90° left/right, flip horizontal.

5. **Image to PDF Converter**
   - Multi-image selection, page ordering, page sizes (A4, Fit, Letter), customizable margins.
   - Generates native PDFs using Android `PdfDocument`.

6. **Format Converter**
   - Convert between JPEG, PNG, and WEBP with quality slider.

7. **Batch Compressor**
   - Compress up to 10 photos simultaneously with real-time per-item progress, Save All to gallery, and Share All.

8. **Room Database History**
   - Persists all compressions locally. Preview, re-save, re-share, or delete anytime.

9. **Bilingual UI**
   - Instant toggle between Hindi (हिंदी) and English in the header bar and settings.

10. **Google AdMob Integration**
    - Configured with official Google Test Ad Unit IDs.
    - Banner ads on Home, Result, and Tools screens.
    - Non-intrusive interstitial dialog triggered once every 3 save/share actions.

---

## AdMob Configuration

All AdMob Ad Unit IDs are centralized in `app/src/main/java/com/example/util/AdMobConfig.kt`:

```kotlin
// Official Google Test App ID for Android
const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"

// AD_SLOT_BANNER_HOME: Replace with your real banner ad unit ID
const val AD_UNIT_BANNER_HOME = "ca-app-pub-3940256099942544/6300978111"

// AD_SLOT_BANNER_RESULT: Replace with your real banner ad unit ID
const val AD_UNIT_BANNER_RESULT = "ca-app-pub-3940256099942544/6300978111"

// AD_SLOT_INTERSTITIAL_SAVE_SHARE: Replace with your real interstitial ad unit ID
const val AD_UNIT_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
```

To replace with your real production AdMob IDs:
1. Create an app in [Google AdMob](https://admob.google.com).
2. Generate Banner and Interstitial Ad Units.
3. Replace the test strings in `AdMobConfig.kt` with your AdMob IDs.

---

## Building and Running

### Development Build
```bash
gradle assembleDebug
```

### Release Build
```bash
gradle assembleRelease
```
The resulting APK will be located in `app/build/outputs/apk/release/app-release.apk`.
