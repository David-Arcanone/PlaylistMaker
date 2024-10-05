package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PlayerViewModel(myPlayerInteractor = get())
    }

    viewModel{
        SearchViewModel(mySearchInteractor = get())
    }

    viewModel {
        SettingsViewModel(mySettingsInteractor = get(), mySharingInteractor = get())
    }
}