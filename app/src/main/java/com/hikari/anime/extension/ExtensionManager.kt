package com.hikari.anime.extension

import com.hikari.anime.core.datastore.ExtensionPreferences
import com.hikari.anime.extension.api.AnimeSource
import com.hikari.anime.extension.source.anigoto.AniGoTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionManager @Inject constructor(
    private val prefs: ExtensionPreferences,
    aniGoTo: AniGoTo
) {
    private val availableSources: List<AnimeSource> = listOf(aniGoTo)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val activeSourceId: StateFlow<Long> = prefs.activeSourceId
        .stateIn(scope, SharingStarted.Eagerly, availableSources.first().id)

    val activeSource: StateFlow<AnimeSource> = prefs.activeSourceId
        .map { id -> availableSources.firstOrNull { it.id == id } ?: availableSources.first() }
        .stateIn(scope, SharingStarted.Eagerly, availableSources.first())

    fun currentSource(): AnimeSource = activeSource.value
    suspend fun setActiveSource(id: Long) = prefs.setActiveSourceId(id)
    fun getAllSources(): List<AnimeSource> = availableSources
}
