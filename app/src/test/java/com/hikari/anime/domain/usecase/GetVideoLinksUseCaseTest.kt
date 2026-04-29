package com.hikari.anime.domain.usecase

import com.hikari.anime.domain.model.VideoLink
import com.hikari.anime.extension.ExtensionManager
import com.hikari.anime.extension.api.AnimeSource
import com.hikari.anime.extension.api.model.SEpisode
import com.hikari.anime.extension.api.model.Video
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import okhttp3.Headers

class GetVideoLinksUseCaseTest {

    @Test
    fun `invoke should map video list from current source to video links`() = runTest {
        // Arrange
        val episodeUrl = "http://example.com/episode/1"
        val mockExtensionManager = mockk<ExtensionManager>()
        val mockAnimeSource = mockk<AnimeSource>()

        val videos = listOf(
            Video(url = "http://video1.com", quality = "1080p", headers = Headers.Builder().add("Referer", "http://example.com").build()),
            Video(url = "http://video2.com", quality = "720p", headers = Headers.headersOf())
        )

        every { mockExtensionManager.currentSource() } returns mockAnimeSource
        coEvery { mockAnimeSource.getVideoList(SEpisode(url = episodeUrl)) } returns videos

        val useCase = GetVideoLinksUseCase(mockExtensionManager)

        // Act
        val result = useCase(episodeUrl)

        // Assert
        assertEquals(2, result.size)

        assertEquals("http://video1.com", result[0].url)
        assertEquals("1080p", result[0].quality)
        assertEquals("http://example.com", result[0].headers["Referer"])

        assertEquals("http://video2.com", result[1].url)
        assertEquals("720p", result[1].quality)
        assertEquals(0, result[1].headers.size)
    }

    @Test
    fun `invoke should return empty list when source returns no videos`() = runTest {
        // Arrange
        val episodeUrl = "http://example.com/episode/1"
        val mockExtensionManager = mockk<ExtensionManager>()
        val mockAnimeSource = mockk<AnimeSource>()

        every { mockExtensionManager.currentSource() } returns mockAnimeSource
        coEvery { mockAnimeSource.getVideoList(SEpisode(url = episodeUrl)) } returns emptyList()

        val useCase = GetVideoLinksUseCase(mockExtensionManager)

        // Act
        val result = useCase(episodeUrl)

        // Assert
        assertTrue(result.isEmpty())
    }
}
