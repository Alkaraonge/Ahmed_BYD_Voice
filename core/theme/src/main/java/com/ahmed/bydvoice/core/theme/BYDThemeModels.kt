package com.ahmed.bydvoice.core.theme

import androidx.compose.ui.graphics.Color

/**
 * تصنيفات الثيمات الستة — ترتيب العرض في اللوحة يتبعها.
 */
enum class ThemeCategory(val labelAr: String) {
    FUTURISTIC("مستقبلي"),
    LUXURY("فخامة"),
    SPORT("رياضي"),
    ARABIC_HERITAGE("تراث عربي"),
    NATURE_SPACE("طبيعة وفضاء"),
    TACTICAL_MINIMAL("تكتيكي هادئ"),
}

/**
 * أسلوب السطح: يحدد كيف يرسم bydCardSurface البطاقات.
 * GLASSMORPHIC يستخدم alpha 0xCC لأن DiLink 3 (Android 9/10) ما يدعم blur حقيقي.
 */
enum class SurfaceStyle {
    SOLID,
    GLASSMORPHIC,
    CARBON_FIBER,
    NEON_WIREFRAME,
    BRUSHED_METAL,
}

/**
 * معرّفات الثيمات الـ50. الثيم الافتراضي/الاحتياطي = MIDNIGHT_GLASS.
 */
enum class BYDThemeId {
    // الثيمات الـ 3 الأساسية
    CYBERPUNK_NEON,      // 1. سايبر بانك نيون
    MIDNIGHT_GLASS,      // 2. الفخامة الزجاجية (الافتراضي)
    CARBON_SPORT,        // 3. الكاربون الرياضي

    // الثيمات 4 - 23
    DESERT_DUNES,        // 4. كثبان الصحراء
    BLUE_TRACK,          // 5. حلبة السباق الزرقاء
    STEALTH_RADAR,       // 6. المقاتلة الشبح
    SCUDERIA_F1,         // 7. فورمولا 1 سكويريا
    FOREST_ECO,          // 8. الأخضر البيئي
    AURORA_BOREALIS,     // 9. الشفق القطبي
    DEEP_OCEAN,          // 10. أعماق المحيط
    VOLCANIC_MAGMA,      // 11. الماغما البركانية
    QUANTUM_MATRIX,      // 12. المصفوفة الكمية
    SYNTHWAVE_80S,       // 13. سينث ويف الثمانينات
    SOLAR_FLARE,         // 14. التوهج الشمسي
    ICE_GLACIER,         // 15. الجليد القطبي
    ROYAL_GOLD,          // 16. الملكي الإمبراطوري
    VINTAGE_LUXURY,      // 17. الكلاسيكي الفخم
    TITANIUM_PLATINUM,   // 18. التيتانيوم والبلاتين
    VELVET_BURGUNDY,     // 19. المخملي العنابي
    NIGHT_VISION_HUD,    // 20. الرؤية الليلية HUD
    MONOCHROME_NOIR,     // 21. أبيض وأسود نوار
    DESERT_MIRAGE,       // 22. سراب الصحراء
    NEBULA_SPACE,        // 23. سديم الفضاء

    // الثيمات 24 - 50
    MARS_ROVER,          // 24. المستكشف المريخي
    DEEP_SPACE,          // 25. الفضاء السحيق
    MESOPOTAMIAN_AZURE,  // 26. اللازورد الرافديني
    TOKYO_DRIFT,         // 27. طوكيو نيون
    BRITISH_RACING,      // 28. الأخضر البريطاني العريق
    CHAMPAGNE_SILK,      // 29. الحرير الشامباني
    COPPER_STEAMPUNK,    // 30. النحاس الصناعي
    CYBER_PULSE,         // 31. النبض الرقمي
    RETRO_8BIT,          // 32. آركيد ريترو 8-بت
    PLATINUM_CHRONO,     // 33. البلاتين الرياضي
    STORM_CHASER,        // 34. عاصفة الرعد
    EMERALD_ROYAL,       // 35. الزمرد الملكي
    SAFARI_OVERLAND,     // 36. سفاري المغامرة
    CARBON_BLUE,         // 37. الكاربون الأزرق
    POLAR_NIGHT,         // 38. الليل القطبي
    AMETHYST_CRYSTAL,    // 39. الجمشت البنفسجي
    CARBON_GOLD,         // 40. الكاربون الذهبي
    ECLIPSE_SHADOW,      // 41. الكسوف المظلم
    MECHA_GUNDAM,        // 42. الميكا المستقبلية
    SANDSTORM_GOLD,      // 43. العاصفة الرملية الذهبية
    PACIFIC_TURQUOISE,   // 44. تيركوز المحيط
    LIQUID_MERCURY,      // 45. الزئبق السائل
    GRAPHITE_ORANGE,     // 46. الغرافيت البرتقالي
    OBSIDIAN_SAPPHIRE,   // 47. الأوبسيديان الياقوتي
    RED_LINE_DRIFT,      // 48. الخط الأحمر دريفت
    ZEN_MINIMALIST,      // 49. الزن الهادئ
    HYPERDRIVE_WARP,     // 50. هايبر درايف
}

/**
 * مخطط ألوان ثيم كامل — 14 توكن.
 * كل الألوان مسطحة (Color) بدون أي اعتماد على الثيمات القديمة.
 */
data class BYDColorScheme(
    val id: BYDThemeId,
    val nameAr: String,
    val nameEn: String,
    val description: String,
    val category: ThemeCategory,
    val surfaceStyle: SurfaceStyle,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val borderGlow: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val glowColor: Color,
)
