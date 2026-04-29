package com.hikari.anime.domain.model

data class Episode(
    val url: String,
    val animeUrl: String,
    val name: String,
    val episodeNumber: Float,
    val dateUpload: Long,
    val seen: Boolean,
    val progress: Long,
    val totalDuration: Long
)
