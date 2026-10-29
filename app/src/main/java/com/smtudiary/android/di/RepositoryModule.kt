package com.smtudiary.android.di

import com.smtudiary.android.data.LessonRepositoryImpl
import com.smtudiary.android.data.repository.LessonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLessonsRepository(impl: LessonRepositoryImpl): LessonRepository

}