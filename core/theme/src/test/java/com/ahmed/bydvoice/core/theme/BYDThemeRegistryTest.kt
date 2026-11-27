package com.ahmed.bydvoice.core.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

class BYDThemeRegistryTest {

    private fun contrastRatio(fg: Color, bg: Color): Float {
        val l1 = fg.luminance()
        val l2 = bg.luminance()
        return (max(l1, l2) + 0.05f) / (min(l1, l2) + 0.05f)
    }

    @Test
    fun `registry must contain exactly 50 unique themes matching all enum ids`() {
        assertEquals(50, BYDThemeId.entries.size)
        assertEquals(50, BYDThemeRegistry.allList.size)
        assertEquals(50, BYDThemeRegistry.allList.map { it.id }.toSet().size)
        assertEquals(50, BYDThemeRegistry.allList.map { it.nameAr }.toSet().size)
        assertEquals(50, BYDThemeRegistry.allList.map { it.nameEn }.toSet().size)
        BYDThemeId.entries.forEach { id ->
            assertEquals(id, BYDThemeRegistry.get(id).id)
        }
    }

    @Test
    fun `every theme must meet WCAG AA and safe-driving ratios`() {
        BYDThemeRegistry.allList.forEach { t ->
            // نص أساسي فوق الخلفية: 7.0 (AAA صغير)
            assertTrue(
                "textPrimary/bg ${t.id}: ${contrastRatio(t.textPrimary, t.background)}",
                contrastRatio(t.textPrimary, t.background) >= 7.0f
            )
            // نص ثانوي فوق الخلفية: 4.5
            assertTrue(
                "textSecondary/bg ${t.id}: ${contrastRatio(t.textSecondary, t.background)}",
                contrastRatio(t.textSecondary, t.background) >= 4.5f
            )
            // onPrimary فوق primary: 4.5
            assertTrue(
                "onPrimary/primary ${t.id}: ${contrastRatio(t.onPrimary, t.primary)}",
                contrastRatio(t.onPrimary, t.primary) >= 4.5f
            )

            // السطح المدمج فوق الخلفية (يحل مشكلة alpha في الثيمات الزجاجية)
            val effectiveSurface = t.surface.compositeOver(t.background)
            assertTrue(
                "textPrimary/surface ${t.id}: ${contrastRatio(t.textPrimary, effectiveSurface)}",
                contrastRatio(t.textPrimary, effectiveSurface) >= 7.0f
            )
            assertTrue(
                "textSecondary/surface ${t.id}: ${contrastRatio(t.textSecondary, effectiveSurface)}",
                contrastRatio(t.textSecondary, effectiveSurface) >= 4.5f
            )
        }
    }

    @Test
    fun `onPrimary must equal bestOnColor for every theme`() {
        BYDThemeRegistry.allList.forEach { t ->
            assertEquals(
                "onPrimary mismatch in ${t.id}",
                BYDThemeRegistry.bestOnColor(t.primary),
                t.onPrimary
            )
        }
    }
}
