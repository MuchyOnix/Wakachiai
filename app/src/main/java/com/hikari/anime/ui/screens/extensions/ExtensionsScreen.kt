package com.hikari.anime.ui.screens.extensions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hikari.anime.extension.api.AnimeSource
import com.hikari.anime.ui.components.HikariTopBar
import com.hikari.anime.ui.components.InfoBanner
import com.hikari.anime.ui.components.SectionHeader
import com.hikari.anime.ui.components.SourceAuthWebView
import com.hikari.anime.ui.theme.HikariCyan
import com.hikari.anime.ui.theme.HikariPink
import com.hikari.anime.ui.theme.HikariPurple

@Composable
fun ExtensionsScreen(
    vm: ExtensionsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var authSource by remember { mutableStateOf<AnimeSource?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HikariTopBar(
                title = "Extensions",
                subtitle = "Compiled source registry and source sessions"
            ) {
                IconButton(onClick = {}) { Icon(Icons.Rounded.MoreVert, contentDescription = "More") }
            }
        }
        item {
            ExtensionHero()
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Sources", "Plugins", "Themes").forEachIndexed { index, label ->
                    FilterChip(selected = index == 1, onClick = {}, label = { Text(label) })
                }
            }
        }
        item {
            InfoBanner(
                title = "MVP source model",
                body = "The plan uses compiled-in source modules for now. Dynamic APK-style extensions can be layered onto this boundary later.",
                icon = Icons.Rounded.Extension,
                tint = HikariPurple
            )
        }
        item { SectionHeader("Installed Sources", "") }
        items(state.sources, key = { it.id }) { source ->
            SourceItem(
                source = source,
                isActive = source.id == state.activeId,
                onActivate = { vm.setActiveSource(source.id) },
                onRefreshSession = { authSource = source }
            )
        }
        authSource?.let { source ->
            item {
                SourceAuthWebView(sourceName = source.name, url = source.baseUrl)
            }
        }
        item {
            InfoBanner(
                title = "Session refresh",
                body = "Use the source menu to open AniGo in WebView when cookies or Cloudflare checks need to be refreshed for OkHttp.",
                icon = Icons.Rounded.CloudQueue,
                tint = HikariCyan
            )
        }
    }
}

@Composable
private fun ExtensionHero() {
    Card(shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(HikariPurple.copy(alpha = 0.18f), HikariCyan.copy(alpha = 0.10f))))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Manage active anime source", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Text("AniGo.to is the active compiled source for the MVP architecture.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(color = HikariPurple, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(70.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Extension, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                }
            }
        }
    }
}

@Composable
private fun SourceItem(
    source: AnimeSource,
    isActive: Boolean,
    onActivate: () -> Unit,
    onRefreshSession: () -> Unit
) {
    Card(shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = HikariPurple, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(46.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.CloudQueue, contentDescription = null, tint = Color.White)
                }
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(source.name, style = MaterialTheme.typography.labelMedium)
                Text("v1.0.0  ${source.lang.uppercase()}  Source", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = isActive, onCheckedChange = { onActivate() })
            IconButton(onClick = onRefreshSession) { Icon(Icons.Rounded.MoreVert, contentDescription = "Refresh source session") }
        }
    }
}

@Composable
private fun UpdateItem(name: String, version: String, tint: Color) {
    Card(shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = tint, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(46.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Update, contentDescription = null, tint = Color.White)
                }
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(name, style = MaterialTheme.typography.labelMedium)
                Text(version, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = {}) { Text("Update") }
        }
    }
}
