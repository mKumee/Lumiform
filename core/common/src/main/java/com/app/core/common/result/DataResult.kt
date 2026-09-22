package com.app.core.common.result

/**
 * Wraps any data-layer outcome so ViewModels never catch exceptions themselves.
 * [isFromCache] lets the UI show a subtle "showing offline data" hint on [Success]
 * without needing a separate signal.
 */
sealed class DataResult<out T> {
    data class Success<out T>(val data: T, val isFromCache: Boolean = false) : DataResult<T>()
    data class Error(val exception: Throwable, val message: String? = exception.message) : DataResult<Nothing>()
    data object Loading : DataResult<Nothing>()
}
