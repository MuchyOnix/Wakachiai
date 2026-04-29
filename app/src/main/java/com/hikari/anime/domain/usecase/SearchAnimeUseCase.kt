package com.hikari.anime.domain.usecase

import com.hikari.anime.data.repository.AnimeRepository
import javax.inject.Inject

class SearchAnimeUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(page: Int, query: String) = repository.searchAnime(page, query)
}
