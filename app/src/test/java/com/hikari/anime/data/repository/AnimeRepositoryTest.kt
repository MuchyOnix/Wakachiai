package com.hikari.anime.data.repository

import app.cash.turbine.test
import com.hikari.anime.core.database.dao.AnimeDao
import com.hikari.anime.core.database.dao.EpisodeDao
import com.hikari.anime.core.util.Result
import com.hikari.anime.extension.ExtensionManager
import com.hikari.anime.extension.api.AnimeSource
import com.hikari.anime.extension.api.model.AnimesPage
import com.hikari.anime.extension.api.model.SAnime
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnimeRepositoryTest {

    private lateinit var extensionManager: ExtensionManager
    private lateinit var animeDao: AnimeDao
    private lateinit var episodeDao: EpisodeDao
    private lateinit var animeSource: AnimeSource
    private lateinit var repository: AnimeRepository

    @Before
    fun setup() {
        extensionManager = mockk()
        animeDao = mockk(relaxed = true)
        episodeDao = mockk(relaxed = true)
        animeSource = mockk()

        every { extensionManager.currentSource() } returns animeSource
        repository = AnimeRepository(extensionManager, animeDao, episodeDao)
    }

    @Test
    fun `getPopularAnime_emitsLoadingAndSuccess`() = runTest {
        val sAnime = SAnime(url = "url", title = "title")
        val page = AnimesPage(animes = listOf(sAnime), hasNextPage = false)
        coEvery { animeSource.getPopularAnime(1) } returns page

        repository.getPopularAnime(1).test {
            assertEquals(Result.Loading, awaitItem())
            val successItem = awaitItem()
            assertTrue(successItem is Result.Success)
            assertEquals(page, (successItem as Result.Success).data)
            awaitComplete()
        }
    }

    @Test
    fun `getPopularAnime_emptyList_emitsError`() = runTest {
        val page = AnimesPage(animes = emptyList(), hasNextPage = false)
        coEvery { animeSource.getPopularAnime(1) } returns page

        repository.getPopularAnime(1).test {
            assertEquals(Result.Loading, awaitItem())
            val errorItem = awaitItem()
            assertTrue(errorItem is Result.Error)
            assertEquals("No anime returned by active source.", (errorItem as Result.Error).throwable.message)
            assertTrue(errorItem.throwable is IllegalStateException)
            awaitComplete()
        }
    }

    @Test
    fun `getPopularAnime_exceptionThrown_emitsError`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { animeSource.getPopularAnime(1) } throws exception

        repository.getPopularAnime(1).test {
            assertEquals(Result.Loading, awaitItem())
            val errorItem = awaitItem()
            assertTrue(errorItem is Result.Error)
            assertEquals(exception, (errorItem as Result.Error).throwable)
            awaitComplete()
        }
    }
}
