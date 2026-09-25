package com.app.network

import com.app.network.data.ItemDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ItemDtoTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parses a page with nested sections, text, image and choice questions`() {
        val payload = """
            [
              {
                "id": 1, "type": "page", "title": "Main Page",
                "items": [
                  {
                    "id": 2, "type": "section", "title": "Introduction",
                    "items": [
                      { "id": 3, "type": "text", "content": "Welcome to the main page!" },
                      { "id": 4, "type": "image", "src": "https://example.com/a.png", "title": "Welcome Image" }
                    ]
                  },
                  {
                    "id": 13, "type": "choice", "content": "What is the main topic?",
                    "response_set": {
                      "id": 101, "multiple_selection": false,
                      "responses": [
                        { "id": 1011, "label": "Safety procedures", "score": 1 },
                        { "id": 1013, "label": "Not applicable", "score": null }
                      ]
                    }
                  }
                ]
              }
            ]
        """.trimIndent()

        val items: List<ItemDto> = json.decodeFromString(payload)
        val page = items.first()

        assertEquals("Main Page", page.title)
        assertEquals(2, page.items?.size)

        val section = page.items!![0]
        assertEquals("Welcome to the main page!", section.items!![0].content)
        assertEquals("Welcome Image", section.items!![1].title)

        val choice = page.items!![1]
        val responseSet = requireNotNull(choice.responseSet)
        assertEquals(false, responseSet.multipleSelection)
        assertEquals(1, responseSet.responses.first().score)
        assertNull(responseSet.responses.last().score)
    }

    @Test
    fun `unknown type still parses - validation happens later at the mapper, not here`() {
        val items: List<ItemDto> = json.decodeFromString("""[ { "id": 1, "type": "mystery" } ]""")
        assertEquals("mystery", items.first().type)
    }
}
