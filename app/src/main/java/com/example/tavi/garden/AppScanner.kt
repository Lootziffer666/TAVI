package com.example.tavi.garden

import android.content.Context
import android.content.pm.PackageManager
import com.example.tavi.data.AppNodeDao
import com.example.tavi.data.AppNodeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

class AppScanner(private val context: Context, private val dao: AppNodeDao) {

    suspend fun syncInstalledApps() = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }
        val installed = pm.queryIntentActivities(intent, PackageManager.GET_META_DATA)
            .map { it.activityInfo.packageName }
            .filter { it != context.packageName }
            .toSet()

        val existing = dao.getAllPackageNames().toSet()
        val newApps = installed - existing
        val removedApps = existing - installed

        val newEntities = newApps.mapNotNull { pkg ->
            runCatching {
                val info = pm.getApplicationInfo(pkg, 0)
                val label = pm.getApplicationLabel(info).toString()
                val hue = (abs(pkg.hashCode()) % 360).toFloat()
                AppNodeEntity(
                    packageName = pkg,
                    appLabel = label,
                    colorHue = hue
                )
            }.getOrNull()
        }

        dao.insertAll(newEntities)
        removedApps.forEach { dao.delete(it) }
    }
}
