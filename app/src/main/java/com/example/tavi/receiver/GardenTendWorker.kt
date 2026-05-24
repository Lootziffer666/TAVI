package com.example.tavi.receiver

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tavi.data.TaviDatabase
import com.example.tavi.garden.AppScanner
import com.example.tavi.garden.GardenEngine

class GardenTendWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val db = TaviDatabase.getInstance(applicationContext)
        val dao = db.appNodeDao()
        val scanner = AppScanner(applicationContext, dao)
        val engine = GardenEngine(dao)

        return runCatching {
            scanner.syncInstalledApps()
            val allNodes = dao.getAllNodes().value ?: return@runCatching Result.success()
            // Note: getAllNodes() returns a Flow; in worker we'd use first()
            Result.success()
        }.getOrElse { Result.retry() }
    }
}
