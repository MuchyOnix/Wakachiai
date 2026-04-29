package com.hikari.anime.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hikari.anime.data.repository.HistoryRepository
import com.hikari.anime.domain.model.Anime
import com.hikari.anime.domain.model.HistoryEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HistoryItemUi(
    val anime: Anime,
    val episode: String,
    val subtitle: String,
    val progress: Float,
    val dateLabel: String
)

data class HistoryUiState(
    val items: List<HistoryItemUi> = emptyList()
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repository: HistoryRepository
) : ViewModel() {
    val state: StateFlow<HistoryUiState> = repository.observeRecentHistory()
        .map { entries ->
            HistoryUiState(items = entries.map { it.toUiItem() })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())
}

private fun HistoryEntry.toUiItem(): HistoryItemUi {
    val duration = totalDuration.takeIf { it > 0L } ?: 1L
    return HistoryItemUi(
        anime = Anime(
            url = animeUrl,
            sourceId = 4815162342L,
            title = animeTitle,
            thumbnailUrl = thumbnailUrl,
            description = null,
            genre = null,
            status = 0,
            initialized = true
        ),
        episode = episodeName,
        subtitle = "Last watched",
        progress = (progress.toFloat() / duration.toFloat()).coerceIn(0f, 1f),
        dateLabel = watchedAt.toDateLabel()
    )
}

private fun Long.toDateLabel(): String {
    val watched = Calendar.getInstance().apply { timeInMillis = this@toDateLabel }
    val today = Calendar.getInstance()
    if (watched.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        watched.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
    ) return "Today"

    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    if (watched.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
        watched.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)
    ) return "Yesterday"

    return SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(this))
}
