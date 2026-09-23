package com.app.lumiform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.data.preferences.PreferencesRepository
import com.app.core.model.ThemeMode

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userPreferences: PreferencesRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemeMode.LIGHT)

    fun toggleTheme() {
        viewModelScope.launch {
            val next = if (themeMode.value == ThemeMode.LIGHT) ThemeMode.DARK else ThemeMode.LIGHT
            userPreferences.setThemeMode(next)
        }
    }
}
