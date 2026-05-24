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
        val output = process.inputStream.bufferedReader().readText()
        val error = process.errorStream.bufferedReader().readText()
        process.waitFor()
        if (error.isNotBlank()) error("Command error: $error")
        output.trim()
    }
}
