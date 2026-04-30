package com.hikari.anime.ui.screens.explore

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hikari.anime.ui.components.AnimePosterCard
import com.hikari.anime.ui.components.EmptyState
import com.hikari.anime.ui.components.HorizontalAnimeItem
import com.hikari.anime.ui.components.SectionHeader
import com.hikari.anime.ui.components.SpotlightCard
import com.hikari.anime.ui.navigation.Screen
import com.hikari.anime.extension.api.model.SAnime
import com.hikari.anime.ui.theme.HikariCyan
import com.hikari.anime.ui.theme.HikariPink
import com.hikari.anime.ui.theme.HikariPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navController: NavHostController,
    vm: ExploreViewModel = hiltViewModel()
) {
    val uiState by vm.state.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(104.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Explore", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = {}) {
                        Icon(Icons.Rounded.NotificationsNone, contentDescription = "Notifications")
                    }
                }
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = vm::onSearchQueryChange,
                    placeholder = { Text("Search anime, genres, studios...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.FilterList, contentDescription = "Filters")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        uiState.spotlight?.let { anime ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                SpotlightCard(
                    anime = anime,
                    onClick = { navController.navigate(Screen.detailRoute(anime.url)) }
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Spacer(Modifier.height(14.dp))
                HomeSourceBanner(count = uiState.animes.size + uiState.latestUpdates.size)
                Spacer(Modifier.height(18.dp))
                SectionHeader("Explore Categories", action = "")
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    item { GenreChip("All", MaterialTheme.colorScheme.onSurfaceVariant, uiState.selectedGenre == null && uiState.selectedType == null && uiState.selectedStatus == null) { vm.onGenreSelected(null); vm.onTypeSelected(null); vm.onStatusSelected(null) } }
                    item { GenreChip("Action", HikariPink, uiState.selectedGenre == "Action") { vm.onGenreSelected(if (uiState.selectedGenre == "Action") null else "Action") } }
                    item { GenreChip("Fantasy", HikariPurple, uiState.selectedGenre == "Fantasy") { vm.onGenreSelected(if (uiState.selectedGenre == "Fantasy") null else "Fantasy") } }
                    item { GenreChip("Drama", Color(0xFF64A3FF), uiState.selectedGenre == "Drama") { vm.onGenreSelected(if (uiState.selectedGenre == "Drama") null else "Drama") } }
                    item { GenreChip("Sci-Fi", HikariCyan, uiState.selectedGenre == "Sci-Fi") { vm.onGenreSelected(if (uiState.selectedGenre == "Sci-Fi") null else "Sci-Fi") } }
                    item { GenreChip("Romance", Color(0xFFFF7B94), uiState.selectedGenre == "Romance") { vm.onGenreSelected(if (uiState.selectedGenre == "Romance") null else "Romance") } }
                    item { GenreChip("TV", HikariPurple, uiState.selectedType == "TV") { vm.onTypeSelected(if (uiState.selectedType == "TV") null else "TV") } }
                    item { GenreChip("Movie", HikariCyan, uiState.selectedType == "Movie") { vm.onTypeSelected(if (uiState.selectedType == "Movie") null else "Movie") } }
                    item { GenreChip("Ongoing", HikariCyan, uiState.selectedStatus == SAnime.ONGOING) { vm.onStatusSelected(if (uiState.selectedStatus == SAnime.ONGOING) null else SAnime.ONGOING) } }
                    item { GenreChip("Completed", HikariPink, uiState.selectedStatus == SAnime.COMPLETED) { vm.onStatusSelected(if (uiState.selectedStatus == SAnime.COMPLETED) null else SAnime.COMPLETED) } }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("Most Viewed") }
        if (uiState.isLoading && uiState.animes.isEmpty()) {
            items(6) {
                LoadingAnimeCard()
            }
        }
        items(uiState.trending, key = { it.url }) { anime ->
            AnimePosterCard(
                anime = anime,
                compact = true,
                onClick = { navController.navigate(Screen.detailRoute(anime.url)) }
            )
        }

        val continueItem = uiState.continueWatching.firstOrNull()
        if (continueItem != null) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    SectionHeader("Continue Watching")
                    Spacer(Modifier.height(8.dp))
                    HorizontalAnimeItem(
                        anime = com.hikari.anime.domain.model.Anime(
                            url = continueItem.animeUrl,
                            sourceId = 4815162342L,
                            title = continueItem.animeTitle,
                            thumbnailUrl = continueItem.thumbnailUrl,
                            description = null,
                            genre = null,
                            status = 0,
                            initialized = true
                        ),
                        subtitle = continueItem.episodeName,
                        progress = (continueItem.progress.toFloat() / continueItem.totalDuration.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)
                    )
                }
            }
        }

        if (uiState.latestUpdates.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("Latest From AniGo") }
            items(uiState.latestUpdates.take(3), key = { "latest-${it.url}" }) { anime ->
                AnimePosterCard(
                    anime = anime,
                    compact = true,
                    onClick = { navController.navigate(Screen.detailRoute(anime.url)) }
                )
            }
        }

        uiState.error?.let {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Unable to refresh source data.", style = MaterialTheme.typography.labelMedium)
                        Button(onClick = vm::retry) { Text("Retry") }
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) { SectionHeader("Recommended for You") }
        if (!uiState.isLoading && uiState.genreFilteredAnimes.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    title = "No source results",
                    body = "AniGo did not return items for the current search or filter set.",
                    tint = HikariPurple,
                    actionLabel = "Retry",
                    onAction = vm::retry
                )
            }
        }
        items(uiState.recommended, key = { "recommended-${it.url}" }) { anime ->
            AnimePosterCard(
                anime = anime,
                compact = true,
                onClick = { navController.navigate(Screen.detailRoute(anime.url)) }
            )
        }
        if (uiState.hasNextPage && !uiState.isLoading && uiState.searchQuery.isBlank()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                LaunchedEffect(uiState.animes.size) {
                    vm.loadNextPage()
                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun HomeSourceBanner(count: Int) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(HikariPurple.copy(alpha = 0.22f), HikariCyan.copy(alpha = 0.14f))))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("AniGo Trending", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text("Live catalog updates from source", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(color = HikariPurple, shape = RoundedCornerShape(50)) {
                Text("$count items", color = Color.White, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
private fun GenreChip(label: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Surface(color = color.copy(alpha = if (selected) 0.28f else 0.16f), shape = RoundedCornerShape(6.dp), modifier = Modifier.size(22.dp)) {
                Icon(Icons.Rounded.Search, contentDescription = null, tint = color, modifier = Modifier.padding(5.dp))
            }
        }
    )
}

@Composable
private fun LoadingAnimeCard() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.height(190.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        }
    }
}
