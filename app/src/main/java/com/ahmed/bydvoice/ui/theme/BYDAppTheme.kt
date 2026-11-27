package com.ahmed.bydvoice.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.drawWithCache
import com.ahmed.bydvoice.core.theme.BYDColorScheme
import com.ahmed.bydvoice.core.theme.SurfaceStyle

/**
 * ثيم التطبيق الرئيسي — يستقبل مخطط الثيم المختار من الـ50.
 * لا يوجد أي ثيم قديم هنا؛ كل الألوان تأتي من BYDColorScheme.
 */
@Composable
fun BYDAppTheme(
    colors: BYDColorScheme,
    content: @Composable () -> Unit,
) {
    val materialScheme = darkColorScheme(
        primary = colors.primary,
        onPrimary = colors.onPrimary,
        secondary = colors.secondary,
        onSecondary = colors.onPrimary,
        tertiary = colors.accent,
        background = colors.background,
        onBackground = colors.textPrimary,
        surface = colors.surface,
        onSurface = colors.textPrimary,
        surfaceVariant = colors.surfaceVariant,
        onSurfaceVariant = colors.textSecondary,
        outline = colors.borderGlow,
    )
    MaterialTheme(
        colorScheme = materialScheme,
        content = content,
    )
}

/**
 * سطح بطاقة يتبع surfaceStyle للثيم:
 *  - CARBON_FIBER: نسيج ألياف كربون (خطوط متقاطعة خفيفة)
 *  - NEON_WIREFRAME: توهج حدودي نيون
 *  - BRUSHED_METAL: تدرج معدني أفقي
 *  - GLASSMORPHIC: تعتيم alpha مدمج بالفعل داخل لون surface (0xCC)
 *  - SOLID: تعبئة مسطحة
 *
 * drawWithCache: نبني المسارات مرة واحدة فقط بدل إعادة حسابها كل إطار.
 */
fun Modifier.bydCardSurface(colors: BYDColorScheme): Modifier =
    this.drawWithCache {
        val weavePath = if (colors.surfaceStyle == SurfaceStyle.CARBON_FIBER) {
            Path().apply {
                val step = 18f
                var x = -size.height
                while (x < size.width) {
                    moveTo(x, 0f)
                    lineTo(x + size.height, size.height)
                    x += step
                }
                x = 0f
                while (x < size.width + size.height) {
                    moveTo(x, 0f)
                    lineTo(x - size.height, size.height)
                    x += step
                }
            }
        } else null

        val metalBrush = if (colors.surfaceStyle == SurfaceStyle.BRUSHED_METAL) {
            Brush.horizontalGradient(
                listOf(
                    colors.surface,
                    colors.surfaceVariant,
                    colors.surface,
                )
            )
        } else null

        onDrawBehind {
            drawRoundRect(
                color = colors.surface,
                size = size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f),
            )
            when (colors.surfaceStyle) {
                SurfaceStyle.CARBON_FIBER -> weavePath?.let {
                    drawPath(it, Color.White.copy(alpha = 0.035f))
                }
                SurfaceStyle.NEON_WIREFRAME -> {
                    drawRoundRect(
                        color = colors.borderGlow.copy(alpha = 0.55f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f),
                        style = Stroke(width = 1.5f),
                    )
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(colors.glowColor, Color.Transparent),
                            center = Offset(size.width * 0.85f, size.height * 0.15f),
                            radius = size.maxDimension * 0.6f,
                        ),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f),
                    )
                }
                SurfaceStyle.BRUSHED_METAL -> metalBrush?.let {
                    drawRoundRect(
                        brush = it,
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f),
                    )
                }
                else -> Unit
            }
        }
    }
