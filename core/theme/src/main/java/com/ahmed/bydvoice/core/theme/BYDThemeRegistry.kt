package com.ahmed.bydvoice.core.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import java.util.EnumMap

object BYDThemeRegistry {

    private val darkText = Color(0xFF07090E)
    private val lightText = Color(0xFFFFFFFF)

    /**
     * يختار onPrimary الأنسب (أبيض أو داكن) حسب سطوع لون primary،
     * ويستخدم سطوع darkText الفعلي لا Color.Black.
     */
    fun bestOnColor(primary: Color): Color {
        val l = primary.luminance()
        val whiteContrast = (lightText.luminance() + 0.05f) / (l + 0.05f)
        val darkContrast = (l + 0.05f) / (darkText.luminance() + 0.05f)
        return if (whiteContrast >= darkContrast) lightText else darkText
    }

    val allList: List<BYDColorScheme> = listOf(
        // ==================== 1 - 10 ====================
        BYDColorScheme(
            id = BYDThemeId.CYBERPUNK_NEON, nameAr = "سايبر بانك نيون", nameEn = "Cyberpunk Neon",
            description = "أجواء مستقبلية متوهجة بالسيان والبنفسجي النيوني",
            category = ThemeCategory.FUTURISTIC, surfaceStyle = SurfaceStyle.NEON_WIREFRAME,
            background = Color(0xFF05050C), surface = Color(0xFF0E0E1C), surfaceVariant = Color(0xFF171730),
            borderGlow = Color(0xFF00F0FF), primary = Color(0xFF00F0FF), onPrimary = darkText,
            secondary = Color(0xFFB500FF), accent = Color(0xFFFF007F),
            textPrimary = Color(0xFFFFFFFF), textSecondary = Color(0xFFA0A0C2),
            glowColor = Color(0x4D00F0FF)
        ),
        BYDColorScheme(
            id = BYDThemeId.MIDNIGHT_GLASS, nameAr = "الفخامة الزجاجية", nameEn = "Midnight Glass",
            description = "ألواح زجاجية بلورية مع أزرق ياقوتي وذهب شامباني",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF070B14), surface = Color(0xCC132036), surfaceVariant = Color(0xD91B2D4D),
            borderGlow = Color(0x883B82F6), primary = Color(0xFF60A5FA), onPrimary = darkText,
            secondary = Color(0xFFD4AF37), accent = Color(0xFFF59E0B),
            textPrimary = Color(0xFFF8FAFC), textSecondary = Color(0xFF9FB0C7),
            glowColor = Color(0x333B82F6)
        ),
        BYDColorScheme(
            id = BYDThemeId.CARBON_SPORT, nameAr = "الكاربون الرياضي", nameEn = "Carbon Sport",
            description = "ألياف كاربون داكنة مع أحمر قرمزي وبرتقالي ناري",
            category = ThemeCategory.SPORT, surfaceStyle = SurfaceStyle.CARBON_FIBER,
            background = Color(0xFF0B0B0D), surface = Color(0xFF151518), surfaceVariant = Color(0xFF212126),
            borderGlow = Color(0xFFE50914), primary = Color(0xFFE50914), onPrimary = lightText,
            secondary = Color(0xFFFF6B00), accent = Color(0xFFFFB800),
            textPrimary = Color(0xFFFFFFFF), textSecondary = Color(0xFFA8A8B3),
            glowColor = Color(0x4DE50914)
        ),
        BYDColorScheme(
            id = BYDThemeId.DESERT_DUNES, nameAr = "كثبان الصحراء", nameEn = "Desert Dunes",
            description = "ألوان رملية دافئة مع برتقالي الغروب وذهب البادية",
            category = ThemeCategory.ARABIC_HERITAGE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF140E09), surface = Color(0xFF241A12), surfaceVariant = Color(0xFF33251A),
            borderGlow = Color(0xFFE07A1D), primary = Color(0xFFF2994A), onPrimary = darkText,
            secondary = Color(0xFFD4A373), accent = Color(0xFFFFB236),
            textPrimary = Color(0xFFFFF6EE), textSecondary = Color(0xFFBAA592),
            glowColor = Color(0x3DE07A1D)
        ),
        BYDColorScheme(
            id = BYDThemeId.BLUE_TRACK, nameAr = "حلبة السباق الزرقاء", nameEn = "Blue Track Racing",
            description = "أزرق حلبات السباق الكهربائية المتوهج مع أبيض رياضي",
            category = ThemeCategory.SPORT, surfaceStyle = SurfaceStyle.CARBON_FIBER,
            background = Color(0xFF060D1A), surface = Color(0xFF0F1B33), surfaceVariant = Color(0xFF17284C),
            borderGlow = Color(0xFF2F80ED), primary = Color(0xFF38A1F3), onPrimary = darkText,
            secondary = Color(0xFF56CCF2), accent = Color(0xFF00FFCC),
            textPrimary = Color(0xFFEDF5FF), textSecondary = Color(0xFF93AACB),
            glowColor = Color(0x4D2F80ED)
        ),
        BYDColorScheme(
            id = BYDThemeId.STEALTH_RADAR, nameAr = "المقاتلة الشبح", nameEn = "Stealth Radar",
            description = "رمادي عسكري داكن مع أخضر رادار فسفوري وطابع تكتيكي",
            category = ThemeCategory.TACTICAL_MINIMAL, surfaceStyle = SurfaceStyle.NEON_WIREFRAME,
            background = Color(0xFF0A0E0F), surface = Color(0xFF131A1D), surfaceVariant = Color(0xFF1D272B),
            borderGlow = Color(0xFF00FF66), primary = Color(0xFF00E676), onPrimary = darkText,
            secondary = Color(0xFF1DE9B6), accent = Color(0xFF76FF03),
            textPrimary = Color(0xFFECEFF1), textSecondary = Color(0xFF90A8B3),
            glowColor = Color(0x3D00FF66)
        ),
        // ✱ التعديل 1: onPrimary من lightText → darkText (5.18:1)
        BYDColorScheme(
            id = BYDThemeId.SCUDERIA_F1, nameAr = "فورمولا 1 سكويريا", nameEn = "Scuderia F1",
            description = "أحمر سباقات ناري مع أصفر الحلبة وألياف كربونية",
            category = ThemeCategory.SPORT, surfaceStyle = SurfaceStyle.CARBON_FIBER,
            background = Color(0xFF0A0102), surface = Color(0xFF1D0408), surfaceVariant = Color(0xFF2E070D),
            borderGlow = Color(0xFFFF1744), primary = Color(0xFFFF1744), onPrimary = darkText,
            secondary = Color(0xFFFFEA00), accent = Color(0xFFFFFFFF),
            textPrimary = Color(0xFFFFF0F2), textSecondary = Color(0xFFC4929A),
            glowColor = Color(0x4DFF1744)
        ),
        BYDColorScheme(
            id = BYDThemeId.FOREST_ECO, nameAr = "الأخضر البيئي", nameEn = "Forest Eco",
            description = "ألوان الطبيعة الخضراء المخصصة للمركبات الكهربائية النظيفة",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF050E09), surface = Color(0xFF0B1E14), surfaceVariant = Color(0xFF122E1F),
            borderGlow = Color(0xFF2ECC71), primary = Color(0xFF2ECC71), onPrimary = darkText,
            secondary = Color(0xFF26DE81), accent = Color(0xFFA5D6A7),
            textPrimary = Color(0xFFE8F5E9), textSecondary = Color(0xFF93CFA0),
            glowColor = Color(0x3D2ECC71)
        ),
        BYDColorScheme(
            id = BYDThemeId.AURORA_BOREALIS, nameAr = "الشفق القطبي", nameEn = "Aurora Borealis",
            description = "موجات زمردية متداخلة مع الأرجواني الشمالي الساحر",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF040612), surface = Color(0xCC0B132B), surfaceVariant = Color(0xD9131F42),
            borderGlow = Color(0xFF00FFB2), primary = Color(0xFF00E5FF), onPrimary = darkText,
            secondary = Color(0xFFD500F9), accent = Color(0xFF00FF88),
            textPrimary = Color(0xFFE6FAFC), textSecondary = Color(0xFF94ABB8),
            glowColor = Color(0x4D00FFB2)
        ),
        BYDColorScheme(
            id = BYDThemeId.DEEP_OCEAN, nameAr = "أعماق المحيط", nameEn = "Deep Ocean",
            description = "أزرق بحري سحيق مع إضاءة مائية مريحة للعين",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF020712), surface = Color(0xCC07142C), surfaceVariant = Color(0xD90D2145),
            borderGlow = Color(0xFF00D2FF), primary = Color(0xFF00B4FF), onPrimary = darkText,
            secondary = Color(0xFF00E5FF), accent = Color(0xFF26C6DA),
            textPrimary = Color(0xFFE5F6FA), textSecondary = Color(0xFF8AA8BA),
            glowColor = Color(0x4D00D2FF)
        ),

        // ==================== 11 - 20 ====================
        BYDColorScheme(
            id = BYDThemeId.VOLCANIC_MAGMA, nameAr = "الماغما البركانية", nameEn = "Volcanic Magma",
            description = "حمم منصهرة بين الصخور السوداء وتوهج برتقالي ناري",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF0D0502), surface = Color(0xFF1C0D07), surfaceVariant = Color(0xFF2C150B),
            borderGlow = Color(0xFFFF3D00), primary = Color(0xFFFF5722), onPrimary = darkText,
            secondary = Color(0xFFFF9100), accent = Color(0xFFFFEA00),
            textPrimary = Color(0xFFFFEBE5), textSecondary = Color(0xFFC79D8E),
            glowColor = Color(0x4DFF3D00)
        ),
        BYDColorScheme(
            id = BYDThemeId.QUANTUM_MATRIX, nameAr = "المصفوفة الكمية", nameEn = "Quantum Matrix",
            description = "أخضر رقمي مشفر مستوحى من عالم المبرمجين والماتريكس",
            category = ThemeCategory.FUTURISTIC, surfaceStyle = SurfaceStyle.NEON_WIREFRAME,
            background = Color(0xFF020803), surface = Color(0xFF071609), surfaceVariant = Color(0xFF0E2612),
            borderGlow = Color(0xFF00FF41), primary = Color(0xFF00FF41), onPrimary = darkText,
            secondary = Color(0xFF00DD30), accent = Color(0xFF69FF82),
            textPrimary = Color(0xFFDBFFDF), textSecondary = Color(0xFF9AE0A0),
            glowColor = Color(0x5200FF41)
        ),
        // ✱ التعديل 2: onPrimary من lightText → darkText (5.71:1)
        BYDColorScheme(
            id = BYDThemeId.SYNTHWAVE_80S, nameAr = "سينث ويف الثمانينات", nameEn = "Synthwave 80s",
            description = "وردي نيون وأرجواني وأزرق ريترو متوهج",
            category = ThemeCategory.FUTURISTIC, surfaceStyle = SurfaceStyle.NEON_WIREFRAME,
            background = Color(0xFF0B0314), surface = Color(0xFF180A28), surfaceVariant = Color(0xFF25103D),
            borderGlow = Color(0xFFFF007F), primary = Color(0xFFFF2A95), onPrimary = darkText,
            secondary = Color(0xFF00F0FF), accent = Color(0xFFE040FB),
            textPrimary = Color(0xFFFFEBF6), textSecondary = Color(0xFFC4A6D1),
            glowColor = Color(0x4DFF2A95)
        ),
        BYDColorScheme(
            id = BYDThemeId.SOLAR_FLARE, nameAr = "التوهج الشمسي", nameEn = "Solar Flare",
            description = "طاقة شمسية صفراء وبرتقالية مع أسود كوني",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF0D0701), surface = Color(0xFF1C1103), surfaceVariant = Color(0xFF2B1B07),
            borderGlow = Color(0xFFFF9100), primary = Color(0xFFFFAB00), onPrimary = darkText,
            secondary = Color(0xFFFF6D00), accent = Color(0xFFFFD600),
            textPrimary = Color(0xFFFFF8E1), textSecondary = Color(0xFFC9B5A8),
            glowColor = Color(0x4DFF9100)
        ),
        BYDColorScheme(
            id = BYDThemeId.ICE_GLACIER, nameAr = "الجليد القطبي", nameEn = "Ice Glacier",
            description = "أزرق جليدي ناصع مع درجات البياض القطبي الصافي",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF070E14), surface = Color(0xCC0F1E2E), surfaceVariant = Color(0xD9172E45),
            borderGlow = Color(0xFF80D8FF), primary = Color(0xFF40C4FF), onPrimary = darkText,
            secondary = Color(0xFF00B0FF), accent = Color(0xFFE1F5FE),
            textPrimary = Color(0xFFF0F8FF), textSecondary = Color(0xFF98B4CC),
            glowColor = Color(0x3D80D8FF)
        ),
        BYDColorScheme(
            id = BYDThemeId.ROYAL_GOLD, nameAr = "الملكي الإمبراطوري", nameEn = "Royal Gold",
            description = "أسود مخملي مطعم بالذهب الخالص عيار 24",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.BRUSHED_METAL,
            background = Color(0xFF0A0804), surface = Color(0xFF151209), surfaceVariant = Color(0xFF221D10),
            borderGlow = Color(0xFFD4AF37), primary = Color(0xFFD4AF37), onPrimary = darkText,
            secondary = Color(0xFFE5C158), accent = Color(0xFFF3E5AB),
            textPrimary = Color(0xFFFFF9E6), textSecondary = Color(0xFFBAA980),
            glowColor = Color(0x3DD4AF37)
        ),
        BYDColorScheme(
            id = BYDThemeId.VINTAGE_LUXURY, nameAr = "الكلاسيكي الفخم", nameEn = "Vintage Luxury",
            description = "أخشاب الجوز والجلد البني الملكي مع نحاس عتيق",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF120C06), surface = Color(0xFF22150D), surfaceVariant = Color(0xFF332015),
            borderGlow = Color(0xFFB57C42), primary = Color(0xFFD48C46), onPrimary = darkText,
            secondary = Color(0xFFD2B48C), accent = Color(0xFFF4A460),
            textPrimary = Color(0xFFFDF5E6), textSecondary = Color(0xFFB89E8B),
            glowColor = Color(0x33CD853F)
        ),
        BYDColorScheme(
            id = BYDThemeId.TITANIUM_PLATINUM, nameAr = "التيتانيوم والبلاتين", nameEn = "Titanium Platinum",
            description = "معدن فضي مصقول بأسلوب تقني صناعي فخم",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.BRUSHED_METAL,
            background = Color(0xFF0F1115), surface = Color(0xFF1A1D24), surfaceVariant = Color(0xFF252A34),
            borderGlow = Color(0xFFB0BEC5), primary = Color(0xFFECEFF1), onPrimary = darkText,
            secondary = Color(0xFFCFD8DC), accent = Color(0xFF90CAF9),
            textPrimary = Color(0xFFFFFFFF), textSecondary = Color(0xFF98A9B5),
            glowColor = Color(0x2EECEFF1)
        ),
        BYDColorScheme(
            id = BYDThemeId.VELVET_BURGUNDY, nameAr = "المخملي العنابي", nameEn = "Velvet Burgundy",
            description = "أحمر عنابي مخملي داكن بلمسة راقية",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF0F0205), surface = Color(0xFF21060C), surfaceVariant = Color(0xFF330A14),
            borderGlow = Color(0xFFC2185B), primary = Color(0xFFD81B60), onPrimary = lightText,
            secondary = Color(0xFFF06292), accent = Color(0xFFFF80AB),
            textPrimary = Color(0xFFFFF0F3), textSecondary = Color(0xFFC7949E),
            glowColor = Color(0x3DD81B60)
        ),
        BYDColorScheme(
            id = BYDThemeId.NIGHT_VISION_HUD, nameAr = "الرؤية الليلية HUD", nameEn = "Night Vision HUD",
            description = "أخضر فوسفوري هادئ لحماية العين في الطرق المظلمة",
            category = ThemeCategory.TACTICAL_MINIMAL, surfaceStyle = SurfaceStyle.NEON_WIREFRAME,
            background = Color(0xFF020603), surface = Color(0xFF061409), surfaceVariant = Color(0xFF0C2412),
            borderGlow = Color(0xFF00E676), primary = Color(0xFF00E676), onPrimary = darkText,
            secondary = Color(0xFF69F0AE), accent = Color(0xFFB9F6CA),
            textPrimary = Color(0xFFE8F8EE), textSecondary = Color(0xFF9BC8A8),
            glowColor = Color(0x4D00FF66)
        ),

        // ==================== 21 - 30 ====================
        BYDColorScheme(
            id = BYDThemeId.MONOCHROME_NOIR, nameAr = "أبيض وأسود نوار", nameEn = "Monochrome Noir",
            description = "تباين سينمائي كلاسيكي نقي بدون ألوان مشتتة",
            category = ThemeCategory.TACTICAL_MINIMAL, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF050505), surface = Color(0xFF121212), surfaceVariant = Color(0xFF1F1F1F),
            borderGlow = Color(0xFFFFFFFF), primary = Color(0xFFFFFFFF), onPrimary = darkText,
            secondary = Color(0xFFE0E0E0), accent = Color(0xFFBDBDBD),
            textPrimary = Color(0xFFFFFFFF), textSecondary = Color(0xFFA3A3A3),
            glowColor = Color(0x33FFFFFF)
        ),
        BYDColorScheme(
            id = BYDThemeId.DESERT_MIRAGE, nameAr = "سراب البادية", nameEn = "Desert Mirage",
            description = "ألوان رمال الرافدين الهادئة مع لمسات ذهبية شمسية",
            category = ThemeCategory.ARABIC_HERITAGE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF100D09), surface = Color(0xFF1F1912), surfaceVariant = Color(0xFF2E251B),
            borderGlow = Color(0xFFD4A373), primary = Color(0xFFE0A96D), onPrimary = darkText,
            secondary = Color(0xFFC48B57), accent = Color(0xFFF3D5B5),
            textPrimary = Color(0xFFFEFAE0), textSecondary = Color(0xFFC2AB93),
            glowColor = Color(0x2ED4A373)
        ),
        BYDColorScheme(
            id = BYDThemeId.NEBULA_SPACE, nameAr = "سديم الفضاء", nameEn = "Nebula Space",
            description = "أرجواني ووردي وأزرق مستوحى من مجرات الفضاء العميق",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF05020D), surface = Color(0xCC120826), surfaceVariant = Color(0xD91E0E3D),
            borderGlow = Color(0xFF9C4DFF), primary = Color(0xFFBA68C8), onPrimary = darkText,
            secondary = Color(0xFFE040FB), accent = Color(0xFF40C4FF),
            textPrimary = Color(0xFFF5ECFF), textSecondary = Color(0xFFAF97CC),
            glowColor = Color(0x4D8A2BE2)
        ),
        BYDColorScheme(
            id = BYDThemeId.MARS_ROVER, nameAr = "المستكشف المريخي", nameEn = "Mars Rover",
            description = "أحمر كوكب المريخ الصخري مع نحاس فضائي متوهج",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.BRUSHED_METAL,
            background = Color(0xFF140704), surface = Color(0xFF261009), surfaceVariant = Color(0xFF38180E),
            borderGlow = Color(0xFFE64A19), primary = Color(0xFFFF6E40), onPrimary = darkText,
            secondary = Color(0xFFFF8A65), accent = Color(0xFFFFAB40),
            textPrimary = Color(0xFFFFEBE6), textSecondary = Color(0xFFC7A095),
            glowColor = Color(0x4DD84315)
        ),
        BYDColorScheme(
            id = BYDThemeId.DEEP_SPACE, nameAr = "الفضاء السحيق", nameEn = "Deep Space",
            description = "أسود كوني عميق مع نجوم زرقاء وإضاءة هادئة",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF020206), surface = Color(0xFF0A0A16), surfaceVariant = Color(0xFF131329),
            borderGlow = Color(0xFF536DFE), primary = Color(0xFF7986CB), onPrimary = darkText,
            secondary = Color(0xFF82B1FF), accent = Color(0xFF448AFF),
            textPrimary = Color(0xFFECEFF1), textSecondary = Color(0xFF94A6B8),
            glowColor = Color(0x3D3F51B5)
        ),
        BYDColorScheme(
            id = BYDThemeId.MESOPOTAMIAN_AZURE, nameAr = "اللازورد الرافديني", nameEn = "Mesopotamian Azure",
            description = "أزرق لازوردي ملكي مستوحى من بوابة عشتار وذهب بابل",
            category = ThemeCategory.ARABIC_HERITAGE, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF040A1E), surface = Color(0xCC0A1740), surfaceVariant = Color(0xD912255E),
            borderGlow = Color(0xFF00D2FF), primary = Color(0xFF00D2FF), onPrimary = darkText,
            secondary = Color(0xFFD4AF37), accent = Color(0xFFFFD700),
            textPrimary = Color(0xFFF0F4FF), textSecondary = Color(0xFF9DB0D9),
            glowColor = Color(0x4D00D2FF)
        ),
        // ✱ التعديل 3: onPrimary من lightText → darkText (5.51:1)
        BYDColorScheme(
            id = BYDThemeId.TOKYO_DRIFT, nameAr = "طوكيو نيون", nameEn = "Tokyo Drift",
            description = "أرجواني نيوني ووردي ليزري مستوحى من شوارع طوكيو الليلية",
            category = ThemeCategory.SPORT, surfaceStyle = SurfaceStyle.NEON_WIREFRAME,
            background = Color(0xFF07020E), surface = Color(0xFF140626), surfaceVariant = Color(0xFF230B42),
            borderGlow = Color(0xFFE040FB), primary = Color(0xFFFF1E8A), onPrimary = darkText,
            secondary = Color(0xFF00E5FF), accent = Color(0xFFD500F9),
            textPrimary = Color(0xFFFFF0FA), textSecondary = Color(0xFFBA93BF),
            glowColor = Color(0x4DE040FB)
        ),
        BYDColorScheme(
            id = BYDThemeId.BRITISH_RACING, nameAr = "الأخضر البريطاني العريق", nameEn = "British Racing Green",
            description = "أخضر سباقات بريطاني عريق مع ذهب كلاسيكي وعاجي",
            category = ThemeCategory.SPORT, surfaceStyle = SurfaceStyle.BRUSHED_METAL,
            background = Color(0xFF031209), surface = Color(0xFF082414), surfaceVariant = Color(0xFF0F3820),
            borderGlow = Color(0xFF00C853), primary = Color(0xFF26A69A), onPrimary = darkText,
            secondary = Color(0xFFD4AF37), accent = Color(0xFFC0CA33),
            textPrimary = Color(0xFFF1F8E9), textSecondary = Color(0xFF9AC284),
            glowColor = Color(0x3D00C853)
        ),
        BYDColorScheme(
            id = BYDThemeId.CHAMPAGNE_SILK, nameAr = "الحرير الشامباني", nameEn = "Champagne Silk",
            description = "فخامة هادئة بدرجات الشامباني والذهب الحريري الناعم",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF100D09), surface = Color(0xCC1E1812), surfaceVariant = Color(0xD92E241B),
            borderGlow = Color(0xFFD4AF37), primary = Color(0xFFE5C158), onPrimary = darkText,
            secondary = Color(0xFFF3E5AB), accent = Color(0xFFFFE082),
            textPrimary = Color(0xFFFFF8E7), textSecondary = Color(0xFFB8A88F),
            glowColor = Color(0x2ED4AF37)
        ),
        BYDColorScheme(
            id = BYDThemeId.COPPER_STEAMPUNK, nameAr = "النحاس الصناعي", nameEn = "Copper Industrial",
            description = "نحاس وبرونز معتق مع طابع صناعي متين",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.BRUSHED_METAL,
            background = Color(0xFF110B07), surface = Color(0xFF22150D), surfaceVariant = Color(0xFF332014),
            borderGlow = Color(0xFFC87D3A), primary = Color(0xFFE08E45), onPrimary = darkText,
            secondary = Color(0xFFD2691E), accent = Color(0xFFFFB066),
            textPrimary = Color(0xFFFFF3EB), textSecondary = Color(0xFFB89986),
            glowColor = Color(0x3DB87333)
        ),

        // ==================== 31 - 40 ====================
        BYDColorScheme(
            id = BYDThemeId.CYBER_PULSE, nameAr = "النبض الرقمي", nameEn = "Cyber Pulse",
            description = "أشعة بنفسجية وضوء ليزر مع خطوط نبضات سيان",
            category = ThemeCategory.FUTURISTIC, surfaceStyle = SurfaceStyle.NEON_WIREFRAME,
            background = Color(0xFF06020E), surface = Color(0xFF110626), surfaceVariant = Color(0xFF1E0B42),
            borderGlow = Color(0xFF9B4DFF), primary = Color(0xFFE040FB), onPrimary = darkText,
            secondary = Color(0xFF00FFFF), accent = Color(0xFF7C4DFF),
            textPrimary = Color(0xFFF8F0FF), textSecondary = Color(0xFFB396C4),
            glowColor = Color(0x4D8A2BE2)
        ),
        BYDColorScheme(
            id = BYDThemeId.RETRO_8BIT, nameAr = "آركيد ريترو 8-بت", nameEn = "Retro 8-Bit Arcade",
            description = "ألوان ألعاب الآركيد الكلاسيكية بالأصفر والسيان والأحمر",
            category = ThemeCategory.FUTURISTIC, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF04040D), surface = Color(0xFF0D0D24), surfaceVariant = Color(0xFF18183D),
            borderGlow = Color(0xFFFFD600), primary = Color(0xFFFFD600), onPrimary = darkText,
            secondary = Color(0xFF00E5FF), accent = Color(0xFFFF1744),
            textPrimary = Color(0xFFFFFFFF), textSecondary = Color(0xFF9494CC),
            glowColor = Color(0x4DFFD600)
        ),
        BYDColorScheme(
            id = BYDThemeId.PLATINUM_CHRONO, nameAr = "البلاتين الرياضي", nameEn = "Platinum Chrono",
            description = "مستوحى من دقة ساعات الكرونوغراف السويسرية والبلاتين والكوبالت",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.BRUSHED_METAL,
            background = Color(0xFF0B0D12), surface = Color(0xFF161A24), surfaceVariant = Color(0xFF212736),
            borderGlow = Color(0xFF64B5F6), primary = Color(0xFFCFD8DC), onPrimary = darkText,
            secondary = Color(0xFF448AFF), accent = Color(0xFF00E5FF),
            textPrimary = Color(0xFFECEFF1), textSecondary = Color(0xFF93A7B5),
            glowColor = Color(0x33448AFF)
        ),
        BYDColorScheme(
            id = BYDThemeId.STORM_CHASER, nameAr = "عاصفة الرعد", nameEn = "Storm Chaser",
            description = "سماء رعدية مظلمة مع ومضات برق سيان وكوبالت متوهجة",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF080C12), surface = Color(0xFF101926), surfaceVariant = Color(0xFF182438),
            borderGlow = Color(0xFF00E5FF), primary = Color(0xFF29B6F6), onPrimary = darkText,
            secondary = Color(0xFF40C4FF), accent = Color(0xFFB3E5FC),
            textPrimary = Color(0xFFE1F5FE), textSecondary = Color(0xFF8CA6C2),
            glowColor = Color(0x4D00E5FF)
        ),
        BYDColorScheme(
            id = BYDThemeId.EMERALD_ROYAL, nameAr = "الزمرد الملكي", nameEn = "Emerald Royal",
            description = "أخضر زمردي عميق وفخم مع زخارف ذهبية ناعمة",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF030C06), surface = Color(0xCC081E11), surfaceVariant = Color(0xD90F2E1C),
            borderGlow = Color(0xFF00E676), primary = Color(0xFF00D66C), onPrimary = darkText,
            secondary = Color(0xFFD4AF37), accent = Color(0xFFB9F6CA),
            textPrimary = Color(0xFFE8F8EE), textSecondary = Color(0xFF8BB59A),
            glowColor = Color(0x3D00E676)
        ),
        BYDColorScheme(
            id = BYDThemeId.SAFARI_OVERLAND, nameAr = "سفاري المغامرة", nameEn = "Safari Overland",
            description = "خاكي رملي وزيتوني بري لعشاق الطرق الوعرة والمغامرات",
            category = ThemeCategory.ARABIC_HERITAGE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF0E0D09), surface = Color(0xFF1C1A12), surfaceVariant = Color(0xFF2A271C),
            borderGlow = Color(0xFFAFB42B), primary = Color(0xFFC0CA33), onPrimary = darkText,
            secondary = Color(0xFFA1887F), accent = Color(0xFFDCE775),
            textPrimary = Color(0xFFF3F3EA), textSecondary = Color(0xFFA8A590),
            glowColor = Color(0x33AFB42B)
        ),
        BYDColorScheme(
            id = BYDThemeId.CARBON_BLUE, nameAr = "الكاربون الأزرق", nameEn = "Carbon Blue",
            description = "ألياف كربونية داكنة مع تفاصيل زرقاء سباقات مشعة",
            category = ThemeCategory.SPORT, surfaceStyle = SurfaceStyle.CARBON_FIBER,
            background = Color(0xFF050912), surface = Color(0xFF0E1626), surfaceVariant = Color(0xFF17243B),
            borderGlow = Color(0xFF0084FF), primary = Color(0xFF1E90FF), onPrimary = darkText,
            secondary = Color(0xFF00D2FF), accent = Color(0xFF80C4FF),
            textPrimary = Color(0xFFEDF5FF), textSecondary = Color(0xFF93A7C4),
            glowColor = Color(0x4D0070F3)
        ),
        BYDColorScheme(
            id = BYDThemeId.POLAR_NIGHT, nameAr = "الليل القطبي", nameEn = "Polar Night",
            description = "ليل قطبي كحلي عميق مع أزرق جليدي متجمد وناصع",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF040914), surface = Color(0xFF0A162E), surfaceVariant = Color(0xFF12254A),
            borderGlow = Color(0xFF29B6F6), primary = Color(0xFF4FC3F7), onPrimary = darkText,
            secondary = Color(0xFF80D8FF), accent = Color(0xFFB3E5FC),
            textPrimary = Color(0xFFEDF8FF), textSecondary = Color(0xFF90A9C7),
            glowColor = Color(0x3D00E5FF)
        ),
        BYDColorScheme(
            id = BYDThemeId.AMETHYST_CRYSTAL, nameAr = "الجمشت البنفسجي", nameEn = "Amethyst Crystal",
            description = "بنفسجي بلوري كريستالي مع أرجواني متوهج",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF07030F), surface = Color(0xCC160A2E), surfaceVariant = Color(0xD925104A),
            borderGlow = Color(0xFFAB47BC), primary = Color(0xFFCE93D8), onPrimary = darkText,
            secondary = Color(0xFFE040FB), accent = Color(0xFFF3E5F5),
            textPrimary = Color(0xFFFBF0FF), textSecondary = Color(0xFFB898C7),
            glowColor = Color(0x3D9C27B0)
        ),
        BYDColorScheme(
            id = BYDThemeId.CARBON_GOLD, nameAr = "الكاربون الذهبي", nameEn = "Carbon Gold",
            description = "ألياف كربونية سوداء مع أحزمة وخطوط ذهب سباقات متوهجة",
            category = ThemeCategory.SPORT, surfaceStyle = SurfaceStyle.CARBON_FIBER,
            background = Color(0xFF080705), surface = Color(0xFF16130D), surfaceVariant = Color(0xFF241F16),
            borderGlow = Color(0xFFD4AF37), primary = Color(0xFFE6C24C), onPrimary = darkText,
            secondary = Color(0xFFFFD700), accent = Color(0xFFFFE680),
            textPrimary = Color(0xFFFFF9E6), textSecondary = Color(0xFFB5A888),
            glowColor = Color(0x4DD4AF37)
        ),

        // ==================== 41 - 50 ====================
        BYDColorScheme(
            id = BYDThemeId.ECLIPSE_SHADOW, nameAr = "الكسوف المظلم", nameEn = "Eclipse Shadow",
            description = "أسود كسوف قاتم مع هالة ضوئية برتقالية دافئة",
            category = ThemeCategory.TACTICAL_MINIMAL, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF030305), surface = Color(0xFF0D0D14), surfaceVariant = Color(0xFF181824),
            borderGlow = Color(0xFFFF6D00), primary = Color(0xFFFF7A1A), onPrimary = darkText,
            secondary = Color(0xFFFF9100), accent = Color(0xFFFFC166),
            textPrimary = Color(0xFFFFF1E8), textSecondary = Color(0xFFA69990),
            glowColor = Color(0x3DFF6D00)
        ),
        BYDColorScheme(
            id = BYDThemeId.MECHA_GUNDAM, nameAr = "الميكا المستقبلية", nameEn = "Mecha Gundam",
            description = "أزرق كوبالت وأحمر وأصفر بتصميم قمرة قيادة مستقبلية",
            category = ThemeCategory.FUTURISTIC, surfaceStyle = SurfaceStyle.NEON_WIREFRAME,
            background = Color(0xFF0A0F1A), surface = Color(0xFF131D33), surfaceVariant = Color(0xFF1E2D4D),
            borderGlow = Color(0xFF2979FF), primary = Color(0xFF448AFF), onPrimary = darkText,
            secondary = Color(0xFFFF1744), accent = Color(0xFFFFEA00),
            textPrimary = Color(0xFFEDF5FF), textSecondary = Color(0xFF93A6C4),
            glowColor = Color(0x4D2979FF)
        ),
        BYDColorScheme(
            id = BYDThemeId.SANDSTORM_GOLD, nameAr = "العاصفة الرملية الذهبية", nameEn = "Sandstorm Gold",
            description = "عاصفة رملية ذهبية مشعة وترابية دافئة",
            category = ThemeCategory.ARABIC_HERITAGE, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF120B04), surface = Color(0xFF22150A), surfaceVariant = Color(0xFF332010),
            borderGlow = Color(0xFFE5A93B), primary = Color(0xFFF5B041), onPrimary = darkText,
            secondary = Color(0xFFEB8934), accent = Color(0xFFFAD7A0),
            textPrimary = Color(0xFFFEF5E7), textSecondary = Color(0xFFBA9E84),
            glowColor = Color(0x3DE5A93B)
        ),
        BYDColorScheme(
            id = BYDThemeId.PACIFIC_TURQUOISE, nameAr = "تيركوز المحيط", nameEn = "Pacific Turquoise",
            description = "فيروزي مائي منعش مستوحى من مياه المحيط الهادئ",
            category = ThemeCategory.NATURE_SPACE, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF030D12), surface = Color(0xCC09202B), surfaceVariant = Color(0xD9103140),
            borderGlow = Color(0xFF00E5FF), primary = Color(0xFF00D4E6), onPrimary = darkText,
            secondary = Color(0xFF26C6DA), accent = Color(0xFFB2EBF2),
            textPrimary = Color(0xFFE6F7F7), textSecondary = Color(0xFF8AB0BA),
            glowColor = Color(0x3D00E5FF)
        ),
        BYDColorScheme(
            id = BYDThemeId.LIQUID_MERCURY, nameAr = "الزئبق السائل", nameEn = "Liquid Mercury",
            description = "كروم معدني فضي سائل ومصقول بأسلوب فائق الحداثة",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.BRUSHED_METAL,
            background = Color(0xFF0A0C10), surface = Color(0xFF151922), surfaceVariant = Color(0xFF202633),
            borderGlow = Color(0xFFCFD8DC), primary = Color(0xFFE1E8ED), onPrimary = darkText,
            secondary = Color(0xFFB0BEC5), accent = Color(0xFF80DEEA),
            textPrimary = Color(0xFFFFFFFF), textSecondary = Color(0xFF90A4AE),
            glowColor = Color(0x2EECEFF1)
        ),
        BYDColorScheme(
            id = BYDThemeId.GRAPHITE_ORANGE, nameAr = "الغرافيت البرتقالي", nameEn = "Graphite Orange",
            description = "رمادي غرافيتي داكن مع برتقالي سباقات رياضي جريء",
            category = ThemeCategory.SPORT, surfaceStyle = SurfaceStyle.CARBON_FIBER,
            background = Color(0xFF0D0F12), surface = Color(0xFF181C22), surfaceVariant = Color(0xFF242A33),
            borderGlow = Color(0xFFFF6600), primary = Color(0xFFFF7315), onPrimary = darkText,
            secondary = Color(0xFFFF9900), accent = Color(0xFFFFCC00),
            textPrimary = Color(0xFFECEFF1), textSecondary = Color(0xFF95A8B5),
            glowColor = Color(0x4DFF6600)
        ),
        BYDColorScheme(
            id = BYDThemeId.OBSIDIAN_SAPPHIRE, nameAr = "الأوبسيديان الياقوتي", nameEn = "Obsidian Sapphire",
            description = "أسود زجاجي بركاني مع أزرق ياقوتي فخم",
            category = ThemeCategory.LUXURY, surfaceStyle = SurfaceStyle.GLASSMORPHIC,
            background = Color(0xFF03060F), surface = Color(0xCC09132B), surfaceVariant = Color(0xD9102247),
            borderGlow = Color(0xFF2979FF), primary = Color(0xFF42A5F5), onPrimary = darkText,
            secondary = Color(0xFF00E5FF), accent = Color(0xFF90CAF9),
            textPrimary = Color(0xFFE8F4FD), textSecondary = Color(0xFF8EA3C2),
            glowColor = Color(0x3D1E88E5)
        ),
        // ✱ التعديل 4: onPrimary من lightText → darkText (5.21:1)
        BYDColorScheme(
            id = BYDThemeId.RED_LINE_DRIFT, nameAr = "الخط الأحمر دريفت", nameEn = "Red Line Drift",
            description = "أحمر RPM ناري مع ألياف كربونية لعشاق الدريفت",
            category = ThemeCategory.SPORT, surfaceStyle = SurfaceStyle.CARBON_FIBER,
            background = Color(0xFF0A0102), surface = Color(0xFF1A0407), surfaceVariant = Color(0xFF2B070D),
            borderGlow = Color(0xFFFF0033), primary = Color(0xFFFF1A47), onPrimary = darkText,
            secondary = Color(0xFFFF5277), accent = Color(0xFFFFFFFF),
            textPrimary = Color(0xFFFFF0F2), textSecondary = Color(0xFFC4929A),
            glowColor = Color(0x4DFF0033)
        ),
        BYDColorScheme(
            id = BYDThemeId.ZEN_MINIMALIST, nameAr = "الزن الهادئ", nameEn = "Zen Minimalist",
            description = "بساطة يابانية هادئة بدرجات الرمادي الحبري والبيج الدافئ",
            category = ThemeCategory.TACTICAL_MINIMAL, surfaceStyle = SurfaceStyle.SOLID,
            background = Color(0xFF0A0B0D), surface = Color(0xFF15171C), surfaceVariant = Color(0xFF20232B),
            borderGlow = Color(0xFF90A4AE), primary = Color(0xFFECEFF1), onPrimary = darkText,
            secondary = Color(0xFFD7CCC8), accent = Color(0xFFB0BEC5),
            textPrimary = Color(0xFFFFFFFF), textSecondary = Color(0xFF99ABB5),
            glowColor = Color(0x22ECEFF1)
        ),
        BYDColorScheme(
            id = BYDThemeId.HYPERDRIVE_WARP, nameAr = "هايبر درايف", nameEn = "Hyperdrive Warp",
            description = "أزرق سرعة الضوء وقفزة فضائية مستقبلية متوهجة",
            category = ThemeCategory.FUTURISTIC, surfaceStyle = SurfaceStyle.NEON_WIREFRAME,
            background = Color(0xFF020412), surface = Color(0xFF070D29), surfaceVariant = Color(0xFF0E1745),
            borderGlow = Color(0xFF00E5FF), primary = Color(0xFF00C8FF), onPrimary = darkText,
            secondary = Color(0xFFD500F9), accent = Color(0xFF00FFCC),
            textPrimary = Color(0xFFE5FAFC), textSecondary = Color(0xFF8DA2C4),
            glowColor = Color(0x5200E5FF)
        )
    )

    private val themeMap: EnumMap<BYDThemeId, BYDColorScheme> =
        EnumMap<BYDThemeId, BYDColorScheme>(BYDThemeId::class.java).apply {
            allList.forEach { scheme -> put(scheme.id, scheme) }
        }

    fun get(id: BYDThemeId): BYDColorScheme =
        themeMap[id] ?: themeMap.getValue(BYDThemeId.MIDNIGHT_GLASS)

    fun byCategory(category: ThemeCategory): List<BYDColorScheme> =
        allList.filter { it.category == category }
}
