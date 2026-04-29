package com.hikari.anime.extension.api.model

import okhttp3.Headers

data class Video(
    val url: String,
    val quality: String,
    val headers: Headers = Headers.headersOf(),
    val subtitleTracks: List<Track> = emptyList(),
    val audioTracks: List<Track> = emptyList()
)
