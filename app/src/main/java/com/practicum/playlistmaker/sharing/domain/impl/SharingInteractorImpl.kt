package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingRepository

class SharingInteractorImpl(private val repository: SharingRepository) : SharingInteractor {
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