package com.example.tavi.fossil

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectDragGestures
import com.example.tavi.garden.GardenNode
import com.example.tavi.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun FossilDeckScreen(
    candidates: List<GardenNode>,
    onKeep: (GardenNode) -> Unit,
    onRemove: (GardenNode) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack)
    ) {
        if (currentIndex >= candidates.size) {
            Text("All clear — no fossils found", color = TaviAccent, fontSize = 18.sp)
        } else {
            // Show stack of up to 3 cards
            val stackSize = minOf(3, candidates.size - currentIndex)
            for (i in stackSize - 1 downTo 0) {
                val node = candidates[currentIndex + i]
                if (i == 0) {
                    SwipeableAppCard(
                        node = node,
                        isTopCard = true,
                        onSwipedRight = { onKeep(node); currentIndex++ },
                        onSwipedLeft = { onRemove(node); currentIndex++ }
                    )
                } else {
                    SwipeableAppCard(
                        node = node,
                        isTopCard = false,
                        onSwipedRight = {},
                        onSwipedLeft = {}
                    )
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            Text("← Remove", color = RiskRed, fontSize = 14.sp)
            Text("Keep →", color = TaviAccent, fontSize = 14.sp)
        }
    }
}

@Composable
fun SwipeableAppCard(
    node: GardenNode,
    isTopCard: Boolean,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val threshold = 300f

    val rotation = (offsetX.value / 30f).coerceIn(-15f, 15f)
    val alpha = if (isTopCard) 1f else 0.7f

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .rotate(rotation)
            .size(width = 280.dp, height = 380.dp)
            .background(
                color = if (isTopCard) DepthMid else SpaceNavy,
                shape = RoundedCornerShape(24.dp)
            )
            .then(
                if (isTopCard) Modifier.pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                                offsetY.snapTo(offsetY.value + dragAmount.y)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                when {
                                    offsetX.value < -threshold -> {
                                        offsetX.animateTo(-1200f, spring())
                                        onSwipedLeft()
                                        offsetX.snapTo(0f); offsetY.snapTo(0f)
                                    }
                                    offsetX.value > threshold -> {
                                        offsetX.animateTo(1200f, spring())
                                        onSwipedRight()
                                        offsetX.snapTo(0f); offsetY.snapTo(0f)
                                    }
                                    else -> {
                                        offsetX.animateTo(0f, spring())
                                        offsetY.animateTo(0f, spring())
                                    }
                                }
                            }
                        }
                    )
                } else Modifier
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(node.label, color = Color.White, fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text("${node.launchCount} launches", color = Color.Gray, fontSize = 13.sp)
            Text(node.growthStage.name, color = TaviAccent, fontSize = 12.sp)

            // Left/right indicators
            if (isTopCard) {
                Spacer(Modifier.height(16.dp))
                val indicatorAlpha = (offsetX.value / threshold).coerceIn(-1f, 1f)
                if (indicatorAlpha < -0.2f) Text("FOSSIL", color = RiskRed.copy(alpha = -indicatorAlpha), fontSize = 22.sp)
                if (indicatorAlpha > 0.2f) Text("KEEP", color = TaviAccent.copy(alpha = indicatorAlpha), fontSize = 22.sp)
            }
        }
    }
}
