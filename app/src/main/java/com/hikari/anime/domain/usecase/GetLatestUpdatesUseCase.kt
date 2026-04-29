package com.hikari.anime.domain.usecase

import com.hikari.anime.data.repository.AnimeRepository
import javax.inject.Inject

class GetLatestUpdatesUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    operator fun invoke(page: Int) = repository.getLatestUpdates(page)
}
