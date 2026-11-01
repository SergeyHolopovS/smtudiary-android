package com.smtudiary.android.data.repository

import com.smtudiary.android.data.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {

    fun observeGroups(): Flow<List<Group>>

    suspend fun refreshGroups(): Result<Unit>

}