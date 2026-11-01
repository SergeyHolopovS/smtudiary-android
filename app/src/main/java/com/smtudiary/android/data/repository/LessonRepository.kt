package com.smtudiary.android.data.repository

import com.smtudiary.android.data.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {
    fun observeLessons(group: String): Flow<List<Lesson>>
    suspend fun refreshLessons(group: String): Result<Unit>
}
