package com.example.tavi.garden

enum class GrowthStage(
    val minLaunches: Int,
    val polygonSides: Int,
    val glowRadius: Float,
    val alpha: Float
) {
    SEED(0, 3, 4f, 0.15f),
    SPROUT(5, 5, 8f, 0.35f),
    PLANT(20, 6, 14f, 0.65f),
    BLOOM(60, 8, 24f, 1.0f);

    companion object {
        fun from(launchCount: Int): GrowthStage = when {
            launchCount >= BLOOM.minLaunches -> BLOOM
            launchCount >= PLANT.minLaunches -> PLANT
            launchCount >= SPROUT.minLaunches -> SPROUT
            else -> SEED
        }
    }
}
