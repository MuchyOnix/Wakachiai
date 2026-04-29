package com.hikari.anime.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hikari.anime.ui.components.AnimePosterCard
import com.hikari.anime.ui.components.EmptyState
import com.hikari.anime.ui.components.HikariTopBar
import com.hikari.anime.ui.components.InfoBanner
import com.hikari.anime.ui.components.PosterImage
import com.hikari.anime.ui.components.SectionHeader
import com.hikari.anime.ui.navigation.Screen
import com.hikari.anime.ui.theme.HikariCyan
import com.hikari.anime.ui.theme.HikariPink
import com.hikari.anime.ui.theme.HikariPurple

@Composable
fun LibraryScreen(
    navController: NavHostController,
    vm: LibraryViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HikariTopBar(
                title = "Library",
                subtitle = "Saved shows, watchlist, and local progress"
            ) {
                IconButton(onClick = {}) { Icon(Icons.Rounded.Search, contentDescription = "Search") }
                IconButton(onClick = {}) { Icon(Icons.Rounded.MoreVert, contentDescription = "More") }
            }
        }
        if (state.savedShows == 0 && state.recentlyAdded.isEmpty()) {
            item {
                InfoBanner(
                    title = "Library syncs from AniGo metadata",
                    body = "Open a title from Explore and add it here. Entries stay backed by the active source and Room cache.",
                    icon = Icons.Rounded.Bookmark,
                    tint = HikariPurple
                )
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LibraryStat("Saved Shows", "${state.savedShows} titles", HikariPurple) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = HikariPurple)
                }
                LibraryStat("Watchlist", "${state.watchlist} titles", HikariPink) {
                    Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = HikariPink)
                }
                LibraryStat("Downloads", state.downloads, HikariCyan) {
                    Icon(Icons.Rounded.Download, contentDescription = null, tint = HikariCyan)
                }
                LibraryStat("Favorites", "${state.favorites} titles", Color(0xFFE94593)) {
                    Icon(Icons.Rounded.Favorite, contentDescription = null, tint = Color(0xFFE94593))
                }
            }
        }
        item {
            SectionHeader("My Collections", "+ New")
        }
        items(state.collections) { (name, count) ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(Modifier.size(width = 76.dp, height = 54.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        val preview = state.recentlyAdded.take(2)
                        if (preview.isEmpty()) {
                            repeat(2) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).fillMaxSize()
                                ) {
                                    androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        } else {
                            preview.forEach {
                                PosterImage(it.thumbnailUrl, it.title, Modifier.weight(1f).fillMaxSize().clip(RoundedCornerShape(8.dp)))
                            }
                        }
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(name, style = MaterialTheme.typography.labelMedium)
                        Text("$count titles", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = {}) { Icon(Icons.Rounded.MoreVert, contentDescription = "More options") }
                }
            }
        }
        item { SectionHeader("Recently Added") }
        if (state.recentlyAdded.isEmpty()) {
            item {
                EmptyState(
                    title = "No titles saved yet",
                    body = "Your library will fill with shows you add from Explore.",
                    icon = Icons.Rounded.Bookmark,
                    tint = HikariPink
                )
            }
        } else {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.recentlyAdded, key = { it.url }) { anime ->
                        AnimePosterCard(
                            anime = anime,
                            modifier = Modifier.size(width = 120.dp, height = 224.dp),
                            compact = true,
                            onClick = { navController.navigate(Screen.detailRoute(anime.url)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryStat(
    title: String,
    subtitle: String,
    tint: Color,
    icon: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = tint.copy(alpha = 0.16f), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(44.dp)) {
                androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) { icon() }
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(title, style = MaterialTheme.typography.labelMedium)
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
    }
}
