package com.example.tavi.gesture

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.math.abs

class TaviGestureRouter {

    private var dragStart = Offset.Zero
    private var totalDelta = Offset.Zero

    fun onDragStart(offset: Offset) {
        dragStart = offset
        totalDelta = Offset.Zero
    }

    fun onDrag(delta: Offset) {
        totalDelta += delta
    }

    fun onDragEnd(screenSize: IntSize, currentPage: Int): GestureIntent {
        val w = screenSize.width.toFloat()
        val h = screenSize.height.toFloat()
        val edge = EdgeZoneConfig.EDGE_FRACTION
        val dx = totalDelta.x
        val dy = totalDelta.y
        val adx = abs(dx)
        val ady = abs(dy)
        val threshold = EdgeZoneConfig.SWIPE_DISTANCE_THRESHOLD

        return when {
            // Bottom edge: swipe up expands orb
            dragStart.y > h * (1 - edge) && ady > threshold && dy < 0 -> GestureIntent.ExpandOrb
            // Top edge: swipe down from status bar area
            dragStart.y < h * edge && ady > threshold && dy > 0 -> GestureIntent.ExpandOrb
            // Left edge: swipe right → previous page
            dragStart.x < w * edge && adx > threshold && dx > 0 -> GestureIntent.NavigatePage(-1)
            // Right edge: swipe left → next page
            dragStart.x > w * (1 - edge) && adx > threshold && dx < 0 -> GestureIntent.NavigatePage(1)
            // Center: swipe up → fossil deck (page 0)
            currentPage == 1 && ady > adx && dy < -threshold -> GestureIntent.OpenFossilDeck
            // Center: swipe down → bot workspaces (page 2)
            currentPage == 1 && ady > adx && dy > threshold -> GestureIntent.OpenBotWorkspaces
            else -> GestureIntent.Passthrough
        }
    }
}
