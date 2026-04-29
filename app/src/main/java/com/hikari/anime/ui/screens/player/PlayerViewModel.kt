package com.hikari.anime.ui.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hikari.anime.data.repository.HistoryRepository
import com.hikari.anime.domain.model.Episode
import com.hikari.anime.domain.model.VideoLink
import com.hikari.anime.domain.usecase.GetEpisodeListUseCase
import com.hikari.anime.domain.usecase.GetVideoLinksUseCase
import com.hikari.anime.domain.usecase.TrackHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val animeUrl: String = "",
    val episodeUrl: String = "",
    val episodeName: String = "Now Playing",
    val episodes: List<Episode> = emptyList(),
    val availableVideos: List<VideoLink> = emptyList(),
    val selectedVideo: VideoLink? = null,
    val savedProgress: Long = 0L,
    val showQualityPicker: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getVideoLinks: GetVideoLinksUseCase,
    private val getEpisodeList: GetEpisodeListUseCase,
    private val historyRepository: HistoryRepository,
    private val trackHistory: TrackHistoryUseCase
) : ViewModel() {
    private val initialEpisodeUrl: String = savedStateHandle["episodeUrl"] ?: ""
    private val animeUrl: String = savedStateHandle["animeUrl"] ?: ""
    private var videoJob: Job? = null
    private var progressJob: Job? = null
    private val _state = MutableStateFlow(
        PlayerUiState(
            animeUrl = animeUrl,
            episodeUrl = initialEpisodeUrl,
            episodeName = initialEpisodeUrl.toEpisodeLabel()
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getEpisodeList.observe(animeUrl).collect { episodes ->
                _state.update { it.copy(episodes = episodes) }
            }
        }
        viewModelScope.launch {
            getEpisodeList.refresh(animeUrl)
        }
        loadEpisode(initialEpisodeUrl)
    }

    fun loadEpisode(episodeUrl: String) {
        videoJob?.cancel()
        progressJob?.cancel()
        _state.update {
            it.copy(
                episodeUrl = episodeUrl,
                episodeName = it.episodes.firstOrNull { episode -> episode.url == episodeUrl }?.name ?: episodeUrl.toEpisodeLabel(),
                availableVideos = emptyList(),
                selectedVideo = null,
                savedProgress = 0L,
                showQualityPicker = false,
                isLoading = true,
                errorMessage = null
            )
        }
        progressJob = viewModelScope.launch {
            historyRepository.observeEpisodeProgress(animeUrl, episodeUrl).collect { progress ->
                _state.update { it.copy(savedProgress = progress) }
            }
        }
        videoJob = viewModelScope.launch {
            resolveVideos(episodeUrl)
        }
    }

    private suspend fun resolveVideos(episodeUrl: String) {
        runCatching { getVideoLinks(episodeUrl) }
            .onSuccess { videos ->
                _state.update {
                    it.copy(
                        availableVideos = videos,
                        selectedVideo = videos.firstOrNull(),
                        isLoading = false,
                        errorMessage = if (videos.isEmpty()) "No playable streams were found." else null
                    )
                }
            }
            .onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to resolve video links."
                    )
                }
            }
    }

    fun selectVideo(video: VideoLink) {
        _state.update { it.copy(selectedVideo = video, showQualityPicker = false) }
    }

    fun toggleQualityPicker() {
        _state.update { it.copy(showQualityPicker = !it.showQualityPicker) }
    }

    fun playNextEpisode() {
        val current = state.value
        val index = current.episodes.indexOfFirst { it.url == current.episodeUrl }
        current.episodes.getOrNull(index + 1)?.let { loadEpisode(it.url) }
    }

    fun playPreviousEpisode() {
        val current = state.value
        val index = current.episodes.indexOfFirst { it.url == current.episodeUrl }
        current.episodes.getOrNull(index - 1)?.let { loadEpisode(it.url) }
    }

    fun saveProgress(progress: Long, duration: Long) {
        if (progress <= 0L || duration <= 0L) return
        viewModelScope.launch {
            trackHistory(
                animeUrl = animeUrl,
                episodeUrl = state.value.episodeUrl,
                episodeName = state.value.episodeName,
                progress = progress,
                duration = duration
            )
        }
    }

    private fun String.toEpisodeLabel(): String =
        substringAfterLast('/').replace('-', ' ').ifBlank { "Episode" }
}
