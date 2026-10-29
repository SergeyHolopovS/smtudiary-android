package com.smtudiary.android.di

import android.content.Context
import androidx.room.Room
import com.smtudiary.android.data.local.AppDatabase
import com.smtudiary.android.data.local.LessonsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "smtudiary.db").build()

    @Provides
    fun provideLessonsDao(db: AppDatabase): LessonsDao = db.lessonsDao()

}