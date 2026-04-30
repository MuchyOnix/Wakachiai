package com.hikari.anime.core.database.entity

import androidx.room.Entity

@Entity(
    tableName = "episode",
    primaryKeys = ["url", "animeUrl"]
)
data class EpisodeEntity(
    val url: String,
    val animeUrl: String,
    val name: String,
    val episodeNumber: Float,
    val dateUpload: Long,
    val seen: Boolean,
    val progress: Long,
    val totalDuration: Long,
    val isDub: Boolean = false
)
