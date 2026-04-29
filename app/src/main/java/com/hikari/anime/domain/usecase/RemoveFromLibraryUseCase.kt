package com.hikari.anime.domain.usecase

import com.hikari.anime.data.repository.LibraryRepository
import javax.inject.Inject

class RemoveFromLibraryUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(animeUrl: String) = repository.removeFromLibrary(animeUrl)
}
