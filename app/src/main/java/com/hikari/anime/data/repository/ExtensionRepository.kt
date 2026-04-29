package com.hikari.anime.data.repository

import com.hikari.anime.extension.ExtensionManager
import com.hikari.anime.extension.api.AnimeSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionRepository @Inject constructor(
    private val extensionManager: ExtensionManager
) {
    val activeSourceId: Flow<Long> = extensionManager.activeSourceId
    fun getAllSources(): List<AnimeSource> = extensionManager.getAllSources()
    suspend fun setActiveSource(id: Long) = extensionManager.setActiveSource(id)
}
