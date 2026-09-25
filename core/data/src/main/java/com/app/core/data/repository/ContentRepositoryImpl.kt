package com.app.core.data.repository

import com.app.core.common.result.DataResult
import com.app.core.data.mapper.buildDomainPages
import com.app.core.data.mapper.toContentEntities
import com.app.core.database.dao.ContentDao
import com.app.core.model.Page
import com.app.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class ContentRepositoryImpl(
    private val api: ApiService,
    private val dao: ContentDao,
    private val defaultDispatcher: CoroutineDispatcher
) : ContentRepository {

    private fun cachedPages(): Flow<List<Page>> = combine(
        dao.observePages(),
        dao.observeItems(),
        dao.observeResponseSets(),
        dao.observeResponses()
    ) { pages, items, responseSets, responses ->
        buildDomainPages(pages, items, responseSets, responses)
    }.flowOn(defaultDispatcher)

    override fun observeContent(): Flow<DataResult<List<Page>>> = flow {
        emit(DataResult.Loading)

        try {
            val fresh = api.getResponseApi()
            val entities = fresh.toContentEntities()
            dao.replaceAll(entities.pages, entities.items, entities.responseSets, entities.responses)
            emit(DataResult.Success(cachedPages().first(), isFromCache = false))
        } catch (error: Throwable) {
            emit(DataResult.Error(error, error.message))
            val cached = cachedPages().first()
            if (cached.isNotEmpty()) {
                emit(DataResult.Success(cached, isFromCache = true))
            }
        }
    }
}
