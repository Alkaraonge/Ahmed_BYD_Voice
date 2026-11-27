package com.ahmed.bydvoice.update

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ahmed.bydvoice.R
import com.ahmed.bydvoice.core.theme.BYDColorScheme
import kotlinx.coroutines.launch

/**
 * شاشة التحديث: مقارنة رقمية + تحقق sha256 — بدون حلقات لا نهائية.
 */
@Composable
fun UpdateScreen(
    colors: BYDColorScheme,
    currentVersionCode: Int,
    currentVersionName: String,
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val updater = remember {
        AppUpdater(context, currentVersionCode, AppUpdater.DEFAULT_VERSION_URL)
    }

    var state by remember { mutableStateOf<UiState>(UiState.Idle) }

    // stringResource ما يصير جوا coroutine — نجيب النصوص من الـContext
    fun errNetwork() = context.getString(R.string.update_network_error)
    fun errVerify() = context.getString(R.string.update_verify_failed)
    fun errNoConnection() = context.getString(R.string.update_no_connection)

    fun hasNetwork(): Boolean {
        val cm = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE)
            as android.net.ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun runCheck() {
        if (!hasNetwork()) {
            state = UiState.Error(errNoConnection())
            return
        }
        state = UiState.Checking
        scope.launch {
            state = when (val r = updater.check()) {
                is AppUpdater.CheckResult.Available -> UiState.Available(r.version)
                AppUpdater.CheckResult.Latest -> UiState.Latest
                is AppUpdater.CheckResult.Failure -> UiState.Error(errNetwork())
            }
        }
    }

    // فحص تلقائي أول ما تُفتح الشاشة (مرة وحدة لكل تشغيل) — داخل effect حتى لا يصير side-effect أثناء التركيب
    LaunchedEffect(Unit) {
        if (state is UiState.Idle) runCheck()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.update_title),
            style = MaterialTheme.typography.headlineSmall,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.update_current_version, currentVersionName),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )

        Spacer(Modifier.height(24.dp))

        when (val s = state) {
            UiState.Idle -> {
                Button(
                    onClick = { runCheck() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary,
                    ),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(stringResource(R.string.update_check))
                }
            }
            UiState.Checking -> CircularProgressIndicator(color = colors.primary)
            UiState.Latest -> Text(
                text = stringResource(R.string.update_latest),
                color = colors.accent,
                style = MaterialTheme.typography.titleMedium,
            )
            is UiState.Available -> {
                Text(
                    text = stringResource(R.string.update_available, s.version.versionName),
                    color = colors.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.update_changelog_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.accent,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = s.version.changelog,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        state = UiState.Downloading(0)
                        scope.launch {
                            state = when (val r = updater.downloadAndVerify(s.version) { percent ->
                                state = UiState.Downloading(percent)
                            }) {
                                is AppUpdater.DownloadResult.Verified -> {
                                    // التحقق نجح — نفتح شاشة التثبيت مباشرة
                                    updater.installVerified(r.apkFile)
                                    UiState.Downloaded
                                }
                                is AppUpdater.DownloadResult.Corrupted -> UiState.Error(errVerify())
                                is AppUpdater.DownloadResult.Failure -> UiState.Error(errNetwork())
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = colors.onPrimary,
                    ),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(stringResource(R.string.update_download))
                }
            }
            is UiState.Downloading -> {
                LinearProgressIndicator(
                    progress = { s.percent / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.accent,
                    trackColor = colors.surfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.update_progress, s.percent),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textPrimary,
                )
            }
            UiState.Downloaded -> Text(
                text = stringResource(R.string.update_downloaded),
                color = colors.accent,
                style = MaterialTheme.typography.titleMedium,
            )
            is UiState.Error -> {
                Text(
                    text = s.message,
                    color = colors.primary,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { state = UiState.Idle },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.surfaceVariant,
                        contentColor = colors.textPrimary,
                    ),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(stringResource(R.string.update_check))
                }
            }
        }
    }
}

private sealed interface UiState {
    data object Idle : UiState
    data object Checking : UiState
    data object Latest : UiState
    data class Available(val version: com.ahmed.bydvoice.core.update.UpdateLogic.RemoteVersion) : UiState
    data class Downloading(val percent: Int) : UiState
    data object Downloaded : UiState
    data class Error(val message: String) : UiState
}
