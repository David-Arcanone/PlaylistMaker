package com.practicum.playlistmaker.settings.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.App.App
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.utils.Utilities

class SettingsRepositoryImpl(
    private val context: Context,
    private val sharedPref: SharedPreferences
) : SettingsRepository {
    override fun getSavedNightTheme() =
        sharedPref.getBoolean(NIGHT_THEME_KEY, false)

    override fun saveNightTheme(newValue: Boolean) {
        (context.applicationContext as App).switchTheme(newValue)
    }

    companion object {
        private const val NIGHT_THEME_KEY = "key_for_night_theme"
    }
}