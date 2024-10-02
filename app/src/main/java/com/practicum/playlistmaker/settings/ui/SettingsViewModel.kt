package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.utils.Creator
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val mySettingsInteractor: SettingsInteractor,
    private val mySharingInteractor: SharingInteractor
) : ViewModel() {
    private var myNightModLiveData =
        MutableLiveData<Boolean>(getNightTheme())
    fun share() {
        mySharingInteractor.openShare()
    }

    fun support() {
        mySharingInteractor.openSupport()
    }

    fun userAgreement() {
        mySharingInteractor.openUserAgreement()
    }

    fun getNightTheme(): Boolean {
        return mySettingsInteractor.getSavedNightTheme()
    }

    fun saveNightTheme(switchValue: Boolean) {
        myNightModLiveData.postValue(switchValue)
        mySettingsInteractor.saveNightTheme(switchValue)
    }
    fun getNightModStateLiveData(): LiveData<Boolean> = myNightModLiveData

    companion object {
        fun getSettingViewModel(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    Creator.provideGetSettingsInteractor(),
                    Creator.provideGetSharingInteractor()
                )
            }
        }
    }
}