package com.hikari.anime.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hikari.anime.data.repository.LibraryRepository
import com.hikari.anime.domain.model.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class LibraryUiState(
    val savedShows: Int = 0,
    val watchlist: Int = 0,
    val downloads: String = "0 episodes - 0 GB",
    val favorites: Int = 0,
    val collections: List<Pair<String, Int>> = listOf(
        "All Anime" to 0,
        "Action Spectaculars" to 0,
        "Fantasy World" to 0,
        "Romance Picks" to 0
    ),
    val recentlyAdded: List<Anime> = emptyList()
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    repository: LibraryRepository
) : ViewModel() {
    val state: StateFlow<LibraryUiState> = repository.observeLibraryAnime()
        .map { saved ->
            LibraryUiState(
                savedShows = saved.size,
                watchlist = saved.count { it.genre?.contains("Action", ignoreCase = true) == true },
                favorites = saved.count { it.rating >= 4.7f },
                collections = listOf(
                    "All Anime" to saved.size,
                    "Action Spectaculars" to saved.count { it.genre?.contains("Action", ignoreCase = true) == true },
                    "Fantasy World" to saved.count { it.genre?.contains("Fantasy", ignoreCase = true) == true },
                    "Romance Picks" to saved.count { it.genre?.contains("Romance", ignoreCase = true) == true }
                ),
                recentlyAdded = saved
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LibraryUiState())
}
