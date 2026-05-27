package com.example.tavi.subscription

object SubscriptionScanner {

    private val KNOWN = mapOf(
        "com.spotify.music"                          to SubscriptionInfo("com.spotify.music",                          "Spotify",             "~€11", "Monthly"),
        "com.netflix.mediaclient"                    to SubscriptionInfo("com.netflix.mediaclient",                    "Netflix",             "~€15", "Monthly"),
        "com.google.android.youtube"                 to SubscriptionInfo("com.google.android.youtube",                 "YouTube Premium",     "~€12", "Monthly"),
        "com.google.android.apps.youtube.music"      to SubscriptionInfo("com.google.android.apps.youtube.music",      "YouTube Music",       "~€10", "Monthly"),
        "com.duolingo"                               to SubscriptionInfo("com.duolingo",                               "Duolingo Plus",       "~€7",  "Monthly"),
        "com.disney.disneyplus"                      to SubscriptionInfo("com.disney.disneyplus",                      "Disney+",             "~€9",  "Monthly"),
        "com.amazon.avod.thirdpartyclient"           to SubscriptionInfo("com.amazon.avod.thirdpartyclient",           "Prime Video",         "varies","Monthly"),
        "com.adobe.reader"                           to SubscriptionInfo("com.adobe.reader",                           "Adobe Acrobat",       "~€15", "Monthly"),
        "com.dropbox.android"                        to SubscriptionInfo("com.dropbox.android",                        "Dropbox",             "~€10", "Monthly"),
        "com.nordvpn.android"                        to SubscriptionInfo("com.nordvpn.android",                        "NordVPN",             "~€4",  "Monthly"),
        "com.expressvpn.vpn"                         to SubscriptionInfo("com.expressvpn.vpn",                         "ExpressVPN",          "~€10", "Monthly"),
        "com.grammarly.android.keyboard"             to SubscriptionInfo("com.grammarly.android.keyboard",             "Grammarly",           "~€12", "Monthly"),
        "com.evernote"                               to SubscriptionInfo("com.evernote",                               "Evernote",            "~€8",  "Monthly"),
        "com.notion.id"                              to SubscriptionInfo("com.notion.id",                              "Notion Plus",         "~€8",  "Monthly"),
        "com.headspace.android"                      to SubscriptionInfo("com.headspace.android",                      "Headspace",           "~€13", "Monthly"),
        "com.calm.android"                           to SubscriptionInfo("com.calm.android",                           "Calm",                "~€15", "Monthly"),
        "com.microsoft.office.word"                  to SubscriptionInfo("com.microsoft.office.word",                  "Microsoft 365",       "~€7",  "Monthly"),
        "com.tidal.android"                          to SubscriptionInfo("com.tidal.android",                          "Tidal",               "~€10", "Monthly"),
        "com.deezer.android"                         to SubscriptionInfo("com.deezer.android",                         "Deezer",              "~€11", "Monthly"),
        "com.linkedin.android"                       to SubscriptionInfo("com.linkedin.android",                       "LinkedIn Premium",    "~€35", "Monthly"),
        "com.canva.editor"                           to SubscriptionInfo("com.canva.editor",                           "Canva Pro",           "~€12", "Monthly"),
        "com.sketch.cloud"                           to SubscriptionInfo("com.sketch.cloud",                           "Sketch",              "~€10", "Monthly"),
        "com.malwarebytes.nebula"                    to SubscriptionInfo("com.malwarebytes.nebula",                    "Malwarebytes Premium", "~€4", "Monthly"),
        "com.nianticlabs.pokemongo"                  to SubscriptionInfo("com.nianticlabs.pokemongo",                  "Pokémon GO Plus",     "varies","Monthly"),
        "com.hbomax.production"                      to SubscriptionInfo("com.hbomax.production",                      "Max (HBO)",           "~€10", "Monthly"),
    )

    fun scan(installedPackages: List<String>): List<SubscriptionInfo> =
        installedPackages.mapNotNull { KNOWN[it] }
}
