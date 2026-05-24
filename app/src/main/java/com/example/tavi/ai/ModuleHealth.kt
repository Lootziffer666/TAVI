package com.example.tavi.ai

enum class ModuleStatus { OK, DEGRADED, FAILED }

data class ModuleHealth(
    val sensor: ModuleStatus = ModuleStatus.OK,
    val localAI: ModuleStatus = ModuleStatus.OK,
    val cloudAI: ModuleStatus = ModuleStatus.OK,
    val garden: ModuleStatus = ModuleStatus.OK
) {
    val isFullyOk: Boolean
        get() = sensor == ModuleStatus.OK &&
                localAI == ModuleStatus.OK &&
                cloudAI == ModuleStatus.OK &&
                garden == ModuleStatus.OK

    val degradedSummary: String
        get() = buildList {
            if (sensor != ModuleStatus.OK) add("Sensor")
            if (localAI != ModuleStatus.OK) add("Local AI")
            if (cloudAI != ModuleStatus.OK) add("Cloud AI")
            if (garden != ModuleStatus.OK) add("Garden")
        }.joinToString(", ")
}
