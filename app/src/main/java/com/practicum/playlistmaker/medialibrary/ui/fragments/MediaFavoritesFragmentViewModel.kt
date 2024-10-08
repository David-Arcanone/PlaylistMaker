package com.practicum.playlistmaker.medialibrary.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.medialibrary.domain.models.MediaFavoritesState

class MediaFavoritesFragmentViewModel(private val emptyFlag: Boolean) : ViewModel() {
    private var mediaFavoritesLiveData = MutableLiveData<MediaFavoritesState>(
        if (emptyFlag) {
            MediaFavoritesState.NoFavorites
        } else {
            MediaFavoritesState.Default
        }
    )

    fun getMediaFavoritesLiveData(): LiveData<MediaFavoritesState> = mediaFavoritesLiveData
}