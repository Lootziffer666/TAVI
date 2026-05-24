package com.example.tavi.garden

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.tavi.data.AppNodeDao
import com.example.tavi.data.AppNodeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GardenRepository(private val dao: AppNodeDao, private val context: Context) {

    fun foregroundNodes(limit: Int = 5): Flow<List<GardenNode>> =
        dao.getForeground(limit).map { entities -> entities.mapToNodes() }

    fun midgroundNodes(): Flow<List<GardenNode>> =
        dao.getMidground().map { entities -> entities.mapToNodes() }

    fun backgroundNodes(): Flow<List<GardenNode>> =
        dao.getBackground().map { entities -> entities.mapToNodes() }

    fun fossilCandidates(): Flow<List<GardenNode>> =
        dao.getFossilCandidates().map { it.mapToNodes() }

    private fun List<AppNodeEntity>.mapToNodes(): List<GardenNode> = map { entity ->
        GardenNode(
            packageName = entity.packageName,
            label = entity.appLabel,
            icon = loadIcon(entity.packageName),
            positionX = entity.positionX,
            positionY = entity.positionY,
            depthLayer = entity.depthLayer,
            colorHue = entity.colorHue,
            affinityScore = entity.userAffinityScore,
            isSpatiallyAnchored = entity.isSpatiallyAnchored,
            scopeTag = entity.scopeTag,
            launchCount = entity.launchCount,
            growthStage = GrowthStage.from(entity.launchCount)
        )
    }

    private fun loadIcon(packageName: String): Drawable? = runCatching {
        context.packageManager.getApplicationIcon(packageName)
    }.getOrNull()
}
