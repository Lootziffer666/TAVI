package com.example.tavi.shell

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tavi.ui.theme.DepthMid
import com.example.tavi.ui.theme.TaviAccent
import kotlinx.coroutines.delay

@Composable
fun AIResponseBanner(message: String?, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        if (message != null) {
            visible = true
            delay(4000)
            visible = false
        }
    }

    AnimatedVisibility(
        visible = visible && message != null,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = DepthMid)
        ) {
            Text(
                text = message ?: "",
                color = TaviAccent,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
