package com.smtudiary.android.data

import com.smtudiary.android.data.local.LessonEntity
import com.smtudiary.android.data.local.LessonsDao
import com.smtudiary.android.data.model.Lesson
import com.smtudiary.android.data.remote.LessonDto
import com.smtudiary.android.data.remote.SmtuDiaryApi
import com.smtudiary.android.data.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor(
    private val api: SmtuDiaryApi,
    private val dao: LessonsDao
) : LessonRepository {
    override fun observeLessons(group: String): Flow<List<Lesson>> =
        dao.observeByGroupOrderByDay(group.toInt()).map { it.map(LessonEntity::toModel) }

    override suspend fun refreshLessons(group: String): Result<Unit> = suspendRunCatching {
        dao.upsertAll(api.getLessonsByGroup(group).map(LessonDto::toEntity))
    }
}
