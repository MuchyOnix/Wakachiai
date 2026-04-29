package com.hikari.anime.domain.usecase

import com.hikari.anime.data.repository.HistoryRepository
import javax.inject.Inject

class TrackHistoryUseCase @Inject constructor(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(
        animeUrl: String,
        episodeUrl: String,
        episodeName: String,
        progress: Long,
        duration: Long
    ) = repository.track(animeUrl, episodeUrl, episodeName, progress, duration)
}
