package com.hikari.anime.ui.screens.player

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import dagger.hilt.android.EntryPointAccessors

@Composable
fun PlayerScreen(
    navController: NavHostController,
    vm: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val okHttpClient = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            PlayerEntryPoint::class.java
        ).okHttpClient()
    }
    val player = remember {
        @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
        val httpDataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
            .setDefaultRequestProperties(mapOf("Referer" to "https://anigo.to/"))
        @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
        val exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(httpDataSourceFactory))
            .build()
        exoPlayer
    }
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.selectedVideo) {
        val video = state.selectedVideo ?: return@LaunchedEffect
        player.setMediaItem(
            MediaItem.Builder()
                .setUri(video.url)
                .setMimeType(if (video.url.contains(".m3u8")) MimeTypes.APPLICATION_M3U8 else MimeTypes.VIDEO_MP4)
                .build()
        )
        player.prepare()
        if (state.savedProgress > 0L) player.seekTo(state.savedProgress)
        player.playWhenReady = true
    }

    DisposableEffect(player) {
        onDispose {
            vm.saveProgress(player.currentPosition, player.duration)
            player.release()
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(it).apply {
                    this.player = player
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            }
        )
        Row(
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = Color.Black.copy(alpha = 0.45f), shape = androidx.compose.foundation.shape.RoundedCornerShape(50)) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
            Text(state.episodeName, color = Color.White, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp))
        }
        Surface(
            color = Color.Black.copy(alpha = 0.45f),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
        ) {
            IconButton(onClick = vm::toggleQualityPicker) {
                Icon(Icons.Rounded.HighQuality, contentDescription = "Quality", tint = Color.White)
            }
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = Color.Black.copy(alpha = 0.45f), shape = RoundedCornerShape(50)) {
                IconButton(onClick = vm::playPreviousEpisode) {
                    Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous episode", tint = Color.White)
                }
            }
            Surface(
                color = Color.Black.copy(alpha = 0.45f),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(start = 12.dp)
            ) {
                IconButton(onClick = vm::playNextEpisode) {
                    Icon(Icons.Rounded.SkipNext, contentDescription = "Next episode", tint = Color.White)
                }
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }
        state.errorMessage?.let { message ->
            Surface(
                color = Color.Black.copy(alpha = 0.74f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(color = Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(54.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("No playable stream", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(message, color = Color.White.copy(alpha = 0.78f), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(14.dp))
                    Button(onClick = { vm.loadEpisode(state.episodeUrl) }) {
                        Text("Retry")
                    }
                }
            }
        }
        if (state.showQualityPicker) {
            Surface(
                color = Color.Black.copy(alpha = 0.72f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 70.dp, end = 12.dp).width(180.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    if (state.availableVideos.isEmpty()) {
                        Text(
                            text = "No qualities found",
                            color = Color.White.copy(alpha = 0.78f),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    } else {
                        state.availableVideos.forEach { video ->
                            Text(
                                text = video.quality,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { vm.selectVideo(video) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
