package com.app.maincontent.presentation

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.app.core.common.result.DataResult
import com.app.core.common.state.RequestStatus
import com.app.core.data.preferences.PreferencesRepository
import com.app.core.data.repository.ContentRepository
import com.app.core.model.Page
import com.app.core.model.Section
import com.app.core.model.TextQuestion
import com.app.maincontent.usecases.ToggleResponseSelectionUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HomeContentViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() = Dispatchers.resetMain()

    private val contentState = MutableStateFlow<DataResult<List<Page>>>(DataResult.Loading)
    private val repository = mockk<ContentRepository> { every { observeContent() } returns contentState }
    private val prefs = mockk<PreferencesRepository> { every { selectedResponses } returns flowOf(emptyMap()) }
    private val toggleUseCase = mockk<ToggleResponseSelectionUseCase>(relaxed = true)

    private fun viewModel() = HomeContentViewModel(repository, prefs, toggleUseCase)

    private suspend fun ReceiveTurbine<ContentUiState>.awaitUntil(predicate: (ContentUiState) -> Boolean): ContentUiState {
        var latest = awaitItem()
        var guard = 0
        while (!predicate(latest) && guard < 20) {
            latest = awaitItem()
            guard++
        }
        return latest
    }

    @Test
    fun `moves to Success once the repository emits data`() = runTest {
        val vm = viewModel()
        contentState.value = DataResult.Success(listOf(Page(id = 1, title = "Main Page", items = listOf(TextQuestion(id = 2, content = "hi")))))

        vm.uiState.test {
            val success = awaitUntil { it.status == RequestStatus.SUCCESS }
            assertEquals(2, success.rows.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `collapsing a section removes its descendants from visibleRows only`() = runTest {
        val vm = viewModel()
        contentState.value = DataResult.Success(
            listOf(Page(id = 1, title = "Page", items = listOf(Section(id = 2, title = "Section", items = listOf(TextQuestion(id = 3, content = "hidden"))))))
        )

        vm.uiState.test {
            val loaded = awaitUntil { it.status == RequestStatus.SUCCESS }
            assertEquals(3, loaded.rows.size)
            assertEquals(3, loaded.visibleRows.size)

            vm.onSectionToggled(2)

            val collapsed = awaitUntil { 2 in it.collapsedSectionIds }
            assertEquals(3, collapsed.rows.size)
            assertEquals(2, collapsed.visibleRows.size)
            assertTrue(2 in collapsed.collapsedSectionIds)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry does nothing while already loading`() = runTest {
        val vm = viewModel()
        contentState.value = DataResult.Loading

        vm.uiState.test {
            awaitUntil { it.status == RequestStatus.LOADING }
            vm.retry()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
