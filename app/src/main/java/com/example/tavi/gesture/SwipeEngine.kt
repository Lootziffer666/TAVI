package com.example.tavi.gesture

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class SwipeState(
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val rotation: Float = 0f,
    val alpha: Float = 1f
)

enum class SwipeDirection { LEFT, RIGHT, NONE }

@Composable
fun rememberSwipeState(): Pair<Animatable<Float, *>, Animatable<Float, *>> {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    return offsetX to offsetY
}

fun Modifier.swipeToDecide(
    threshold: Float = 300f,
    onSwipedLeft: () -> Unit,
    onSwipedRight: () -> Unit,
    offsetXAnimatable: Animatable<Float, *>,
    coroutineScope: CoroutineScope
): Modifier = this.pointerInput(Unit) {
    detectDragGestures(
        onDragEnd = {
            coroutineScope.launch {
                when {
                    offsetXAnimatable.value < -threshold -> {
                        offsetXAnimatable.animateTo(-1200f, spring())
                        onSwipedLeft()
                        offsetXAnimatable.snapTo(0f)
                    }
                    offsetXAnimatable.value > threshold -> {
                        offsetXAnimatable.animateTo(1200f, spring())
                        onSwipedRight()
                        offsetXAnimatable.snapTo(0f)
                    }
                    else -> offsetXAnimatable.animateTo(0f, spring())
                }
            }
        },
        onDrag = { change, dragAmount ->
            change.consume()
            coroutineScope.launch {
                offsetXAnimatable.snapTo(offsetXAnimatable.value + dragAmount.x)
            }
        }
    )
}
