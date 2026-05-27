package com.example.tavi.manipulation

object ManipulationEngine {

    private val STREAK = ManipulationPattern(
        id = "streak",
        name = "Streak",
        category = PatternCategory.ENGAGEMENT,
        explanation = "An unbroken chain of days to protect. Breaking it is designed to feel like personal failure.",
        reflectionQuestion = "Are you opening this because you want to, or because you're afraid to break a streak?",
        childRelevant = true
    )
    private val DAILY_REWARD = ManipulationPattern(
        id = "daily_reward",
        name = "Daily reward",
        category = PatternCategory.ENGAGEMENT,
        explanation = "A small reward for opening the app each day. Creates an obligation to show up daily regardless of real need.",
        reflectionQuestion = "Would you open this app today if there was no reward waiting?",
        childRelevant = true
    )
    private val VARIABLE_REWARD = ManipulationPattern(
        id = "variable_reward",
        name = "Variable reward",
        category = PatternCategory.ENGAGEMENT,
        explanation = "Unpredictable rewards trigger dopamine spikes stronger than predictable ones — the same mechanic as slot machines.",
        reflectionQuestion = "What are you hoping to find this time that you didn't find last time?"
    )
    private val LOOT_BOX = ManipulationPattern(
        id = "loot_box",
        name = "Loot box",
        category = PatternCategory.COMMERCE,
        explanation = "A randomized purchase — you pay without knowing what you'll receive. Structurally identical to gambling.",
        reflectionQuestion = "If you knew exactly what you'd get, would you still buy it?",
        childRelevant = true
    )
    private val GACHA = ManipulationPattern(
        id = "gacha",
        name = "Gacha pull",
        category = PatternCategory.COMMERCE,
        explanation = "Rare characters or items at fractional probabilities. Designed to require many pulls to get what you want.",
        reflectionQuestion = "How much would it cost to guarantee the item you want at the listed drop rate?",
        childRelevant = true
    )
    private val BATTLE_PASS = ManipulationPattern(
        id = "battle_pass",
        name = "Battle pass",
        category = PatternCategory.COMMERCE,
        explanation = "Prepaid season rewards that expire. Once you've paid, you must play enough to feel you didn't waste the purchase.",
        reflectionQuestion = "Are you playing because you enjoy it, or to avoid losing what you already paid for?",
        childRelevant = true
    )
    private val PAY_TO_WIN = ManipulationPattern(
        id = "pay_to_win",
        name = "Pay-to-win",
        category = PatternCategory.COMMERCE,
        explanation = "Spending money gives a competitive advantage. Enjoyment and fairness are tied directly to spending.",
        reflectionQuestion = "How much would you need to spend to compete fairly with paying players?"
    )
    private val ENERGY_GATE = ManipulationPattern(
        id = "energy_gate",
        name = "Energy gate",
        category = PatternCategory.COMMERCE,
        explanation = "Artificial play limits that refill over time or can be bypassed with payment. You stop when the app decides, not when you decide.",
        reflectionQuestion = "Are you opening the app because you want to play, or because your energy has refilled?",
        childRelevant = true
    )
    private val SUBSCRIPTION_TRAP = ManipulationPattern(
        id = "sub_trap",
        name = "Subscription trap",
        category = PatternCategory.COMMERCE,
        explanation = "Auto-renewing charges designed to be forgotten. Cancellation is often hidden or deliberately difficult.",
        reflectionQuestion = "Do you know the exact amount this subscription costs per year?"
    )
    private val FOMO_COUNTDOWN = ManipulationPattern(
        id = "fomo",
        name = "FOMO countdown",
        category = PatternCategory.URGENCY,
        explanation = "A countdown clock on limited content. Designed to override deliberation with urgency.",
        reflectionQuestion = "Would you make this decision right now if there was no timer?",
        childRelevant = true
    )
    private val COMEBACK_REWARD = ManipulationPattern(
        id = "comeback",
        name = "Comeback reward",
        category = PatternCategory.URGENCY,
        explanation = "Bonuses waiting for you after time away. Converts your absence into a debt the app rewards you for repaying.",
        reflectionQuestion = "Are you returning because you wanted to, or because the app offered you something?"
    )
    private val ENDLESS_SCROLL = ManipulationPattern(
        id = "endless_scroll",
        name = "Endless scroll",
        category = PatternCategory.ATTENTION,
        explanation = "A feed with no natural stopping point. The absence of an end is deliberate — designed so you never reach 'done'.",
        reflectionQuestion = "What were you looking for when you started scrolling?"
    )
    private val AUTOPLAY = ManipulationPattern(
        id = "autoplay",
        name = "Autoplay",
        category = PatternCategory.ATTENTION,
        explanation = "The next video starts before you choose. Your inaction is treated as consent to keep watching.",
        reflectionQuestion = "Did you choose to watch this video, or did it start while you were still deciding?",
        childRelevant = true
    )
    private val PUSH_FLOOD = ManipulationPattern(
        id = "push_flood",
        name = "Push flood",
        category = PatternCategory.ATTENTION,
        explanation = "High-frequency notifications designed to create habitual check-ins. Each notification trains you to return.",
        reflectionQuestion = "How many of today's notifications actually required your attention?",
        childRelevant = true
    )
    private val SOCIAL_PRESSURE = ManipulationPattern(
        id = "social_pressure",
        name = "Social pressure",
        category = PatternCategory.SOCIAL,
        explanation = "Visibility of who is online, who has viewed your content, or who hasn't responded. Makes disengagement feel socially costly.",
        reflectionQuestion = "What would happen socially if you didn't check this for 24 hours?",
        childRelevant = true
    )

    val ALL_PATTERNS: List<ManipulationPattern> = listOf(
        STREAK, DAILY_REWARD, VARIABLE_REWARD,
        LOOT_BOX, GACHA, BATTLE_PASS, PAY_TO_WIN, ENERGY_GATE, SUBSCRIPTION_TRAP,
        FOMO_COUNTDOWN, COMEBACK_REWARD,
        ENDLESS_SCROLL, AUTOPLAY, PUSH_FLOOD,
        SOCIAL_PRESSURE
    )

    fun patternById(id: String): ManipulationPattern? = ALL_PATTERNS.find { it.id == id }

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
