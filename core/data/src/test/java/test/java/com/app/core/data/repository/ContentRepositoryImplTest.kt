package test.java.com.app.core.data.repository

import app.cash.turbine.test
import com.app.core.common.result.DataResult
import com.app.core.data.repository.ContentRepositoryImpl
import com.app.core.database.dao.ContentDao
import com.app.core.database.entity.ItemEntity
import com.app.core.database.entity.ItemType
import com.app.core.database.entity.PageEntity
import com.app.core.database.entity.ResponseEntity
import com.app.core.database.entity.ResponseSetEntity
import com.app.core.model.Page
import com.app.core.model.TextQuestion
import com.app.network.ApiService
import com.app.network.data.ItemDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
class ContentRepositoryImplTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    private val api = mockk<ApiService>()
    private val dao = mockk<ContentDao>()

    private val pages = MutableStateFlow<List<PageEntity>>(emptyList())
    private val items = MutableStateFlow<List<ItemEntity>>(emptyList())
    private val responseSets = MutableStateFlow<List<ResponseSetEntity>>(emptyList())
    private val responses = MutableStateFlow<List<ResponseEntity>>(emptyList())

    private lateinit var repository: ContentRepositoryImpl

    @Before
    fun setUp() {
        every { dao.observePages() } returns pages
        every { dao.observeItems() } returns items
        every { dao.observeResponseSets() } returns responseSets
        every { dao.observeResponses() } returns responses
        repository = ContentRepositoryImpl(api, dao, testDispatcher)
    }

    @Test
    fun `success path - Loading then a fresh Success`() = runTest(testDispatcher) {
        coEvery { api.getResponseApi() } returns listOf(
            ItemDto(id = 1, type = "page", title = "Main Page", items = listOf(ItemDto(id = 2, type = "text", content = "hello")))
        )
        coEvery { dao.replaceAll(any(), any(), any(), any()) } answers {
            pages.value = listOf(PageEntity(1, "Main Page", 0))
            items.value = listOf(ItemEntity(2, parentPageId = 1, parentItemId = null, type = ItemType.TEXT, title = null, content = "hello", imageSrc = null, orderIndex = 0))
        }

        repository.observeContent().test {
            assertEquals(DataResult.Loading, awaitItem())
            val success = awaitItem() as DataResult.Success<List<Page>>
            assertEquals("Main Page", success.data.first().title)
            assertEquals(false, success.isFromCache)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { dao.replaceAll(any(), any(), any(), any()) }
    }

    @Test
    fun `failure with an existing cache - falls back to it, tagged as isFromCache`() = runTest(testDispatcher) {
        pages.value = listOf(PageEntity(1, "Cached Page", 0))
        items.value = listOf(ItemEntity(2, parentPageId = 1, parentItemId = null, type = ItemType.TEXT, title = null, content = "cached", imageSrc = null, orderIndex = 0))
        coEvery { api.getResponseApi() } throws java.io.IOException("no connection")

        repository.observeContent().test {
            assertEquals(DataResult.Loading, awaitItem())
            val error = awaitItem() as DataResult.Error
            assertTrue(error.exception is java.io.IOException)
            val fallback = awaitItem() as DataResult.Success<List<Page>>
            assertTrue(fallback.isFromCache)
            assertEquals("cached", (fallback.data.first().items.first() as TextQuestion).content)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 0) { dao.replaceAll(any(), any(), any(), any()) }
    }

    @Test
    fun `failure with nothing cached yet - just the error, no empty Success afterwards`() = runTest(testDispatcher) {
        coEvery { api.getResponseApi() } throws java.io.IOException("no connection")

        repository.observeContent().test {
            assertEquals(DataResult.Loading, awaitItem())
            val error = awaitItem() as DataResult.Error
            assertTrue(error.exception is java.io.IOException)
            awaitComplete()
        }
    }
}