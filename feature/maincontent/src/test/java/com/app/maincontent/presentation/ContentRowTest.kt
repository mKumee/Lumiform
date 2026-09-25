package com.app.maincontent.presentation

import com.app.core.model.Page
import com.app.core.model.Section
import com.app.core.model.TextQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentRowTest {

    private val tree = listOf(
        Page(
            id = 1, title = "Main Page",
            items = listOf(
                Section(
                    id = 2, title = "Chapter 1",
                    items = listOf(
                        TextQuestion(id = 3, content = "Intro text"),
                        Section(id = 4, title = "Subsection 1.1", items = listOf(TextQuestion(id = 5, content = "Nested text")))
                    )
                )
            )
        )
    )

    @Test
    fun `depth increases one level per nesting`() {
        val rows = tree.toContentRows()
        assertEquals(0, rows.first { it.id == 1 }.depth)
        assertEquals(1, rows.first { it.id == 2 }.depth)
        assertEquals(2, rows.first { it.id == 3 }.depth)
        assertEquals(2, rows.first { it.id == 4 }.depth)
        assertEquals(3, rows.first { it.id == 5 }.depth)
    }

    @Test
    fun `only top level sections get a numbered order label`() {
        val rows = tree.toContentRows()
        assertEquals("01", rows.first { it.id == 2 }.orderLabel)
        assertEquals(null, rows.first { it.id == 4 }.orderLabel)
    }

    @Test
    fun `collapsing an outer section hides everything nested inside it`() {
        val visible = tree.toContentRows().visibleWith(collapsedSectionIds = setOf(2))
        assertEquals(setOf(1, 2), visible.map { it.id }.toSet())
        assertFalse(visible.any { it.id == 3 })
        assertFalse(visible.any { it.id == 5 })
    }

    @Test
    fun `collapsing the nested section leaves its parent's own content visible`() {
        val visible = tree.toContentRows().visibleWith(collapsedSectionIds = setOf(4))
        assertTrue(visible.any { it.id == 3 })
        assertTrue(visible.any { it.id == 4 })
        assertFalse(visible.any { it.id == 5 })
    }
}
