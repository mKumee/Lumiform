package com.app.core.data.repository

import com.app.core.common.result.DataResult
import com.app.core.data.mapper.buildDomainPages
import com.app.core.data.mapper.toContentEntities
import com.app.core.data.util.networkBoundResource
import com.app.core.database.dao.ContentDao
import com.app.core.model.Page
import com.app.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn


class OfflineFirstContentRepository(
    private val api: ApiService,
    private val dao: ContentDao,
    private val defaultDispatcher: CoroutineDispatcher
) : ContentRepository {

    override fun observeContent(): Flow<DataResult<List<Page>>> = networkBoundResource(
        query = {
            combine(
                dao.observePages(),
                dao.observeItems(),
                dao.observeResponseSets(),
                dao.observeResponses()
            ) { pages, items, responseSets, responses ->
                buildDomainPages(pages, items, responseSets, responses)
            }.flowOn(defaultDispatcher)
        },
        shouldFetch = { true },
        fetch = { api.getResponseApi()},
        saveFetchResult = { dtoItems ->
            val entities = dtoItems.toContentEntities()
            dao.replaceAll(entities.pages, entities.items, entities.responseSets, entities.responses)
        }
    )
}
