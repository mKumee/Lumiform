package com.app.core.data.mapper

import com.app.core.database.entity.ItemEntity
import com.app.core.database.entity.ItemType
import com.app.core.database.entity.PageEntity
import com.app.core.database.entity.ResponseEntity
import com.app.core.database.entity.ResponseSetEntity
import com.app.core.model.ChoiceQuestion
import com.app.core.model.ContentNode
import com.app.core.model.ImageQuestion
import com.app.core.model.Page
import com.app.core.model.Response
import com.app.core.model.ResponseSet
import com.app.core.model.Section
import com.app.core.model.TextQuestion


fun buildDomainPages(
    pages: List<PageEntity>,
    items: List<ItemEntity>,
    responseSets: List<ResponseSetEntity>,
    responses: List<ResponseEntity>
): List<Page> {
    val responsesBySetId: Map<Int, List<Response>> = responses
        .groupBy { it.responseSetId }
        .mapValues { (_, list) -> list.sortedBy { it.orderIndex }.map { Response(it.id, it.label, it.score) } }

    val responseSetByQuestionId: Map<Int, ResponseSetEntity> = responseSets.associateBy { it.questionItemId }
    val childrenOfPage: Map<Int, List<ItemEntity>> = items.filter { it.parentPageId != null }.groupBy { it.parentPageId!! }
    val childrenOfItem: Map<Int, List<ItemEntity>> = items.filter { it.parentItemId != null }.groupBy { it.parentItemId!! }

    fun buildNode(entity: ItemEntity): ContentNode = when (entity.type) {
        ItemType.SECTION -> Section(
            id = entity.id,
            title = entity.title.orEmpty(),
            items = childrenOfItem[entity.id].orEmpty().sortedBy { it.orderIndex }.map(::buildNode)
        )

        ItemType.TEXT -> TextQuestion(id = entity.id, content = entity.content.orEmpty())

        ItemType.IMAGE -> ImageQuestion(
            id = entity.id,
            src = entity.imageSrc.orEmpty(),
            title = entity.title.orEmpty()
        )

        ItemType.CHOICE -> {
            val set = responseSetByQuestionId[entity.id]
            ChoiceQuestion(
                id = entity.id,
                content = entity.content.orEmpty(),
                responseSet = ResponseSet(
                    id = set?.id ?: entity.id,
                    multipleSelection = set?.multipleSelection ?: false,
                    responses = set?.let { responsesBySetId[it.id] }.orEmpty()
                )
            )
        }

        else -> error("Unknown content item type in cache: ${entity.type}")
    }

    return pages.sortedBy { it.orderIndex }.map { page ->
        Page(
            id = page.id,
            title = page.title,
            items = childrenOfPage[page.id].orEmpty().sortedBy { it.orderIndex }.map(::buildNode)
        )
    }
}
