package com.example.tavi.garden

import com.example.tavi.data.AppNodeEntity
import com.example.tavi.data.AppNodeDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max

class GardenEngine(private val dao: AppNodeDao) {

    private val maxFocusItems = 5

    suspend fun recalculate(nodes: List<AppNodeEntity>): List<AppNodeEntity> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        nodes.map { node ->
            val frequencyScore = if (node.launchCount > 0) ln(node.launchCount + 1.0) / ln(61.0) else 0.0
            val hoursElapsed = (now - node.lastLaunchedEpochMs) / 3_600_000.0
            val recencyScore = exp(-0.1 * hoursElapsed)
            val affinityScore = (0.4 * frequencyScore + 0.4 * recencyScore + 0.2 * node.userAffinityScore)
                .toFloat()
                .coerceIn(0f, 1f)
            val stage = GrowthStage.from(node.launchCount)
            node.copy(
                userAffinityScore = affinityScore,
                growthStageIndex = stage.ordinal,
                polygonSides = stage.polygonSides
            )
        }
    }

    suspend fun assignLayers(nodes: List<AppNodeEntity>): List<AppNodeEntity> = withContext(Dispatchers.IO) {
        val sorted = nodes.sortedByDescending { it.userAffinityScore }
        sorted.mapIndexed { index, node ->
            val layer = when {
                node.isSpatiallyAnchored || index < maxFocusItems -> 0
                index < maxFocusItems + 10 -> -1
                else -> -2
            }
            node.copy(depthLayer = layer)
        }
    }

    suspend fun persistUpdates(updated: List<AppNodeEntity>) = withContext(Dispatchers.IO) {
        updated.forEach { node ->
            dao.updateAffinityScore(node.packageName, node.userAffinityScore)
            dao.updateGrowthStage(node.packageName, node.growthStageIndex, node.polygonSides)
            dao.updateDepthLayer(node.packageName, node.depthLayer)
        }
    }

    suspend fun recordLaunch(packageName: String) = withContext(Dispatchers.IO) {
        dao.recordLaunch(packageName, System.currentTimeMillis())
    }

    suspend fun markAsFossil(packageName: String) = withContext(Dispatchers.IO) {
        dao.updateFossilStatus(packageName, "removed_candidate")
        dao.updateAffinityScore(packageName, 0f)
        dao.updateDepthLayer(packageName, -2)
    }

    suspend fun toggleAnchor(packageName: String, anchored: Boolean) = withContext(Dispatchers.IO) {
        dao.updateAnchor(packageName, anchored)
    }
}
