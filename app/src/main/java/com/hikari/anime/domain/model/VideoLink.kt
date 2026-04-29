package com.hikari.anime.domain.model

import okhttp3.Headers

data class VideoLink(
    val url: String,
    val quality: String,
    val headers: Headers = Headers.headersOf()
)
