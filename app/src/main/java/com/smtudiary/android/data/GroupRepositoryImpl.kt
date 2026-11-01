package com.smtudiary.android.data

import com.smtudiary.android.data.local.GroupEntity
import com.smtudiary.android.data.local.GroupsDao
import com.smtudiary.android.data.model.Group
import com.smtudiary.android.data.remote.SmtuDiaryApi
import com.smtudiary.android.data.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val api: SmtuDiaryApi,
    private val dao: GroupsDao
) : GroupRepository {
    override fun observeGroups(): Flow<List<Group>> =
        dao.observeGroups().map { it.map(GroupEntity::toModel) }

    override suspend fun refreshGroups(): Result<Unit> = suspendRunCatching {
        dao.upsertAll(api.getGroups().map { GroupEntity(it) })
    }
}
