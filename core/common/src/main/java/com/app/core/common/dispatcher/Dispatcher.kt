package com.app.core.common.dispatcher

import javax.inject.Qualifier


@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class Dispatcher(val appDispatcher: AppDispatcher)

enum class AppDispatcher {
    DEFAULT,
    IO,
    MAIN,
    MAIN_IMMEDIATE
}
