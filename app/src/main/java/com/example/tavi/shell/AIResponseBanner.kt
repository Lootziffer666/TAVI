package com.example.tavi.shell

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tavi.ui.theme.BreathBlue
import com.example.tavi.ui.theme.DepthMid
import com.example.tavi.ui.theme.TaviAccent
import kotlinx.coroutines.delay

@Composable
fun AIResponseBanner(
    message: String?,
    modifier: Modifier = Modifier,
    onSaveAsCapsule: (() -> Unit)? = null
) {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { visible = false },
            colors = CardDefaults.cardColors(containerColor = DepthMid)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .heightIn(max = 180.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = message ?: "",
                        color = TaviAccent,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                if (onSaveAsCapsule != null) {
                    TextButton(
                        onClick = { onSaveAsCapsule(); visible = false },
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.dp)
                    ) {
                        Text("Save as capsule", style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = BreathBlue)
                    }
                }
            }
        }
    }
}
