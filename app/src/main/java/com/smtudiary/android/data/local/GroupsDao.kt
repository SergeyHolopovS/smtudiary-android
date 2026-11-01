package com.smtudiary.android.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupsDao {

    @Query("SELECT * FROM `groups` ORDER BY `group`")
    fun observeGroups(): Flow<List<GroupEntity>>

    @Upsert
    suspend fun upsertAll(groups: List<GroupEntity>)

}