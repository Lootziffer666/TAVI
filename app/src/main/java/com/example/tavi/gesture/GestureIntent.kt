package com.example.tavi.gesture

sealed class GestureIntent {
    object ExpandOrb : GestureIntent()
    object CollapseOrb : GestureIntent()
    data class NavigatePage(val delta: Int) : GestureIntent()
    object OpenFossilDeck : GestureIntent()
    object OpenBotWorkspaces : GestureIntent()
    object Passthrough : GestureIntent()
}
