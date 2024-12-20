package com.practicum.playlistmaker.medialibrary.ui.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.medialibrary.domain.models.MediaPlaylistsState
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistInteractor
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaPlaylistsFragmentViewModel(private val myPlaylistInteractor: NewPlaylistInteractor) : ViewModel() {
    private var mediaPlaylistsLiveData = MutableLiveData<MediaPlaylistsState>(
            MediaPlaylistsState.Default
    )
    private var isClickOnPlaylistAllowed = true
    private var clickDebounceJob: Job? = null
    init {
        getPlaylists()
    }
    private fun getPlaylists(){
        viewModelScope.launch {
            myPlaylistInteractor.getAllSavedPlaylists().collect{
                foundList -> processPlaylists(foundList)
            }
        }
    }
    private fun processPlaylists(savedList:List<Playlist>){
        if(savedList.isEmpty()){
            renderState(MediaPlaylistsState.NoPlaylists)
        } else{
            renderState(MediaPlaylistsState.PlaylistsFound(savedList))
        }
    }
    private fun renderState(state: MediaPlaylistsState) {
        mediaPlaylistsLiveData.postValue(state)
    }
    fun updatePlaylistsIfInitialized() {
        if (mediaPlaylistsLiveData.value is MediaPlaylistsState.PlaylistsFound || mediaPlaylistsLiveData.value is MediaPlaylistsState.NoPlaylists) {
            getPlaylists()
        }
    }

    fun showClickOnPlaylist(currentPlaylist: Playlist, fragmentOpener: () -> Unit) {//на случай если создать фрагмент с плейлистом для удаления и редактирования
        if (isClickOnPlaylistAllowed) {//клик разрешен
            isClickOnPlaylistAllowed = false
            clickDebounceJob = viewModelScope.launch {
                delay(DEBOUNCE_DELAY)
                isClickOnPlaylistAllowed = true
            }
            fragmentOpener()
        }
    }


    fun getMediaPlaylistsLiveData(): LiveData<MediaPlaylistsState> = mediaPlaylistsLiveData
    companion object{
        const val DEBOUNCE_DELAY= 2000L
    }
}