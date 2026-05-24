package com.example.tavi.shell

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.tavi.garden.GardenNode
import com.example.tavi.garden.GrowthStage
import com.example.tavi.sensor.TiltState
import kotlin.math.*

@Composable
fun GardenCanvas(
    foreground: List<GardenNode>,
    midground: List<GardenNode>,
    background: List<GardenNode>,
    tilt: TiltState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "canvas")
    val gridScroll by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10_000, easing = LinearEasing)),
        label = "grid"
    )
    val nodeRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(60_000, easing = LinearEasing)),
        label = "rotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val vanishX = size.width / 2 + tilt.x * 40f
        val vanishY = size.height * 0.4f + tilt.y * 20f

        drawVoxelGrid(vanishX, vanishY, gridScroll, tilt)
        drawNodesLayer(background, vanishX, vanishY, tilt, scale = 0.3f, alpha = 0.13f, nodeRotation)
        drawNodesLayer(midground, vanishX, vanishY, tilt, scale = 0.6f, alpha = 0.42f, nodeRotation)
    }
}

private fun DrawScope.drawVoxelGrid(vanishX: Float, vanishY: Float, scroll: Float, tilt: TiltState) {
    val lineColor = Color(0xFF1A2840)
    val cols = 8
    val rows = 12
    val offsetY = (scroll * size.height / rows)

    for (col in -1..cols) {
        val x = size.width * col / cols.toFloat()
        drawLine(
            color = lineColor,
            start = Offset(x + tilt.x * 10f, 0f),
            end = Offset(vanishX, vanishY),
            strokeWidth = 1f
        )
    }
    for (row in 0..rows) {
        val t = (row.toFloat() / rows + scroll) % 1f
        val y = t * size.height
        val alpha = (1f - abs(t - 0.5f) * 2f).coerceIn(0.05f, 0.4f)
        drawLine(
            color = lineColor.copy(alpha = alpha),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawNodesLayer(
    nodes: List<GardenNode>,
    vanishX: Float,
    vanishY: Float,
    tilt: TiltState,
    scale: Float,
    alpha: Float,
    rotation: Float
) {
    nodes.forEach { node ->
        val phase = node.animationPhaseOffset
        val baseX = size.width * (0.2f + (node.packageName.hashCode() and 0x7FFFFFFF) % 1000 / 1000f * 0.6f)
        val baseY = size.height * (0.2f + (node.packageName.hashCode().ushr(10) and 0x7FFFFFFF) % 1000 / 1000f * 0.6f)
        val x = baseX + tilt.x * 20f * scale
        val y = baseY + tilt.y * 20f * scale
        val r = 12f * scale + node.growthStage.glowRadius * scale
        val sides = node.growthStage.polygonSides
        val hue = node.colorHue
        val nodeColor = Color.hsv(hue, 0.55f, 0.80f).copy(alpha = alpha * node.growthStage.alpha)

        val angleOffset = Math.toRadians(rotation.toDouble() + phase * 360)
        val path = androidx.compose.ui.graphics.Path().apply {
            for (i in 0 until sides) {
                val angle = angleOffset + 2 * PI * i / sides
                val px = x + r * cos(angle).toFloat()
                val py = y + r * sin(angle).toFloat()
                if (i == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }
        drawPath(path, nodeColor)
    }
}
