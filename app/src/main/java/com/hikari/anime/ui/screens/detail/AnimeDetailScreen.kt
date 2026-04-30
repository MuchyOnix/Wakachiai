package com.hikari.anime.ui.screens.detail

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hikari.anime.ui.components.PosterImage
import com.hikari.anime.ui.components.EmptyState
import com.hikari.anime.ui.components.InfoBanner
import com.hikari.anime.ui.components.MetadataPill
import com.hikari.anime.ui.navigation.Screen
import com.hikari.anime.ui.theme.HikariCyan
import com.hikari.anime.ui.theme.HikariPink
import com.hikari.anime.ui.theme.HikariPurple

@Composable
fun AnimeDetailScreen(
    navController: NavHostController,
    vm: AnimeDetailViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val anime = state.anime

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 22.dp)
    ) {
        item {
            Box(Modifier.fillMaxWidth().height(380.dp)) {
                PosterImage(anime?.thumbnailUrl, anime?.title ?: "Anime", Modifier.fillMaxSize())
                Box(
                    Modifier.matchParentSize().background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.8f), MaterialTheme.colorScheme.background),
                            startY = 0f
                        )
                    )
                )
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(50))
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                    Text(anime?.title ?: "Loading", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(4.dp))
                    if (!anime?.genre.isNullOrBlank()) {
                        Text(anime?.genre.orEmpty(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            modifier = Modifier.weight(1f).height(50.dp),
                            enabled = state.episodes.isNotEmpty(),
                            onClick = {
                                val first = state.episodes.firstOrNull()
                                if (first != null) navController.navigate(Screen.playerRoute(first.url, state.animeUrl))
                            }
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Play First Episode")
                        }
                        OutlinedButton(
                            modifier = Modifier.height(50.dp),
                            onClick = vm::toggleLibrary
                        ) {
                            Icon(
                                if (state.isInLibrary) Icons.Rounded.BookmarkRemove else Icons.Rounded.BookmarkAdd,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 18.dp)) {
                state.errorMessage?.let { message ->
                    InfoBanner(
                        title = "Source refresh failed",
                        body = message.ifBlank { "Unable to refresh details from the active source." },
                        icon = Icons.Rounded.BookmarkAdd,
                        tint = HikariPink,
                        modifier = Modifier.padding(bottom = 14.dp),
                        action = {
                            Button(onClick = {
                                vm.loadDetails()
                                vm.refreshEpisodes()
                            }) { Text("Retry") }
                        }
                    )
                }
                Text("Synopsis", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(
                    anime?.description ?: "Details are loading from the active source.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                val tags = anime?.genre.orEmpty().split(",").map { it.trim() }.filter { it.isNotBlank() }.take(6)
                if (tags.isNotEmpty()) {
                    Spacer(Modifier.height(14.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tags, key = { it }) { tag ->
                            MetadataPill(tag, tint = if (tag.equals("TV", true) || tag.equals("Movie", true)) HikariCyan else HikariPurple)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text("Episodes", style = MaterialTheme.typography.titleMedium)
            }
        }
        if (state.isEpisodeLoading && state.episodes.isEmpty()) {
            items(5) {
                Card(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp).height(72.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    }
                }
            }
        }
        if (!state.isEpisodeLoading && state.episodes.isEmpty()) {
            item {
                EmptyState(
                    title = "Episodes are not available yet",
                    body = "AniGo renders some episode lists through client-side source scripts. Public detail metadata is cached now; episodes will appear here when the source exposes them.",
                    icon = Icons.Rounded.PlayArrow,
                    tint = HikariPurple,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }
        }
        items(state.episodes, key = { it.url }) { episode ->
            Card(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                onClick = { navController.navigate(Screen.playerRoute(episode.url, state.animeUrl)) }
            ) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)).background(HikariPurple.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = HikariPurple)
                    }
                    Column(Modifier.weight(1f).padding(start = 16.dp)) {
                        Text(episode.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Episode ${episode.episodeNumber.toInt()}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (episode.isDub) {
                                Spacer(Modifier.width(8.dp))
                                androidx.compose.material3.Surface(color = HikariPink.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                    Text("DUB", color = HikariPink, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
