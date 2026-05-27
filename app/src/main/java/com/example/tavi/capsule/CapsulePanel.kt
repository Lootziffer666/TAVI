package com.example.tavi.capsule

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tavi.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CapsulePanel(
    capsules: List<WorkCapsule>,
    visible: Boolean,
    onCapsuleCopy: (WorkCapsule) -> Unit,
    onCapsuleDelete: (WorkCapsule) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(max = 300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DepthMid)
        ) {
            if (capsules.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No capsules yet. Type  cap: save <title>  or long-press an AI response.",
                        color = FallbackGrey,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                val fmt = SimpleDateFormat("MMM d HH:mm", Locale.getDefault())
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(capsules.sortedByDescending { it.timestamp }, key = { it.id }) { capsule ->
                        CapsuleRow(
                            capsule = capsule,
                            formattedTime = fmt.format(Date(capsule.timestamp)),
                            onCopy = { onCapsuleCopy(capsule) },
                            onDelete = { onCapsuleDelete(capsule) }
                        )
                        HorizontalDivider(color = SpaceNavy.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CapsuleRow(
    capsule: WorkCapsule,
    formattedTime: String,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = capsule.title,
                style = MaterialTheme.typography.labelMedium,
                color = BreathBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = capsule.content.take(60) + if (capsule.content.length > 60) "…" else "",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = FallbackGrey,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${capsule.source.name.lowercase()}  ·  $formattedTime",
                style = MaterialTheme.typography.labelSmall,
                color = FallbackGrey.copy(alpha = 0.6f)
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RiskRed, modifier = Modifier.size(16.dp))
        }
        Button(
            onClick = onCopy,
            modifier = Modifier.height(30.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BreathBlue.copy(alpha = 0.2f))
        ) {
            Text("Copy", style = MaterialTheme.typography.labelSmall, color = BreathBlue)
        }
    }
}
