package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun getSavedNightTheme() = repository.getSavedNightTheme()

    override fun saveNightTheme(newValue: Boolean) {
        repository.saveNightTheme(newValue)
    }
}