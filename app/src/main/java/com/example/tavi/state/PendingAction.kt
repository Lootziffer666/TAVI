package com.example.tavi.state

sealed class PendingAction {
    data class ShellCommand(
        val display: String,
        val translated: String?,
        val executable: String
    ) : PendingAction()

    data class DemoteApp(val packageName: String, val label: String) : PendingAction()
    data class PromoteApp(val packageName: String, val label: String) : PendingAction()
    data class ScopeChange(val from: String?, val to: String) : PendingAction()
}
