package com.hikari.anime.extension.source.anigoto

import com.hikari.anime.extension.api.model.Video
import com.hikari.anime.extension.api.util.GET
import com.hikari.anime.extension.api.util.asJsoup
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.net.URLEncoder
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class GogoCdnExtractor(
    private val client: OkHttpClient,
    private val keyConfig: GogoKeyConfig? = null,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    suspend fun videosFromUrl(url: String): List<Video> {
        val document = client.newCall(GET(url)).execute().asJsoup()
        val directSources = parseDirectSources(document.html())
        if (directSources.isNotEmpty()) return directSources

        val keys = keyConfig?.takeIf { it.isValid } ?: return emptyList()
        val dataValue = document.selectFirst("div#player")?.attr("data-value")
            ?.takeIf { it.isNotBlank() }
            ?: return emptyList()

        return runCatching {
            val decryptedParams = cryptoHandler(dataValue, keys.secretKey, keys.iv, decrypt = true)
            val id = decryptedParams.substringAfter("id=").substringBefore("&")
            if (id.isBlank()) return@runCatching emptyList()

            val encryptedId = cryptoHandler(id, keys.secretKey, keys.iv, decrypt = false)
            val ajaxParams = decryptedParams.replace(id, URLEncoder.encode(encryptedId, "UTF-8"))
            val ajaxUrl = url.toHttpUrl().newBuilder()
                .encodedPath("/encrypt-ajax.php")
                .encodedQuery(ajaxParams)
                .build()
            val encryptedResponse = client.newCall(GET(ajaxUrl.toString())).execute().use { response ->
                json.decodeFromString<EncryptedResponse>(response.body?.string().orEmpty())
            }
            val sourcesJson = cryptoHandler(encryptedResponse.data, keys.secondKey, keys.iv, decrypt = true)
            val sources = json.decodeFromString<AjaxResponse>(sourcesJson)
            (sources.source + sources.backupSource).distinctBy { it.file }.map {
                Video(url = it.file, quality = it.label ?: "Auto")
            }
        }.getOrElse { emptyList() }
    }

    private fun parseDirectSources(html: String): List<Video> {
        val regex = Regex("""\{[^{}]*file\s*:\s*["']([^"']+)["'][^{}]*?(?:label\s*:\s*["']([^"']+)["'])?[^{}]*}""")
        return regex.findAll(html)
            .map { match ->
                val file = match.groupValues[1]
                val label = match.groupValues.getOrNull(2).orEmpty().ifBlank { "Auto" }
                Video(url = file, quality = label)
            }
            .filter { it.url.startsWith("http") }
            .distinctBy { it.url }
            .toList()
    }

    private fun cryptoHandler(input: String, key: ByteArray, iv: ByteArray, decrypt: Boolean): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            if (decrypt) Cipher.DECRYPT_MODE else Cipher.ENCRYPT_MODE,
            SecretKeySpec(key, "AES"),
            IvParameterSpec(iv)
        )
        return if (decrypt) {
            String(cipher.doFinal(Base64.getDecoder().decode(input)), Charsets.UTF_8)
        } else {
            Base64.getEncoder().encodeToString(cipher.doFinal(input.toByteArray(Charsets.UTF_8)))
        }
    }
}
