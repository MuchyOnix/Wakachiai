package com.hikari.anime.data.repository

import com.hikari.anime.core.database.dao.AnimeDao
import com.hikari.anime.core.database.dao.EpisodeDao
import com.hikari.anime.core.util.Result
import com.hikari.anime.data.mapper.toDomain
import com.hikari.anime.data.mapper.toEntity
import com.hikari.anime.domain.model.Anime
import com.hikari.anime.domain.model.Episode
import com.hikari.anime.extension.ExtensionManager
import com.hikari.anime.extension.api.model.AnimesPage
import com.hikari.anime.extension.api.model.FilterList
import com.hikari.anime.extension.api.model.SAnime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
    private val extensionManager: ExtensionManager,
    private val animeDao: AnimeDao,
    private val episodeDao: EpisodeDao
) {
    fun getPopularAnime(page: Int): Flow<Result<AnimesPage>> = flow {
        emit(Result.Loading)
        runCatching {
            val remote = extensionManager.currentSource().getPopularAnime(page)
            if (remote.animes.isNotEmpty()) remote else throw IllegalStateException("No anime returned by active source.")
        }.onSuccess { pageResult ->
            animeDao.upsertAll(pageResult.animes.map { it.toEntity() })
            emit(Result.Success(pageResult))
        }.onFailure { error ->
            emit(Result.Error(error))
        }
    }.flowOn(Dispatchers.IO)

    fun searchAnime(page: Int, query: String): Flow<Result<AnimesPage>> = flow {
        emit(Result.Loading)
        runCatching {
            extensionManager.currentSource().searchAnime(page, query, FilterList())
        }.onSuccess { remote ->
            emit(Result.Success(remote))
        }.onFailure { error ->
            emit(Result.Error(error))
        }
    }.flowOn(Dispatchers.IO)

    fun getLatestUpdates(page: Int): Flow<Result<AnimesPage>> = flow {
        emit(Result.Loading)
        runCatching {
            val remote = extensionManager.currentSource().getLatestUpdates(page)
            if (remote.animes.isNotEmpty()) remote else throw IllegalStateException("No latest updates returned by active source.")
        }.onSuccess { pageResult ->
            animeDao.upsertAll(pageResult.animes.map { it.toEntity() })
            emit(Result.Success(pageResult))
        }.onFailure { error ->
            emit(Result.Error(error))
        }
    }.flowOn(Dispatchers.IO)

    fun getAnimeDetails(animeUrl: String): Flow<Result<Anime>> = flow {
        emit(Result.Loading)
        val cached = animeDao.getByUrl(animeUrl)
        if (cached?.initialized == true) emit(Result.Success(cached.toDomain()))
        runCatching {
            val source = extensionManager.currentSource()
            source.getAnimeDetails(SAnime(url = animeUrl)).toDomain(source.id)
        }.onSuccess { fresh ->
            animeDao.upsert(
                SAnime(
                    url = fresh.url,
                    title = fresh.title,
                    thumbnailUrl = fresh.thumbnailUrl,
                    description = fresh.description,
                    genre = fresh.genre,
                    status = fresh.status,
                    initialized = true
                ).toEntity(fresh.sourceId)
            )
            emit(Result.Success(fresh))
        }.onFailure { error ->
            if (cached == null) emit(Result.Error(error))
        }
    }.flowOn(Dispatchers.IO)

    fun observeEpisodes(animeUrl: String): Flow<List<Episode>> =
        episodeDao.observeByAnime(animeUrl).map { it.map { entity -> entity.toDomain() } }

    suspend fun refreshEpisodes(animeUrl: String): Result<List<Episode>> {
        return runCatching {
            extensionManager.currentSource().getEpisodeList(SAnime(url = animeUrl))
        }.fold(
            onSuccess = { sourceEpisodes ->
                val entities = sourceEpisodes.map { it.toEntity(animeUrl) }
                episodeDao.upsertAll(entities)
                Result.Success(entities.map { it.toDomain() })
            },
            onFailure = { error -> Result.Error(error) }
        )
    }
}
