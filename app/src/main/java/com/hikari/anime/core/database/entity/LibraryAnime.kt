package com.hikari.anime.core.database.entity

data class LibraryAnime(
    val animeUrl: String,
    val collectionId: Long,
    val isFavorite: Boolean,
    val isOnWatchlist: Boolean,
    val addedAt: Long,
    val sourceId: Long,
    val title: String,
    val thumbnailUrl: String?,
    val description: String?,
    val genre: String?,
    val status: Int,
    val initialized: Boolean
)
