package com.hikari.anime.core.util

import android.net.Uri
import okhttp3.HttpUrl.Companion.toHttpUrl

fun String.asAbsoluteUrl(baseUrl: String): String {
    if (startsWith("http://") || startsWith("https://")) return this
    if (startsWith("//")) return "https:$this"
    return baseUrl.toHttpUrl().resolve(this)?.toString() ?: this
}

fun String.encodeRouteArg(): String = Uri.encode(this)

fun Long.progressFraction(total: Long): Float =
    if (total <= 0L) 0f else (toFloat() / total.toFloat()).coerceIn(0f, 1f)
