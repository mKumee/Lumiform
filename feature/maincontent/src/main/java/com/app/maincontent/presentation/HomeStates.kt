package com.app.maincontent.presentation

import com.app.core.common.state.RequestStatus
import com.app.core.common.state.UiState

data class ContentUiState(
    val rows: List<ContentRow> = emptyList(),
    val selections: Map<Int, Set<Int>> = emptyMap(),
    val collapsedSectionIds: Set<Int> = emptySet(),
    val status: RequestStatus = RequestStatus.IDLE,
    val isShowingCachedData: Boolean = false,
    val errorMessage: String? = null
) : UiState {
    val visibleRows: List<ContentRow> get() = rows.visibleWith(collapsedSectionIds)
}
