package com.example.tavi.fossil

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.tavi.garden.GardenNode
import com.example.tavi.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(40.dp))
            Text(
                "Fossil Deck",
                style = MaterialTheme.typography.headlineMedium,
                color = TaviAccent,
                fontWeight = FontWeight.Bold
            )
            Text(
                "← Remove   Keep →",
                color = Color.Gray,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(24.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                if (currentIndex >= candidates.size) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = TaviAccent,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("All clear", color = TaviAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("No fossils remaining", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    // Render stack: bottom cards first, top card last
                    val stackSize = minOf(3, candidates.size - currentIndex)
                    for (i in stackSize - 1 downTo 0) {
                        val node = candidates[currentIndex + i]
                        SwipeableAppCard(
                            node = node,
                            isTopCard = i == 0,
                            stackOffset = i,
                            onSwipedRight = { onKeep(node); currentIndex++ },
                            onSwipedLeft = { onRemove(node); currentIndex++ }
                        )
                    }
                }
            }

            // Action legend with icons
            Row(
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, null, tint = RiskRed, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Remove", color = RiskRed, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Keep", color = TaviAccent, fontSize = 13.sp)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.Check, null, tint = TaviAccent, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun SwipeableAppCard(
    node: GardenNode,
    isTopCard: Boolean,
    stackOffset: Int,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit
) {
    val screenWidth = with(LocalDensity.current) { 400.dp.toPx() }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val dragModifier = if (isTopCard) {
        Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    change.consume()
                    scope.launch {
                        offsetX.snapTo(offsetX.value + dragAmount.x)
                        offsetY.snapTo(offsetY.value + dragAmount.y)
                        rotation.snapTo(offsetX.value / 20f)
                    }
                },
                onDragEnd = {
                    scope.launch {
                        if (offsetX.value.absoluteValue > screenWidth / 3f) {
                            val targetX = if (offsetX.value > 0) screenWidth * 1.5f else -screenWidth * 1.5f
                            offsetX.animateTo(targetX, tween(300))
                            if (offsetX.value > 0) onSwipedRight() else onSwipedLeft()
                        } else {
                            launch { offsetX.animateTo(0f, tween(300)) }
                            launch { offsetY.animateTo(0f, tween(300)) }
                            launch { rotation.animateTo(0f, tween(300)) }
                        }
                    }
                }
            )
        }
    } else Modifier

    Card(
        modifier = Modifier
            .fillMaxWidth(0.82f)
            .aspectRatio(0.72f)
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer(
                rotationZ = if (isTopCard) rotation.value else (stackOffset * 2.5f),
                scaleX = if (isTopCard) 1f else (1f - stackOffset * 0.04f),
                scaleY = if (isTopCard) 1f else (1f - stackOffset * 0.04f),
                alpha = if (isTopCard) 1f else (1f - stackOffset * 0.15f)
            )
            .then(dragModifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isTopCard) DepthMid else SpaceNavy
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isTopCard) 8.dp else 2.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isTopCard) TaviAccent.copy(alpha = 0.3f) else Color.DarkGray
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(SpaceNavy, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(node.icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = node.label,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = node.label,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${node.launchCount} launches · ${node.growthStage.name}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))

            // Swipe direction indicators (only on top card)
            if (isTopCard) {
                val swipeRatio = (offsetX.value / (screenWidth / 3f)).coerceIn(-1f, 1f)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = RiskRed.copy(alpha = (-swipeRatio).coerceIn(0.1f, 1f)),
                        modifier = Modifier.size(40.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Keep",
                        tint = TaviAccent.copy(alpha = swipeRatio.coerceIn(0.1f, 1f)),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
