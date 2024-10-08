package com.practicum.playlistmaker.medialibrary.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.medialibrary.domain.models.MediaPlaylistsState

class MediaPlaylistsFragmentViewModel(private val emptyFlag: Boolean) : ViewModel() {
    private var mediaPlaylistsLiveData = MutableLiveData<MediaPlaylistsState>(
        if (emptyFlag) {
            MediaPlaylistsState.NoPlaylists
        } else {
            MediaPlaylistsState.Default
        }
    )

    fun getMediaPlaylistsLiveData(): LiveData<MediaPlaylistsState> = mediaPlaylistsLiveData
}