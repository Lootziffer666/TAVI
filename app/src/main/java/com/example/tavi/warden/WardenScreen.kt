package com.example.tavi.warden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tavi.ai.ModuleHealth
import com.example.tavi.ai.ModuleStatus
import com.example.tavi.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun WardenScreen(
    warden: TaviWarden,
    onClose: () -> Unit,
    moduleHealth: ModuleHealth = ModuleHealth(),
    modifier: Modifier = Modifier
) {
    val isShizukuEnabled by warden.isShizukuEnabled.collectAsStateWithLifecycle(false)
    val isPrivateMode by warden.isPrivateMode.collectAsStateWithLifecycle(false)
    val isCloudAi by warden.isCloudAiEnabled.collectAsStateWithLifecycle(false)
    val isBotWorkspaces by warden.isBotWorkspacesEnabled.collectAsStateWithLifecycle(true)
    val isSessionOnly by warden.isSessionOnlyMode.collectAsStateWithLifecycle(false)
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Warden",
                style = MaterialTheme.typography.headlineMedium,
                color = TaviAccent
            )
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onClose) {
                Text("Close", color = Color.Gray)
            }
        }

        Text(
            "Control what TAVI can access. Everything off by default.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp, top = 4.dp)
        )

        WardenToggleRow(
            title = "Private room",
            subtitle = "No cloud calls, no DataStore writes during this session",
            checked = isPrivateMode,
            onCheckedChange = { enabled ->
                scope.launch {
                    if (enabled) warden.enablePrivateMode() else warden.disablePrivateMode()
                }
            },
            accentColor = PrivatePurple
        )

        HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))

        WardenToggleRow(
            title = "Session-only customization",
            subtitle = "Anchors, scope tags, and bot changes this session won't persist after restart",
            checked = isSessionOnly,
            onCheckedChange = { enabled ->
                scope.launch {
                    if (enabled) warden.enableSessionOnlyMode() else warden.disableSessionOnlyMode()
                }
            },
            accentColor = TaviAccent
        )

        HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))

        WardenToggleRow(
            title = "Shizuku power adapter",
            subtitle = "Enables ! commands via Shizuku shell. Requires Shizuku app installed.",
            checked = isShizukuEnabled,
            onCheckedChange = { enabled ->
                scope.launch {
                    if (enabled) warden.enableShizuku() else warden.disableShizuku()
                }
            },
            accentColor = GlowAmber
        )

        HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))

        WardenToggleRow(
            title = "Cloud AI fallback",
            subtitle = "Uses Gemini API when local Gemma model is unavailable. Requires API key.",
            checked = isCloudAi,
            onCheckedChange = { enabled ->
                scope.launch {
                    if (enabled) warden.enableCloudAi() else warden.disableCloudAi()
                }
            },
            accentColor = TaviAccent
        )

        HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))

        WardenToggleRow(
            title = "Bot workspaces",
            subtitle = "Show AI tool pages (ChatGPT, Claude, Gemini, etc.)",
            checked = isBotWorkspaces,
            onCheckedChange = { enabled ->
                scope.launch {
                    if (enabled) warden.enableBotWorkspaces() else warden.disableBotWorkspaces()
                }
            },
            accentColor = BreathBlue
        )

        Spacer(Modifier.height(24.dp))

        // Module health — shown only when something is degraded
        if (!moduleHealth.isFullyOk) {
            HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(bottom = 16.dp))
            Text(
                "Module status",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ModuleHealthRow("Sensor", moduleHealth.sensor)
            ModuleHealthRow("Local AI", moduleHealth.localAI)
            ModuleHealthRow("Cloud AI", moduleHealth.cloudAI)
            ModuleHealthRow("Garden", moduleHealth.garden)
            Spacer(Modifier.height(16.dp))
        }

        // Emergency off
        OutlinedButton(
            onClick = {
                scope.launch { warden.triggerEmergencyOff() }
                onClose()
            },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RiskRed),
            border = androidx.compose.foundation.BorderStroke(1.dp, RiskRed),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Emergency off — disable all power adapters")
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "TAVI never accesses Accessibility Services. Shizuku is optional.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun ModuleHealthRow(name: String, status: ModuleStatus) {
    val (label, color) = when (status) {
        ModuleStatus.OK -> return  // don't show healthy modules
        ModuleStatus.DEGRADED -> "degraded" to GlowAmber
        ModuleStatus.FAILED -> "unavailable" to RiskRed
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(name, style = MaterialTheme.typography.bodyMedium, color = Color.White, modifier = Modifier.weight(1f))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

@Composable
private fun WardenToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = Color.White)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.3f)
            )
        )
    }
}
