package com.example.tavi.shell

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tavi.garden.GardenNode
import com.example.tavi.ui.theme.BreathBlue
import com.example.tavi.ui.theme.BreathTeal
import com.example.tavi.ui.theme.SpaceNavy
import com.example.tavi.ui.theme.TaviAccent

@Composable
fun FocusZone(
    nodes: List<GardenNode>,
    onNodeTap: (GardenNode) -> Unit,
    onNodeLongPress: (GardenNode) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breath")
    val breathPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8_000, easing = LinearEasing)),
        label = "breathPhase"
    )
    // Breath ring: inhale 0-0.375, hold 0.375-0.5, exhale 0.5-0.875
    val strokeWidth by remember(breathPhase) {
        derivedStateOf {
            when {
                breathPhase < 0.375f -> 2f + 4f * (breathPhase / 0.375f)
                breathPhase < 0.5f -> 6f
                breathPhase < 0.875f -> 6f - 4f * ((breathPhase - 0.5f) / 0.375f)
                else -> 2f
            }
        }
    }
    val breathColor by remember(breathPhase) {
        derivedStateOf {
            if (breathPhase < 0.5f) BreathBlue.copy(alpha = 0.4f + breathPhase * 0.3f)
            else BreathTeal.copy(alpha = 0.7f - (breathPhase - 0.5f) * 0.3f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth(0.88f)
            .wrapContentHeight()
            .clip(RoundedCornerShape(32.dp))
            .background(SpaceNavy.copy(alpha = 0.58f))
            .border(strokeWidth.dp, breathColor, RoundedCornerShape(32.dp))
            .padding(16.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(nodes.take(5), key = { it.packageName }) { node ->
                AppNodeIcon(
                    node = node,
                    onTap = { onNodeTap(node) },
                    onLongPress = { onNodeLongPress(node) }
                )
            }
        }
    }
}

@Composable
private fun AppNodeIcon(
    node: GardenNode,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLongPress() }
                )
            }
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(node.icon)
                    .build(),
                contentDescription = node.label,
                modifier = Modifier.size(56.dp).clip(CircleShape)
            )
            if (node.isSpatiallyAnchored) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .background(TaviAccent, CircleShape)
                )
            }
        }
        Text(
            text = node.label.take(10),
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}
