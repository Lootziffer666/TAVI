package com.example.tavi.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_nodes")
data class AppNodeEntity(
    @PrimaryKey val packageName: String,
    val appLabel: String = "",
    val launchCount: Int = 0,
    val lastLaunchedEpochMs: Long = 0L,
    val depthLayer: Int = -2,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val colorHue: Float = 0f,
    val userAffinityScore: Float = 0f,
    val growthStageIndex: Int = 0,
    val polygonSides: Int = 3,
    val isSpatiallyAnchored: Boolean = false,
    val scopeTag: String? = null,
    val fossilStatus: String? = null
)
