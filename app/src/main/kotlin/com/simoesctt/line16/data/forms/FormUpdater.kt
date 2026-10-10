package com.simoesctt.line16.data.forms

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

/**
 * Downloads IRS PDF forms over HTTPS and stores them in the app's files dir.
 *
 * Safety: every downloaded file is hashed and compared against a pinned
 * SHA-256 that ships inside this app. If the hash doesn't match, the file
 * is discarded and the previous version is kept. A MITM, a compromised IRS
 * server, or a corrupted CDN cannot feed us a wrong form.
 */
object FormUpdater {

    private const val IRS_BASE = "https://www.irs.gov/pub/irs-pdf/"

    // Pinned SHA-256 of the currently-shipped, known-good PDF for each year.
    // When you update a form, you compute the hash once and paste it here.
    private val PINNED_SHA256: Map<Int, String> = mapOf(
        2025 to "3d31c226df0d189ced80e039d01cf0f8820c1019681a0f0ca6264de277b7e982",
    )

    /** Where downloaded forms live. Backed up nowhere; cleared on uninstall. */
    fun formFile(context: Context, year: Int): File =
        File(context.filesDir, "forms/f1040_$year.pdf").apply { parentFile?.mkdirs() }

    /** True if a downloaded or asset-bundled form exists for the year. */
    fun hasForm(context: Context, year: Int): Boolean =
        formFile(context, year).exists() || assetExists(context, "f1040_$year.pdf")

    /** Opens the best available form for the year: downloaded, else asset. */
    fun openForm(context: Context, year: Int): java.io.InputStream {
        val downloaded = formFile(context, year)
        return if (downloaded.exists()) downloaded.inputStream()
        else context.assets.open("f1040_$year.pdf")
    }

    private fun assetExists(context: Context, name: String): Boolean =
        runCatching { context.assets.open(name).close() }.isSuccess

    sealed class Result {
        data class Success(val file: File, val sha256: String) : Result()
        data class HashMismatch(val expected: String, val actual: String) : Result()
        data class Error(val message: String) : Result()
    }

    suspend fun download(context: Context, year: Int): Result = withContext(Dispatchers.IO) {
        val expected = PINNED_SHA256[year]
            ?: return@withContext Result.Error("No pinned hash for year $year")
        val url = URL("${IRS_BASE}f1040.pdf")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 30_000
            requestMethod = "GET"
        }
        try {
            conn.connect()
            if (conn.responseCode != 200) {
                return@withContext Result.Error("HTTP ${conn.responseCode}")
            }
            val tmp = File(context.cacheDir, "f1040_$year.part")
            conn.inputStream.use { input ->
                FileOutputStream(tmp).use { output -> input.copyTo(output) }
            }
            val actual = sha256Of(tmp)
            if (!actual.equals(expected, ignoreCase = true)) {
                tmp.delete()
                return@withContext Result.HashMismatch(expected, actual)
            }
            val dest = formFile(context, year)
            tmp.copyTo(dest, overwrite = true)
            tmp.delete()
            Result.Success(dest, actual)
        } catch (e: Exception) {
            Result.Error(e.message ?: "unknown")
        } finally {
            conn.disconnect()
        }
    }

    private fun sha256Of(file: File): String {
        val md = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { stream ->
            val buf = ByteArray(8192)
            while (true) {
                val n = stream.read(buf)
                if (n <= 0) break
                md.update(buf, 0, n)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
}
