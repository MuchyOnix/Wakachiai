package com.hikari.anime.extension

import com.hikari.anime.core.datastore.ExtensionPreferences
import com.hikari.anime.extension.api.AnimeSource
import com.hikari.anime.extension.source.anigoto.AniGoTo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExtensionManagerTest {

    private lateinit var prefs: ExtensionPreferences
    private lateinit var aniGoTo: AniGoTo
    private lateinit var extensionManager: ExtensionManager

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        prefs = mockk(relaxed = true)
        aniGoTo = mockk(relaxed = true)

        every { aniGoTo.id } returns 1L
        every { aniGoTo.name } returns "Test Source"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization loads active source from preferences`() = runTest(testDispatcher) {
        val activeSourceIdFlow = MutableStateFlow(1L)
        every { prefs.activeSourceId } returns activeSourceIdFlow

        extensionManager = ExtensionManager(prefs, aniGoTo)

        assertEquals(1L, extensionManager.activeSourceId.first())
        assertEquals(aniGoTo, extensionManager.activeSource.first())
        assertEquals(aniGoTo, extensionManager.currentSource())
    }

    @Test
    fun `setActiveSource updates preferences`() = runTest(testDispatcher) {
        val activeSourceIdFlow = MutableStateFlow(1L)
        every { prefs.activeSourceId } returns activeSourceIdFlow

        extensionManager = ExtensionManager(prefs, aniGoTo)

        val newSourceId = 2L

        extensionManager.setActiveSource(newSourceId)

        coVerify { prefs.setActiveSourceId(newSourceId) }
    }

    @Test
    fun `getAllSources returns available sources`() = runTest(testDispatcher) {
        val activeSourceIdFlow = MutableStateFlow(1L)
        every { prefs.activeSourceId } returns activeSourceIdFlow

        extensionManager = ExtensionManager(prefs, aniGoTo)

        val sources = extensionManager.getAllSources()

        assertEquals(1, sources.size)
        assertEquals(aniGoTo, sources.first())
    }

    @Test
    fun `active source maps correctly to available source`() = runTest(testDispatcher) {
        val activeSourceIdFlow = MutableStateFlow(999L) // Non-existent source
        every { prefs.activeSourceId } returns activeSourceIdFlow

        extensionManager = ExtensionManager(prefs, aniGoTo)

        // Even if an unknown ID is provided, it should fallback to the first available source
        assertEquals(aniGoTo, extensionManager.activeSource.first())
        assertEquals(aniGoTo, extensionManager.currentSource())
    }
}
