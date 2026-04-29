package com.hikari.anime.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hikari.anime.core.database.dao.AnimeDao
import com.hikari.anime.core.database.dao.EpisodeDao
import com.hikari.anime.core.database.dao.HistoryDao
import com.hikari.anime.core.database.dao.LibraryDao
import com.hikari.anime.core.database.entity.AnimeEntity
import com.hikari.anime.core.database.entity.EpisodeEntity
import com.hikari.anime.core.database.entity.HistoryEntity
import com.hikari.anime.core.database.entity.LibraryEntryEntity

@Database(
    entities = [
        AnimeEntity::class,
        EpisodeEntity::class,
        HistoryEntity::class,
        LibraryEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HikariDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun historyDao(): HistoryDao
    abstract fun libraryDao(): LibraryDao
}
