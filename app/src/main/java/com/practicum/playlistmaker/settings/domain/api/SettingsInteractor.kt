package com.practicum.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun getSavedNightTheme():Boolean
    fun saveNightTheme(newValue: Boolean)
}