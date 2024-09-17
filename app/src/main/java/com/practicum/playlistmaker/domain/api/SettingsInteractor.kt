package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.consumer.TracksConsumer

interface SettingsInteractor {
    fun getSavedNightTheme():Boolean
    fun saveNightTheme(newValue: Boolean)

    fun openShare()
    fun openUserAgreement()
    fun openSupport()
}