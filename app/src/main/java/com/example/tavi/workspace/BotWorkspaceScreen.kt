package com.example.tavi.workspace

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tavi.ui.theme.SpaceBlack

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BotWorkspaceScreen(bot: BotInfo, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBlack)
    ) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.userAgentString = settings.userAgentString + " TaviBot/1.0"
                    loadUrl(bot.url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
