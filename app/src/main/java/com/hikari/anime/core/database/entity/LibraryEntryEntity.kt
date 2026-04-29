package com.hikari.anime.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "library",
    foreignKeys = [
        ForeignKey(
            entity = AnimeEntity::class,
            parentColumns = ["url"],
            childColumns = ["animeUrl"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LibraryEntryEntity(
    @PrimaryKey val animeUrl: String,
    val collectionId: Long,
    val isFavorite: Boolean,
    val isOnWatchlist: Boolean,
    val addedAt: Long
)
