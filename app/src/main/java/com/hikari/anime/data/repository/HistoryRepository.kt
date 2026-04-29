package com.hikari.anime.data.repository

import com.hikari.anime.core.database.dao.HistoryDao
import com.hikari.anime.core.database.dao.EpisodeDao
import com.hikari.anime.core.database.entity.HistoryEntity
import com.hikari.anime.data.mapper.toDomain
import com.hikari.anime.domain.model.HistoryEntry
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val episodeDao: EpisodeDao,
    private val historyDao: HistoryDao
) {
    fun observeRecentHistory(): Flow<List<HistoryEntry>> =
        historyDao.observeRecentHistory().map { rows -> rows.map { it.toDomain() } }

    fun observePagedHistory(): Flow<PagingData<HistoryEntry>> =
        Pager(PagingConfig(pageSize = 30, prefetchDistance = 10)) {
            historyDao.pagingRecentHistory()
        }.flow.map { pagingData -> pagingData.map { it.toDomain() } }

    fun observeContinueWatching(): Flow<List<HistoryEntry>> =
        historyDao.observeContinueWatching().map { rows -> rows.map { it.toDomain() } }

    fun observeEpisodeProgress(animeUrl: String, episodeUrl: String): Flow<Long> =
        historyDao.observeLatestEpisodeProgress(animeUrl, episodeUrl).map { it?.progress ?: 0L }

    suspend fun track(animeUrl: String, episodeUrl: String, episodeName: String, progress: Long, duration: Long) {
        episodeDao.updateProgress(
            animeUrl = animeUrl,
            episodeUrl = episodeUrl,
            progress = progress,
            duration = duration,
            seen = duration > 0L && progress >= duration * 0.9f
        )
        historyDao.upsertHistory(
            HistoryEntity(
                animeUrl = animeUrl,
                episodeUrl = episodeUrl,
                episodeName = episodeName,
                watchedAt = System.currentTimeMillis(),
                progress = progress,
                totalDuration = duration
            )
        )
    }
}
