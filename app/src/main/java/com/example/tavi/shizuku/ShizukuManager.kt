package com.example.tavi.shizuku

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku

object ShizukuManager {

    fun isReady(): Boolean = runCatching { Shizuku.pingBinder() }.getOrElse { false }

    fun checkPermission(): Boolean = runCatching {
        Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
    }.getOrElse { false }

    fun requestPermission(requestCode: Int) {
        runCatching { Shizuku.requestPermission(requestCode) }
    }

    suspend fun executeCommand(command: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            if (!isReady() || !checkPermission()) error("Shizuku not available or permission denied")
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)

            // Read stdout and stderr concurrently — sequential reads deadlock when the process
            // fills one pipe buffer while we're blocked draining the other.
            val stdoutDeferred = async { process.inputStream.bufferedReader().readText() }
            val stderrDeferred = async { process.errorStream.bufferedReader().readText() }
            val stdout = stdoutDeferred.await()
            val stderr = stderrDeferred.await()
            val exitCode = process.waitFor()

            if (exitCode != 0) error("Exit $exitCode: ${stderr.ifBlank { stdout }}")
            if (stderr.isNotBlank() && stdout.isNotBlank()) "$stdout\n[stderr: $stderr]".trim()
            else stdout.ifBlank { stderr }.trim()
        }
    }
}
