package com.example.tavi.snippet

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tavi.ui.theme.*

@Composable
fun SnippetPanel(
    snippets: List<SnippetEntry>,
    visible: Boolean,
    onSnippetCopy: (SnippetEntry) -> Unit,
    onSnippetDelete: (SnippetEntry) -> Unit,
    onSnippetFavorite: (SnippetEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
    ) {
        val sorted = remember(snippets) {
            snippets.sortedWith(compareByDescending<SnippetEntry> { it.isFavorite }.thenByDescending { it.timestamp })
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(max = 300.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DepthMid)
        ) {
            if (sorted.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No snippets yet. Type  snip: save <title>  to save a snippet.",
                        color = FallbackGrey,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(sorted, key = { it.id }) { snippet ->
                        SnippetRow(
                            snippet = snippet,
                            onCopy = { onSnippetCopy(snippet) },
                            onDelete = { onSnippetDelete(snippet) },
                            onFavorite = { onSnippetFavorite(snippet) }
                        )
                        HorizontalDivider(color = SpaceNavy.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun SnippetRow(
    snippet: SnippetEntry,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = snippet.title,
                style = MaterialTheme.typography.labelMedium,
                color = TaviAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = snippet.content.take(60) + if (snippet.content.length > 60) "…" else "",
                style = MaterialTheme.typography.bodySmall,
                color = FallbackGrey,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onFavorite, modifier = Modifier.size(32.dp)) {
            Icon(
                if (snippet.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (snippet.isFavorite) GlowAmber else FallbackGrey,
                modifier = Modifier.size(16.dp)
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RiskRed, modifier = Modifier.size(16.dp))
        }
        Button(
            onClick = onCopy,
            modifier = Modifier.height(30.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = TaviAccent.copy(alpha = 0.2f))
        ) {
            Text("Copy", style = MaterialTheme.typography.labelSmall, color = TaviAccent)
        }
    }
}
