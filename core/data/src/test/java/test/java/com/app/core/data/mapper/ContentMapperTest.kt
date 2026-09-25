package test.java.com.app.core.data.mapper

import com.app.core.data.mapper.buildDomainPages
import com.app.core.data.mapper.toContentEntities
import com.app.core.model.ChoiceQuestion
import com.app.core.model.Section
import com.app.core.model.TextQuestion
import com.app.network.data.ItemDto
import com.app.network.data.ResponseDto
import com.app.network.data.ResponseSetDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ContentMapperTest {

    @Test
    fun `flattening then rebuilding reproduces the same tree`() {
        val original = listOf(
            ItemDto(
                id = 1, type = "page", title = "Main Page",
                items = listOf(
                    ItemDto(
                        id = 2, type = "section", title = "Introduction",
                        items = listOf(
                            ItemDto(id = 3, type = "text", content = "hi there"),
                            ItemDto(
                                id = 4, type = "choice", content = "pick one",
                                responseSet = ResponseSetDto(
                                    id = 100, multipleSelection = true,
                                    responses = listOf(ResponseDto(id = 1, label = "A"), ResponseDto(id = 2, label = "B"))
                                )
                            )
                        )
                    )
                )
            )
        )

        val entities = original.toContentEntities()
        val rebuilt = buildDomainPages(entities.pages, entities.items, entities.responseSets, entities.responses)

        val section = rebuilt.first().items.first() as Section
        assertEquals("hi there", (section.items[0] as TextQuestion).content)
        val choice = section.items[1] as ChoiceQuestion
        assertEquals(true, choice.responseSet.multipleSelection)
        assertEquals(2, choice.responseSet.responses.size)
    }

    @Test(expected = IllegalStateException::class)
    fun `a page nested inside another item is rejected`() {
        listOf(ItemDto(id = 1, type = "page", items = listOf(ItemDto(id = 2, type = "page"))))
            .toContentEntities()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `a choice question without response_set fails loudly`() {
        listOf(ItemDto(id = 1, type = "page", items = listOf(ItemDto(id = 2, type = "choice", content = "x"))))
            .toContentEntities()
    }
}
