package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun getSavedNightTheme() = repository.getSavedNightTheme()

    override fun saveNightTheme(newValue: Boolean) {
        repository.saveNightTheme(newValue)
    }

    override fun openShare() {
        repository.openShare()
    }

    override fun openSupport() {
        repository.openSupport()
    }

    override fun openUserAgreement() {
        repository.openUserAgreement()
    }
}