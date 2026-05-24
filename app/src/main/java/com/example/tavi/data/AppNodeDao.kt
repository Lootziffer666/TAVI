package com.example.tavi.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppNodeDao {
    @Query("SELECT * FROM app_nodes ORDER BY userAffinityScore DESC")
    fun getAllNodes(): Flow<List<AppNodeEntity>>

    @Query("SELECT * FROM app_nodes WHERE depthLayer = 0 ORDER BY userAffinityScore DESC LIMIT :limit")
    fun getForeground(limit: Int = 5): Flow<List<AppNodeEntity>>

    @Query("SELECT * FROM app_nodes WHERE depthLayer = -1 ORDER BY userAffinityScore DESC")
    fun getMidground(): Flow<List<AppNodeEntity>>

    @Query("SELECT * FROM app_nodes WHERE depthLayer = -2 AND fossilStatus IS NULL ORDER BY userAffinityScore ASC")
    fun getBackground(): Flow<List<AppNodeEntity>>

    @Query("SELECT * FROM app_nodes ORDER BY userAffinityScore ASC LIMIT :limit")
    fun getLowAffinityNodes(limit: Int = 20): Flow<List<AppNodeEntity>>

    @Query("SELECT * FROM app_nodes WHERE fossilStatus IS NULL ORDER BY userAffinityScore ASC")
    fun getFossilCandidates(): Flow<List<AppNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(nodes: List<AppNodeEntity>)

    @Update
    suspend fun update(node: AppNodeEntity)

    @Query("UPDATE app_nodes SET userAffinityScore = :score WHERE packageName = :pkg")
    suspend fun updateAffinityScore(pkg: String, score: Float)

    @Query("UPDATE app_nodes SET growthStageIndex = :stageIdx, polygonSides = :sides WHERE packageName = :pkg")
    suspend fun updateGrowthStage(pkg: String, stageIdx: Int, sides: Int)

    @Query("UPDATE app_nodes SET depthLayer = :layer WHERE packageName = :pkg")
    suspend fun updateDepthLayer(pkg: String, layer: Int)

    @Query("UPDATE app_nodes SET positionX = :x, positionY = :y WHERE packageName = :pkg")
    suspend fun updateSpatialPosition(pkg: String, x: Float, y: Float)

    @Query("UPDATE app_nodes SET isSpatiallyAnchored = :anchored WHERE packageName = :pkg")
    suspend fun updateAnchor(pkg: String, anchored: Boolean)

    @Query("UPDATE app_nodes SET fossilStatus = :status WHERE packageName = :pkg")
    suspend fun updateFossilStatus(pkg: String, status: String?)

    @Query("UPDATE app_nodes SET launchCount = launchCount + 1, lastLaunchedEpochMs = :timestamp WHERE packageName = :pkg")
    suspend fun recordLaunch(pkg: String, timestamp: Long)

    @Query("DELETE FROM app_nodes WHERE packageName = :pkg")
    suspend fun delete(pkg: String)

    @Query("SELECT packageName FROM app_nodes")
    suspend fun getAllPackageNames(): List<String>
}
