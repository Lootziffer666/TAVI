package com.example.tavi.shell

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.tavi.ui.theme.SpaceNavy
import com.example.tavi.ui.theme.TaviAccent

@Composable
fun PromptOrb(
    isExpanded: Boolean,
    promptText: String,
    isThinking: Boolean,
    onToggle: () -> Unit,
    onTextChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val orbScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbPulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 24.dp)
    ) {
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SpaceNavy)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = promptText,
                        onValueChange = onTextChanged,
                        placeholder = { Text("? ask  ! act  /build  >bot", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TaviAccent,
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = { onSubmit() })
                    )
                    if (isThinking) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(start = 8.dp).size(24.dp),
                            color = TaviAccent,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onToggle,
            containerColor = if (isExpanded) Color.DarkGray else TaviAccent,
            modifier = Modifier.scale(if (isExpanded) 1f else orbScale)
        ) {
            Icon(
                if (isExpanded) Icons.Default.Close else Icons.Default.Search,
                contentDescription = if (isExpanded) "Close" else "Open prompt",
                tint = if (isExpanded) Color.White else Color.Black
            )
        }
    }
}
