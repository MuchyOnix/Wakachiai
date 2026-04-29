package com.hikari.anime.extension.source.anigoto

import com.hikari.anime.extension.api.AnimeSource
import com.hikari.anime.extension.api.model.AnimesPage
import com.hikari.anime.extension.api.model.FilterList
import com.hikari.anime.extension.api.model.SAnime
import com.hikari.anime.extension.api.model.SEpisode
import com.hikari.anime.extension.api.model.Video
import com.hikari.anime.core.util.asAbsoluteUrl
import com.hikari.anime.extension.api.util.GET
import com.hikari.anime.extension.api.util.asJsoup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder
import javax.inject.Inject

class AniGoTo @Inject constructor(
    private val client: OkHttpClient
) : AnimeSource {
    override val id: Long = 4815162342L
    override val name: String = "AniGo.to"
    override val baseUrl: String = "https://anigo.to"
    override val lang: String = "en"
    override val supportsLatest: Boolean = true

    override suspend fun getPopularAnime(page: Int): AnimesPage = withContext(Dispatchers.IO) {
        val document = client.newCall(GET("$baseUrl/home")).execute().asJsoup()
        val animes = parseHomeSection(document, "MOST VIEWED")
            .ifEmpty { parseHomeSection(document, "NEW RELEASES") }
            .ifEmpty { parseAnimePage(document).animes }
        AnimesPage(animes, hasNextPage = false)
    }

    override suspend fun getLatestUpdates(page: Int): AnimesPage = withContext(Dispatchers.IO) {
        val document = client.newCall(GET("$baseUrl/home")).execute().asJsoup()
        val animes = parseHomeSection(document, "LATEST UPDATES").ifEmpty { parseAnimePage(document).animes }
        AnimesPage(animes, hasNextPage = false)
    }

    override suspend fun searchAnime(page: Int, query: String, filters: FilterList): AnimesPage =
        withContext(Dispatchers.IO) {
            val encoded = URLEncoder.encode(query, "UTF-8")
            parseAnimePage(client.newCall(GET("$baseUrl/browser?keyword=$encoded&page=$page")).execute().asJsoup())
        }

    override suspend fun getAnimeDetails(anime: SAnime): SAnime = withContext(Dispatchers.IO) {
        val document = client.newCall(GET(anime.url.asAbsoluteUrl(baseUrl))).execute().asJsoup()
        val type = document.selectFirst(".aniMeta b, .type, .aniMeta span:not(.rating)")?.text()?.normalized()
        val rating = document.selectFirst(".aniMeta .rating, .rating")?.text()?.normalized()
        val episodeCount = detailValue(document, "Episodes")
            ?: document.selectFirst(".aniMeta .total, .total")?.text()?.normalized()
        val premiered = detailValue(document, "Premiered")
        val statusText = detailValue(document, "Status").orEmpty()
        val genres = document.select(AniGoToConstants.DETAILS_GENRE)
            .map { it.text().normalized() }
            .filter { it.isNotBlank() }
            .distinct()
        anime.apply {
            title = title.ifBlank { document.selectFirst(AniGoToConstants.DETAILS_TITLE)?.text().orEmpty() }
            thumbnailUrl = thumbnailUrl ?: document.selectFirst(AniGoToConstants.DETAILS_POSTER)?.imageUrl()
            description = document.selectFirst(AniGoToConstants.DETAILS_SYNOPSIS)?.text()
                ?: document.selectFirst(".description, .overview")?.text()
                ?: extractDetailDescription(document)
            genre = (genres + listOfNotNull(type, rating, premiered, episodeCount?.let { "$it episodes" }))
                .filter { it.isNotBlank() }
                .distinct()
                .joinToString(", ")
            status = when {
                statusText.contains("releasing", ignoreCase = true) -> SAnime.ONGOING
                statusText.contains("completed", ignoreCase = true) -> SAnime.COMPLETED
                else -> status
            }
            initialized = true
        }
    }

    override suspend fun getEpisodeList(anime: SAnime): List<SEpisode> = withContext(Dispatchers.IO) {
        val document = client.newCall(GET(anime.url.asAbsoluteUrl(baseUrl))).execute().asJsoup()
        val animeId = document.selectFirst(AniGoToConstants.MOVIE_ID)?.attr("value").orEmpty()
        val lastEp = document.selectFirst(AniGoToConstants.EPISODE_LAST)?.attr("ep_end").orEmpty().ifBlank { "1" }
        if (animeId.isBlank()) return@withContext parseInlineEpisodes(document, anime.url.asAbsoluteUrl(baseUrl))

        val ajaxUrl = "https://ajax.gogocdn.net/ajax/load-list-episode?ep_start=0&ep_end=$lastEp&id=$animeId"
        val epDoc = client.newCall(GET(ajaxUrl)).execute().asJsoup()
        epDoc.select(AniGoToConstants.EPISODE_LINK).map { element ->
            val name = element.selectFirst(AniGoToConstants.EPISODE_NAME)?.text() ?: "Episode"
            SEpisode(
                url = element.attr("href").trim(),
                name = name,
                episodeNumber = name.substringAfter("EP ", "").toFloatOrNull() ?: -1f
            )
        }.reversed()
    }

    override suspend fun getVideoList(episode: SEpisode): List<Video> = withContext(Dispatchers.IO) {
        val document = client.newCall(GET(episode.url.asAbsoluteUrl(baseUrl))).execute().asJsoup()
        val iframeUrl = document.selectFirst(AniGoToConstants.PLAYER_IFRAME)
            ?.attr("src")
            ?.let { if (it.startsWith("//")) "https:$it" else it }
            ?: return@withContext emptyList()

        when {
            "gogocdn" in iframeUrl || "gogoanime" in iframeUrl -> GogoCdnExtractor(client).videosFromUrl(iframeUrl)
            "vidstreaming" in iframeUrl -> VidStreamingExtractor(client).videosFromUrl(iframeUrl)
            else -> emptyList()
        }
    }

    override fun getFilterList(): FilterList = aniGoToFilters()

    private fun parseHomeSection(document: Document, heading: String): List<SAnime> {
        val section = document.select("section").firstOrNull { section ->
            section.selectFirst(".sectionTitle")?.text()?.normalized()?.contains(heading, ignoreCase = true) == true
        } ?: return emptyList()
        return section.select(AniGoToConstants.POSTER)
            .mapNotNull { parseAnimeElement(it) }
            .distinctBy { it.url }
    }

    private fun parseAnimePage(document: Document): AnimesPage {
        val animes = document.select(AniGoToConstants.POSTER)
            .mapNotNull { parseAnimeElement(it) }
            .distinctBy { it.url }
        val hasNext = document.selectFirst(AniGoToConstants.NEXT_PAGE) != null
        return AnimesPage(animes, hasNext)
    }

    private fun parseAnimeElement(element: Element): SAnime? {
        val link = when {
            element.tagName().equals("a", ignoreCase = true) && element.hasAttr("href") -> element
            else -> element.selectFirst("a[href*=/watch/], ${AniGoToConstants.POSTER_LINK}")
        } ?: return null
        val title = element.selectFirst(AniGoToConstants.POSTER_TITLE)?.text()?.ifBlank { null }
            ?: element.selectFirst(AniGoToConstants.POSTER_IMAGE)?.attr("alt")?.ifBlank { null }
            ?: return null
        val image = element.selectFirst(AniGoToConstants.POSTER_IMAGE)?.imageUrl()
            ?: extractBackgroundImage(element)
        val type = element.selectFirst(".type")?.text()?.uppercase()
        val rating = element.selectFirst(".rating")?.text()
        val total = element.selectFirst(".total")?.text()
        return SAnime(
            url = link.attr("href").asAbsoluteUrl(baseUrl),
            title = title,
            thumbnailUrl = image,
            genre = listOfNotNull(type, rating, total?.let { "$it episodes" }).joinToString(", "),
            status = SAnime.UNKNOWN,
            initialized = false
        )
    }

    private fun parseInlineEpisodes(document: Document, animeUrl: String): List<SEpisode> {
        val explicitEpisodes = document.select("a[href*=/watch/][href*=episode], a[href*=ep-], .episodeList a[href]")
            .filterNot { it.hasAttr(":href") }
            .mapIndexed { index, element ->
            SEpisode(
                url = element.attr("href").asAbsoluteUrl(baseUrl),
                name = element.text().ifBlank { "Episode ${index + 1}" },
                episodeNumber = (index + 1).toFloat()
            )
        }.distinctBy { it.url }
        if (explicitEpisodes.isNotEmpty()) return explicitEpisodes

        val count = detailValue(document, "Episodes")?.toIntOrNull() ?: return emptyList()
        return (1..count).map { number ->
            SEpisode(
                url = "${animeUrl.substringBefore("#")}#ep=$number",
                name = "Episode $number",
                episodeNumber = number.toFloat()
            )
        }
    }

    private fun Element.imageUrl(): String? =
        attr("data-src").ifBlank { attr("src") }.ifBlank { null }?.asAbsoluteUrl(baseUrl)

    private fun extractBackgroundImage(element: Element): String? {
        val style = element.select("[style*=background-image]").firstOrNull()?.attr("style")
            ?: element.attr("style")
        return Regex("""url\(['"]?([^'")]+)['"]?\)""").find(style)?.groupValues?.getOrNull(1)?.asAbsoluteUrl(baseUrl)
    }

    private fun extractDetailDescription(document: Document): String? {
        val text = document.text()
        val start = text.indexOf("PG 13")
        val country = text.indexOf("Country:")
        return if (start >= 0 && country > start) {
            text.substring(start, country).substringAfter("min", "").trim().ifBlank { null }
        } else {
            null
        }
    }

    private fun detailValue(document: Document, label: String): String? =
        document.select(".detail > div, ${AniGoToConstants.DETAILS_META} > div").firstNotNullOfOrNull { row ->
            val rowText = row.ownText().normalized()
            if (!rowText.startsWith("$label:", ignoreCase = true)) return@firstNotNullOfOrNull null
            row.selectFirst("span")?.text()?.normalized()
                ?: row.text().substringAfter("$label:", "").normalized().ifBlank { null }
        }

    private fun String.normalized(): String = replace(Regex("\\s+"), " ").trim()
}
