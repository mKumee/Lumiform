package com.app.core.data.mapper

import com.app.core.database.entity.ItemEntity
import com.app.core.database.entity.PageEntity
import com.app.core.database.entity.ResponseEntity
import com.app.core.database.entity.ResponseSetEntity


data class ContentEntities(
    val pages: List<PageEntity>,
    val items: List<ItemEntity>,
    val responseSets: List<ResponseSetEntity>,
    val responses: List<ResponseEntity>
)
