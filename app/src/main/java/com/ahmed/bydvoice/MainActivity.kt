package com.ahmed.bydvoice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import com.ahmed.bydvoice.core.theme.BYDThemeRegistry
import com.ahmed.bydvoice.core.voice.VoiceCommand
import com.ahmed.bydvoice.core.voice.VoiceMode
import com.ahmed.bydvoice.core.voice.VoiceTab
import com.ahmed.bydvoice.ui.dashboard.DayNightMode
import com.ahmed.bydvoice.ui.dashboard.ThemeDashboardScreen
import com.ahmed.bydvoice.ui.dashboard.ThemeViewModel
import com.ahmed.bydvoice.ui.preview.HudPreviewScreen
import com.ahmed.bydvoice.ui.theme.BYDAppTheme
import com.ahmed.bydvoice.ui.voice.VoiceScreen
import com.ahmed.bydvoice.update.AppUpdater
import com.ahmed.bydvoice.update.UpdateNotifier
import com.ahmed.bydvoice.update.UpdateScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ThemeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[ThemeViewModel::class.java]

        setContent {
            // الاستماع للحالة حتى يتحدث الثيم فور اختيار ثيم جديد
            val state by viewModel.uiState.collectAsState()
            val scheme = BYDThemeRegistry.get(state.activeId)

            // ساعة حية: كل 30 ثانية يُعاد تقييم الوضع التلقائي (نهاري/ليلي)
            LaunchedEffect(Unit) {
                while (true) {
                    kotlinx.coroutines.delay(30_000)
                    viewModel.refreshClock()
                }
            }

            // فحص تحديث عند كل تشغيل للتطبيق — إذا توفر إصدار، إشعار
            LaunchedEffect(Unit) {
                val startupUpdater = AppUpdater(
                    this@MainActivity,
                    BuildConfig.VERSION_CODE,
                    AppUpdater.DEFAULT_VERSION_URL,
                )
                when (val r = startupUpdater.check()) {
                    is AppUpdater.CheckResult.Available ->
                        UpdateNotifier.notifyUpdateAvailable(this@MainActivity, r.version.versionName)
                    else -> Unit
                }
            }

            // التبويب النشط بالنشاط حتى الأوامر الصوتية تگدر تبدّله
            var tabIndex by rememberSaveable { mutableIntStateOf(0) }

            // تنفيذ الأوامر الصوتية: وضع / ثيم / تحديث / فتح تبويب
            val handleVoice: (VoiceCommand) -> Unit = { cmd ->
                when (cmd) {
                    is VoiceCommand.SetMode -> viewModel.setMode(
                        when (cmd.mode) {
                            VoiceMode.DAY -> DayNightMode.DAY
                            VoiceMode.NIGHT -> DayNightMode.NIGHT
                            VoiceMode.AUTO -> DayNightMode.AUTO
                        }
                    )
                    is VoiceCommand.SetTheme -> viewModel.selectTheme(cmd.themeId)
                    VoiceCommand.CheckUpdate -> tabIndex = AppTab.entries.indexOf(AppTab.UPDATE)
                    is VoiceCommand.OpenTab -> tabIndex = when (cmd.tab) {
                        VoiceTab.THEMES -> AppTab.entries.indexOf(AppTab.THEMES)
                        VoiceTab.PREVIEW -> AppTab.entries.indexOf(AppTab.PREVIEW)
                        VoiceTab.UPDATE -> AppTab.entries.indexOf(AppTab.UPDATE)
                        VoiceTab.VOICE -> AppTab.entries.indexOf(AppTab.VOICE)
                    }
                    is VoiceCommand.Unknown -> Unit
                }
            }

            BYDAppTheme(colors = scheme) {
                AppTabs(
                    scheme = scheme,
                    tabIndex = tabIndex,
                    onTabChange = { tabIndex = it },
                    currentVersionCode = BuildConfig.VERSION_CODE,
                    currentVersionName = BuildConfig.VERSION_NAME,
                    onVoiceCommand = handleVoice,
                ) { innerModifier ->
                    ThemeDashboardScreen(viewModel)
                }
            }
        }
    }
}

private enum class AppTab(val icon: ImageVector) {
    THEMES(Icons.Default.Star),
    PREVIEW(Icons.Default.Build),
    UPDATE(Icons.Default.Refresh),
    VOICE(Icons.Default.Mic),
}

@Composable
private fun AppTabs(
    scheme: com.ahmed.bydvoice.core.theme.BYDColorScheme,
    tabIndex: Int,
    onTabChange: (Int) -> Unit,
    currentVersionCode: Int,
    currentVersionName: String,
    onVoiceCommand: (VoiceCommand) -> Unit,
    dashboardContent: @Composable (Modifier) -> Unit,
) {
    val tabs = AppTab.entries

    Column(modifier = Modifier.fillMaxSize().background(scheme.background)) {
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = scheme.surface,
            contentColor = scheme.textPrimary,
            indicator = { positions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(positions[tabIndex]),
                    color = scheme.primary,
                )
            },
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { onTabChange(index) },
                    icon = { Icon(tab.icon, contentDescription = null, tint = if (tabIndex == index) scheme.primary else scheme.textSecondary) },
                    text = {
                        Text(
                            text = when (tab) {
                                AppTab.THEMES -> stringResource(R.string.tab_themes)
                                AppTab.PREVIEW -> stringResource(R.string.tab_preview)
                                AppTab.UPDATE -> stringResource(R.string.tab_update)
                                AppTab.VOICE -> stringResource(R.string.tab_voice)
                            },
                            color = if (tabIndex == index) scheme.primary else scheme.textSecondary,
                        )
                    },
                )
            }
        }

        when (tabs[tabIndex]) {
            AppTab.THEMES -> dashboardContent(Modifier.fillMaxSize())
            AppTab.PREVIEW -> HudPreviewScreen(scheme)
            AppTab.UPDATE -> UpdateScreen(
                colors = scheme,
                currentVersionCode = currentVersionCode,
                currentVersionName = currentVersionName,
            )
            AppTab.VOICE -> VoiceScreen(
                colors = scheme,
                onCommand = onVoiceCommand,
            )
        }
    }
}
