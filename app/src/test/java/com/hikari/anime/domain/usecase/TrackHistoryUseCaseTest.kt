package com.hikari.anime.domain.usecase

import com.hikari.anime.data.repository.HistoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class TrackHistoryUseCaseTest {

    private val repository: HistoryRepository = mockk(relaxed = true)
    private val useCase = TrackHistoryUseCase(repository)

    @Test
    fun `invoke should call repository track`() = runTest {
        // Arrange
        val animeUrl = "https://example.com/anime"
        val episodeUrl = "https://example.com/episode1"
        val episodeName = "Episode 1"
        val progress = 1000L
        val duration = 5000L

        // Act
        useCase(animeUrl, episodeUrl, episodeName, progress, duration)

        // Assert
        coVerify(exactly = 1) {
            repository.track(animeUrl, episodeUrl, episodeName, progress, duration)
        }
    }
}
