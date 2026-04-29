package com.hikari.anime.domain.usecase

import com.hikari.anime.domain.model.VideoLink
import com.hikari.anime.extension.ExtensionManager
import com.hikari.anime.extension.api.model.SEpisode
import javax.inject.Inject

class GetVideoLinksUseCase @Inject constructor(
    private val extensionManager: ExtensionManager
) {
    suspend operator fun invoke(episodeUrl: String): List<VideoLink> {
        val source = extensionManager.currentSource()
        return source.getVideoList(SEpisode(url = episodeUrl))
            .map { video -> VideoLink(video.url, video.quality, video.headers) }
    }
}
