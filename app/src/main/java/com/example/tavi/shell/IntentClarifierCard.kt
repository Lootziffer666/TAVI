package com.example.tavi.shell

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tavi.garden.GardenNode
import com.example.tavi.intent.IntentSuggestion
import com.example.tavi.ui.theme.BreathBlue
import com.example.tavi.ui.theme.DepthMid
import com.example.tavi.ui.theme.FallbackGrey
import com.example.tavi.ui.theme.TaviAccent

// One Anchor for TaviState.Capture — shown when intent clarifier is active.
// Failure behavior: Skip always present; tapping it launches the app directly.
@Composable
fun IntentClarifierCard(
    node: GardenNode,
    suggestions: List<IntentSuggestion>,
    visible: Boolean,
    onSuggestionSelected: (IntentSuggestion) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible && suggestions.isNotEmpty(),
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

                // Intent suggestion chips
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
