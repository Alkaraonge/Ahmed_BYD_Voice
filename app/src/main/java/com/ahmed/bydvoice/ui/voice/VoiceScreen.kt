package com.ahmed.bydvoice.ui.voice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ahmed.bydvoice.R
import com.ahmed.bydvoice.core.theme.BYDColorScheme
import com.ahmed.bydvoice.core.theme.BYDThemeRegistry
import com.ahmed.bydvoice.core.voice.VoiceCommand
import com.ahmed.bydvoice.core.voice.VoiceCommandParser
import com.ahmed.bydvoice.core.voice.VoiceMode
import com.ahmed.bydvoice.core.voice.VoiceTab
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import org.vosk.android.RecognitionListener as VoskRecognitionListener

/**
 * تبويب التحكم الصوتي: اضغط وتكلم بالعراقي، والمفسّر ينفذ.
 * وضعان: أونلاين (خدمة أندرويد) أو أوفلاين كامل (Vosk — الموديل ينزّل مرة وحدة).
 * التفسير دائماً أوفلاين في وحدة core:voice.
 */
@Composable
fun VoiceScreen(
    colors: BYDColorScheme,
    onCommand: (VoiceCommand) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var listening by remember { mutableStateOf(false) }
    var heard by remember { mutableStateOf<String?>(null) }
    var applied by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf(false) }

    // ---- أوفلاين (Vosk) ----
    val engine = remember { OfflineSpeechEngine(context) }
    var offline by remember { mutableStateOf(false) }
    var engineState by remember { mutableStateOf<OfflineSpeechEngine.State>(OfflineSpeechEngine.State.NoModel) }
    var voskModel by remember { mutableStateOf<Model?>(null) }
    var voskRecognizer by remember { mutableStateOf<Recognizer?>(null) }
    var voskService by remember { mutableStateOf<SpeechService?>(null) }
    var downloadPercent by remember { mutableIntStateOf(0) }

    // إذا الموديل منزّل سلفاً نحمله للذاكرة أول ما يُختار الأوفلاين
    LaunchedEffect(offline) {
        if (offline && voskModel == null && engine.isModelPresent() &&
            engineState is OfflineSpeechEngine.State.NoModel
        ) {
            engineState = engine.loadModel().also { st ->
                if (st is OfflineSpeechEngine.State.Ready) voskModel = st.model
            }
        }
    }

    val recognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }
    DisposableEffect(Unit) {
        onDispose {
            runCatching { recognizer?.destroy() }
            runCatching { voskService?.stop() }
            runCatching { voskService?.shutdown() }
            runCatching { voskRecognizer?.close() }
            runCatching { voskModel?.close() }
        }
    }

    fun commandLabel(cmd: VoiceCommand): String = when (cmd) {
        is VoiceCommand.SetMode -> context.getString(
            when (cmd.mode) {
                VoiceMode.DAY -> R.string.mode_day
                VoiceMode.NIGHT -> R.string.mode_night
                VoiceMode.AUTO -> R.string.mode_auto
            }
        )
        is VoiceCommand.SetTheme -> BYDThemeRegistry.get(cmd.themeId).nameAr
        VoiceCommand.CheckUpdate -> context.getString(R.string.tab_update)
        is VoiceCommand.OpenTab -> context.getString(
            when (cmd.tab) {
                VoiceTab.THEMES -> R.string.tab_themes
                VoiceTab.PREVIEW -> R.string.tab_preview
                VoiceTab.UPDATE -> R.string.tab_update
                VoiceTab.VOICE -> R.string.tab_voice
            }
        )
        is VoiceCommand.Unknown -> context.getString(R.string.voice_error)
    }

    // النص المسمع — مشترك بين الوضعين
    fun handleHeardText(text: String?) {
        if (text.isNullOrBlank()) { error = true; return }
        heard = text
        when (val cmd = VoiceCommandParser.parse(text)) {
            is VoiceCommand.Unknown -> error = true
            else -> {
                onCommand(cmd)
                applied = commandLabel(cmd)
            }
        }
    }

    fun beginOnlineListening() {
        val r = recognizer ?: run { error = true; return }
        r.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                listening = true; error = false
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { listening = false }
            override fun onError(errorCode: Int) { listening = false; error = true }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onResults(results: Bundle?) {
                listening = false
                handleHeardText(
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                )
            }
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-IQ")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.voice_listening))
        }
        r.startListening(intent)
    }

    fun beginOfflineListening() {
        val model = voskModel ?: run { error = true; return }
        val service = voskService ?: run {
            runCatching {
                val rec = Recognizer(model, 16000.0f).also { voskRecognizer = it }
                SpeechService(rec, 16000.0f)
            }.getOrNull()?.also { voskService = it }
        } ?: run { error = true; return }

        error = false
        val voskListener = object : VoskRecognitionListener {
            override fun onPartialResult(hypothesis: String?) {}
            override fun onResult(hypothesis: String?) {
                val text = hypothesis?.let { extractVoskText(it) }?.trim()
                if (!text.isNullOrBlank()) {
                    runCatching { service.stop() }
                    listening = false
                    handleHeardText(text)
                }
            }
            override fun onFinalResult(hypothesis: String?) {
                val wasListening = listening
                listening = false
                if (wasListening) {
                    val text = hypothesis?.let { extractVoskText(it) }?.trim()
                    if (!text.isNullOrBlank()) {
                        handleHeardText(text)
                    }
                }
            }
            override fun onError(e: Exception?) {
                runCatching { service.stop() }
                listening = false
                error = true
            }
            override fun onTimeout() {
                runCatching { service.stop() }
                listening = false
            }
        }
        runCatching { service.startListening(voskListener, 10000) }
            .onSuccess { listening = true }
            .onFailure {
                listening = false
                error = true
            }
    }

    fun beginListening() {
        if (offline) beginOfflineListening() else beginOnlineListening()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) beginListening() else error = true }

    fun onMicClick() {
        when {
            context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED -> beginListening()
            else -> permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // نبض زر المايك أثناء الاستماع
    val pulse by rememberInfiniteTransition(label = "micPulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "micPulse",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = stringResource(R.string.voice_title),
            style = MaterialTheme.typography.headlineMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.voice_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
        )

        // مفتاح الوضع: أونلاين / أوفلاين
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModeToggleChip(
                label = stringResource(R.string.voice_mode_online),
                active = !offline,
                colors = colors,
                onClick = { offline = false },
            )
            ModeToggleChip(
                label = stringResource(R.string.voice_mode_offline),
                active = offline,
                colors = colors,
                onClick = { offline = true },
            )
        }

        // حالة الموديل الأوفلاين
        if (offline) {
            when (val st = engineState) {
                is OfflineSpeechEngine.State.Downloading -> Text(
                    text = stringResource(R.string.voice_downloading, st.percent),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.accent,
                )
                is OfflineSpeechEngine.State.Ready -> Text(
                    text = stringResource(R.string.voice_model_ready),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.primary,
                )
                OfflineSpeechEngine.State.Failed -> Text(
                    text = stringResource(R.string.voice_model_failed),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.accent,
                )
                OfflineSpeechEngine.State.NoModel -> {
                    Button(
                        onClick = {
                            scope.launch {
                                engineState = OfflineSpeechEngine.State.Downloading(0)
                                engineState = engine.downloadAndPrepare { p ->
                                    downloadPercent = p
                                    engineState = OfflineSpeechEngine.State.Downloading(p)
                                }.also { result ->
                                    if (result is OfflineSpeechEngine.State.Ready) {
                                        voskModel = result.model
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                    ) {
                        Text(
                            stringResource(R.string.voice_download_model),
                            color = colors.onPrimary,
                        )
                    }
                    Text(
                        text = stringResource(R.string.voice_offline_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // زر المايك الكبير
        Box(
            modifier = Modifier
                .size((110 * if (listening) pulse else 1f).dp)
                .clip(CircleShape)
                .background(
                    when {
                        listening -> colors.accent
                        error -> colors.accent.copy(alpha = 0.5f)
                        else -> colors.primary
                    }
                )
                .border(2.dp, colors.glowColor, CircleShape)
                .clickable(onClick = ::onMicClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Mic,
                contentDescription = stringResource(R.string.voice_listen),
                tint = colors.onPrimary,
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            text = stringResource(
                when {
                    listening -> R.string.voice_listening
                    error -> R.string.voice_error
                    else -> R.string.voice_listen
                }
            ),
            style = MaterialTheme.typography.titleMedium,
            color = if (error) colors.accent else colors.textPrimary,
        )

        // آخر جملة مسموعة
        heard?.let { h ->
            Surface(shape = RoundedCornerShape(14.dp), color = colors.surface) {
                Text(
                    text = stringResource(R.string.voice_heard, h),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        applied?.let { a ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = colors.accent.copy(alpha = 0.15f),
            ) {
                Text(
                    text = stringResource(R.string.voice_applied, a),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.accent,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp),
                )
            }
        }

        Spacer(Modifier.height(2.dp))

        Text(
            text = stringResource(R.string.voice_hint),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        if (recognizer == null && !offline) {
            Text(
                text = stringResource(R.string.voice_permission),
                style = MaterialTheme.typography.bodySmall,
                color = colors.accent,
            )
        }
    }
}

@Composable
private fun ModeToggleChip(
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

/** نتيجة Vosk تأتي JSON — نستخرج حقل text فقط */
private fun extractVoskText(json: String): String? = runCatching {
    JSONObject(json).optString("text").takeIf { it.isNotBlank() }
}.getOrNull()
