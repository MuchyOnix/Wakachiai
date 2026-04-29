package com.hikari.anime.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hikari.anime.ui.screens.detail.AnimeDetailScreen
import com.hikari.anime.ui.screens.explore.ExploreScreen
import com.hikari.anime.ui.screens.extensions.ExtensionsScreen
import com.hikari.anime.ui.screens.history.HistoryScreen
import com.hikari.anime.ui.screens.library.LibraryScreen
import com.hikari.anime.ui.screens.player.PlayerScreen
import com.hikari.anime.ui.theme.HikariPurple

@Composable
fun HikariNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBar = Screen.tabs.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBar) {
                HikariBottomBar(navController, currentRoute)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(if (showBar) padding else androidx.compose.foundation.layout.PaddingValues())) {
            NavHost(navController, startDestination = Screen.Explore.route) {
                composable(Screen.Explore.route) { ExploreScreen(navController) }
                composable(Screen.Library.route) { LibraryScreen(navController) }
                composable(Screen.History.route) { HistoryScreen(navController) }
                composable(Screen.Extensions.route) { ExtensionsScreen() }
                composable(Screen.AnimeDetail.route) { AnimeDetailScreen(navController) }
                composable(Screen.Player.route) { PlayerScreen(navController) }
            }
        }
    }
}

@Composable
private fun HikariBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        tonalElevation = 12.dp
    ) {
        Screen.tabs.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = HikariPurple,
                    selectedTextColor = HikariPurple,
                    indicatorColor = HikariPurple.copy(alpha = 0.14f)
                )
            )
        }
    }
}
