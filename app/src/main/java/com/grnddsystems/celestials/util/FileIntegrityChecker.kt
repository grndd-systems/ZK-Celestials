package com.grnddsystems.celestials.util

import android.content.Context
import android.content.SharedPreferences
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.Security

object FileIntegrityChecker {

    private const val PREFS_NAME = "file_integrity_cache"
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun computeMD5(file: File): String {
        require(file.exists()) { "File does not exist: ${file.absolutePath}" }

        // Use native provider (Conscrypt/AndroidOpenSSL) instead of BouncyCastle for performance
        val md = try {
            MessageDigest.getInstance("MD5", "AndroidOpenSSL")
        } catch (e: Exception) {
            MessageDigest.getInstance("MD5")
        }
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(1024 * 1024)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }

        return md.digest().joinToString("") { "%02x".format(it) }
    }

    fun verifyFileMD5(file: File, expectedMD5: String): Boolean {
        return try {
            if (expectedMD5.isBlank()) return file.exists()

            if (!file.exists()) return false

            // Check cache: if file size and lastModified match a previous successful verification, skip MD5
            val cacheKey = file.absolutePath
            val cached = prefs?.getString(cacheKey, null)
            if (cached != null) {
                val parts = cached.split("|")
                if (parts.size == 3) {
                    val cachedSize = parts[0].toLongOrNull()
                    val cachedModified = parts[1].toLongOrNull()
                    val cachedHash = parts[2]
                    if (cachedSize == file.length() &&
                        cachedModified == file.lastModified() &&
                        cachedHash.equals(expectedMD5, ignoreCase = true)
                    ) {
                        return true
                    }
                }
            }

            // Full MD5 verification
            val computedMD5 = computeMD5(file)
            val matches = computedMD5.equals(expectedMD5, ignoreCase = true)

            if (matches) {
                // Cache the result
                prefs?.edit()
                    ?.putString(cacheKey, "${file.length()}|${file.lastModified()}|$computedMD5")
                    ?.apply()
            }

            matches
        } catch (e: IOException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
