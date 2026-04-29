package com.hikari.anime.domain.usecase

import com.hikari.anime.data.repository.HistoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TrackHistoryUseCaseTest {

    private lateinit var trackHistoryUseCase: TrackHistoryUseCase
    private lateinit var repository: HistoryRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        trackHistoryUseCase = TrackHistoryUseCase(repository)
    }

    @Test
    fun `invoke should call repository track method with correct parameters`() = runTest {
        // Arrange
        val animeUrl = "https://example.com/anime/1"
        val episodeUrl = "https://example.com/anime/1/episode/1"
        val episodeName = "Episode 1"
        val progress = 1000L
        val duration = 5000L

        // Act
        trackHistoryUseCase(animeUrl, episodeUrl, episodeName, progress, duration)

        // Assert
        coVerify(exactly = 1) {
            repository.track(
                animeUrl = animeUrl,
                episodeUrl = episodeUrl,
                episodeName = episodeName,
                progress = progress,
                duration = duration
            )
        }
    }
}
