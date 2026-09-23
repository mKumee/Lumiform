package com.app.core.data.mapper

import com.app.core.database.entity.ItemEntity
import com.app.core.database.entity.ItemType
import com.app.core.database.entity.PageEntity
import com.app.core.database.entity.ResponseEntity
import com.app.core.database.entity.ResponseSetEntity
import com.app.network.data.ItemDto
import kotlin.collections.forEachIndexed


fun List<ItemDto>.toContentEntities(): ContentEntities {
    val pages = mutableListOf<PageEntity>()
    val items = mutableListOf<ItemEntity>()
    val responseSets = mutableListOf<ResponseSetEntity>()
    val responses = mutableListOf<ResponseEntity>()

    fun flatten(children: List<ItemDto>, parentPageId: Int?, parentItemId: Int?) {
        children.forEachIndexed { index, child ->
            when (child.type) {
                "page" -> error("Page type, can not be added (id=${child.id})")

                "section" -> {
                    items += ItemEntity(
                        id = child.id.toInt(),
                        parentPageId = parentPageId,
                        parentItemId = parentItemId,
                        type = ItemType.SECTION,
                        title = child.title,
                        content = null,
                        imageSrc = null,
                        orderIndex = index
                    )
                    flatten(child.items.orEmpty(), parentPageId = null, parentItemId = child.id.toInt())
                }

                "text" -> items += ItemEntity(
                    id = child.id.toInt(), parentPageId = parentPageId, parentItemId = parentItemId,
                    type = ItemType.TEXT, title = null, content = child.content, imageSrc = null,
                    orderIndex = index
                )

                "image" -> items += ItemEntity(
                    id = child.id.toInt(), parentPageId = parentPageId, parentItemId = parentItemId,
                    type = ItemType.IMAGE, title = child.title, content = null, imageSrc = child.src,
                    orderIndex = index
                )

                "choice" -> {
                    val responseSet = requireNotNull(child.responseSet) {
                        "choice question ${child.id} is missing response_set"
                    }
                    items += ItemEntity(
                        id = child.id.toInt(), parentPageId = parentPageId, parentItemId = parentItemId,
                        type = ItemType.CHOICE, title = null, content = child.content, imageSrc = null,
                        orderIndex = index
                    )
                    responseSets += ResponseSetEntity(
                        id = responseSet.id.toInt(),
                        questionItemId = child.id.toInt(),
                        multipleSelection = responseSet.multipleSelection
                    )
                    responseSet.responses.forEachIndexed { responseIndex, response ->
                        responses += ResponseEntity(
                            id = response.id.toInt(),
                            responseSetId = responseSet.id.toInt(),
                            label = response.label,
                            score = response.score,
                            orderIndex = responseIndex
                        )
                    }
                }

                else -> error("Unknown item type in response: ${child.type}")
            }
        }
    }

    forEachIndexed { index, root ->
        if (root.type != "page") error("Top level item ${root.id} must be of type 'page'")
        pages += PageEntity(id = root.id.toInt(), title = root.title.orEmpty(), orderIndex = index)
        flatten(root.items.orEmpty(), parentPageId = root.id.toInt(), parentItemId = null)
    }

    return ContentEntities(pages, items, responseSets, responses)
}
