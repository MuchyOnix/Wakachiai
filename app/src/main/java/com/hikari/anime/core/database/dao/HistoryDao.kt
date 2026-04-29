package com.hikari.anime.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import androidx.paging.PagingSource
import com.hikari.anime.core.database.entity.ContinueWatchingEntry
import com.hikari.anime.core.database.entity.HistoryEntity
import com.hikari.anime.core.database.entity.HistoryWithAnime
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query(
        """
        SELECT h.id, h.animeUrl, h.episodeUrl, h.episodeName, h.watchedAt, h.progress,
               h.totalDuration, COALESCE(a.title, h.animeUrl) AS animeTitle, a.thumbnailUrl
        FROM history h
        LEFT JOIN anime a ON h.animeUrl = a.url
        ORDER BY h.watchedAt DESC
        LIMIT :limit OFFSET :offset
        """
    )
    fun observeRecentHistory(limit: Int = 50, offset: Int = 0): Flow<List<HistoryWithAnime>>

    @Query(
        """
        SELECT h.id, h.animeUrl, h.episodeUrl, h.episodeName, h.watchedAt, h.progress,
               h.totalDuration, COALESCE(a.title, h.animeUrl) AS animeTitle, a.thumbnailUrl
        FROM history h
        LEFT JOIN anime a ON h.animeUrl = a.url
        ORDER BY h.watchedAt DESC
        """
    )
    fun pagingRecentHistory(): PagingSource<Int, HistoryWithAnime>

    @Query(
        """
        SELECT h.id, h.animeUrl, h.episodeUrl, h.episodeName, h.watchedAt, h.progress,
               h.totalDuration, COALESCE(a.title, h.animeUrl) AS animeTitle, a.thumbnailUrl
        FROM history h
        LEFT JOIN anime a ON h.animeUrl = a.url
        INNER JOIN (
            SELECT animeUrl, MAX(watchedAt) AS latestWatchedAt
            FROM history
            WHERE totalDuration > 0 AND progress < totalDuration
            GROUP BY animeUrl
        ) latest ON latest.animeUrl = h.animeUrl AND latest.latestWatchedAt = h.watchedAt
        ORDER BY h.watchedAt DESC
        LIMIT :limit
        """
    )
    fun observeContinueWatching(limit: Int = 10): Flow<List<ContinueWatchingEntry>>

    @Query(
        """
        SELECT * FROM history
        WHERE animeUrl = :animeUrl AND episodeUrl = :episodeUrl
        ORDER BY watchedAt DESC
        LIMIT 1
        """
    )
    fun observeLatestEpisodeProgress(animeUrl: String, episodeUrl: String): Flow<HistoryEntity?>

    @Upsert
    suspend fun upsertHistory(entry: HistoryEntity)
}
