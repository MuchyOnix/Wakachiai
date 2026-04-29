package com.hikari.anime.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hikari.anime.core.util.Result
import com.hikari.anime.data.mapper.toDomain
import com.hikari.anime.data.repository.HistoryRepository
import com.hikari.anime.domain.model.Anime
import com.hikari.anime.domain.model.HistoryEntry
import com.hikari.anime.domain.usecase.GetLatestUpdatesUseCase
import com.hikari.anime.domain.usecase.GetPopularAnimeUseCase
import com.hikari.anime.domain.usecase.SearchAnimeUseCase
import com.hikari.anime.extension.ExtensionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val activeSourceName: String = "",
    val searchQuery: String = "",
    val animes: List<Anime> = emptyList(),
    val latestUpdates: List<Anime> = emptyList(),
    val continueWatching: List<HistoryEntry> = emptyList(),
    val selectedGenre: String? = null,
    val selectedType: String? = null,
    val selectedStatus: Int? = null,
    val hasNextPage: Boolean = true,
    val isLoading: Boolean = false,
    val error: Throwable? = null
) {
    val spotlight: Anime? = animes.firstOrNull()
    val genreFilteredAnimes: List<Anime> =
        animes
            .let { list ->
                selectedGenre?.let { genre -> list.filter { it.genre?.contains(genre, ignoreCase = true) == true } } ?: list
            }
            .let { list ->
                selectedType?.let { type -> list.filter { it.genre?.contains(type, ignoreCase = true) == true } } ?: list
            }
            .let { list ->
                selectedStatus?.let { status -> list.filter { it.status == status } } ?: list
            }
    val trending: List<Anime> = genreFilteredAnimes.drop(1).take(6)
    val recommended: List<Anime> = genreFilteredAnimes.takeLast(4)
    val genres: List<String> = listOf("Action", "Fantasy", "Drama", "Sci-Fi", "Romance")
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val extensionManager: ExtensionManager,
    private val getPopularAnime: GetPopularAnimeUseCase,
    private val searchAnime: SearchAnimeUseCase,
    private val getLatestUpdates: GetLatestUpdatesUseCase,
    historyRepository: HistoryRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ExploreUiState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null
    private var currentPage = 1

    init {
        loadNextPage(reset = true)
        loadLatestUpdates()
        viewModelScope.launch {
            historyRepository.observeContinueWatching().collect { entries ->
                _state.update { it.copy(continueWatching = entries) }
            }
        }
        viewModelScope.launch {
            extensionManager.activeSource.collect { source ->
                _state.update { it.copy(activeSourceName = source.name) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            if (query.isBlank()) loadNextPage(reset = true) else executeSearch(query)
        }
    }

    fun loadNextPage(reset: Boolean = false) {
        if (reset) currentPage = 1
        viewModelScope.launch {
            getPopularAnime(currentPage).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(isLoading = true) }
                    is Result.Success -> {
                        val items = result.data.animes.map { it.toDomain() }
                        _state.update { state ->
                            state.copy(
                                animes = if (reset) items else state.animes + items,
                                hasNextPage = result.data.hasNextPage,
                                isLoading = false,
                                error = null
                            )
                        }
                        currentPage++
                    }
                    is Result.Error -> _state.update { it.copy(error = result.throwable, isLoading = false) }
                }
            }
        }
    }

    fun onGenreSelected(genre: String?) {
        _state.update { it.copy(selectedGenre = genre) }
    }

    fun onTypeSelected(type: String?) {
        _state.update { it.copy(selectedType = type) }
    }

    fun onStatusSelected(status: Int?) {
        _state.update { it.copy(selectedStatus = status) }
    }

    fun retry() {
        if (state.value.searchQuery.isBlank()) loadNextPage(reset = true) else executeSearch(state.value.searchQuery)
        loadLatestUpdates()
    }

    private fun loadLatestUpdates() {
        viewModelScope.launch {
            getLatestUpdates(1).collect { result ->
                if (result is Result.Success) {
                    _state.update { state ->
                        state.copy(latestUpdates = result.data.animes.map { it.toDomain() })
                    }
                }
            }
        }
    }

    private fun executeSearch(query: String) {
        viewModelScope.launch {
            searchAnime(1, query).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(isLoading = true) }
                    is Result.Success -> _state.update {
                        it.copy(
                            animes = result.data.animes.map { anime -> anime.toDomain() },
                            hasNextPage = result.data.hasNextPage,
                            isLoading = false
                        )
                    }
                    is Result.Error -> _state.update { it.copy(error = result.throwable, isLoading = false) }
                }
            }
        }
    }
}
