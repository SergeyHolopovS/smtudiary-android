package com.smtudiary.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SettingsKeys {
    val GROUP = stringPreferencesKey("")
}

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val group: Flow<String> = dataStore.data.map { preferences -> preferences[SettingsKeys.GROUP] ?: "" }

    suspend fun setGroup(group: String) =
        dataStore.edit { preferences -> preferences[SettingsKeys.GROUP] = group }

}