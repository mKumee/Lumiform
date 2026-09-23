package com.app.maincontent.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.app.core.model.ThemeMode
import com.app.maincontent.presentation.HomeContentScreen
import com.app.maincontent.presentation.HomeContentViewModel

object ContentDestinations {
    const val CONTENT_ROUTE = "content"
}

fun NavGraphBuilder.contentGraph(
    onImageClick: (src: String, title: String) -> Unit,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit
) {
    composable(ContentDestinations.CONTENT_ROUTE) {
        val viewModel = hiltViewModel<HomeContentViewModel>()
        HomeContentScreen(
            viewModel = viewModel,
            onImageClick = onImageClick,
            themeMode = themeMode,
            onToggleTheme = onToggleTheme
        )
    }
}
