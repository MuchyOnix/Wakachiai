package com.hikari.anime.domain.usecase

import com.hikari.anime.core.util.Result
import com.hikari.anime.data.repository.AnimeRepository
import com.hikari.anime.domain.model.Episode
import javax.inject.Inject

class GetEpisodeListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    fun observe(animeUrl: String) = repository.observeEpisodes(animeUrl)
    suspend fun refresh(animeUrl: String): Result<List<Episode>> = repository.refreshEpisodes(animeUrl)
}
