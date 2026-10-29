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
import kotlin.coroutines.cancellation.CancellationException

class LessonRepositoryImpl @Inject constructor(
    private val api: SmtuDiaryApi,
    private val dao: LessonsDao
) : LessonRepository {
    override suspend fun observeLessons(group: String): Flow<List<Lesson>> =
        dao.observeByGroupOrderByDay(group).map { it.map(LessonEntity::toDomain) }

    override suspend fun refreshLessons(group: String): Result<Unit> = suspendRunCatching {
        dao.upsertAll(api.getLessonsByGroup(group).map(LessonDto::toEntity) )
    }
}

private inline fun <T> suspendRunCatching(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}