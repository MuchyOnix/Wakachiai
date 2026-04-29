package com.hikari.anime.ui.screens.explore

import com.hikari.anime.core.util.Result
import com.hikari.anime.data.repository.HistoryRepository
import com.hikari.anime.domain.usecase.GetLatestUpdatesUseCase
import com.hikari.anime.domain.usecase.GetPopularAnimeUseCase
import com.hikari.anime.domain.usecase.SearchAnimeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {

    private lateinit var viewModel: ExploreViewModel
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var getPopularAnimeUseCase: GetPopularAnimeUseCase
    private lateinit var searchAnimeUseCase: SearchAnimeUseCase
    private lateinit var getLatestUpdatesUseCase: GetLatestUpdatesUseCase
    private lateinit var historyRepository: HistoryRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        getPopularAnimeUseCase = mock {
            on { invoke(any()) } doReturn emptyFlow()
        }
        searchAnimeUseCase = mock {
            on { invoke(any(), any()) } doReturn emptyFlow()
        }
        getLatestUpdatesUseCase = mock {
            on { invoke(any()) } doReturn emptyFlow()
        }
        historyRepository = mock {
            on { observeContinueWatching() } doReturn emptyFlow()
        }

        viewModel = ExploreViewModel(
            getPopularAnimeUseCase,
            searchAnimeUseCase,
            getLatestUpdatesUseCase,
            historyRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChange updates state with new query`() = runTest(testDispatcher) {
        val query = "Naruto"
        viewModel.onSearchQueryChange(query)

        assertEquals(query, viewModel.state.value.searchQuery)
    }

    @Test
    fun `onSearchQueryChange debounces rapid typing and executes search only once`() = runTest(testDispatcher) {
        viewModel.onSearchQueryChange("N")
        advanceTimeBy(100)

        viewModel.onSearchQueryChange("Na")
        advanceTimeBy(100)

        viewModel.onSearchQueryChange("Nar")
        advanceTimeBy(100)

        // None of the earlier searches should have executed
        verifyNoInteractions(searchAnimeUseCase)

        // Fast forward past the 300ms debounce of the last event
        advanceTimeBy(300)

        // Ensure search happens with the latest string only
        verify(searchAnimeUseCase, times(1)).invoke(1, "Nar")
    }

    @Test
    fun `onSearchQueryChange calls loadNextPage when query is blank after delay`() = runTest(testDispatcher) {
        // Initial setup calls loadNextPage(reset = true)
        testScheduler.advanceUntilIdle()
        verify(getPopularAnimeUseCase, times(1)).invoke(1)

        // Set search query and wait
        viewModel.onSearchQueryChange("Naruto")
        advanceTimeBy(301)
        verify(searchAnimeUseCase, times(1)).invoke(1, "Naruto")

        // Set blank query
        viewModel.onSearchQueryChange("")
        advanceTimeBy(301)

        // Assert loadNextPage was called again
        verify(getPopularAnimeUseCase, times(2)).invoke(1)
    }
}
