package com.ahmed.bydvoice.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ahmed.bydvoice.core.theme.BYDThemeId
import com.ahmed.bydvoice.core.theme.BYDThemeRegistry
import com.ahmed.bydvoice.core.theme.ThemeCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

/** وضع العرض: تلقائي (حسب الوقت) أو فرض نهاري/ليلي */
enum class DayNightMode { AUTO, DAY, NIGHT }

private val DEFAULT_DAY_THEME = BYDThemeId.DESERT_DUNES
private val DEFAULT_NIGHT_THEME = BYDThemeId.MIDNIGHT_GLASS
private const val DAY_START_HOUR = 6
private const val DAY_END_HOUR = 18

data class DashboardUiState(
    val mode: DayNightMode = DayNightMode.AUTO,
    val dayThemeId: BYDThemeId = DEFAULT_DAY_THEME,
    val nightThemeId: BYDThemeId = DEFAULT_NIGHT_THEME,
    val searchQuery: String = "",
    val categoryFilter: ThemeCategory? = null,
    val nowHour: Int = -1,
    /** حالة كشافات السيارة (true=مشعلة) — null يعني غير متاح، نرجع للساعة */
    val carLightsOn: Boolean? = null,
) {
    /**
     * الوضع الفعلي بعد تطبيق التلقائي.
     * الأولوية: كشافات السيارة إذا قُرئت، وإلا الساعة (6:00–17:59 = نهاري).
     */
    val effectiveMode: DayNightMode
        get() = when {
            mode != DayNightMode.AUTO -> mode
            carLightsOn != null -> if (carLightsOn) DayNightMode.NIGHT else DayNightMode.DAY
            else -> if (nowHour in DAY_START_HOUR until DAY_END_HOUR) DayNightMode.DAY
            else DayNightMode.NIGHT
        }

    /** الثيم النشط حالياً — اللي ينعرض ويتطبق */
    val activeId: BYDThemeId
        get() = if (effectiveMode == DayNightMode.DAY) dayThemeId else nightThemeId
}

class ThemeViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("byd_theme_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            mode = runCatching { DayNightMode.valueOf(prefs.getString(KEY_MODE, null) ?: "") }
                .getOrDefault(DayNightMode.AUTO),
            dayThemeId = loadTheme(KEY_DAY_THEME, DEFAULT_DAY_THEME),
            nightThemeId = loadTheme(KEY_NIGHT_THEME, DEFAULT_NIGHT_THEME),
            nowHour = currentHour(),
            carLightsOn = probeCarLights(),
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun onSearchChange(query: String) = _uiState.update { it.copy(searchQuery = query) }

    fun onCategoryChange(category: ThemeCategory?) =
        _uiState.update { it.copy(categoryFilter = category) }

    fun setMode(mode: DayNightMode) {
        _uiState.update { it.copy(mode = mode) }
        viewModelScope.launch {
            prefs.edit().putString(KEY_MODE, mode.name).apply()
        }
    }

    /** تحديث الإشارة — تستدعيها الشاشة كل 30 ثانية: ضوء السيارة أولاً ثم الساعة */
    fun refreshClock() = _uiState.update {
        it.copy(nowHour = currentHour(), carLightsOn = probeCarLights())
    }

    /**
     * قراءة حالة الكشافات من android.car عبر reflection:
     * لا نحتاج مكتبة السيارة بالترجمة، وأي فشل (صلاحية/جهاز) يرجع null = رجوع للساعة.
     */
    private fun probeCarLights(): Boolean? = try {
        val carClazz = Class.forName("android.car.Car")
        val car = carClazz
            .getMethod("createCar", Context::class.java)
            .invoke(null, getApplication<Application>())
        try {
            val propMgr = carClazz
                .getMethod("getCarManager", String::class.java)
                .invoke(car, "property")
            val headlightProp = Class.forName("android.car.VehiclePropertyIds")
                .getField("HEADLIGHTS_STATE").getInt(null)
            val lightOn = Class.forName("android.car.VehicleLightState")
                .getField("ON").getInt(null)
            val state = Class.forName("android.car.hardware.property.CarPropertyManager")
                .getMethod("getIntProperty", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                .invoke(propMgr, headlightProp, 0) as Int
            state == lightOn
        } finally {
            runCatching { carClazz.getMethod("disconnect").invoke(car) }
        }
    } catch (e: Throwable) {
        null
    }

    /**
     * اختيار ثيم: يُحفظ داخل خانة الوضع الحالي (نهاري أو ليلي) —
     * يعني بالوضع التلقائي الضغطة الوحدة تغيّر ثيم النهار نهاراً وثيم الليل ليلاً.
     */
    fun selectTheme(id: BYDThemeId) {
        val slotIsDay = _uiState.value.effectiveMode == DayNightMode.DAY
        _uiState.update {
            if (slotIsDay) it.copy(dayThemeId = id) else it.copy(nightThemeId = id)
        }
        viewModelScope.launch {
            prefs.edit()
                .putString(if (slotIsDay) KEY_DAY_THEME else KEY_NIGHT_THEME, id.name)
                .apply()
        }
    }

    val currentScheme get() = BYDThemeRegistry.get(_uiState.value.activeId)

    private fun loadTheme(key: String, fallback: BYDThemeId): BYDThemeId =
        runCatching { BYDThemeId.valueOf(prefs.getString(key, null) ?: fallback.name) }
            .getOrDefault(fallback)

    private fun currentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    companion object {
        private const val KEY_MODE = "day_night_mode"
        private const val KEY_DAY_THEME = "day_theme_id"
        private const val KEY_NIGHT_THEME = "night_theme_id"
    }
}
