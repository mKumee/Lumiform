package com.app.core.data.util

import com.app.core.common.result.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = {}
): Flow<DataResult<ResultType>> = flow {
    emit(DataResult.Loading)

    val cached = query().first()

    if (shouldFetch(cached)) {
        try {
            saveFetchResult(fetch())
            emitAll(query().map { DataResult.Success(it, isFromCache = false) })
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            emit(DataResult.Error(throwable, throwable.message))
            // Still surface whatever is cached so the screen isn't left empty on failure.
            if (hasUsableCache(cached)) {
                emit(DataResult.Success(cached, isFromCache = true))
            }
        }
    } else {
        emitAll(query().map { DataResult.Success(it, isFromCache = false) })
    }
}

@PublishedApi
internal fun hasUsableCache(cached: Any?): Boolean = when (cached) {
    is Collection<*> -> cached.isNotEmpty()
    null -> false
    else -> true
}
