package com.hikari.anime.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hikari.anime.core.database.entity.EpisodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episode WHERE animeUrl = :animeUrl ORDER BY episodeNumber")
    fun observeByAnime(animeUrl: String): Flow<List<EpisodeEntity>>

    @Query(
        """
        UPDATE episode
        SET seen = :seen, progress = :progress, totalDuration = :duration
        WHERE animeUrl = :animeUrl AND url = :episodeUrl
        """
    )
    suspend fun updateProgress(
        animeUrl: String,
        episodeUrl: String,
        progress: Long,
        duration: Long,
        seen: Boolean
    )

    @Upsert
    suspend fun upsertAll(episodes: List<EpisodeEntity>)
}
