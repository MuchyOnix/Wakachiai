package com.hikari.anime.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.hikari.anime.ui.components.EmptyState
import com.hikari.anime.ui.components.HikariTopBar
import com.hikari.anime.ui.components.HorizontalAnimeItem
import com.hikari.anime.ui.navigation.Screen
import com.hikari.anime.ui.theme.HikariCyan

@Composable
fun HistoryScreen(
    navController: NavHostController,
    vm: HistoryViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val grouped = state.items.groupBy { it.dateLabel }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HikariTopBar(
                title = "History",
                subtitle = "Progress is stored locally as you watch"
            ) {
                IconButton(onClick = {}) { Icon(Icons.Rounded.FilterList, contentDescription = "Filter") }
                IconButton(onClick = {}) { Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete") }
            }
            ScrollableTabRow(selectedTabIndex = 0, edgePadding = 0.dp, containerColor = MaterialTheme.colorScheme.background, contentColor = HikariCyan) {
                Tab(selected = true, onClick = {}, text = { Text("Recently Watched") })
                Tab(selected = false, onClick = {}, text = { Text("Watch Timeline") })
            }
        }
        if (grouped.isEmpty()) {
            item {
                EmptyState(
                    title = "No watch history yet",
                    body = "Start playback from a title detail page and Hikari will track progress here.",
                    icon = Icons.Rounded.History,
                    tint = HikariCyan
                )
            }
        }
        grouped.forEach { (date, entries) ->
            item {
                Text(date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(entries, key = { it.anime.url + it.episode }) { item ->
                HorizontalAnimeItem(
                    anime = item.anime,
                    subtitle = "${item.episode}  ${item.subtitle}",
                    progress = item.progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (grouped.isNotEmpty()) item {
            androidx.compose.material3.Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Showing history from the last 30 days", style = MaterialTheme.typography.labelMedium)
                    Text("Change", style = MaterialTheme.typography.labelMedium, color = HikariCyan)
                }
            }
        }
    }
}
