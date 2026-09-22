package com.app.core.common.dispatcher

import javax.inject.Qualifier

/**
 * Single Hilt qualifier parameterised by [AppDispatcher], instead of one @IoDispatcher /
 * @MainDispatcher / @DefaultDispatcher annotation each. One annotation to declare, one to
 * remember at every injection site - e.g. `@Dispatcher(AppDispatcher.IO) private val io: CoroutineDispatcher`.
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class Dispatcher(val appDispatcher: AppDispatcher)

enum class AppDispatcher {
    DEFAULT,
    IO,
    MAIN,
    MAIN_IMMEDIATE
}
