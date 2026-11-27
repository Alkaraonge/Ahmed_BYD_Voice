package com.ahmed.bydvoice.core.update

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateLogicTest {

    @Test
    fun `numeric compare - remote greater is update`() {
        assertTrue(UpdateLogic.isUpdateAvailable(currentVersionCode = 100, remoteVersionCode = 101))
    }

    @Test
    fun `numeric compare - equal is not update`() {
        // فخ مقارنة النصوص: نفس الرقم يجب أن يعني "لا تحديث"
        assertFalse(UpdateLogic.isUpdateAvailable(currentVersionCode = 105, remoteVersionCode = 105))
    }

    @Test
    fun `numeric compare - remote lower is not update`() {
        assertFalse(UpdateLogic.isUpdateAvailable(currentVersionCode = 200, remoteVersionCode = 105))
    }

    @Test
    fun `numeric compare - big remote beats small current even lexically smaller`() {
        // نفس الثغرة اللي ضربت v1.0.5: "100" مقابل "999999" نصياً تنكسر، رقمياً تشتغل
        assertTrue(UpdateLogic.isUpdateAvailable(currentVersionCode = 100, remoteVersionCode = 999999))
    }

    @Test
    fun `parse version json extracts all fields`() {
        val json = "{\"versionCode\":\"110\",\"versionName\":\"1.1.0\",\"url\":\"https://example.com/app.apk\",\"sha256\":\"ABCD1234\"}"
        val v = UpdateLogic.parseVersionJson(json)
        assertEquals(110, v.versionCode)
        assertEquals("1.1.0", v.versionName)
        assertEquals("https://example.com/app.apk", v.url)
        assertEquals("abcd1234", v.sha256)
    }

    @Test
    fun `parse version json extracts changelog when present`() {
        val withLog = "{\"versionCode\":\"120\",\"versionName\":\"1.2.0\",\"url\":\"https://x/a.apk\",\"sha256\":\"ab\",\"changelog\":\"إصلاحات\"}"
        val withoutLog = "{\"versionCode\":\"120\",\"versionName\":\"1.2.0\",\"url\":\"https://x/a.apk\",\"sha256\":\"ab\"}"
        assertEquals("إصلاحات", UpdateLogic.parseVersionJson(withLog).changelog)
        assertEquals("", UpdateLogic.parseVersionJson(withoutLog).changelog)
    }

    @Test
    fun `sha256 verify accepts matching digest and rejects mismatch`() {
        val bytes = "ahmed-byd-voice".toByteArray()
        val digest = UpdateLogic.sha256Hex(bytes)
        assertTrue(UpdateLogic.verifySha256(bytes, digest))
        assertFalse(UpdateLogic.verifySha256(bytes, "0".repeat(64)))
    }
}
