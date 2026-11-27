package com.ahmed.bydvoice.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ahmed.bydvoice.R
import com.ahmed.bydvoice.core.theme.BYDColorScheme
import com.ahmed.bydvoice.core.theme.BYDThemeRegistry
import com.ahmed.bydvoice.core.theme.ThemeCategory
import com.ahmed.bydvoice.ui.dashboard.DayNightMode
import com.ahmed.bydvoice.ui.theme.bydCardSurface

/**
 * لوحة الثيمات الـ50: بحث + فلتر تصنيف + شبكة بطاقات.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDashboardScreen(viewModel: ThemeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val colors = viewModel.currentScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // العنوان
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.dashboard_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.dashboard_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                )
            }
            Text(
                text = stringResource(R.string.version_label),
                style = MaterialTheme.typography.labelMedium,
                color = colors.accent,
            )
        }

        Spacer(Modifier.height(12.dp))

        // البحث
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(stringResource(R.string.search_hint), color = colors.textSecondary)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = colors.textSecondary)
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
        )

        Spacer(Modifier.height(10.dp))

        // وضع العرض: تلقائي / نهاري / ليلي
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModeChip(
                label = stringResource(R.string.mode_auto),
                active = state.mode == DayNightMode.AUTO,
                colors = colors,
                onClick = { viewModel.setMode(DayNightMode.AUTO) },
            )
            ModeChip(
                label = stringResource(R.string.mode_day),
                active = state.mode == DayNightMode.DAY,
                colors = colors,
                onClick = { viewModel.setMode(DayNightMode.DAY) },
            )
            ModeChip(
                label = stringResource(R.string.mode_night),
                active = state.mode == DayNightMode.NIGHT,
                colors = colors,
                onClick = { viewModel.setMode(DayNightMode.NIGHT) },
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(
                    if (state.effectiveMode == DayNightMode.DAY) R.string.mode_day
                    else R.string.mode_night,
                ),
                style = MaterialTheme.typography.labelMedium,
                color = colors.accent,
            )
        }

        Text(
            text = stringResource(
                R.string.theme_slot_hint,
                if (state.effectiveMode == DayNightMode.DAY)
                    stringResource(R.string.mode_day) else stringResource(R.string.mode_night),
            ),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
        )

        // شريحة التشخيص: مصدر الوضع + حالة قراءة الكشافات (تساعد فحص DiLink الحقيقي)
        val sourceLabel = when {
            state.mode != DayNightMode.AUTO -> stringResource(R.string.diag_manual)
            state.carLightsOn != null -> stringResource(R.string.diag_car)
            else -> stringResource(R.string.diag_clock)
        }
        val lightsLabel = when (state.carLightsOn) {
            true -> stringResource(R.string.diag_lights_on)
            false -> stringResource(R.string.diag_lights_off)
            null -> stringResource(R.string.diag_lights_na)
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = colors.surfaceVariant.copy(alpha = 0.6f),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.diag_source, sourceLabel),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                )
                Text(
                    text = lightsLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.accent,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // فلتر التصنيفات
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                CategoryChip(
                    label = stringResource(R.string.all_categories),
                    selected = state.categoryFilter == null,
                    colors = colors,
                    onClick = { viewModel.onCategoryChange(null) },
                )
            }
            items(ThemeCategory.entries) { cat ->
                CategoryChip(
                    label = cat.labelAr,
                    selected = state.categoryFilter == cat,
                    colors = colors,
                    onClick = { viewModel.onCategoryChange(if (state.categoryFilter == cat) null else cat) },
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // الشبكة
        val filtered = BYDThemeRegistry.allList.filter { t ->
            (state.categoryFilter == null || t.category == state.categoryFilter) &&
                (state.searchQuery.isBlank() ||
                    t.nameAr.contains(state.searchQuery) ||
                    t.nameEn.contains(state.searchQuery, ignoreCase = true))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filtered, key = { it.id.name }) { scheme ->
                ThemeCard(
                    scheme = scheme,
                    selected = scheme.id == state.activeId,
                    colors = colors,
                    onClick = { viewModel.selectTheme(scheme.id) },
                )
            }
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    active: Boolean,
    colors: BYDColorScheme,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = active,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.accent,
            selectedLabelColor = colors.onPrimary,
            containerColor = colors.surface,
            labelColor = colors.textSecondary,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = active,
            borderColor = colors.borderGlow,
            selectedBorderColor = colors.accent,
        ),
    )
}

@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    colors: BYDColorScheme,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colors.primary,
            selectedLabelColor = colors.onPrimary,
            containerColor = colors.surface,
            labelColor = colors.textSecondary,
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = colors.borderGlow,
            selectedBorderColor = colors.primary,
        ),
    )
}

@Composable
private fun ThemeCard(
    scheme: BYDColorScheme,
    selected: Boolean,
    colors: BYDColorScheme,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) colors.accent else colors.borderGlow.copy(alpha = 0.25f)
    Column(
        modifier = Modifier
            .bydCardSurface(scheme)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        // شرائط الألوان
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ColorDot(scheme.primary)
            ColorDot(scheme.secondary)
            ColorDot(scheme.accent)
            Spacer(Modifier.weight(1f))
            if (selected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = scheme.accent,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = scheme.nameAr,
            style = MaterialTheme.typography.titleMedium,
            color = scheme.textPrimary,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = scheme.description,
            style = MaterialTheme.typography.bodySmall,
            color = scheme.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            minLines = 2,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = scheme.category.labelAr,
            style = MaterialTheme.typography.labelSmall,
            color = scheme.primary,
        )
    }
}

@Composable
private fun ColorDot(color: androidx.compose.ui.graphics.Color) {
    Box(
        Modifier
            .size(18.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(listOf(color, color.copy(alpha = 0.6f)))
            )
            .border(1.dp, color.copy(alpha = 0.8f), CircleShape)
    )
}
