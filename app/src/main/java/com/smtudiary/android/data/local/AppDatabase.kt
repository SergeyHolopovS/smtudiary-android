package com.smtudiary.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LessonEntity::class, GroupEntity::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lessonsDao(): LessonsDao

    abstract fun groupsDao(): GroupsDao

}