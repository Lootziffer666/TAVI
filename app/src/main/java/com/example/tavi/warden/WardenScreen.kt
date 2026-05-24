package com.example.tavi.warden

import androidx.compose.foundation.BorderStroke
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
import com.example.tavi.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WardenScreen(
    warden: TaviWarden,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isShizukuEnabled by warden.isShizukuEnabled.collectAsStateWithLifecycle(false)
    val isPrivateMode by warden.isPrivateMode.collectAsStateWithLifecycle(false)
    val isCloudAi by warden.isCloudAiEnabled.collectAsStateWithLifecycle(false)
    val isBotWorkspaces by warden.isBotWorkspacesEnabled.collectAsStateWithLifecycle(true)
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

        Spacer(Modifier.height(32.dp))

        // Emergency off — prominent destructive action
        OutlinedButton(
            onClick = {
                scope.launch {
                    warden.triggerEmergencyOff()
                }
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
