package com.example.tavi.clipboard

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tavi.quickaction.QuickActionSuggester
import com.example.tavi.quickaction.QuickActionType
import com.example.tavi.ui.theme.BreathBlue
import com.example.tavi.ui.theme.DepthMid
import com.example.tavi.ui.theme.TaviAccent
import com.example.tavi.workspace.BotInfo

@Composable
fun ClipPanel(
    clips: List<ClipEntry>,
    bots: List<BotInfo>,
    visible: Boolean,
    onClipSelected: (ClipEntry) -> Unit,
    onHandoff: (botId: String, content: String) -> Unit,
    onQuickAction: (ClipEntry, QuickActionType) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible && clips.isNotEmpty(),
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(clips.take(5)) { entry ->
                    ClipChip(
                        entry = entry,
                        bots = bots,
                        onSelect = { onClipSelected(entry) },
                        onHandoff = { botId -> onHandoff(botId, entry.content) },
                        onQuickAction = { type -> onQuickAction(entry, type) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClipChip(
    entry: ClipEntry,
    bots: List<BotInfo>,
    onSelect: () -> Unit,
    onHandoff: (botId: String) -> Unit,
    onQuickAction: (QuickActionType) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        SuggestionChip(
            onClick = onSelect,
            label = {
                Text(
                    text = entry.content.take(30) + if (entry.content.length > 30) "…" else "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = DepthMid)
        )
        // Quick action chips (Save as snippet, Save as capsule, Open URL, Dial)
        val quickActions = QuickActionSuggester.suggest(entry)
        if (quickActions.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                quickActions.forEach { action ->
                    SuggestionChip(
                        onClick = { onQuickAction(action.type) },
                        label = { Text(action.label, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = DepthMid)
                    )
                }
            }
        }
        // Handoff icons — only for URL-type clips when bots are configured
        if (entry.type == ClipType.URL && bots.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                bots.take(3).forEach { bot ->
                    IconButton(
                        onClick = { onHandoff(bot.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send to ${bot.name}",
                            tint = BreathBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
