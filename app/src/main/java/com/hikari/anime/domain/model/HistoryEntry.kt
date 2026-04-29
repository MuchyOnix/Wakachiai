package com.hikari.anime.domain.model

data class HistoryEntry(
    val id: Long,
    val animeUrl: String,
    val animeTitle: String,
    val episodeUrl: String,
    val episodeName: String,
    val thumbnailUrl: String?,
    val watchedAt: Long,
    val progress: Long,
    val totalDuration: Long
)
