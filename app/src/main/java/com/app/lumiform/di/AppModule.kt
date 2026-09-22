package com.app.lumiform.di

import android.content.Context
import androidx.room.Room
import com.app.core.common.dispatcher.AppDispatcher
import com.app.core.common.dispatcher.Dispatcher
import com.app.core.data.repository.ContentRepository
import com.app.core.data.repository.OfflineFirstContentRepository
import com.app.core.database.LumiformDatabase
import com.app.core.database.dao.ContentDao
import com.app.network.ApiConfig
import com.app.network.ApiService
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .cache(cache)
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

    //Repository
    @Provides
    @Singleton
    fun provideContentRepository(
        api: ApiService,
        dao: ContentDao,
        @Dispatcher(AppDispatcher.DEFAULT) defaultDispatcher: CoroutineDispatcher
    ): ContentRepository = OfflineFirstContentRepository(api, dao, defaultDispatcher)

}