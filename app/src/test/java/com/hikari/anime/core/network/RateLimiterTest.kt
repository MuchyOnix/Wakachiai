package com.hikari.anime.core.network

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureTimeMillis

class RateLimiterTest {

    @Test
    fun testSequentialRequests() = runBlocking {
        val limiter = RateLimiter()
        val host = "test.com"

        val time = measureTimeMillis {
            limiter.await(host, 100L)
            limiter.await(host, 100L)
        }

        assertTrue("Sequential requests should take at least 100ms, took $time", time >= 100L)
    }

    @Test
    fun testConcurrentRequests() = runBlocking {
        val limiter = RateLimiter()
        val host = "concurrent.com"

        val time = measureTimeMillis {
            val jobs = (1..3).map {
                async {
                    limiter.await(host, 100L)
                }
            }
            jobs.awaitAll()
        }

        // With 3 concurrent requests, the first one goes immediately.
        // The second one waits 100ms.
        // The third one waits 200ms.
        // So total time should be at least 200ms.
        assertTrue("3 concurrent requests with 100ms gap should take at least 200ms, took $time", time >= 200L)
    }

    @Test
    fun testDifferentHosts() = runBlocking {
        val limiter = RateLimiter()

        val time = measureTimeMillis {
            val jobs = (1..3).map {
                async {
                    // Using different hosts, so they shouldn't block each other
                    limiter.await("host$it.com", 200L)
                }
            }
            jobs.awaitAll()
        }

        // Since they are to different hosts, they should all run concurrently
        // and finish in roughly 0ms (because the first request to a new host has no delay).
        // It definitely shouldn't take 400ms (which would happen if they were serialized on the same host).
        assertTrue("Requests to different hosts shouldn't block each other, took $time", time < 100L)
    }
}
