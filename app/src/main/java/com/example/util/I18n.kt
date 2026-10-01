package com.example.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String) {
    HINDI("hi", "हिंदी"),
    ENGLISH("en", "English")
}

object I18n {
    var currentLanguage by mutableStateOf(AppLanguage.HINDI)

    fun t(key: String): String {
        return translations[key]?.get(currentLanguage)
            ?: translations[key]?.get(AppLanguage.ENGLISH)
            ?: key
    }

    private val translations = mapOf(
        // App identity
        "app_name" to mapOf(
            AppLanguage.HINDI to "PhotoSize Pro",
            AppLanguage.ENGLISH to "PhotoSize Pro"
        ),
        "app_subtitle" to mapOf(
            AppLanguage.HINDI to "भारतीय फॉर्म्स के लिए फोटो, सिग्नेचर और डॉक्यूमेंट कंप्रेसर",
            AppLanguage.ENGLISH to "Photo, Signature & Document Size Compressor for Indian forms"
        ),

        // Navigation
        "nav_home" to mapOf(AppLanguage.HINDI to "होम", AppLanguage.ENGLISH to "Home"),
        "nav_tools" to mapOf(AppLanguage.HINDI to "टूल्स", AppLanguage.ENGLISH to "Tools"),
        "nav_history" to mapOf(AppLanguage.HINDI to "इतिहास", AppLanguage.ENGLISH to "History"),
        "nav_settings" to mapOf(AppLanguage.HINDI to "सेटिंग्स", AppLanguage.ENGLISH to "Settings"),

        // Hero
        "hero_title" to mapOf(
            AppLanguage.HINDI to "फोटो और सिग्नेचर को किसी भी फॉर्म के साइज में बदलो",
            AppLanguage.ENGLISH to "Resize Photo & Signature for Any Govt or Exam Form"
        ),
        "hero_subtitle" to mapOf(
            AppLanguage.HINDI to "SSC, UPSC, रेलवे, बैंक और कॉलेज फॉर्म्स के लिए तुरंत सटीक KB साइज पाएं",
            AppLanguage.ENGLISH to "Get exact KB size instantly for SSC, UPSC, Railway, Bank & College forms"
        ),
        "btn_choose_photo" to mapOf(AppLanguage.HINDI to "फोटो चुनो", AppLanguage.ENGLISH to "Choose Photo"),
        "btn_camera" to mapOf(AppLanguage.HINDI to "कैमरा", AppLanguage.ENGLISH to "Camera"),
        "btn_gallery" to mapOf(AppLanguage.HINDI to "गैलरी", AppLanguage.ENGLISH to "Gallery"),

        // Presets Header
        "popular_presets" to mapOf(
            AppLanguage.HINDI to "पॉपुलर एग्जाम प्रीसेट्स",
            AppLanguage.ENGLISH to "Popular Exam Presets"
        ),
        "popular_presets_sub" to mapOf(
            AppLanguage.HINDI to "एक क्लिक में एग्जाम के नियमानुसार साइज सेट करें",
            AppLanguage.ENGLISH to "One-tap sizes adhering to official exam guidelines"
        ),

        // Preset names
        "preset_ssc_photo" to mapOf(AppLanguage.HINDI to "SSC फोटो", AppLanguage.ENGLISH to "SSC Photo"),
        "preset_ssc_sign" to mapOf(AppLanguage.HINDI to "SSC सिग्नेचर", AppLanguage.ENGLISH to "SSC Signature"),
        "preset_rrb_photo" to mapOf(AppLanguage.HINDI to "रेलवे (RRB) फोटो", AppLanguage.ENGLISH to "Railway (RRB) Photo"),
        "preset_rrb_sign" to mapOf(AppLanguage.HINDI to "रेलवे सिग्नेचर", AppLanguage.ENGLISH to "Railway Signature"),
        "preset_upsc_photo" to mapOf(AppLanguage.HINDI to "UPSC फोटो", AppLanguage.ENGLISH to "UPSC Photo"),
        "preset_bank_photo" to mapOf(AppLanguage.HINDI to "बैंक (IBPS/SBI) फोटो", AppLanguage.ENGLISH to "Bank (IBPS/SBI) Photo"),
        "preset_bank_sign" to mapOf(AppLanguage.HINDI to "बैंक सिग्नेचर", AppLanguage.ENGLISH to "Bank Signature"),
        "preset_passport" to mapOf(AppLanguage.HINDI to "पासपोर्ट साइज फोटो", AppLanguage.ENGLISH to "Passport Size Photo"),
        "preset_aadhaar_pan" to mapOf(AppLanguage.HINDI to "आधार / पैन डॉक्यूमेंट", AppLanguage.ENGLISH to "Aadhaar / PAN Doc"),
        "preset_scholarship" to mapOf(AppLanguage.HINDI to "स्कॉलरशिप / एडमिशन", AppLanguage.ENGLISH to "Scholarship / Admission"),
        "preset_custom" to mapOf(AppLanguage.HINDI to "कस्टम साइज", AppLanguage.ENGLISH to "Custom Size"),

        // Quick Tools
        "quick_tools" to mapOf(AppLanguage.HINDI to "क्विक टूल्स", AppLanguage.ENGLISH to "Quick Tools"),
        "tool_compress" to mapOf(AppLanguage.HINDI to "कंप्रेस और रीसाइज", AppLanguage.ENGLISH to "Compress & Resize"),
        "tool_compress_desc" to mapOf(AppLanguage.HINDI to "सटीक KB में साइज कम करें", AppLanguage.ENGLISH to "Reduce exact KB size"),
        "tool_signature" to mapOf(AppLanguage.HINDI to "सिग्नेचर टूल", AppLanguage.ENGLISH to "Signature Tool"),
        "tool_signature_desc" to mapOf(AppLanguage.HINDI to "सफेद बैकग्राउंड और साफ साइन", AppLanguage.ENGLISH to "Clean white bg & sign pad"),
        "tool_crop" to mapOf(AppLanguage.HINDI to "क्रॉप और रोटेट", AppLanguage.ENGLISH to "Crop & Rotate"),
        "tool_crop_desc" to mapOf(AppLanguage.HINDI to "35x45mm व अन्य अनुपातों में क्रॉप", AppLanguage.ENGLISH to "Crop to 35x45mm & ratios"),
        "tool_pdf" to mapOf(AppLanguage.HINDI to "इमेज से PDF", AppLanguage.ENGLISH to "Image to PDF"),
        "tool_pdf_desc" to mapOf(AppLanguage.HINDI to "मल्टीपल पेज PDF बनाएं", AppLanguage.ENGLISH to "Combine images to PDF"),
        "tool_convert" to mapOf(AppLanguage.HINDI to "फॉर्मेट बदलें", AppLanguage.ENGLISH to "Convert Format"),
        "tool_convert_desc" to mapOf(AppLanguage.HINDI to "JPG, PNG, WEBP कन्वर्टर", AppLanguage.ENGLISH to "JPG, PNG, WEBP converter"),
        "tool_batch" to mapOf(AppLanguage.HINDI to "बैच कंप्रेस", AppLanguage.ENGLISH to "Batch Compress"),
        "tool_batch_desc" to mapOf(AppLanguage.HINDI to "एक साथ कई फोटो कंप्रेस करें", AppLanguage.ENGLISH to "Compress multiple images"),

        // How it works
        "how_it_works" to mapOf(AppLanguage.HINDI to "यह कैसे काम करता है?", AppLanguage.ENGLISH to "How It Works"),
        "step_1_title" to mapOf(AppLanguage.HINDI to "1. फोटो चुनो", AppLanguage.ENGLISH to "1. Choose Photo"),
        "step_1_desc" to mapOf(AppLanguage.HINDI to "गैलरी या कैमरे से फोटो/सिग्नेचर चुनें", AppLanguage.ENGLISH to "Select photo or signature from gallery or camera"),
        "step_2_title" to mapOf(AppLanguage.HINDI to "2. एग्जाम साइज सेट करो", AppLanguage.ENGLISH to "2. Set Exam Size"),
        "step_2_desc" to mapOf(AppLanguage.HINDI to "SSC, UPSC या कस्टम KB लक्ष्य चुनें", AppLanguage.ENGLISH to "Select SSC, UPSC or custom target KB"),
        "step_3_title" to mapOf(AppLanguage.HINDI to "3. डाउनलोड व अपलोड", AppLanguage.ENGLISH to "3. Download & Upload"),
        "step_3_desc" to mapOf(AppLanguage.HINDI to "सटीक साइज में सेव करें और फॉर्म पर अपलोड करें", AppLanguage.ENGLISH to "Save exact file size and upload directly to form"),

        // Trust badges
        "trust_private" to mapOf(AppLanguage.HINDI to "100% प्राइवेट", AppLanguage.ENGLISH to "100% Private"),
        "trust_no_upload" to mapOf(AppLanguage.HINDI to "सर्वर पे अपलोड नहीं होता", AppLanguage.ENGLISH to "No Server Upload"),
        "trust_free" to mapOf(AppLanguage.HINDI to "100% फ्री", AppLanguage.ENGLISH to "100% Free"),
        "trust_offline" to mapOf(AppLanguage.HINDI to "ऑफलाइन चलता है", AppLanguage.ENGLISH to "Works 100% Offline"),

        // Editor
        "editor_title" to mapOf(AppLanguage.HINDI to "कंप्रेस व रीसाइज एडिटर", AppLanguage.ENGLISH to "Compress & Resize Editor"),
        "original_image" to mapOf(AppLanguage.HINDI to "मूल फोटो", AppLanguage.ENGLISH to "Original Image"),
        "target_size_kb" to mapOf(AppLanguage.HINDI to "टारगेट साइज (KB)", AppLanguage.ENGLISH to "Target Size (KB)"),
        "enter_target_kb" to mapOf(AppLanguage.HINDI to "KB दर्ज करें (जैसे 50)", AppLanguage.ENGLISH to "Enter KB (e.g. 50)"),
        "dimensions" to mapOf(AppLanguage.HINDI to "आयाम (Dimensions)", AppLanguage.ENGLISH to "Dimensions"),
        "width" to mapOf(AppLanguage.HINDI to "चौड़ाई (Width)", AppLanguage.ENGLISH to "Width"),
        "height" to mapOf(AppLanguage.HINDI to "ऊंचाई (Height)", AppLanguage.ENGLISH to "Height"),
        "lock_aspect" to mapOf(AppLanguage.HINDI to "अनुपात लॉक (Aspect Ratio)", AppLanguage.ENGLISH to "Lock Aspect Ratio"),
        "output_format" to mapOf(AppLanguage.HINDI to "आउटपुट फॉर्मेट", AppLanguage.ENGLISH to "Output Format"),
        "bg_color" to mapOf(AppLanguage.HINDI to "बैकग्राउंड रंग", AppLanguage.ENGLISH to "Background Color"),
        "bg_white" to mapOf(AppLanguage.HINDI to "सफेद (White)", AppLanguage.ENGLISH to "White"),
        "bg_transparent" to mapOf(AppLanguage.HINDI to "पारदर्शी (Transparent)", AppLanguage.ENGLISH to "Transparent"),
        "btn_compress_now" to mapOf(AppLanguage.HINDI to "कंप्रेस करें (Compress Now)", AppLanguage.ENGLISH to "Compress Now"),
        "compressing" to mapOf(AppLanguage.HINDI to "कंप्रेस हो रहा है...", AppLanguage.ENGLISH to "Compressing..."),
        "result_success" to mapOf(AppLanguage.HINDI to "सफलतापूर्वक कंप्रेस हुआ!", AppLanguage.ENGLISH to "Successfully Compressed!"),
        "result_warning_target" to mapOf(
            AppLanguage.HINDI to "टारगेट बहुत छोटा था, सर्वोत्तम गुणवत्ता के साथ निकटतम साइज दिया गया है",
            AppLanguage.ENGLISH to "Target was very small; optimized to best achievable size"
        ),
        "before_after" to mapOf(AppLanguage.HINDI to "पहले vs बाद में (स्लाइड करें)", AppLanguage.ENGLISH to "Before vs After (Drag to Compare)"),
        "btn_save_gallery" to mapOf(AppLanguage.HINDI to "गैलरी में सेव करें", AppLanguage.ENGLISH to "Save to Gallery"),
        "btn_share" to mapOf(AppLanguage.HINDI to "शेयर करें", AppLanguage.ENGLISH to "Share"),
        "btn_change_size" to mapOf(AppLanguage.HINDI to "साइज बदलें", AppLanguage.ENGLISH to "Change Size"),
        "btn_choose_another" to mapOf(AppLanguage.HINDI to "दूसरी फोटो चुनें", AppLanguage.ENGLISH to "Choose Another"),
        "saved_to_gallery" to mapOf(AppLanguage.HINDI to "गैलरी में सेव हो गया!", AppLanguage.ENGLISH to "Saved to Gallery!"),
        "kb_saved" to mapOf(AppLanguage.HINDI to "साइज घटाया गया", AppLanguage.ENGLISH to "Saved"),

        // Signature tool
        "sig_tool_title" to mapOf(AppLanguage.HINDI to "सिग्नेचर टूल", AppLanguage.ENGLISH to "Signature Tool"),
        "sig_tab_clean" to mapOf(AppLanguage.HINDI to "फोटो साफ करें", AppLanguage.ENGLISH to "Clean Photo"),
        "sig_tab_draw" to mapOf(AppLanguage.HINDI to "उंगली से साइन करें", AppLanguage.ENGLISH to "Draw with Finger"),
        "sig_threshold" to mapOf(AppLanguage.HINDI to "बैकग्राउंड सफेदी (White Threshold)", AppLanguage.ENGLISH to "White Background Threshold"),
        "sig_contrast" to mapOf(AppLanguage.HINDI to "स्याही का कंट्रास्ट (Contrast)", AppLanguage.ENGLISH to "Ink Contrast Boost"),
        "sig_autotrim" to mapOf(AppLanguage.HINDI to "खाली मार्जिन हटाएं (Auto Trim)", AppLanguage.ENGLISH to "Auto Trim Empty Margins"),
        "sig_color" to mapOf(AppLanguage.HINDI to "पेन का रंग", AppLanguage.ENGLISH to "Pen Color"),
        "sig_thickness" to mapOf(AppLanguage.HINDI to "पेन की मोटाई", AppLanguage.ENGLISH to "Pen Thickness"),
        "btn_undo" to mapOf(AppLanguage.HINDI to "वापस (Undo)", AppLanguage.ENGLISH to "Undo"),
        "btn_clear" to mapOf(AppLanguage.HINDI to "साफ करें (Clear)", AppLanguage.ENGLISH to "Clear"),
        "btn_export_signature" to mapOf(AppLanguage.HINDI to "सिग्नेचर एक्सपोर्ट करें", AppLanguage.ENGLISH to "Export Signature"),

        // Crop tool
        "crop_title" to mapOf(AppLanguage.HINDI to "क्रॉप व रोटेट", AppLanguage.ENGLISH to "Crop & Rotate"),
        "crop_free" to mapOf(AppLanguage.HINDI to "फ्री", AppLanguage.ENGLISH to "Free"),
        "btn_rotate_left" to mapOf(AppLanguage.HINDI to "बाएं 90°", AppLanguage.ENGLISH to "Left 90°"),
        "btn_rotate_right" to mapOf(AppLanguage.HINDI to "दाएं 90°", AppLanguage.ENGLISH to "Right 90°"),
        "btn_flip_h" to mapOf(AppLanguage.HINDI to "फ्लिप (H)", AppLanguage.ENGLISH to "Flip H"),
        "btn_apply_crop" to mapOf(AppLanguage.HINDI to "क्रॉप लागू करें", AppLanguage.ENGLISH to "Apply Crop"),

        // PDF tool
        "pdf_title" to mapOf(AppLanguage.HINDI to "इमेज से PDF बनाएं", AppLanguage.ENGLISH to "Image to PDF"),
        "btn_add_images" to mapOf(AppLanguage.HINDI to "फोटो जोड़ें", AppLanguage.ENGLISH to "Add Images"),
        "pdf_page_size" to mapOf(AppLanguage.HINDI to "पेज साइज", AppLanguage.ENGLISH to "Page Size"),
        "pdf_margin" to mapOf(AppLanguage.HINDI to "मार्जिन", AppLanguage.ENGLISH to "Margin"),
        "btn_create_pdf" to mapOf(AppLanguage.HINDI to "PDF बनाएं", AppLanguage.ENGLISH to "Generate PDF"),
        "pdf_created" to mapOf(AppLanguage.HINDI to "PDF तैयार है!", AppLanguage.ENGLISH to "PDF Ready!"),

        // Batch tool
        "batch_title" to mapOf(AppLanguage.HINDI to "बैच कंप्रेस (मल्टीपल फोटो)", AppLanguage.ENGLISH to "Batch Compress"),
        "batch_select_hint" to mapOf(AppLanguage.HINDI to "कंप्रेस करने के लिए फोटो चुनें", AppLanguage.ENGLISH to "Select photos to compress"),
        "btn_compress_all" to mapOf(AppLanguage.HINDI to "सभी कंप्रेस करें", AppLanguage.ENGLISH to "Compress All"),
        "btn_save_all" to mapOf(AppLanguage.HINDI to "सभी सेव करें", AppLanguage.ENGLISH to "Save All"),

        // History
        "history_title" to mapOf(AppLanguage.HINDI to "कंप्रेशन इतिहास", AppLanguage.ENGLISH to "Compression History"),
        "history_empty" to mapOf(AppLanguage.HINDI to "अभी कोई इतिहास नहीं है", AppLanguage.ENGLISH to "No compression history yet"),
        "history_empty_sub" to mapOf(
            AppLanguage.HINDI to "कंप्रेस की गई फोटो और दस्तावेज यहां दिखाई देंगे",
            AppLanguage.ENGLISH to "Compressed photos and documents will appear here"
        ),
        "btn_clear_history" to mapOf(AppLanguage.HINDI to "इतिहास हटाएं", AppLanguage.ENGLISH to "Clear History"),
        "confirm_clear_history" to mapOf(
            AppLanguage.HINDI to "क्या आप पूरा इतिहास मिटाना चाहते हैं?",
            AppLanguage.ENGLISH to "Do you want to delete all compression history?"
        ),

        // Settings
        "settings_title" to mapOf(AppLanguage.HINDI to "सेटिंग्स", AppLanguage.ENGLISH to "Settings"),
        "setting_language" to mapOf(AppLanguage.HINDI to "ऐप भाषा (Language)", AppLanguage.ENGLISH to "App Language"),
        "setting_theme" to mapOf(AppLanguage.HINDI to "थीम मोड (Theme)", AppLanguage.ENGLISH to "Theme Mode"),
        "theme_system" to mapOf(AppLanguage.HINDI to "सिस्टम डिफॉल्ट", AppLanguage.ENGLISH to "System Default"),
        "theme_light" to mapOf(AppLanguage.HINDI to "लाइट (Light)", AppLanguage.ENGLISH to "Light"),
        "theme_dark" to mapOf(AppLanguage.HINDI to "डार्क (Dark)", AppLanguage.ENGLISH to "Dark"),
        "setting_privacy" to mapOf(AppLanguage.HINDI to "प्राइवेसी पॉलिसी", AppLanguage.ENGLISH to "Privacy Policy"),
        "privacy_desc" to mapOf(
            AppLanguage.HINDI to "आपकी कोई भी फोटो या डेटा कभी भी डिवाइस से बाहर नहीं जाता। 100% ऑफलाइन और सुरक्षित।",
            AppLanguage.ENGLISH to "None of your photos or data ever leave your device. 100% offline and secure."
        ),
        "setting_about" to mapOf(AppLanguage.HINDI to "ऐप के बारे में", AppLanguage.ENGLISH to "About App"),
        "about_desc" to mapOf(
            AppLanguage.HINDI to "PhotoSize Pro v1.0.0 — भारतीय सरकारी, बैंक और परीक्षा फॉर्म्स के लिए विशेष रूप से डिज़ाइन किया गया।",
            AppLanguage.ENGLISH to "PhotoSize Pro v1.0.0 — Crafted specifically for Indian government, bank and exam forms."
        ),
        "admob_info" to mapOf(
            AppLanguage.HINDI to "विज्ञापन सूचना (AdMob)",
            AppLanguage.ENGLISH to "Advertisement Notice (AdMob)"
        ),
        "admob_desc" to mapOf(
            AppLanguage.HINDI to "वर्तमान में Google ऑफिशियल टेस्ट Ad Unit IDs सक्रिय हैं।",
            AppLanguage.ENGLISH to "Official Google Test Ad Unit IDs are currently active."
        ),

        // Onboarding
        "onb_slide1_title" to mapOf(AppLanguage.HINDI to "फोटो व साइन चुनें", AppLanguage.ENGLISH to "Pick Photo or Sign"),
        "onb_slide1_desc" to mapOf(
            AppLanguage.HINDI to "गैलरी से किसी भी साइज की फोटो या सिग्नेचर चुनें या कैमरे से फोटो खींचें।",
            AppLanguage.ENGLISH to "Pick any photo or signature from gallery or snap with camera."
        ),
        "onb_slide2_title" to mapOf(AppLanguage.HINDI to "एग्जाम का साइज सेट करें", AppLanguage.ENGLISH to "Set Exam Requirements"),
        "onb_slide2_desc" to mapOf(
            AppLanguage.HINDI to "SSC, UPSC, रेलवे, बैंक या कस्टम KB और डाइमेंशन एक क्लिक में चुनें।",
            AppLanguage.ENGLISH to "Select SSC, UPSC, Railway, Bank or custom KB and dimensions in 1 tap."
        ),
        "onb_slide3_title" to mapOf(AppLanguage.HINDI to "डाउनलोड कर फॉर्म भरें", AppLanguage.ENGLISH to "Download & Upload"),
        "onb_slide3_desc" to mapOf(
            AppLanguage.HINDI to "सटीक फाइल साइज पाएं जो फॉर्म पोर्टल पर बिना किसी रिजेक्शन के तुरंत अपलोड होगी।",
            AppLanguage.ENGLISH to "Get the exact file size that uploads instantly without any rejection."
        ),
        "btn_skip" to mapOf(AppLanguage.HINDI to "छोड़ें (Skip)", AppLanguage.ENGLISH to "Skip"),
        "btn_next" to mapOf(AppLanguage.HINDI to "आगे (Next)", AppLanguage.ENGLISH to "Next"),
        "btn_get_started" to mapOf(AppLanguage.HINDI to "शुरू करें", AppLanguage.ENGLISH to "Get Started"),
        "btn_cancel" to mapOf(AppLanguage.HINDI to "रद्द करें", AppLanguage.ENGLISH to "Cancel"),
        "btn_confirm" to mapOf(AppLanguage.HINDI to "पुष्टि करें", AppLanguage.ENGLISH to "Confirm"),
        "delete" to mapOf(AppLanguage.HINDI to "हटाएं", AppLanguage.ENGLISH to "Delete")
    )
}
