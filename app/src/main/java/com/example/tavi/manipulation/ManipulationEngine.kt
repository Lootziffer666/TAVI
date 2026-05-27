package com.example.tavi.manipulation

object ManipulationEngine {

    private val STREAK           = ManipulationPattern("streak",            "Streak",            PatternCategory.ENGAGEMENT)
    private val DAILY_REWARD     = ManipulationPattern("daily_reward",      "Daily reward",      PatternCategory.ENGAGEMENT)
    private val VARIABLE_REWARD  = ManipulationPattern("variable_reward",   "Variable reward",   PatternCategory.ENGAGEMENT)
    private val LOOT_BOX         = ManipulationPattern("loot_box",          "Loot box",          PatternCategory.COMMERCE)
    private val GACHA            = ManipulationPattern("gacha",             "Gacha pull",        PatternCategory.COMMERCE)
    private val BATTLE_PASS      = ManipulationPattern("battle_pass",       "Battle pass",       PatternCategory.COMMERCE)
    private val PAY_TO_WIN       = ManipulationPattern("pay_to_win",        "Pay-to-win",        PatternCategory.COMMERCE)
    private val ENERGY_GATE      = ManipulationPattern("energy_gate",       "Energy gate",       PatternCategory.COMMERCE)
    private val SUBSCRIPTION_TRAP = ManipulationPattern("sub_trap",         "Subscription trap", PatternCategory.COMMERCE)
    private val FOMO_COUNTDOWN   = ManipulationPattern("fomo",              "FOMO countdown",    PatternCategory.URGENCY)
    private val COMEBACK_REWARD  = ManipulationPattern("comeback",          "Comeback reward",   PatternCategory.URGENCY)
    private val ENDLESS_SCROLL   = ManipulationPattern("endless_scroll",    "Endless scroll",    PatternCategory.ATTENTION)
    private val AUTOPLAY         = ManipulationPattern("autoplay",          "Autoplay",          PatternCategory.ATTENTION)
    private val PUSH_FLOOD       = ManipulationPattern("push_flood",        "Push flood",        PatternCategory.ATTENTION)
    private val SOCIAL_PRESSURE  = ManipulationPattern("social_pressure",   "Social pressure",   PatternCategory.SOCIAL)

    fun detect(packageName: String): List<ManipulationPattern> {
        val pkg = packageName.lowercase()
        return buildList {
            when {
                pkg.contains("tiktok") ->
                    addAll(listOf(ENDLESS_SCROLL, VARIABLE_REWARD, AUTOPLAY, PUSH_FLOOD))
                pkg.contains("instagram") ->
                    addAll(listOf(ENDLESS_SCROLL, VARIABLE_REWARD, PUSH_FLOOD))
                pkg.contains("youtube") ->
                    addAll(listOf(AUTOPLAY, ENDLESS_SCROLL))
                pkg.contains("twitter") || pkg.startsWith("com.x.") ->
                    addAll(listOf(ENDLESS_SCROLL, VARIABLE_REWARD, PUSH_FLOOD))
                pkg.contains("facebook") || pkg.contains("meta.") ->
                    addAll(listOf(ENDLESS_SCROLL, VARIABLE_REWARD, PUSH_FLOOD))
                pkg.contains("reddit") ->
                    addAll(listOf(ENDLESS_SCROLL, VARIABLE_REWARD))
                pkg.contains("snapchat") ->
                    addAll(listOf(STREAK, FOMO_COUNTDOWN, PUSH_FLOOD))
                pkg.contains("bereal") ->
                    addAll(listOf(STREAK, FOMO_COUNTDOWN))
                pkg.contains("duolingo") ->
                    addAll(listOf(STREAK, DAILY_REWARD, PUSH_FLOOD, ENERGY_GATE))
                pkg.contains("supercell") || pkg.contains("clashofclans") ||
                pkg.contains("clashroyale") || pkg.contains("brawlstars") ->
                    addAll(listOf(DAILY_REWARD, BATTLE_PASS, PAY_TO_WIN))
                pkg.contains("eamobile") || pkg.contains("ea.games") ->
                    addAll(listOf(LOOT_BOX, PAY_TO_WIN))
                pkg.contains("zynga") ->
                    addAll(listOf(DAILY_REWARD, ENERGY_GATE))
                pkg.contains("genshin") || pkg.contains("honkai") || pkg.contains("mihoyo") ||
                pkg.contains("hoyoverse") || pkg.contains("starrail") ->
                    addAll(listOf(GACHA, DAILY_REWARD, BATTLE_PASS, FOMO_COUNTDOWN))
                pkg.contains("candycrush") || (pkg.contains("king.") && !pkg.contains("kingroot")) ->
                    addAll(listOf(ENERGY_GATE, DAILY_REWARD, FOMO_COUNTDOWN))
                pkg.contains("pokemongo") || pkg.contains("niantic") ->
                    addAll(listOf(DAILY_REWARD, ENERGY_GATE, VARIABLE_REWARD))
                pkg.contains("roblox") ->
                    addAll(listOf(LOOT_BOX, PAY_TO_WIN, SOCIAL_PRESSURE))
                pkg.contains("fortnite") || pkg.contains("epicgames") ->
                    addAll(listOf(BATTLE_PASS, PAY_TO_WIN, FOMO_COUNTDOWN))
                pkg.contains("pubg") || pkg.contains("bgmi") ->
                    addAll(listOf(BATTLE_PASS, LOOT_BOX, PAY_TO_WIN))
                pkg.contains("freefire") ->
                    addAll(listOf(BATTLE_PASS, LOOT_BOX, DAILY_REWARD))
                pkg.contains("netease") ->
                    addAll(listOf(GACHA, DAILY_REWARD, BATTLE_PASS))
                pkg.contains("netflix") ->
                    addAll(listOf(AUTOPLAY, SUBSCRIPTION_TRAP))
                pkg.contains("spotify") ->
                    addAll(listOf(SUBSCRIPTION_TRAP))
                pkg.contains("linkedin") ->
                    addAll(listOf(PUSH_FLOOD, SOCIAL_PRESSURE, SUBSCRIPTION_TRAP))
                pkg.contains("bumble") || pkg.contains("tinder") || pkg.contains("hinge") ->
                    addAll(listOf(VARIABLE_REWARD, SUBSCRIPTION_TRAP, PUSH_FLOOD))
                else -> Unit
            }
        }
    }
}
