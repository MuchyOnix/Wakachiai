package com.hikari.anime.domain.model

data class Anime(
    val url: String,
    val sourceId: Long,
    val title: String,
    val thumbnailUrl: String?,
    val description: String?,
    val genre: String?,
    val status: Int,
    val initialized: Boolean,
    val rating: Float = 4.7f,
    val episodeCount: Int = 12
)
