package com.example.tavi.shell

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.tavi.state.PendingAction
import com.example.tavi.ui.theme.*

@Composable
fun ActionPreflightCard(
    action: PendingAction,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (typeLabel, commandLine, reversibility) = when (action) {
        is PendingAction.ShellCommand -> Triple(
            "Shell command",
            action.translated ?: action.executable,
            "Depends on command. Some effects persist until manually reversed."
        )
        is PendingAction.DemoteApp -> Triple(
            "Remove from home",
            action.label,
            "Re-swipe right in Fossil Deck to restore."
        )
        is PendingAction.PromoteApp -> Triple(
            "Pin to home",
            action.label,
            "Long-press the app node to unpin."
        )
        is PendingAction.ScopeChange -> Triple(
            "Scope change",
            "${action.from ?: "none"} → ${action.to}",
            "Tap the scope chip again or type a new scope to change."
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DepthMid),
        border = androidx.compose.foundation.BorderStroke(1.dp, RiskRed.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = GlowAmber, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(typeLabel, style = MaterialTheme.typography.labelLarge, color = GlowAmber)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = commandLine,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = TaviAccent
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = reversibility,
                style = MaterialTheme.typography.bodySmall,
                color = FallbackGrey
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FallbackGrey)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RiskRed)
                ) {
                    Text("Execute")
                }
            }
        }
    }
}
