package com.hikari.anime.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime")
data class AnimeEntity(
    @PrimaryKey val url: String,
    val sourceId: Long,
    val title: String,
    val thumbnailUrl: String?,
    val description: String?,
    val genre: String?,
    val status: Int,
    val initialized: Boolean
)
