package com.hikari.anime.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.hikari.anime.core.database.entity.AnimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Query("SELECT * FROM anime WHERE url = :url")
    suspend fun getByUrl(url: String): AnimeEntity?

    @Query("SELECT * FROM anime ORDER BY title")
    fun observeAll(): Flow<List<AnimeEntity>>

    @Upsert
    suspend fun upsert(anime: AnimeEntity)

    @Upsert
    suspend fun upsertAll(animes: List<AnimeEntity>)
}
