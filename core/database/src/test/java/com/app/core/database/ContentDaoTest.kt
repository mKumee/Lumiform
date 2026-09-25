package com.app.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.app.core.database.entity.ItemEntity
import com.app.core.database.entity.ItemType
import com.app.core.database.entity.PageEntity
import com.app.core.database.entity.ResponseEntity
import com.app.core.database.entity.ResponseSetEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ContentDaoTest {

    private lateinit var db: LumiformDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), LumiformDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `replaceAll swaps out the old snapshot entirely`() = runTest {
        val dao = db.contentDao()
        dao.replaceAll(listOf(PageEntity(1, "old", 0)), emptyList(), emptyList(), emptyList())

        dao.replaceAll(
            pages = listOf(PageEntity(2, "new", 0)),
            items = listOf(ItemEntity(3, parentPageId = 2, parentItemId = null, type = ItemType.TEXT, title = null, content = "hi", imageSrc = null, orderIndex = 0)),
            responseSets = emptyList(),
            responses = emptyList()
        )

        val pages = dao.observePages().first()
        val items = dao.observeItems().first()
        assertEquals(1, pages.size)
        assertEquals("new", pages.first().title)
        assertTrue(pages.none { it.id == 1 })
        assertEquals("hi", items.first().content)
    }

    @Test
    fun `response sets and responses link back to the right question`() = runTest {
        val dao = db.contentDao()
        dao.replaceAll(
            pages = listOf(PageEntity(1, "Page", 0)),
            items = listOf(ItemEntity(2, parentPageId = 1, parentItemId = null, type = ItemType.CHOICE, title = null, content = "Pick one", imageSrc = null, orderIndex = 0)),
            responseSets = listOf(ResponseSetEntity(100, questionItemId = 2, multipleSelection = false)),
            responses = listOf(
                ResponseEntity(1000, responseSetId = 100, label = "A", score = 1, orderIndex = 0),
                ResponseEntity(1001, responseSetId = 100, label = "B", score = null, orderIndex = 1)
            )
        )

        val responseSets = dao.observeResponseSets().first()
        val responses = dao.observeResponses().first()
        assertEquals(1, responseSets.size)
        assertEquals(2, responses.count { it.responseSetId == 100 })
    }
}
