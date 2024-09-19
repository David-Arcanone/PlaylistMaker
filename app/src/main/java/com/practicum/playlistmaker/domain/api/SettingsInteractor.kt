package com.practicum.playlistmaker.domain.api

interface SettingsInteractor {
    fun getSavedNightTheme():Boolean
    fun saveNightTheme(newValue: Boolean)
    fun openShare()
    fun openUserAgreement()
    fun openSupport()
}