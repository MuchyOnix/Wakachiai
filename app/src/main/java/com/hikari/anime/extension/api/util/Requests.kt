package com.hikari.anime.extension.api.util

import okhttp3.FormBody
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun GET(url: String): Request = Request.Builder()
    .url(url)
    .header("User-Agent", DESKTOP_USER_AGENT)
    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .get()
    .build()

fun POST(url: String, body: FormBody): Request = Request.Builder()
    .url(url)
    .header("User-Agent", DESKTOP_USER_AGENT)
    .post(body)
    .build()

fun Response.asJsoup(): Document = use { response ->
    val body = response.body?.string().orEmpty()
    Jsoup.parse(body, request.url.toString())
}

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
