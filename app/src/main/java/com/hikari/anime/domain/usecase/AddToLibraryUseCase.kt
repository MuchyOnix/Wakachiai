package com.hikari.anime.domain.usecase

import com.hikari.anime.data.repository.LibraryRepository
import javax.inject.Inject

class AddToLibraryUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(animeUrl: String) = repository.addToLibrary(animeUrl)
}
