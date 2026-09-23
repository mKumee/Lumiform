package com.app.lumiform.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.app.core.model.ThemeMode
import com.app.imagedetail.navigation.ImageViewerDestinations
import com.app.imagedetail.navigation.imageViewerGraph
import com.app.maincontent.navigation.ContentDestinations
import com.app.maincontent.navigation.contentGraph


@Composable
fun AppNavHost(
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ContentDestinations.CONTENT_ROUTE, modifier = modifier) {
        contentGraph(
            onImageClick = { src, title -> navController.navigate(ImageViewerDestinations.detailRoute(src, title)) },
            themeMode = themeMode,
            onToggleTheme = onToggleTheme
        )
        imageViewerGraph(navController)
    }
}
