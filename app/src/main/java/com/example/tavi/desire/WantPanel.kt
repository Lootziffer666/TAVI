package com.example.tavi.desire

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tavi.ui.theme.DepthMid
import com.example.tavi.ui.theme.GlowAmber
import com.example.tavi.ui.theme.RiskRed
import com.example.tavi.ui.theme.TaviAccent

@Composable
fun WantPanel(
    items: List<WantItem>,
    visible: Boolean,
    onDo: (WantItem) -> Unit,
    onDelete: (WantItem) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = DepthMid),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("Want shelf", color = TaviAccent, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Close", color = Color.Gray, fontSize = 12.sp) }
                }
                if (items.isEmpty()) {
                    Text("Nothing parked yet.", color = Color.Gray, fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        items(items) { item ->
                            WantItemRow(item = item, onDo = { onDo(item) }, onDelete = { onDelete(item) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WantItemRow(item: WantItem, onDo: () -> Unit, onDelete: () -> Unit) {
    val age = remember(item.timestamp) { formatAge(item.timestamp) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, color = Color.White, fontSize = 13.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(age, color = Color.Gray, fontSize = 11.sp)
                if (item.subscriptionCost != null) {
                    Text(item.subscriptionCost, color = GlowAmber, fontSize = 11.sp)
                }
                if (item.manipulationHints.isNotEmpty()) {
                    Text(item.manipulationHints.first(), color = RiskRed.copy(alpha = 0.7f), fontSize = 11.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        TextButton(onClick = onDo) { Text("Do it", color = TaviAccent, fontSize = 12.sp) }
        TextButton(onClick = onDelete) { Text("Drop", color = Color.Gray, fontSize = 12.sp) }
    }
}

private fun formatAge(timestamp: Long): String {
    val elapsed = System.currentTimeMillis() - timestamp
    return when {
        elapsed < 60_000 -> "just now"
        elapsed < 3_600_000 -> "${elapsed / 60_000} min ago"
        elapsed < 86_400_000 -> "${elapsed / 3_600_000} h ago"
        else -> "${elapsed / 86_400_000} d ago"
    }
}
