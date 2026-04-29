package com.hikari.anime.core.network

import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RateLimiter @Inject constructor() {
    private val lastRequests = ConcurrentHashMap<String, Long>()

    suspend fun await(host: String, minimumGapMs: Long = 350L) {
        val now = System.currentTimeMillis()
        val last = lastRequests[host] ?: 0L
        val remaining = minimumGapMs - (now - last)
        if (remaining > 0) delay(remaining)
        lastRequests[host] = System.currentTimeMillis()
    }
}
