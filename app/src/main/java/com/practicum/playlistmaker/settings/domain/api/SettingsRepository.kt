package com.practicum.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun getSavedNightTheme():Boolean
    fun saveNightTheme(newValue: Boolean)
}