package com.hikari.anime.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hikari.anime.core.database.entity.LibraryAnime
import com.hikari.anime.core.database.entity.LibraryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT COUNT(*) FROM library WHERE collectionId = 0")
    fun observeAllAnimeCount(): Flow<Int>

    @Query("SELECT * FROM library WHERE isOnWatchlist = 1")
    fun observeWatchlist(): Flow<List<LibraryEntryEntity>>

    @Query("SELECT * FROM library ORDER BY addedAt DESC")
    fun observeAllEntries(): Flow<List<LibraryEntryEntity>>

    @Query("SELECT * FROM library WHERE animeUrl = :animeUrl LIMIT 1")
    fun observeEntry(animeUrl: String): Flow<LibraryEntryEntity?>

    @Query(
        """
        SELECT l.animeUrl, l.collectionId, l.isFavorite, l.isOnWatchlist, l.addedAt,
               a.sourceId, a.title, a.thumbnailUrl, a.description, a.genre, a.status, a.initialized
        FROM library l
        JOIN anime a ON l.animeUrl = a.url
        ORDER BY l.addedAt DESC
        """
    )
    fun observeLibraryAnime(): Flow<List<LibraryAnime>>

    @Query("DELETE FROM library WHERE animeUrl = :animeUrl")
    suspend fun deleteByAnimeUrl(animeUrl: String)

    @Upsert
    suspend fun upsert(entry: LibraryEntryEntity)
}
