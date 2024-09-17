package com.practicum.playlistmaker.App

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.utils.Utilities

class App : Application() {
    //мой индикатор темы
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val darkTheme =
            getSharedPreferences(Utilities.PLAYLIST_SAVED_PREFERENCES, MODE_PRIVATE).getBoolean(
                Utilities.NIGHT_THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        getSharedPreferences(Utilities.PLAYLIST_SAVED_PREFERENCES, MODE_PRIVATE).edit()
                .putBoolean(Utilities.NIGHT_THEME_KEY, darkThemeEnabled)
                .apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}