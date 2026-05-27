package com.example.tavi.intent

object IntentClarifierEngine {

    fun suggest(packageName: String): List<IntentSuggestion> {
        val pkg = packageName.lowercase()
        return when {
            pkg.contains("youtube") -> listOf(
                IntentSuggestion("Watch"),
                IntentSuggestion("Search"),
                IntentSuggestion("Continue")
            )
            pkg.contains("maps") || pkg.contains("navigation") || pkg.contains("waze") -> listOf(
                IntentSuggestion("Navigate"),
                IntentSuggestion("Search place"),
                IntentSuggestion("Explore")
            )
            pkg.contains("spotify") || pkg.contains("tidal") || pkg.contains("deezer") || pkg.contains("music") -> listOf(
                IntentSuggestion("Play"),
                IntentSuggestion("Discover"),
                IntentSuggestion("Podcast")
            )
            pkg.contains("instagram") -> listOf(
                IntentSuggestion("Browse feed"),
                IntentSuggestion("Stories"),
                IntentSuggestion("Post")
            )
            pkg.contains("twitter") || pkg.contains(".x.") -> listOf(
                IntentSuggestion("Browse"),
                IntentSuggestion("Post"),
                IntentSuggestion("Notifications")
            )
            pkg.contains("reddit") -> listOf(
                IntentSuggestion("Browse"),
                IntentSuggestion("Notifications")
            )
            pkg.contains("tiktok") -> listOf(
                IntentSuggestion("Watch"),
                IntentSuggestion("Create")
            )
            pkg.contains("linkedin") -> listOf(
                IntentSuggestion("Browse"),
                IntentSuggestion("Network"),
                IntentSuggestion("Messages")
            )
            pkg.contains("gmail") || pkg.contains("mail") || pkg.contains("outlook") || pkg.contains("protonmail") -> listOf(
                IntentSuggestion("Inbox"),
                IntentSuggestion("Compose")
            )
            pkg.contains("slack") || pkg.contains("teams") -> listOf(
                IntentSuggestion("Check messages"),
                IntentSuggestion("Reply"),
                IntentSuggestion("Channels")
            )
            pkg.contains("discord") -> listOf(
                IntentSuggestion("Servers"),
                IntentSuggestion("DMs"),
                IntentSuggestion("Notifications")
            )
            pkg.contains("telegram") || pkg.contains("whatsapp") || pkg.contains("signal") -> listOf(
                IntentSuggestion("Messages"),
                IntentSuggestion("Calls")
            )
            pkg.contains("chrome") || pkg.contains("firefox") || pkg.contains("brave") || pkg.contains("browser") -> listOf(
                IntentSuggestion("Browse"),
                IntentSuggestion("Search"),
                IntentSuggestion("Bookmarks")
            )
            pkg.contains("camera") -> listOf(
                IntentSuggestion("Photo"),
                IntentSuggestion("Video"),
                IntentSuggestion("Scan")
            )
            pkg.contains("photos") || pkg.contains("gallery") -> listOf(
                IntentSuggestion("Browse"),
                IntentSuggestion("Recent")
            )
            pkg.contains("clock") || pkg.contains("alarm") -> listOf(
                IntentSuggestion("Alarm"),
                IntentSuggestion("Timer"),
                IntentSuggestion("World clock")
            )
            pkg.contains("calendar") -> listOf(
                IntentSuggestion("Today"),
                IntentSuggestion("Add event"),
                IntentSuggestion("Upcoming")
            )
            pkg.contains("notes") || pkg.contains("keep") || pkg.contains("notion") || pkg.contains("obsidian") -> listOf(
                IntentSuggestion("New note"),
                IntentSuggestion("Browse"),
                IntentSuggestion("Search")
            )
            pkg.contains("settings") -> listOf(
                IntentSuggestion("Configure"),
                IntentSuggestion("Check status")
            )
            pkg.contains("files") || pkg.contains("documents") -> listOf(
                IntentSuggestion("Browse"),
                IntentSuggestion("Recent"),
                IntentSuggestion("Download")
            )
            pkg.contains("amazon") || pkg.contains("ebay") || pkg.contains("shop") -> listOf(
                IntentSuggestion("Browse"),
                IntentSuggestion("Orders"),
                IntentSuggestion("Search")
            )
            pkg.contains("netflix") || pkg.contains("primevideo") || pkg.contains("disneyplus") || pkg.contains("hbo") -> listOf(
                IntentSuggestion("Continue watching"),
                IntentSuggestion("Browse"),
                IntentSuggestion("Search")
            )
            pkg.contains(".game") || pkg.contains(".games") || pkg.contains("gaming") ||
            pkg == "com.google.android.play.games" -> listOf(
                IntentSuggestion("Play"),
                IntentSuggestion("Continue")
            )
            else -> emptyList()
        }
    }
}
