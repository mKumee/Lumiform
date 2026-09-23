package com.app.imagedetail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.imagedetail.presentation.ImageDetailScreen
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

object ImageViewerDestinations {
    private const val ARG_URL = "url"
    private const val ARG_TITLE = "title"
    const val DETAIL_ROUTE = "image_detail/{$ARG_URL}/{$ARG_TITLE}"

    fun detailRoute(url: String, title: String): String {
        val encodedUrl = URLEncoder.encode(url, UTF_8.name())
        val encodedTitle = URLEncoder.encode(title.ifBlank { "Image" }, UTF_8.name())
        return "image_detail/$encodedUrl/$encodedTitle"
    }
}

fun NavGraphBuilder.imageViewerGraph(navController: NavHostController) {
    composable(
        route = ImageViewerDestinations.DETAIL_ROUTE,
        arguments = listOf(navArgument("url") { type = NavType.StringType }, navArgument("title") { type = NavType.StringType })
    ) { backStackEntry ->
        val encodedUrl = backStackEntry.arguments?.getString("url").orEmpty()
        val encodedTitle = backStackEntry.arguments?.getString("title").orEmpty()
        ImageDetailScreen(
            imageUrl = URLDecoder.decode(encodedUrl, UTF_8.name()),
            title = URLDecoder.decode(encodedTitle, UTF_8.name()),
            onBack = { navController.popBackStack() }
        )
    }
}
