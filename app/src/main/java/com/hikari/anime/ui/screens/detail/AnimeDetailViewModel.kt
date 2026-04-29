package com.hikari.anime.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hikari.anime.core.util.Result
import com.hikari.anime.data.repository.AnimeRepository
import com.hikari.anime.data.repository.LibraryRepository
import com.hikari.anime.domain.model.Anime
import com.hikari.anime.domain.model.Episode
import com.hikari.anime.domain.usecase.AddToLibraryUseCase
import com.hikari.anime.domain.usecase.GetEpisodeListUseCase
import com.hikari.anime.domain.usecase.RemoveFromLibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val animeUrl: String = "",
    val anime: Anime? = null,
    val episodes: List<Episode> = emptyList(),
    val isInLibrary: Boolean = false,
    val isLoading: Boolean = true,
    val isEpisodeLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AnimeRepository,
    libraryRepository: LibraryRepository,
    private val episodes: GetEpisodeListUseCase,
    private val addToLibrary: AddToLibraryUseCase,
    private val removeFromLibrary: RemoveFromLibraryUseCase
) : ViewModel() {
    private val animeUrl: String = savedStateHandle["animeUrl"] ?: ""
    private val _state = MutableStateFlow(DetailUiState(animeUrl = animeUrl))
    val state = _state.asStateFlow()

    init {
        loadDetails()
        viewModelScope.launch {
            episodes.observe(animeUrl).collect { list -> _state.update { it.copy(episodes = list) } }
        }
        viewModelScope.launch {
            libraryRepository.observeIsInLibrary(animeUrl).collect { isInLibrary ->
                _state.update { it.copy(isInLibrary = isInLibrary) }
            }
        }
        refreshEpisodes()
    }

    fun loadDetails() {
        viewModelScope.launch {
            repository.getAnimeDetails(animeUrl).collect { result ->
                when (result) {
                    Result.Loading -> _state.update { it.copy(isLoading = true) }
                    is Result.Success -> _state.update { it.copy(anime = result.data, isLoading = false, errorMessage = null) }
                    is Result.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.throwable.message) }
                }
            }
        }
    }

    fun refreshEpisodes() {
        viewModelScope.launch {
            _state.update { it.copy(isEpisodeLoading = true) }
            when (val result = episodes.refresh(animeUrl)) {
                is Result.Success<*> -> _state.update { it.copy(isEpisodeLoading = false, errorMessage = null) }
                is Result.Error -> _state.update { it.copy(isEpisodeLoading = false, errorMessage = result.throwable.message) }
                Result.Loading -> Unit
            }
        }
    }

    fun toggleLibrary() {
        viewModelScope.launch {
            if (state.value.isInLibrary) removeFromLibrary(animeUrl) else addToLibrary(animeUrl)
        }
    }
}
