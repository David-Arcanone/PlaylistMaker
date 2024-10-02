package com.practicum.playlistmaker.settings.data.repository

import android.content.Context
import com.practicum.playlistmaker.App.App
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.utils.Utilities

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val sharedPref = context.getSharedPreferences(
        Utilities.PLAYLIST_SAVED_PREFERENCES,
        Context.MODE_PRIVATE
    )

    override fun getSavedNightTheme() =
        sharedPref.getBoolean(NIGHT_THEME_KEY, false)

    override fun saveNightTheme(newValue: Boolean) {
        (context.applicationContext as App).switchTheme(newValue)
    }

    companion object {
        private const val NIGHT_THEME_KEY = "key_for_night_theme"
    }
}