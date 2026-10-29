package com.smtudiary.android.di

import android.R.attr.level
import android.content.Context
import android.content.pm.ApplicationInfo
import com.smtudiary.android.data.remote.SmtuDiaryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideJson() = Json { ignoreUnknownKeys = true }

    @Provides @Singleton
    fun provideOkHttp(@ApplicationContext context: Context): OkHttpClient {
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .apply {
                if (isDebug) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                }
            }
            .build()
    }

    @Provides @Singleton
    fun provideApi(client: OkHttpClient, json: Json): SmtuDiaryApi =
        Retrofit.Builder()
            .baseUrl("https://smtudiary.ru/api/v1/lessons/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(SmtuDiaryApi::class.java)

}