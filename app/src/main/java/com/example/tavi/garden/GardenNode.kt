package com.example.tavi.garden

import android.graphics.drawable.Drawable

data class GardenNode(
    val packageName: String,
    val label: String,
    val icon: Drawable?,
    val positionX: Float,
    val positionY: Float,
    val depthLayer: Int,
    val colorHue: Float,
    val affinityScore: Float,
    val isSpatiallyAnchored: Boolean,
    val scopeTag: String?,
    val launchCount: Int,
    val growthStage: GrowthStage,
    val animationPhaseOffset: Float = (packageName.hashCode() and 0x7FFFFFFF) / Int.MAX_VALUE.toFloat()
)
