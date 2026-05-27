package com.example.tavi.shell

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tavi.garden.GardenNode
import com.example.tavi.intent.IntentSuggestion
import com.example.tavi.manipulation.ManipulationPattern
import com.example.tavi.ui.theme.BreathBlue
import com.example.tavi.ui.theme.DepthMid
import com.example.tavi.ui.theme.FallbackGrey
import com.example.tavi.ui.theme.GlowAmber
import com.example.tavi.ui.theme.RiskRed
import com.example.tavi.ui.theme.TaviAccent

// One Anchor for TaviState.Capture — shown when intent clarifier is active.
// Failure behavior: Skip always present; tapping it launches the app directly.
@Suppress("NAME_SHADOWING")
@Composable
fun IntentClarifierCard(
    node: GardenNode,
    suggestions: List<IntentSuggestion>,
    patterns: List<ManipulationPattern> = emptyList(),
    visible: Boolean,
    onSuggestionSelected: (IntentSuggestion) -> Unit,
    onSkip: () -> Unit,
    showWatchToggle: Boolean = false,
    watchGameEnabled: Boolean = false,
    onWatchToggle: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var expandedPatternId by remember { mutableStateOf<String?>(null) }

    AnimatedVisibility(
        visible = visible && (suggestions.isNotEmpty() || patterns.isNotEmpty()),
        enter = slideInVertically { it } + fadeIn(animationSpec = androidx.compose.animation.core.tween(180)),
        exit = slideOutVertically { it } + fadeOut(animationSpec = androidx.compose.animation.core.tween(120)),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DepthMid),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // App identity row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(node.icon)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                    Column {
                        Text(
                            text = node.label,
                            color = TaviAccent,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "What are you here for?",
                            color = FallbackGrey,
                            fontSize = 12.sp
                        )
                    }
                }

                // Intent suggestion chips — only shown when suggestions exist
                if (suggestions.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(suggestions) { suggestion ->
                            SuggestionChip(
                                onClick = { onSuggestionSelected(suggestion) },
                                label = { Text(suggestion.label) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = Color.Transparent,
                                    labelColor = BreathBlue
                                ),
                                border = SuggestionChipDefaults.suggestionChipBorder(
                                    enabled = true,
                                    borderColor = BreathBlue.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }

                // Pattern warning row — names known manipulation mechanics neutrally
                if (patterns.isNotEmpty()) {
                    val hasChildRelevant = patterns.any { it.childRelevant }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Known patterns",
                                color = RiskRed.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                            if (hasChildRelevant) {
                                Text(
                                    text = "· children",
                                    color = GlowAmber.copy(alpha = 0.85f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(patterns) { pattern ->
                                SuggestionChip(
                                    onClick = {
                                        expandedPatternId =
                                            if (expandedPatternId == pattern.id) null else pattern.id
                                    },
                                    label = { Text(pattern.name, fontSize = 11.sp) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = if (expandedPatternId == pattern.id)
                                            RiskRed.copy(alpha = 0.18f) else RiskRed.copy(alpha = 0.08f),
                                        labelColor = RiskRed.copy(alpha = 0.85f)
                                    ),
                                    border = SuggestionChipDefaults.suggestionChipBorder(
                                        enabled = true,
                                        borderColor = RiskRed.copy(alpha = 0.25f)
                                    )
                                )
                            }
                        }
                        // Expanded detail — shown below chips when a pattern is tapped
                        val expanded = patterns.firstOrNull { it.id == expandedPatternId }
                        AnimatedVisibility(visible = expanded != null) {
                            if (expanded != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            RiskRed.copy(alpha = 0.06f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = expanded.explanation,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                    Text(
                                        text = "→ ${expanded.reflectionQuestion}",
                                        color = FallbackGrey,
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Watch toggle — only shown when patterns detected + Cloud AI available
                if (showWatchToggle) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Watch during play", color = Color.White, fontSize = 13.sp)
                            Text(
                                "Real-time screen analysis via Gemini",
                                color = FallbackGrey,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = watchGameEnabled,
                            onCheckedChange = { onWatchToggle() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GlowAmber,
                                checkedTrackColor = GlowAmber.copy(alpha = 0.3f)
                            )
                        )
                    }
                }

                // Skip row — always present (failure behavior: in doubt, let through)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onSkip) {
                        Text("Just open", color = FallbackGrey, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
