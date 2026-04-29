package com.hikari.anime.core.network

import android.content.Context
import android.webkit.CookieManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cache = Cache(File(context.cacheDir, "http_cache"), 50L * 1024L * 1024L)
        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                val cookies = CookieManager.getInstance().getCookie(request.url.toString())
                val builder = request.newBuilder()
                    .header("User-Agent", "Mozilla/5.0 HikariAnime/1.0")
                if (!cookies.isNullOrBlank()) builder.header("Cookie", cookies)
                chain.proceed(builder.build())
            }
            .addNetworkInterceptor { chain ->
                chain.proceed(chain.request()).newBuilder()
                    .header("Cache-Control", "public, max-age=300")
                    .build()
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .followRedirects(true)
            .build()
    }
}
