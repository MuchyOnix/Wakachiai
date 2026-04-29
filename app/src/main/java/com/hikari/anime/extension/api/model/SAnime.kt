package com.hikari.anime.extension.api.model

data class SAnime(
    var url: String = "",
    var title: String = "",
    var thumbnailUrl: String? = null,
    var genre: String? = null,
    var description: String? = null,
    var status: Int = UNKNOWN,
    var initialized: Boolean = false
) {
    companion object {
        const val UNKNOWN = 0
        const val ONGOING = 1
        const val COMPLETED = 2
    }
}
