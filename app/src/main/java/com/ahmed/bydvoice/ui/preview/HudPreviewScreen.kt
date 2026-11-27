package com.ahmed.bydvoice.ui.preview

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ahmed.bydvoice.R
import com.ahmed.bydvoice.core.theme.BYDColorScheme

/**
 * معاينة حية: عداد دائري HUD بألوان الثيم المختار — إبرة تدور باستمرار
 * وشريط تقدم وشرائط ألوان الثيم. كلها تتبع الـ14 توكن.
 */
@Composable
fun HudPreviewScreen(colors: BYDColorScheme) {
    val transition = rememberInfiniteTransition(label = "hud")

    val needle by transition.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "needle",
    )
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = colors.nameAr,
            style = MaterialTheme.typography.headlineSmall,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = colors.nameEn,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )

        Spacer(Modifier.height(20.dp))

        // العداد الدائري
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(230.dp)) {
                val stroke = 14f
                val arcSize = Size(size.width - stroke, size.height - stroke)
                val topLeft = Offset(stroke / 2, stroke / 2)

                // قوس الخلفية
                drawArc(
                    color = colors.surfaceVariant,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                // قوس القيمة الحية
                drawArc(
                    brush = Brush.sweepGradient(
                        0f to colors.primary,
                        0.6f to colors.secondary,
                        1f to colors.accent,
                    ),
                    startAngle = 135f,
                    sweepAngle = 270f * needle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round),
                )
                // توهج نهاية القوس
                drawCircle(
                    color = colors.glowColor,
                    radius = 26f,
                    center = center,
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(needle * 260).toInt()}",
                    style = MaterialTheme.typography.displayMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.preview_speed_unit),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textSecondary,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // شريط موجي متحرك
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(colors.surface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(wave)
                    .height(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(colors.primary, colors.secondary, colors.accent)
                        )
                    ),
            )
        }

        Spacer(Modifier.height(24.dp))

        // شرائط الـ14 توكن
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TokenStrip(colors.background, Modifier.weight(1f))
            TokenStrip(colors.surface, Modifier.weight(1f))
            TokenStrip(colors.surfaceVariant, Modifier.weight(1f))
            TokenStrip(colors.primary, Modifier.weight(1f))
            TokenStrip(colors.secondary, Modifier.weight(1f))
            TokenStrip(colors.accent, Modifier.weight(1f))
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.preview_tokens_hint),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun TokenStrip(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color),
    )
}
