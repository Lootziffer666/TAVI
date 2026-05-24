package com.example.tavi.shizuku

import rikka.shizuku.Shizuku

object ShizukuManager {

    fun isReady(): Boolean = runCatching { Shizuku.pingBinder() }.getOrElse { false }

    fun checkPermission(): Boolean = runCatching {
        Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
    }.getOrElse { false }

    fun requestPermission(requestCode: Int) {
        runCatching { Shizuku.requestPermission(requestCode) }
    }

    fun executeCommand(command: String): Result<String> = runCatching {
        if (!isReady() || !checkPermission()) error("Shizuku not available or permission denied")
        val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
        val stdout = process.inputStream.bufferedReader().readText()
        val stderr = process.errorStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        // Use exit code as the error signal — many commands write to stderr even on success
        if (exitCode != 0) error("Exit $exitCode: ${stderr.ifBlank { stdout }}")
        // Return stdout; append stderr as a note if both are non-empty (informational only)
        if (stderr.isNotBlank() && stdout.isNotBlank()) "$stdout\n[stderr: $stderr]".trim()
        else stdout.ifBlank { stderr }.trim()
    }
}
