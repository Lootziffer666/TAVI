package com.example.tavi.warden

import com.example.tavi.data.TaviPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TaviWarden(private val prefs: TaviPreferences) {

    val isEmergencyOff: Flow<Boolean> = prefs.wardenEmergencyOff
    val isPrivateMode: Flow<Boolean> = prefs.privateModeEnabled
    val isShizukuEnabled: Flow<Boolean> = prefs.shizukuEnabled
    val isCloudAiEnabled: Flow<Boolean> = prefs.cloudAiEnabled
    val isBotWorkspacesEnabled: Flow<Boolean> = prefs.botWorkspacesEnabled
    val isSessionOnlyMode: Flow<Boolean> = prefs.sessionOnlyMode

    val isFullyOperational: Flow<Boolean> = combine(
        prefs.wardenEmergencyOff,
        prefs.privateModeEnabled
    ) { emergency, private -> !emergency && !private }

    suspend fun enableShizuku() = prefs.setShizukuEnabled(true)
    suspend fun disableShizuku() = prefs.setShizukuEnabled(false)
    suspend fun enablePrivateMode() = prefs.setPrivateModeEnabled(true)
    suspend fun disablePrivateMode() = prefs.setPrivateModeEnabled(false)
    suspend fun enableCloudAi() = prefs.setCloudAiEnabled(true)
    suspend fun disableCloudAi() = prefs.setCloudAiEnabled(false)
    suspend fun enableBotWorkspaces() = prefs.setBotWorkspacesEnabled(true)
    suspend fun disableBotWorkspaces() = prefs.setBotWorkspacesEnabled(false)
    suspend fun enableSessionOnlyMode() = prefs.setSessionOnlyMode(true)
    suspend fun disableSessionOnlyMode() = prefs.setSessionOnlyMode(false)
    suspend fun triggerEmergencyOff() {
        prefs.setShizukuEnabled(false)
        prefs.setCloudAiEnabled(false)
        prefs.setEmergencyOff(true)
    }
}
