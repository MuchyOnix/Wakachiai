package com.hikari.anime.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.extensionDataStore by preferencesDataStore("extension_preferences")

@Singleton
class ExtensionPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val activeSourceKey = longPreferencesKey("active_source_id")

    val activeSourceId: Flow<Long> = context.extensionDataStore.data
        .map { prefs -> prefs[activeSourceKey] ?: 4815162342L }

    suspend fun setActiveSourceId(id: Long) {
        context.extensionDataStore.edit { prefs -> prefs[activeSourceKey] = id }
    }
}
