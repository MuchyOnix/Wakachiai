package com.hikari.anime.ui.screens.player

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerEntryPoint {
    fun okHttpClient(): OkHttpClient
}
