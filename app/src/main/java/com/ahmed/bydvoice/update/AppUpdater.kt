package com.ahmed.bydvoice.update

import android.content.Context
import com.ahmed.bydvoice.core.update.UpdateLogic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AppUpdater(
    private val context: Context,
    private val currentVersionCode: Int,
    private val versionJsonUrl: String,
) {

    sealed interface CheckResult {
        data class Available(val version: UpdateLogic.RemoteVersion) : CheckResult
        data object Latest : CheckResult
        data class Failure(val reason: String) : CheckResult
    }

    sealed interface DownloadResult {
        data class Verified(val apkFile: File) : DownloadResult
        data class Corrupted(val expected: String) : DownloadResult
        data class Failure(val reason: String) : DownloadResult
    }

    private fun httpsOnly(url: String): URL {
        val u = URL(url)
        require(u.protocol == "https") { "refusing non-https url" }
        return u
    }

    suspend fun check(): CheckResult = withContext(Dispatchers.IO) {
        var conn: HttpURLConnection? = null
        try {
            conn = (httpsOnly(versionJsonUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 10_000
                instanceFollowRedirects = true
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext CheckResult.Failure("HTTP ${conn.responseCode}")
            }
            val body = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val remote = UpdateLogic.parseVersionJson(body)
            if (UpdateLogic.isUpdateAvailable(currentVersionCode, remote.versionCode)) {
                CheckResult.Available(remote)
            } else {
                CheckResult.Latest
            }
        } catch (e: Exception) {
            CheckResult.Failure(e.message ?: "network error")
        } finally {
            conn?.disconnect()
        }
    }

    suspend fun downloadAndVerify(
        version: UpdateLogic.RemoteVersion,
        onProgress: (Int) -> Unit = {},
    ): DownloadResult = withContext(Dispatchers.IO) {
            var conn: HttpURLConnection? = null
            val out = File(context.cacheDir, "update_${version.versionCode}.apk")
            try {
                if (out.exists()) out.delete()
                conn = (httpsOnly(version.url).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15_000
                    readTimeout = 60_000
                    instanceFollowRedirects = true
                }
                if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext DownloadResult.Failure("HTTP ${conn.responseCode}")
                }
                val total = conn.contentLengthLong.takeIf { it > 0 } ?: -1L
                val digest = java.security.MessageDigest.getInstance("SHA-256")
                var downloaded = 0L
                conn.inputStream.use { input ->
                    FileOutputStream(out).use { output ->
                        val buffer = ByteArray(16 * 1024)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            digest.update(buffer, 0, read)
                            downloaded += read
                            if (total > 0) {
                                onProgress(((downloaded * 100) / total).toInt().coerceIn(0, 100))
                            }
                        }
                    }
                }
                if (total <= 0) onProgress(100)
                val expected = version.sha256.trim().lowercase()
                val actual = digest.digest().joinToString("") { "%02x".format(it) }
                if (expected.length != 64 || actual != expected) {
                    out.delete()
                    return@withContext DownloadResult.Corrupted(version.sha256)
                }
                DownloadResult.Verified(out)
            } catch (e: Exception) {
                if (out.exists()) out.delete()
                DownloadResult.Failure(e.message ?: "download error")
            } finally {
                conn?.disconnect()
            }
        }

    fun installVerified(apkFile: File) {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile,
        )
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    companion object {
        const val DEFAULT_VERSION_URL =
            "https://github.com/Alkaraonge/Ahmed_BYD_Voice/releases/latest/download/version.json"
    }
}
