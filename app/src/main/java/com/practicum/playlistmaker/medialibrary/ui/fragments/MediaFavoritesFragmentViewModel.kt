package com.practicum.playlistmaker.medialibrary.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryInteractor
import com.practicum.playlistmaker.medialibrary.domain.models.MediaFavoritesState
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaFavoritesFragmentViewModel(
    private val myFavoritesHistoryInteractor: FavoritesHistoryInteractor,
    private val mySearchInteractor: SearchInteractor
) : ViewModel() {
    private var mediaFavoritesLiveData =
        MutableLiveData<MediaFavoritesState>(MediaFavoritesState.Default)
    private var isClickOnTrackAllowed = true
    private var clickDebounceJob: Job? = null

    init {
        getFavorites()
    }

    private fun getFavorites() {
        viewModelScope.launch {
            myFavoritesHistoryInteractor.getSavedFavorites().collect { favorites ->
                processFavorites(favorites)
            }
        }
    }

    private fun processFavorites(favorites: List<Track>) {
        if (favorites.isEmpty()) {
            renderState(MediaFavoritesState.NoFavorites)
        } else {
            renderState(MediaFavoritesState.Favorites(favorites))
        }
    }

    private fun renderState(state: MediaFavoritesState) {
        mediaFavoritesLiveData.postValue(state)
    }

    fun updateFavoritesIfInitialized() {
        if (mediaFavoritesLiveData.value is MediaFavoritesState.Favorites) {
            getFavorites()
        }
    }

    fun showClickOnTrack(newTrack: Track, fragmentOpener: () -> Unit) {
        if (isClickOnTrackAllowed) {//клик разрешен
            isClickOnTrackAllowed = false
            //по условиям запрет на активацию по первому нажатию на трек и отменять старый Job не нужно
            clickDebounceJob = viewModelScope.launch {
                delay(SearchViewModel.SEARCH_DEBOUNCE_DELAY)
                isClickOnTrackAllowed = true
            }
            mySearchInteractor.updateHistoryWithNewTrack(newTrack)//нужно в история поиска добавить посещение из залайканных
            fragmentOpener()
        }
    }

    fun getMediaFavoritesLiveData(): LiveData<MediaFavoritesState> = mediaFavoritesLiveData
}