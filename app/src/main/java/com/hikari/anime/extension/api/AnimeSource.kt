package com.hikari.anime.extension.api

import com.hikari.anime.extension.api.model.AnimesPage
import com.hikari.anime.extension.api.model.FilterList
import com.hikari.anime.extension.api.model.SAnime
import com.hikari.anime.extension.api.model.SEpisode
import com.hikari.anime.extension.api.model.Video

interface AnimeSource {
    val id: Long
    val name: String
    val baseUrl: String
    val lang: String
    val supportsLatest: Boolean

    suspend fun getPopularAnime(page: Int): AnimesPage
    suspend fun getLatestUpdates(page: Int): AnimesPage
    suspend fun searchAnime(page: Int, query: String, filters: FilterList): AnimesPage
    suspend fun getAnimeDetails(anime: SAnime): SAnime
    suspend fun getEpisodeList(anime: SAnime): List<SEpisode>
    suspend fun getVideoList(episode: SEpisode): List<Video>
    fun getFilterList(): FilterList = FilterList()
}
