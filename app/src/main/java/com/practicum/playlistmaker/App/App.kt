package com.practicum.playlistmaker.App

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.utils.Utilities
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule)
        }

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