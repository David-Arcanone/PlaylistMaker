package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.medialibrary.ui.fragments.MediaFavoritesFragmentViewModel
import com.practicum.playlistmaker.medialibrary.ui.fragments.MediaPlaylistsFragmentViewModel
import com.practicum.playlistmaker.newPlaylist.ui.NewPlaylistViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PlayerViewModel(myPlayerInteractor = get(),get(),get())
    }

    viewModel {
        SearchViewModel(mySearchInteractor = get())
    }

    viewModel {
        SettingsViewModel(mySettingsInteractor = get(), mySharingInteractor = get())
    }
    viewModel { (emptyFlag: Boolean) ->
        MediaPlaylistsFragmentViewModel(get())
    }
    viewModel { (emptyFlag: Boolean) ->
        MediaFavoritesFragmentViewModel(get(),get())
    }
    viewModel {
        NewPlaylistViewModel(get())
    }
}