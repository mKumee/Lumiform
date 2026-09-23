package com.app.maincontent.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.common.result.DataResult
import com.app.core.common.state.RequestStatus
import com.app.core.data.preferences.PreferencesRepository
import com.app.core.data.repository.ContentRepository
import com.app.core.model.Page
import com.app.maincontent.usecases.ToggleResponseSelectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeContentViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val userPreferences: PreferencesRepository,
    private val toggleResponseSelectionUseCase: ToggleResponseSelectionUseCase
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)
    private val collapsedSectionIds = MutableStateFlow<Set<Int>>(emptySet())

    private val contentResult: StateFlow<DataResult<List<Page>>> =
        refreshTrigger
            .flatMapLatest { contentRepository.observeContent() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DataResult.Loading)

    val uiState: StateFlow<ContentUiState> = combine(
        contentResult, userPreferences.selectedResponses, collapsedSectionIds
    ) { result, selections, collapsed ->
        when (result) {
            DataResult.Loading -> ContentUiState(status = RequestStatus.LOADING, collapsedSectionIds = collapsed)
            is DataResult.Error -> ContentUiState(status = RequestStatus.ERROR, errorMessage = result.message, collapsedSectionIds = collapsed)
            is DataResult.Success -> ContentUiState(
                rows = result.data.toContentRows(),
                selections = selections,
                collapsedSectionIds = collapsed,
                status = RequestStatus.SUCCESS,
                isShowingCachedData = result.isFromCache
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ContentUiState())

    fun retry() {
        if (uiState.value.status == RequestStatus.LOADING) return
        refreshTrigger.update { it + 1 }
    }

    fun onSectionToggled(sectionId: Int) {
        collapsedSectionIds.update { current -> if (sectionId in current) current - sectionId else current + sectionId }
    }

    fun onResponseToggled(questionId: Int, responseId: Int, allowMultipleSelection: Boolean) {
        viewModelScope.launch { toggleResponseSelectionUseCase(questionId, responseId, allowMultipleSelection) }
    }
}