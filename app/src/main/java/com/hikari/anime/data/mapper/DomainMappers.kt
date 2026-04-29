package com.hikari.anime.data.mapper

import com.hikari.anime.core.database.entity.AnimeEntity
import com.hikari.anime.core.database.entity.ContinueWatchingEntry
import com.hikari.anime.core.database.entity.EpisodeEntity
import com.hikari.anime.core.database.entity.HistoryWithAnime
import com.hikari.anime.core.database.entity.LibraryAnime
import com.hikari.anime.domain.model.Anime
import com.hikari.anime.domain.model.Episode
import com.hikari.anime.domain.model.HistoryEntry
import com.hikari.anime.extension.api.model.SAnime
import com.hikari.anime.extension.api.model.SEpisode

fun SAnime.toEntity(sourceId: Long = 4815162342L) = AnimeEntity(
    url = url,
    sourceId = sourceId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    description = description,
    genre = genre,
    status = status,
    initialized = initialized
)

fun AnimeEntity.toDomain() = Anime(
    url = url,
    sourceId = sourceId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    description = description,
    genre = genre,
    status = status,
    initialized = initialized
)

fun SAnime.toDomain(sourceId: Long = 4815162342L) = Anime(
    url = url,
    sourceId = sourceId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    description = description,
    genre = genre,
    status = status,
    initialized = initialized
)

fun SEpisode.toEntity(animeUrl: String) = EpisodeEntity(
    url = url,
    animeUrl = animeUrl,
    name = name,
    episodeNumber = episodeNumber,
    dateUpload = dateUpload,
    seen = false,
    progress = 0L,
    totalDuration = 24L * 60L * 1000L
)

fun EpisodeEntity.toDomain() = Episode(
    url = url,
    animeUrl = animeUrl,
    name = name,
    episodeNumber = episodeNumber,
    dateUpload = dateUpload,
    seen = seen,
    progress = progress,
    totalDuration = totalDuration
)

fun HistoryWithAnime.toDomain() = HistoryEntry(
    id = id,
    animeUrl = animeUrl,
    animeTitle = animeTitle,
    episodeUrl = episodeUrl,
    episodeName = episodeName,
    thumbnailUrl = thumbnailUrl,
    watchedAt = watchedAt,
    progress = progress,
    totalDuration = totalDuration
)

fun LibraryAnime.toAnimeDomain() = Anime(
    url = animeUrl,
    sourceId = sourceId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    description = description,
    genre = genre,
    status = status,
    initialized = initialized
)

fun ContinueWatchingEntry.toDomain() = HistoryEntry(
    id = id,
    animeUrl = animeUrl,
    animeTitle = animeTitle,
    episodeUrl = episodeUrl,
    episodeName = episodeName,
    thumbnailUrl = thumbnailUrl,
    watchedAt = watchedAt,
    progress = progress,
    totalDuration = totalDuration
)
