package com.ahmed.bydvoice.ui.voice

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.vosk.Model
import java.io.BufferedInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

class OfflineSpeechEngine(private val context: Context) {

    sealed interface State {
        data object NoModel : State
        data class Downloading(val percent: Int) : State
        data class Ready(val model: Model) : State
        data object Failed : State
    }

    private val modelDir: File get() = File(context.filesDir, "vosk-model-ar")

    private fun resolveModelRoot(): File? {
        if (File(modelDir, "am").isDirectory && File(modelDir, "graph").isDirectory) {
            return modelDir
        }
        return modelDir.listFiles()?.firstOrNull { sub ->
            sub.isDirectory && File(sub, "am").isDirectory && File(sub, "graph").isDirectory
        }
    }

    fun isModelPresent(): Boolean = resolveModelRoot() != null

    suspend fun loadModel(): State = withContext(Dispatchers.IO) {
        val actualRoot = resolveModelRoot() ?: return@withContext State.Failed
        runCatching { State.Ready(Model(actualRoot.absolutePath)) }
            .getOrElse { State.Failed }
    }

    suspend fun downloadAndPrepare(onProgress: (Int) -> Unit): State = withContext(Dispatchers.IO) {
        val tmpZip = File(context.cacheDir, "vosk-ar.zip")
        val root = modelDir.canonicalFile
        var conn: HttpURLConnection? = null
        try {
            val url = URL(MODEL_URL)
            require(url.protocol == "https") { "refusing non-https model url" }
            conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 20_000
                readTimeout = 120_000
                instanceFollowRedirects = true
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext State.Failed
            }

            val total = conn.contentLengthLong
            var read = 0L
            BufferedInputStream(conn.inputStream).use { inp ->
                tmpZip.outputStream().use { out ->
                    val buf = ByteArray(64 * 1024)
                    while (true) {
                        val n = inp.read(buf)
                        if (n <= 0) break
                        out.write(buf, 0, n)
                        read += n
                        if (total > 0L) {
                            onProgress(((read * 100L) / total).toInt().coerceIn(0, 99))
                        }
                    }
                }
            }

            root.deleteRecursively()
            root.mkdirs()
            val rootPrefix = root.path + File.separator

            ZipInputStream(tmpZip.inputStream().buffered()).use { zin ->
                while (true) {
                    val entry = zin.nextEntry ?: break
                    val outFile = File(root, entry.name).canonicalFile
                    if (outFile.path != root.path && !outFile.path.startsWith(rootPrefix)) {
                        zin.closeEntry()
                        root.deleteRecursively()
                        return@withContext State.Failed
                    }
                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        outFile.outputStream().use { zin.copyTo(it) }
                    }
                    zin.closeEntry()
                }
            }

            if (!isModelPresent()) {
                root.deleteRecursively()
                return@withContext State.Failed
            }
            onProgress(100)
            loadModel()
        } catch (_: Throwable) {
            root.deleteRecursively()
            State.Failed
        } finally {
            conn?.disconnect()
            tmpZip.delete()
        }
    }

    companion object {
        const val MODEL_URL =
            "https://alphacephei.com/vosk/models/vosk-model-small-ar-0.22.zip"
    }
}
