package com.hikari.anime.core.database

import android.content.Context
import androidx.room.Room
import com.hikari.anime.core.database.dao.AnimeDao
import com.hikari.anime.core.database.dao.EpisodeDao
import com.hikari.anime.core.database.dao.HistoryDao
import com.hikari.anime.core.database.dao.LibraryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HikariDatabase =
        Room.databaseBuilder(context, HikariDatabase::class.java, "hikari.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides fun provideAnimeDao(db: HikariDatabase): AnimeDao = db.animeDao()
    @Provides fun provideEpisodeDao(db: HikariDatabase): EpisodeDao = db.episodeDao()
    @Provides fun provideHistoryDao(db: HikariDatabase): HistoryDao = db.historyDao()
    @Provides fun provideLibraryDao(db: HikariDatabase): LibraryDao = db.libraryDao()
}
