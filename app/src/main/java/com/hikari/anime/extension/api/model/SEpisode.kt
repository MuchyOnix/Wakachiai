package com.hikari.anime.extension.api.model

data class SEpisode(
    var url: String = "",
    var name: String = "",
    var episodeNumber: Float = -1f,
    var dateUpload: Long = 0L,
    var scanlator: String? = null,
    var isDub: Boolean = false
)
