package com.example.tavi.shell

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import com.example.tavi.garden.GardenNode
import com.example.tavi.sensor.TiltState
import com.example.tavi.state.TaviState
import com.example.tavi.ui.components.StateAnchor

@Composable
fun SpatialLauncherScreen(
    foreground: List<GardenNode>,
    midground: List<GardenNode>,
    background: List<GardenNode>,
    tilt: TiltState,
    taviState: TaviState,
    isOrbExpanded: Boolean,
    promptText: String,
    isThinking: Boolean,
    aiMessage: String?,
    onOrbToggle: () -> Unit,
    onTextChanged: (String) -> Unit,
    onPromptSubmit: () -> Unit,
    onNodeTap: (GardenNode) -> Unit,
    onNodeLongPress: (GardenNode) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            // Tilt applied here, inside the page — not on the pager item itself
            .graphicsLayer {
                rotationX = tilt.y * 6f
                rotationY = -tilt.x * 6f
                cameraDistance = 12f * density
            }
    ) {
        // Layer 0: Garden canvas (bg + mid)
        GardenCanvas(
            foreground = foreground,
            midground = midground,
            background = background,
            tilt = tilt,
            modifier = Modifier.fillMaxSize().zIndex(0f)
        )

        // Layer 1: State anchor chip
        StateAnchor(
            state = taviState,
            modifier = Modifier.align(Alignment.TopCenter).zIndex(3f)
        )

        // Layer 2: AI response banner
        AIResponseBanner(
            message = aiMessage,
            modifier = Modifier.align(Alignment.TopCenter).zIndex(2f).padding(top = 48.dp)
        )

        // Layer 3: Focus zone (center)
        FocusZone(
            nodes = foreground,
            onNodeTap = onNodeTap,
            onNodeLongPress = onNodeLongPress,
            modifier = Modifier.align(Alignment.Center).zIndex(2f)
        )

        // Layer 4: Prompt orb (bottom center)
        PromptOrb(
            isExpanded = isOrbExpanded,
            promptText = promptText,
            isThinking = isThinking,
            onToggle = onOrbToggle,
            onTextChanged = onTextChanged,
            onSubmit = onPromptSubmit,
            modifier = Modifier.align(Alignment.BottomCenter).zIndex(4f)
        )
    }
}
