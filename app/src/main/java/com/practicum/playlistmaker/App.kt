package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    //мой индикатор темы
    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs =getSharedPreferences(Utilities.PLAYLIST_SAVED_PREFERENCES,MODE_PRIVATE)
        darkTheme=sharedPrefs.getBoolean(Utilities.NIGHT_THEME_KEY,false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        val sharedPrefs =getSharedPreferences(Utilities.PLAYLIST_SAVED_PREFERENCES,MODE_PRIVATE)
        SharedPreferenceManager.saveNightTheme(sharedPrefs,darkTheme)
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}