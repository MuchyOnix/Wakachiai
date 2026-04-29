package com.hikari.anime.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Scale
import com.hikari.anime.domain.model.Anime
import com.hikari.anime.ui.theme.HikariCyan
import com.hikari.anime.ui.theme.HikariPink
import com.hikari.anime.ui.theme.HikariPurple

@Composable
fun HikariTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            subtitle?.let {
                Spacer(Modifier.height(3.dp))
                Text(
                    it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

@Composable
fun InfoBanner(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.CloudQueue,
    tint: Color = HikariPurple,
    action: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(tint.copy(alpha = 0.18f), HikariCyan.copy(alpha = 0.10f))))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(color = tint.copy(alpha = 0.18f), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(46.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = tint)
                }
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) {
                Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(body, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            action?.let {
                Spacer(Modifier.width(10.dp))
                it()
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Star,
    tint: Color = HikariPurple,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            Modifier.padding(horizontal = 22.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(color = tint.copy(alpha = 0.14f), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(56.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(5.dp))
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (actionLabel != null && onAction != null) {
                Spacer(Modifier.height(14.dp))
                Button(onClick = onAction) { Text(actionLabel) }
            }
        }
    }
}

@Composable
fun MetadataPill(
    text: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier, color = tint.copy(alpha = 0.13f), shape = RoundedCornerShape(50)) {
        Text(
            text = text,
            color = tint,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    action: String = "See all",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(action, style = MaterialTheme.typography.labelMedium, color = HikariPurple)
    }
}

@Composable
fun AnimePosterCard(
    anime: Anime,
    modifier: Modifier = Modifier,
    progress: Float? = null,
    compact: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 126.dp else 168.dp)
            ) {
                PosterImage(anime.thumbnailUrl, anime.title, Modifier.fillMaxSize())
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.62f))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFC947), modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(3.dp))
                    Text("${anime.rating}", color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }
            Column(Modifier.padding(8.dp)) {
                Text(
                    anime.title,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${anime.episodeCount} Episodes",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                progress?.let {
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { it },
                        modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(8.dp)),
                        color = HikariCyan,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SpotlightCard(
    anime: Anime,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Box(Modifier.fillMaxWidth().height(230.dp)) {
            PosterImage(anime.thumbnailUrl, anime.title, Modifier.fillMaxSize())
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))
                        )
                    )
            )
            Surface(
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 14.dp, bottom = 64.dp),
                color = HikariPink,
                shape = RoundedCornerShape(5.dp)
            ) {
                Text("Spotlight", color = Color.White, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
            }
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(14.dp)
            ) {
                Text(anime.title, color = Color.White, style = MaterialTheme.typography.titleLarge)
                Text(anime.genre.orEmpty(), color = Color.White.copy(alpha = 0.86f), style = MaterialTheme.typography.labelMedium)
            }
            RatingPill(
                rating = anime.rating,
                modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp)
            )
        }
    }
}

@Composable
fun HorizontalAnimeItem(
    anime: Anime,
    subtitle: String,
    modifier: Modifier = Modifier,
    progress: Float? = null,
    trailingMenu: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(width = 82.dp, height = 76.dp).clip(RoundedCornerShape(8.dp))) {
                PosterImage(anime.thumbnailUrl, anime.title, Modifier.fillMaxSize())
                Surface(
                    color = Color.Black.copy(alpha = 0.42f),
                    shape = CircleShape,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp).size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(anime.title, style = MaterialTheme.typography.labelMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                progress?.let {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { it },
                            modifier = Modifier.weight(1f).height(3.dp).clip(RoundedCornerShape(8.dp)),
                            color = HikariCyan,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("${(it * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (trailingMenu) {
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.MoreVert, contentDescription = "More options")
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = tint.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(46.dp)) {
                Box(contentAlignment = Alignment.Center) { icon() }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun PosterImage(
    url: String?,
    title: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(modifier.background(Brush.linearGradient(listOf(HikariPurple, HikariCyan)))) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .crossfade(200)
                .diskCacheKey(url ?: title)
                .memoryCacheKey(url ?: title)
                .networkCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .size(240, 340)
                .scale(Scale.FILL)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun RatingPill(rating: Float, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = Color.Black.copy(alpha = 0.45f), shape = RoundedCornerShape(50)) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFC947), modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("$rating", color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}
