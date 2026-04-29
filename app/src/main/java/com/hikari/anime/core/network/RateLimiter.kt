package com.hikari.anime.core.network

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RateLimiter @Inject constructor() {
    private val lastRequests = ConcurrentHashMap<String, Long>()
    private val mutexes = ConcurrentHashMap<String, Mutex>()

    suspend fun await(host: String, minimumGapMs: Long = 350L) {
        val mutex = mutexes.getOrPut(host) { Mutex() }
        mutex.withLock {
            val now = System.currentTimeMillis()
            val last = lastRequests[host] ?: 0L
            val remaining = minimumGapMs - (now - last)
            if (remaining > 0) delay(remaining)
            lastRequests[host] = System.currentTimeMillis()
        }
    }
}
