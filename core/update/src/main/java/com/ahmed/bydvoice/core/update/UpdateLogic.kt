package com.ahmed.bydvoice.core.update

import java.security.MessageDigest

/**
 * منطق المُحدِّث الصحيح — الدرس المستفاد من فشل v1.0.5:
 *
 * 1. المقارنة **رقمية** بـ versionCode: ‏`remote > current`.
 *    مقارنة النصوص ("1.0.5" != داخلي) تسبب حلقة تحديث لا نهائية.
 * 2. التحقق من sha256 **قبل** تثبيت أي ملف منزّل — وإلا أي استجابة
 *    معدّلة يمكن أن تُستبدل بها الـAPK.
 */
object UpdateLogic {

    data class RemoteVersion(
        val versionCode: Int,
        val versionName: String,
        val url: String,
        val sha256: String,
        val changelog: String = "",
    )

    fun isUpdateAvailable(currentVersionCode: Int, remoteVersionCode: Int): Boolean =
        remoteVersionCode > currentVersionCode

    /**
     * تحليل version.json بأقل كود ممكن — بدون مكتبة JSON ثقيلة.
     * العقد المطلوبة: versionCode, versionName, url, sha256
     */
    fun parseVersionJson(json: String): RemoteVersion {
        // يقبل القيمة نصاً ("120") أو رقماً (120) — حتى لا يفشل يدوياً عدّل الـJSON
        fun field(name: String): String {
            val m = Regex("\"$name\"\\s*:\\s*\"([^\"]+)\"").find(json)
                ?: Regex("\"$name\"\\s*:\\s*([0-9]+)").find(json)
                ?: throw IllegalArgumentException("missing field: $name")
            return m.groupValues[1]
        }
        fun optionalField(name: String): String =
            Regex("\"$name\"\\s*:\\s*\"([^\"]+)\"").find(json)?.groupValues?.get(1) ?: ""
        return RemoteVersion(
            versionCode = field("versionCode").toInt(),
            versionName = field("versionName"),
            url = field("url"),
            sha256 = field("sha256").lowercase(),
            changelog = optionalField("changelog"),
        )
    }

    fun sha256Hex(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }

    fun verifySha256(bytes: ByteArray, expectedSha256: String): Boolean =
        sha256Hex(bytes) == expectedSha256.lowercase()
}
