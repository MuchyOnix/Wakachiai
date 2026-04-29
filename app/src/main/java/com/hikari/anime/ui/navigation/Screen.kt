package com.hikari.anime.ui.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    data object Explore : Screen("explore", Icons.Rounded.Explore, "Explore")
    data object Library : Screen("library", Icons.Rounded.BookmarkBorder, "Library")
    data object History : Screen("history", Icons.Rounded.History, "History")
    data object Extensions : Screen("extensions", Icons.Rounded.Extension, "Extensions")
    data object AnimeDetail : Screen("detail/{animeUrl}", Icons.Rounded.Info, "")
    data object Player : Screen("player/{episodeUrl}/{animeUrl}", Icons.Rounded.PlayArrow, "")

    companion object {
        val tabs = listOf(Explore, Library, History, Extensions)
        fun detailRoute(animeUrl: String) = "detail/${Uri.encode(animeUrl)}"
        fun playerRoute(episodeUrl: String, animeUrl: String) =
            "player/${Uri.encode(episodeUrl)}/${Uri.encode(animeUrl)}"
    }
}
