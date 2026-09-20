package com.app.network

object ApiConfig {
    const val BASE_URL = "https://gist.githubusercontent.com/"
    const val HTTP_CACHE_SIZE_BYTES = 10L * 1024 * 1024 // 10 MB
    const val CACHE_MAX_AGE_SECONDS = 60 * 5
    const val CACHE_MAX_STALE_DAYS = 7
}
