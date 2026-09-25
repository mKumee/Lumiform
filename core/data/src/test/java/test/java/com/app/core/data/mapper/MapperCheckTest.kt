package test.java.com.app.core.data.mapper

import com.app.core.data.mapper.buildDomainPages
import com.app.core.data.mapper.toContentEntities
import com.app.network.data.ItemDto
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureTimeMillis


class MapperCheckTest {

    @Test
    fun `mapping a large document stays fast`() {
        val bigDocument = buildLargeDocument(pageCount = 20, sectionsPerPage = 10, questionsPerSection = 10)

        val elapsedMs = measureTimeMillis {
            val entities = bigDocument.toContentEntities()
            buildDomainPages(entities.pages, entities.items, entities.responseSets, entities.responses)
        }

        println("mapped ${bigDocument.size} pages (~2000 leaf items) in ${elapsedMs}ms")
        assertTrue("mapping took ${elapsedMs}ms, expected well under 1000ms on CI hardware", elapsedMs < 1000)
    }

    private fun buildLargeDocument(pageCount: Int, sectionsPerPage: Int, questionsPerSection: Int): List<ItemDto> {
        var nextId = 1
        fun id() = nextId++

        return (1..pageCount).map { pageIndex ->
            ItemDto(
                id = id().toLong(), type = "page", title = "Page $pageIndex",
                items = (1..sectionsPerPage).map { sectionIndex ->
                    ItemDto(
                        id = id().toLong(), type = "section", title = "Section $sectionIndex",
                        items = (1..questionsPerSection).map {
                            ItemDto(id = id().toLong(), type = "text", content = "generated question body")
                        }
                    )
                }
            )
        }
    }
}
