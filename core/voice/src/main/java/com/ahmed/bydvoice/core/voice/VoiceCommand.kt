package com.ahmed.bydvoice.core.voice

import com.ahmed.bydvoice.core.theme.BYDThemeId
import com.ahmed.bydvoice.core.theme.BYDThemeRegistry

/** وضع العرض المطلوب بصوت المستخدم */
enum class VoiceMode { DAY, NIGHT, AUTO }

/** تبويبات التطبيق الممكن فتحها بالصوت */
enum class VoiceTab { THEMES, PREVIEW, UPDATE, VOICE }

/**
 * نتيجة تفسير الجملة المنطوقة إلى أمر داخلي.
 * منطق نقي 100% — يُختبر بوحدة JVM بدون جهاز.
 */
sealed interface VoiceCommand {
    data class SetMode(val mode: VoiceMode) : VoiceCommand
    data class SetTheme(val themeId: BYDThemeId) : VoiceCommand
    data object CheckUpdate : VoiceCommand
    data class OpenTab(val tab: VoiceTab) : VoiceCommand
    data class Unknown(val heard: String) : VoiceCommand
}

/**
 * مفسّر الأوامر الصوتية العربية.
 *
 * ترتيب الأولوية مهم:
 * 1) اسم ثيم — لأن أسماء مثل «الليل القطبي» و«الرؤية الليلية» تحتوي كلمات قد تتصادم مع أوامر الوضع
 * 2) وضع العرض (نهاري / ليلي / تلقائي)
 * 3) التحديث
 * 4) فتح تبويب
 */
object VoiceCommandParser {

    fun parse(raw: String): VoiceCommand {
        val t = normalize(raw)
        if (t.isBlank()) return VoiceCommand.Unknown(raw)

        // 1) اسم ثيم بالعربي أو الإنجليزي
        val theme = BYDThemeRegistry.allList.firstOrNull { s ->
            containsName(t, s.nameAr) || containsName(t, s.nameEn)
        }
        if (theme != null) return VoiceCommand.SetTheme(theme.id)

        // 2) وضع العرض
        return when {
            t.contains("نهار") || t.contains("day") -> VoiceCommand.SetMode(VoiceMode.DAY)
            t.contains("ليل") || t.contains("night") -> VoiceCommand.SetMode(VoiceMode.NIGHT)
            t.contains("تلقائي") || t.contains("توماتيك") || t.contains("auto") ->
                VoiceCommand.SetMode(VoiceMode.AUTO)

            // 3) تحديث
            t.contains("تحديث") || t.contains("update") -> VoiceCommand.CheckUpdate

            // 4) تبويبات
            t.contains("معاين") -> VoiceCommand.OpenTab(VoiceTab.PREVIEW)
            t.contains("ثيم") -> VoiceCommand.OpenTab(VoiceTab.THEMES)
            t.contains("صوت") || t.contains("ميكروفون") -> VoiceCommand.OpenTab(VoiceTab.VOICE)

            else -> VoiceCommand.Unknown(raw)
        }
    }

    /**
     * توحيد النص: إزالة التشكيل، توحيد الألف والتاء المربوطة والألف المقصورة،
     * حذف الترقيم، وخفض الحالة — حتى التعرف الصوتي بأخطائه يفهم.
     */
    internal fun normalize(s: String): String = s
        .lowercase()
        .replace('أ', 'ا').replace('إ', 'ا').replace('آ', 'ا')
        .replace('ة', 'ه').replace('ى', 'ي')
        .replace(Regex("[\\u064B-\\u0652\\u0670]"), "") // تشكيل + ألف خنجرية
        .replace(Regex("[^\\p{L}\\p{N}\\s]"), " ")      // أي رمز غير حرف/رقم = فراغ
        .replace(Regex("\\s+"), " ")
        .trim()

    /** الاسم المطابق لازم طوله ≥4 حتى ما نطابق كلمة قصيرة بالغلط */
    private fun containsName(input: String, name: String): Boolean {
        val n = normalize(name)
        return n.length >= 4 && input.contains(n)
    }
}
