package com.hikari.anime.extension.source.anigoto

import com.hikari.anime.extension.api.model.Video
import com.hikari.anime.extension.api.util.GET
import com.hikari.anime.extension.api.util.asJsoup
import okhttp3.OkHttpClient

class VidStreamingExtractor(private val client: OkHttpClient) {
    suspend fun videosFromUrl(url: String): List<Video> {
        val document = client.newCall(GET(url)).execute().asJsoup()
        val html = document.html()
        val regex = Regex("""\{[^{}]*file\s*:\s*["']([^"']+)["'][^{}]*?(?:label\s*:\s*["']([^"']+)["'])?[^{}]*}""")
        return regex.findAll(html)
            .map { match ->
                Video(
                    url = match.groupValues[1],
                    quality = match.groupValues.getOrNull(2).orEmpty().ifBlank { "Auto" }
                )
            }
            .filter { it.url.startsWith("http") }
            .distinctBy { it.url }
            .toList()
    }
}
