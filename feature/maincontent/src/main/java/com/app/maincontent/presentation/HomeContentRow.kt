package com.app.maincontent.presentation

import com.app.core.model.ChoiceQuestion
import com.app.core.model.ContentItems
import com.app.core.model.ImageQuestion
import com.app.core.model.Page
import com.app.core.model.ResponseSet
import com.app.core.model.Section
import com.app.core.model.TextQuestion
enum class ContentRowType { PAGE, SECTION, TEXT, IMAGE, CHOICE }
data class ContentRow(
    val id: Int,
    val depth: Int,
    val type: ContentRowType,
    val ancestorSectionIds: List<Int>,
    val title: String? = null,
    val content: String? = null,
    val imageSrc: String? = null,
    val responseSet: ResponseSet? = null,
    val pageNumber: Int? = null,
    val totalPages: Int? = null,
    val orderLabel: String? = null
)

fun List<Page>.toContentRows(): List<ContentRow> {
    val rows = mutableListOf<ContentRow>()
    val totalPages = size

    fun walk(node: ContentItems, depth: Int, ancestorSectionIds: List<Int>) {
        when (node) {
            is Page -> error("Page should only appear at the top level - handled by the outer loop")

            is Section -> {
                rows += ContentRow(node.id, depth, ContentRowType.SECTION, ancestorSectionIds, title = node.title)
                val childAncestors = ancestorSectionIds + node.id
                node.items.forEach { walk(it, depth + 1, childAncestors) }
            }

            is TextQuestion ->
                rows += ContentRow(node.id, depth, ContentRowType.TEXT, ancestorSectionIds, content = node.content)

            is ImageQuestion ->
                rows += ContentRow(node.id, depth, ContentRowType.IMAGE, ancestorSectionIds, title = node.title, imageSrc = node.src)

            is ChoiceQuestion ->
                rows += ContentRow(node.id, depth, ContentRowType.CHOICE, ancestorSectionIds, content = node.content, responseSet = node.responseSet)
        }
    }

    forEachIndexed { pageIndex, page ->
        rows += ContentRow(
            page.id, depth = 0, ContentRowType.PAGE, ancestorSectionIds = emptyList(),
            title = page.title, pageNumber = pageIndex + 1, totalPages = totalPages
        )
     var topLevelSectionIndex = 0
        page.items.forEach { child ->
            if (child is Section) {
                topLevelSectionIndex++
                val label = topLevelSectionIndex.toString().padStart(2, '0')
                rows += ContentRow(child.id, depth = 1, ContentRowType.SECTION, ancestorSectionIds = emptyList(), title = child.title, orderLabel = label)
                val childAncestors = listOf(child.id)
                child.items.forEach { grandchild -> walk(grandchild, depth = 2, ancestorSectionIds = childAncestors) }
            } else {
                walk(child, depth = 1, ancestorSectionIds = emptyList())
            }
        }
    }
    return rows
}

fun List<ContentRow>.visibleWith(collapsedSectionIds: Set<Int>): List<ContentRow> =
    if (collapsedSectionIds.isEmpty()) this
    else filter { row -> row.ancestorSectionIds.none { it in collapsedSectionIds } }
