package com.app.core.common.result


sealed class DataResult<out T> {
    data class Success<out T>(val data: T, val isFromCache: Boolean = false) : DataResult<T>()
    data class Error(val exception: Throwable, val message: String? = exception.message) : DataResult<Nothing>()
    data object Loading : DataResult<Nothing>()
}
