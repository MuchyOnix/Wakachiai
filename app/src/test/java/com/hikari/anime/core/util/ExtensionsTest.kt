package com.hikari.anime.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsTest {

    @Test
    fun `asAbsoluteUrl with http prefix returns itself`() {
        val url = "http://example.com/image.png"
        val baseUrl = "https://base.com"
        assertEquals(url, url.asAbsoluteUrl(baseUrl))
    }

    @Test
    fun `asAbsoluteUrl with https prefix returns itself`() {
        val url = "https://example.com/image.png"
        val baseUrl = "https://base.com"
        assertEquals(url, url.asAbsoluteUrl(baseUrl))
    }

    @Test
    fun `asAbsoluteUrl with double slash prefix prepends https`() {
        val url = "//example.com/image.png"
        val baseUrl = "https://base.com"
        assertEquals("https://example.com/image.png", url.asAbsoluteUrl(baseUrl))
    }

    @Test
    fun `asAbsoluteUrl with relative path and leading slash resolves against base url host`() {
        val url = "/image.png"
        val baseUrl = "https://base.com/some/path"
        assertEquals("https://base.com/image.png", url.asAbsoluteUrl(baseUrl))
    }

    @Test
    fun `asAbsoluteUrl with relative path without leading slash resolves against base url path`() {
        val url = "image.png"
        val baseUrl = "https://base.com/some/path/"
        assertEquals("https://base.com/some/path/image.png", url.asAbsoluteUrl(baseUrl))
    }

    @Test
    fun `asAbsoluteUrl with unresolvable url returns original string`() {
        val url = "custom://path"
        val baseUrl = "https://base.com"
        assertEquals("custom://path", url.asAbsoluteUrl(baseUrl))
    }
}
