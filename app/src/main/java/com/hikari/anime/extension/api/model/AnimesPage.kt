package com.hikari.anime.extension.api.model

data class AnimesPage(
    val animes: List<SAnime>,
    val hasNextPage: Boolean
)
