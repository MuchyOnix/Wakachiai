package com.hikari.anime.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsTest {

    @Test
    fun progressFraction_normalCase() {
        val progress = 5L
        val total = 10L
        val result = progress.progressFraction(total)
        assertEquals(0.5f, result, 0.001f)
    }

    @Test
    fun progressFraction_zeroTotal_returnsZero() {
        val progress = 5L
        val total = 0L
        val result = progress.progressFraction(total)
        assertEquals(0f, result, 0.0f)
    }

    @Test
    fun progressFraction_negativeTotal_returnsZero() {
        val progress = 5L
        val total = -10L
        val result = progress.progressFraction(total)
        assertEquals(0f, result, 0.0f)
    }

    @Test
    fun progressFraction_negativeProgress_coercesToZero() {
        val progress = -5L
        val total = 10L
        val result = progress.progressFraction(total)
        assertEquals(0f, result, 0.0f)
    }

    @Test
    fun progressFraction_progressGreaterThanTotal_coercesToOne() {
        val progress = 15L
        val total = 10L
        val result = progress.progressFraction(total)
        assertEquals(1f, result, 0.0f)
    }

    @Test
    fun progressFraction_zeroProgress_returnsZero() {
        val progress = 0L
        val total = 10L
        val result = progress.progressFraction(total)
        assertEquals(0f, result, 0.0f)
    }

    @Test
    fun progressFraction_progressEqualsTotal_returnsOne() {
        val progress = 10L
        val total = 10L
        val result = progress.progressFraction(total)
        assertEquals(1f, result, 0.0f)
    }
}
