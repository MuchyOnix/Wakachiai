package com.hikari.anime.core.database.entity

data class HistoryWithAnime(
    val id: Long,
    val animeUrl: String,
    val episodeUrl: String,
    val episodeName: String,
    val watchedAt: Long,
    val progress: Long,
    val totalDuration: Long,
    val animeTitle: String,
    val thumbnailUrl: String?
)
