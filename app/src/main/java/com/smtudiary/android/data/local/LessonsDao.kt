package com.smtudiary.android.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonsDao {

    @Query("SELECT * FROM lessons WHERE `group` = :group ORDER BY day")
    fun observeByGroupOrderByDay(group: Int): Flow<List<LessonEntity>>

    @Upsert
    suspend fun upsertAll(lessons: List<LessonEntity>)

}