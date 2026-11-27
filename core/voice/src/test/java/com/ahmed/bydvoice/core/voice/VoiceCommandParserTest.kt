package com.ahmed.bydvoice.core.voice

import com.ahmed.bydvoice.core.theme.BYDThemeId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceCommandParserTest {

    private val parser = VoiceCommandParser

    @Test
    fun `وضع ليلي يرجع SetMode NIGHT`() {
        assertEquals(VoiceCommand.SetMode(VoiceMode.NIGHT), parser.parse("وضع ليلي"))
    }

    @Test
    fun `وضع نهاري يرجع SetMode DAY`() {
        assertEquals(VoiceCommand.SetMode(VoiceMode.DAY), parser.parse("خليه نهاري"))
    }

    @Test
    fun `تلقائي يرجع SetMode AUTO`() {
        assertEquals(VoiceCommand.SetMode(VoiceMode.AUTO), parser.parse("ارجعه تلقائي"))
    }

    @Test
    fun `توماتيك يفهم AUTO بالعراقي`() {
        assertEquals(VoiceCommand.SetMode(VoiceMode.AUTO), parser.parse("خليه اوتوماتيك"))
    }

    @Test
    fun `اسم الثيم بالعربي يرجع SetTheme`() {
        val cmd = parser.parse("غيّر الثيم إلى الفخامة الزجاجية")
        assertEquals(VoiceCommand.SetTheme(BYDThemeId.MIDNIGHT_GLASS), cmd)
    }

    @Test
    fun `اسم الثيم بالإنجليزي يرجع SetTheme`() {
        val cmd = parser.parse("change theme to Cyberpunk Neon")
        assertEquals(VoiceCommand.SetTheme(BYDThemeId.CYBERPUNK_NEON), cmd)
    }

    @Test
    fun `تحقق من التحديث يرجع CheckUpdate`() {
        assertEquals(VoiceCommand.CheckUpdate, parser.parse("تحقق من التحديث"))
    }

    @Test
    fun `افتح المعاينة يرجع OpenTab PREVIEW`() {
        assertEquals(VoiceCommand.OpenTab(VoiceTab.PREVIEW), parser.parse("افتح المعاينة"))
    }

    @Test
    fun `كلمة غريبة ترجع Unknown`() {
        val cmd = parser.parse("شكراً حبيبي")
        assertTrue(cmd is VoiceCommand.Unknown)
    }

    @Test
    fun `التشكيل لا يكسر التفسير`() {
        assertEquals(VoiceCommand.SetMode(VoiceMode.NIGHT), parser.parse("وضع لَيْلِيّ"))
    }

    @Test
    fun `وضع ليلي لا يتصادم مع اسم ثيم يحتوي ليل`() {
        // "الليل القطبي" و"الرؤية الليلية" ثيمات — لكن "وضع ليلي" ما يطابق اسمها كاملاً
        assertEquals(VoiceCommand.SetMode(VoiceMode.NIGHT), parser.parse("وضع ليلي"))
    }

    @Test
    fun `النص الفارغ يرجع Unknown`() {
        assertTrue(parser.parse("!!!") is VoiceCommand.Unknown)
    }
}
