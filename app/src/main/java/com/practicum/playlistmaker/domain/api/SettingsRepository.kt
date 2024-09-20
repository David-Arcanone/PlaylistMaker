package com.practicum.playlistmaker.domain.api

interface SettingsRepository {
    fun openSupport()
    fun openShare()
    fun openUserAgreement()
    fun getSavedNightTheme():Boolean
    fun saveNightTheme(newValue: Boolean)
}