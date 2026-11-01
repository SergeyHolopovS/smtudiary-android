package com.smtudiary.android.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.smtudiary.android.data.local.AppDatabase
import com.smtudiary.android.data.local.GroupsDao
import com.smtudiary.android.data.local.LessonsDao
import com.smtudiary.android.data.local.settingsDataStore
import com.smtudiary.android.data.repository.SettingsRepository
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
        Room.databaseBuilder(ctx, AppDatabase::class.java, "smtudiary.db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideLessonsDao(db: AppDatabase): LessonsDao = db.lessonsDao()

    @Provides
    fun provideGroupsDao(db: AppDatabase): GroupsDao = db.groupsDao()

    @Provides @Singleton
    fun provideDataStore(
        @ApplicationContext ctx: Context
    ): DataStore<Preferences> =
        ctx.settingsDataStore

    @Provides @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>
    ): SettingsRepository =
        SettingsRepository(dataStore)

}
