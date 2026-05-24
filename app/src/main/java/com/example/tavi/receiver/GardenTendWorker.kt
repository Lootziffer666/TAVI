package com.example.tavi.receiver

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tavi.data.TaviDatabase
import com.example.tavi.garden.AppScanner
import com.example.tavi.garden.GardenEngine
import kotlinx.coroutines.flow.first

class GardenTendWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val db = TaviDatabase.getInstance(applicationContext)
        val dao = db.appNodeDao()
        val scanner = AppScanner(applicationContext, dao)
        val engine = GardenEngine(dao)

        return runCatching {
            scanner.syncInstalledApps()
            val allNodes = dao.getAllNodes().first()
            val recalculated = engine.recalculate(allNodes)
            val layered = engine.assignLayers(recalculated)
            engine.persistUpdates(layered)
            Result.success()
        }.getOrElse { Result.retry() }
    }
}
