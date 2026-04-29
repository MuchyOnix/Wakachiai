package com.hikari.anime.data.repository

import com.hikari.anime.core.database.dao.AnimeDao
import com.hikari.anime.core.database.dao.LibraryDao
import com.hikari.anime.core.database.entity.LibraryEntryEntity
import com.hikari.anime.data.mapper.toAnimeDomain
import com.hikari.anime.domain.model.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository @Inject constructor(
    private val animeDao: AnimeDao,
    private val libraryDao: LibraryDao
) {
    fun observeAllAnimeCount(): Flow<Int> = libraryDao.observeAllAnimeCount()
    fun observeWatchlist(): Flow<List<LibraryEntryEntity>> = libraryDao.observeWatchlist()
    fun observeAllEntries(): Flow<List<LibraryEntryEntity>> = libraryDao.observeAllEntries()
    fun observeIsInLibrary(animeUrl: String): Flow<Boolean> =
        libraryDao.observeEntry(animeUrl).map { it != null }

    fun observeLibraryAnime(): Flow<List<Anime>> =
        libraryDao.observeLibraryAnime().map { rows -> rows.map { it.toAnimeDomain() } }

    suspend fun addToLibrary(animeUrl: String, favorite: Boolean = false, watchlist: Boolean = false) {
        val existing = animeDao.getByUrl(animeUrl)
        if (existing == null) return
        libraryDao.upsert(
            LibraryEntryEntity(
                animeUrl = animeUrl,
                collectionId = 0L,
                isFavorite = favorite,
                isOnWatchlist = watchlist,
                addedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFromLibrary(animeUrl: String) {
        libraryDao.deleteByAnimeUrl(animeUrl)
    }
}
