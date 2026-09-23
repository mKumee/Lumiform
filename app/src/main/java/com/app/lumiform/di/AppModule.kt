package com.app.lumiform.di

import android.content.Context
import androidx.room.Room
import com.app.core.common.dispatcher.AppDispatcher
import com.app.core.common.dispatcher.Dispatcher
import com.app.core.data.preferences.PreferencesRepository
import com.app.core.data.repository.ContentRepository
import com.app.core.data.repository.ContentRepositoryImpl
import com.app.core.database.LumiformDatabase
import com.app.core.database.dao.ContentDao
import com.app.network.ApiConfig
import com.app.network.ApiService
import com.app.network.OfflineCacheInterceptor
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Dispatcher(AppDispatcher.IO)
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(AppDispatcher.DEFAULT)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Dispatcher(AppDispatcher.MAIN)
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Dispatcher(AppDispatcher.MAIN_IMMEDIATE)
    fun provideMainImmediateDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache =
        Cache(File(context.cacheDir, "http_cache"), ApiConfig.HTTP_CACHE_SIZE_BYTES)

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        offlineCacheInterceptor: OfflineCacheInterceptor,
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(offlineCacheInterceptor)
        .addNetworkInterceptor { chain ->
            chain.proceed(chain.request()).newBuilder()
                .header("Cache-Control", "public, max-age=${ApiConfig.CACHE_MAX_AGE_SECONDS}")
                .build()
        }
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()


    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideOfflineCacheInterceptor(@ApplicationContext context: Context): OfflineCacheInterceptor =
        OfflineCacheInterceptor(context)

    @Provides
    @Singleton
    fun provideContentApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)


    //Database
    @Provides
    @Singleton
    fun provideLumiformDatabase(@ApplicationContext context: Context): LumiformDatabase =
        Room.databaseBuilder(context, LumiformDatabase::class.java, LumiformDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLumiformDao(database: LumiformDatabase): ContentDao = database.contentDao()

    @Provides
    @Singleton
    fun provideUserPreferencesDataSource(@ApplicationContext context: Context): PreferencesRepository =
        PreferencesRepository(context)

    //Repository
    @Provides
    @Singleton
    fun provideContentRepository(
        api: ApiService,
        dao: ContentDao,
        @Dispatcher(AppDispatcher.DEFAULT) defaultDispatcher: CoroutineDispatcher
    ): ContentRepository = ContentRepositoryImpl(api, dao, defaultDispatcher)

}