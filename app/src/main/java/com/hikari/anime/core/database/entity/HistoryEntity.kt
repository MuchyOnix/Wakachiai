package com.hikari.anime.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val animeUrl: String,
    val episodeUrl: String,
    val episodeName: String,
    val watchedAt: Long,
    val progress: Long,
    val totalDuration: Long
)
