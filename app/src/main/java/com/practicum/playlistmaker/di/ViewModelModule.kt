package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.editPlaylist.ui.EditPlaylistViewModel
import com.practicum.playlistmaker.medialibrary.ui.fragments.MediaFavoritesFragmentViewModel
import com.practicum.playlistmaker.medialibrary.ui.fragments.MediaPlaylistsFragmentViewModel
import com.practicum.playlistmaker.newPlaylist.ui.NewPlaylistViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.playlistOverview.ui.PlaylistOverviewViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PlayerViewModel(
            myPlayerInteractor = get(),
            myFavoritesHistoryInteractor = get(),
            myNewPlaylistInteractor = get(),
            myPlaylistOverviewInteractor = get()
        )
    }

    viewModel {
        SearchViewModel(mySearchInteractor = get())
    }

    viewModel {
        SettingsViewModel(
            mySettingsInteractor = get(),
            mySharingInteractor = get())
    }
    viewModel { (emptyFlag: Boolean) ->
        MediaPlaylistsFragmentViewModel(get())
    }
    viewModel { (emptyFlag: Boolean) ->
        MediaFavoritesFragmentViewModel(
            mySearchInteractor = get(),
            myFavoritesHistoryInteractor = get())
    }
    viewModel {
        NewPlaylistViewModel(myPlaylistInteractor = get())
    }
    viewModel {(flag: Int) ->
        EditPlaylistViewModel(
            myPlaylistInteractor = get(),
            myPlaylistId = flag)
    }
    viewModel { (flag: Int) ->
        PlaylistOverviewViewModel(
            myNewPlaylistOverviewInteractor = get(),
            myPlaylistInteractor = get(),
            mySearchInteractor = get(),
            mySharingInteractor = get(),
            myPlaylistId = flag
        )
    }
}