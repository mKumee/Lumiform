package com.app.maincontent.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.common.state.RequestStatus
import com.app.core.model.ThemeMode
import com.app.uicomponents.components.ErrorView
import com.app.uicomponents.components.LoadingView
import com.app.uicomponents.components.OfflineBanner
import com.app.uicomponents.components.PageBannerCard
import com.app.uicomponents.theme.LumiformColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContentScreen(
    viewModel: HomeContentViewModel,
    onImageClick: (src: String, title: String) -> Unit,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = LumiformColors.current
   //todo Marina; declare strings res, remove hardcoded strings
    Scaffold(containerColor = colors.background) { innerPadding ->
        when (uiState.status) {
            RequestStatus.LOADING -> LoadingView(modifier = Modifier.padding(innerPadding).background(colors.background))

            RequestStatus.ERROR -> ErrorView(
                message = uiState.errorMessage ?: "Something went wrong",
                onRetry = viewModel::retry,
                modifier = Modifier.padding(innerPadding).background(colors.background)
            )

            else -> LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize().background(colors.background),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (uiState.isShowingCachedData) {
                    item(key = "offline_banner") { OfflineBanner() }
                }

                uiState.visibleRows.forEach { row ->
                    if (row.type == ContentRowType.PAGE) {
                        stickyHeader(key = row.id) {
                            PageBannerCard(
                                title = row.title.orEmpty(),
                                pageNumber = row.pageNumber ?: 1,
                                totalPages = row.totalPages ?: 1,
                                themeMode = themeMode,
                                onToggleTheme = onToggleTheme
                            )
                        }
                    } else {
                        item(key = row.id) {
                            val isExpanded = row.id !in uiState.collapsedSectionIds
                            HomeContentRowItem(
                                row = row,
                                selections = uiState.selections,
                                isSectionExpanded = isExpanded,
                                onSectionToggled = viewModel::onSectionToggled,
                                onResponseToggled = viewModel::onResponseToggled,
                                onImageClick = onImageClick,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}