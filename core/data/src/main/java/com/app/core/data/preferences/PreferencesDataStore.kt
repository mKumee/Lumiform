package com.app.core.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Serializable
private data class SelectionEntry(val questionId: Int, val responseIds: List<Int>)

class PreferencesRepository(context: Context) {
    private val dataStore = context.dataStore
    private val json = Json { ignoreUnknownKeys = true }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SELECTED_RESPONSES = stringPreferencesKey("selected_responses_json")
    }

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE]?.let { name -> ThemeMode.entries.firstOrNull { it.name == name } } ?: ThemeMode.LIGHT
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    val selectedResponses: Flow<Map<Int, Set<Int>>> = dataStore.data.map { prefs ->
        prefs[Keys.SELECTED_RESPONSES]?.let { raw -> decodeSelections(raw) } ?: emptyMap()
    }

    suspend fun setSelectedResponses(selections: Map<Int, Set<Int>>) {
        dataStore.edit { it[Keys.SELECTED_RESPONSES] = encodeSelections(selections) }
    }

    private fun encodeSelections(selections: Map<Int, Set<Int>>): String =
        json.encodeToString(selections.map { (questionId, responseIds) -> SelectionEntry(questionId, responseIds.toList()) })

    private fun decodeSelections(raw: String): Map<Int, Set<Int>> =
        runCatching { json.decodeFromString<List<SelectionEntry>>(raw) }
            .getOrDefault(emptyList())
            .associate { it.questionId to it.responseIds.toSet() }
}
